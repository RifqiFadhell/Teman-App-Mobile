package id.teman.app.ui.sharedviewmodel

import android.app.Application
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.BuildConfig
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.restaurant.ItemRestaurantModel
import id.teman.app.domain.model.user.ProfileSpec
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.domain.repository.food.FoodRepository
import id.teman.app.manager.UserManager
import id.teman.app.manager.UserState
import id.teman.app.preference.Preference
import id.teman.app.utils.Event
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@HiltViewModel
class MainViewModel @Inject constructor(
    private val geocoder: Geocoder,
    private val json: Json,
    private val foodRepository: FoodRepository,
    private val remoteConfig: FirebaseRemoteConfig,
    private val userManager: UserManager,
    private val preference: Preference,
    private val application: Application
) : ViewModel() {

    var locationUiState by mutableStateOf(LocationUiState())
        private set

    var userInfoState by mutableStateOf(UserProfileUiState())
        private set

    var isAlreadyUpdateHomeAddress: Boolean = false

    init {
        updateUserInfo()
        viewModelScope.launch {
            userManager.start()
            userManager.observeUserState().collect { userState ->
                if (userState is UserState.Revoked) {
                    locationUiState = locationUiState.copy(logoutEvent = Event(Unit))
                }
            }
        }
    }

    fun checkIsAppUpToDate() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val latestVersion = remoteConfig.getLong("app_version")
                if (latestVersion > BuildConfig.VERSION_CODE) {
                    locationUiState = LocationUiState(isNeedUpdate = true)
                }
            }
        }
    }

    fun checkIfInsuranceShow(): Boolean {
        var isInsuranceShow = false
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isInsuranceShow = remoteConfig.getBoolean("is_insurance_show")
            }
        }
        return isInsuranceShow
    }

    fun updateUserInfo() {
        viewModelScope.launch {
            val isUserLoggedIn = preference.getIsUserLoggedIn.first()
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
            }
        }
    }

    fun removeAllPreference() {
        viewModelScope.launch {
            preference.clearLoginValues()
        }
    }

    fun changeLoadingState(showLoading: Boolean) {
        locationUiState = locationUiState.copy(loading = showLoading)
    }

    fun updateUserLocation(latitude: Double, longitude: Double) {
        locationUiState =
            locationUiState.copy(currentUserLocationLatLng = LatLng(latitude, longitude))
        if (!isAlreadyUpdateHomeAddress) {
            updateHomeLocationAddress(latitude, longitude)
        }
        changeLoadingState(false)
    }

    fun changeHomeLocationAddress(newPlaceDetail: PlaceDetailSpec) {
        locationUiState = locationUiState.copy(
            placeDetailSpec = newPlaceDetail, userLocation = newPlaceDetail.shortLocationAddress,
            currentUserLocationLatLng = newPlaceDetail.locationLatLng
        )
    }

    private fun getUserProfile(): UserInfo? {
        var userInfo: UserInfo? = null
        val userInfoJson = runBlocking { preference.getUserInfo.first() }
        if (userInfoJson.isNotBlank()) {
            userInfo = json.decodeFromString(userInfoJson)
        }
        return userInfo
    }

    fun updateDestination(spec: PlaceDetailSpec?) {
        locationUiState = locationUiState.copy(destinationFormPlaceDetail = spec)
    }

    fun updateHomeLocationAddress(latitude: Double, longitude: Double) {
        if (Geocoder.isPresent()) {
            val addresses: MutableList<Address>
            try {
                addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    5
                ) as ArrayList<Address>

                if (addresses.isNotEmpty()) {
                    val city = addresses.getOrNull(0)?.subAdminArea.orEmpty()
                    val subAdminArea = addresses.getOrNull(0)?.subLocality.orEmpty()
                    val spec = PlaceDetailSpec(
                        LatLng(latitude, longitude),
                        addresses.getOrNull(0)?.getAddressLine(0).orEmpty(),
                        "$subAdminArea, $city"
                    )
                    locationUiState = locationUiState.copy(
                        userLocation = "$subAdminArea, $city", placeDetailSpec = spec,
                        currentUserLocationLatLng = LatLng(latitude, longitude)
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        isAlreadyUpdateHomeAddress = true
    }

    fun changeLocationStatusState(state: LocationPermissionState) {
        locationUiState = locationUiState.copy(userLocationStatus = Event(state))
    }

    fun getListRestaurantNearby() {
        locationUiState = locationUiState.copy(loading = true)
        viewModelScope.launch {
            foodRepository.getListRestaurant(locationUiState.currentUserLocationLatLng)
                .catch { exception ->
                    locationUiState =
                        locationUiState.copy(loading = false, error = exception.message)
                }.collect {
                    locationUiState = locationUiState.copy(loading = false, listRestaurant = it)
                }
        }
    }

    fun checkFromDeeplink(intent: Intent?) {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener {
                it?.let { pendingDynamicLinkData ->
                    val deeplink = pendingDynamicLinkData.link
                    val referralCode: String? = deeplink?.getQueryParameter("referral")
                    locationUiState = locationUiState.copy(successGetReferral = referralCode.orEmpty())
                } ?: run {

                }
            }
    }

    fun saveDataLocationSend(placeDetailSpecSend: PlaceDetailSpec? = null) {
        locationUiState = locationUiState.copy(placeDetailSpecSend = placeDetailSpecSend)
    }

    data class LocationUiState(
        val loading: Boolean = false,
        val isNeedUpdate: Boolean = false,
        val userLocation: String = "Lokasi belum terdeteksi",
        val userLocationStatus: Event<LocationPermissionState>? = null,
        val destinationFormPlaceDetail: PlaceDetailSpec? = null,
        val currentUserLocationLatLng: LatLng = LatLng(-6.172131022973852, 107.0425259263954),
        val placeDetailSpec: PlaceDetailSpec? = null,
        val error: String? = null,
        val listRestaurant: List<ItemRestaurantModel>? = emptyList(),
        val logoutEvent: Event<Unit>? = null,
        val successGetReferral: String? = "",
        val placeDetailSpecSend: PlaceDetailSpec? = null
    )

    data class UserProfileUiState(
        val profileSpec: ProfileSpec? = null
    )
}

sealed class LocationPermissionState {
    object LocationActive : LocationPermissionState()
    object LocationDenied : LocationPermissionState()
}