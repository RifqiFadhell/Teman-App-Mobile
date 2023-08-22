package id.teman.app.ui.myaccount.edit

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.component1
import com.google.firebase.dynamiclinks.ktx.component2
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.BuildConfig
import id.teman.app.common.createPartFromString
import id.teman.app.common.orFalse
import id.teman.app.domain.model.user.ProfileSpec
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.domain.repository.user.UserRepository
import id.teman.app.preference.Preference
import id.teman.app.ui.myaccount.referral.ReferralCodeConstant.DOMAIN_PREFIX_DEEP_LINK
import id.teman.app.ui.myaccount.referral.ReferralCodeConstant.URL_DEEP_LINK
import id.teman.app.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileDescriptor
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository,
    private val json: Json,
    private val preference: Preference
) : ViewModel() {

    var editProfileUiState by mutableStateOf(EditProfileUiState())
        private set

    var userInfoState by mutableStateOf(UserProfileUiState())
        private set

    init {
        updateUserInfo()
    }

    fun updateProfile(userName: String? = "", profileImage: Uri, uriPath: String) {
        editProfileUiState = editProfileUiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val map: MutableMap<String, RequestBody> = mutableMapOf()
            val photo = if (profileImage != Uri.EMPTY) createMultipartImageFromUri(
                profileImage,
                "user_photo",
                uriPath
            ) else null
            map["name"] = createPartFromString(userName.orEmpty())
            userRepository.updateUserProfile(
                partMap = map,
                profileImageFile = photo
            ).catch { exception ->
                editProfileUiState = editProfileUiState.copy(
                    loading = false,
                    error = Event(exception.message.orEmpty()), isEnable = false
                )
            }.collect {
                editProfileUiState = editProfileUiState.copy(
                    loading = false,
                    successUpdate = Event(Unit),
                    isEnable = false
                )
                val rawJson = json.encodeToString(it)
                preference.setUserInfo(rawJson)
                updateUserInfo()
            }
        }
    }

    fun updateNotification(isNotificationShow: Boolean) {
        editProfileUiState = editProfileUiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val map: MutableMap<String, RequestBody> = mutableMapOf()
            map["notification"] = createPartFromString(isNotificationShow.toString())
            userRepository.updateUserProfile(
                partMap = map
            ).catch { exception ->
                editProfileUiState = editProfileUiState.copy(
                    loading = false,
                    error = Event(exception.message.orEmpty())
                )
            }.collect {
                editProfileUiState = editProfileUiState.copy(
                    loading = false,
                    isNotificationShow = it.notification,
                    successUpdate = Event(Unit)
                )
                val rawJson = json.encodeToString(it)
                preference.setUserInfo(rawJson)
            }
        }
    }

    private fun updateUserInfo() {
        val isUserLoggedIn = runBlocking { preference.getIsUserLoggedIn.first() }
        if (isUserLoggedIn) {
            with(getUserProfile()) {
                userInfoState = userInfoState.copy(
                    profileSpec = ProfileSpec(
                        this?.name.orEmpty(),
                        this?.phoneNumber.orEmpty(),
                        this?.userPhoto.orEmpty()
                    )
                )
            }
            editProfileUiState =
                editProfileUiState.copy(isNotificationShow = getUserProfile()?.notification.orFalse())
        }
    }

    fun generateDynamicLink() {
        Firebase.dynamicLinks.shortLinkAsync {
            link =
                Uri.parse(URL_DEEP_LINK + "?referral=${getUserProfile()?.referralCode.orEmpty()}")
            domainUriPrefix = DOMAIN_PREFIX_DEEP_LINK
            androidParameters(BuildConfig.APPLICATION_ID) {
                minimumVersion = 20
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            editProfileUiState =
                editProfileUiState.copy(successReferralCode = Event(shortLink.toString()))
        }.addOnFailureListener { result ->
            result
        }

//        val link = URL_DEEP_LINK + "referral?=${getUserProfile()?.referralCode.orEmpty()}"
//        FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLink(Uri.parse(link))
//            .setDomainUriPrefix(DOMAIN_PREFIX_DEEP_LINK)
//            .setAndroidParameters(
//                DynamicLink.AndroidParameters.Builder(BuildConfig.APPLICATION_ID)
//                    .setMinimumVersion(19)
//                    .build()
//            )
//            .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
//            .addOnSuccessListener { shortDynamicLink ->
//                val invitationUrl = shortDynamicLink.shortLink.toString()
//
//            }
//            .addOnFailureListener { result ->
//                result
//                Toast.makeText(
//                    application, application.getString(R.string.unstable_connection),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
    }

    fun getUserProfile(): UserInfo? {
        var userInfo: UserInfo? = null
        val userInfoJson = runBlocking { preference.getUserInfo.first() }
        if (userInfoJson.isNotBlank()) {
            userInfo = json.decodeFromString(userInfoJson)
        }
        return userInfo
    }

    private fun createMultipartImageFromUri(
        uri: Uri,
        key: String,
        uriPath: String
    ): MultipartBody.Part? {
        if (uri.path == null) return null
        val file = File(uriPath.ifEmpty { getPath(application, uri) })
        val requestFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            key, file.name,
            requestFile
        )
    }

    private fun getPath(context: Context, uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            context.contentResolver.query(uri, projection, null, null, null) ?: return ""
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s: String = cursor.getString(columnIndex)
        cursor.close()
        return s
    }

    fun test(context: Context, uri: Uri?) {
        if (uri == null) return
        val contentResolver: ContentResolver = context.contentResolver ?: return
        val parcelFileDescriptor: ParcelFileDescriptor? =
            contentResolver.openFileDescriptor(uri, "rw")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor?.fileDescriptor ?: return
        val bitmap: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
    }

    data class UserProfileUiState(
        val profileSpec: ProfileSpec? = null
    )

    data class EditProfileUiState(
        val loading: Boolean = false,
        val error: Event<String>? = null,
        val isEnable: Boolean? = false,
        val successUpdate: Event<Unit>? = null,
        val successReferralCode: Event<String>? = null,
        val isNotificationShow: Boolean? = true
    )
}