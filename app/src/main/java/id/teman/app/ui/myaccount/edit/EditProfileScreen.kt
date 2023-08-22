package id.teman.app.ui.myaccount.edit

import android.annotation.SuppressLint
import id.teman.app.R as rAppModule
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.common.ButtonType
import id.teman.app.common.CenterTopAppBar
import id.teman.app.common.CustomChip
import id.teman.app.common.CustomLoading
import id.teman.app.common.TopBar
import id.teman.app.common.isNotNullOrEmpty
import id.teman.app.domain.model.camera.CameraResult
import id.teman.app.domain.model.camera.CameraSpec
import id.teman.app.domain.model.camera.CameraType
import id.teman.app.ui.destinations.LargeCameraScreenDestination
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Destination
@Composable
fun EditProfileScreen(
    navigator: DestinationsNavigator,
    editProfileViewModel: EditProfileViewModel = hiltViewModel(),
    resultLargePhoto: ResultRecipient<LargeCameraScreenDestination, String>,
) {
    val profileState = editProfileViewModel.userInfoState
    val uiState = editProfileViewModel.editProfileUiState
    var profileImage by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var profileImagePath by rememberSaveable { mutableStateOf("") }
    var errorProfileImage by rememberSaveable { mutableStateOf<String?>(null) }
    var nameUpdated by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = { false }
    )

    resultLargePhoto.onNavResult { result ->
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val value = Json.decodeFromString<CameraResult>(result.value)
                when (value.cameraType) {
                    CameraType.PROFILE -> {
                        errorProfileImage = null
                        profileImage = Uri.parse(value.uri)
                        coroutineScope.launch {
                            modalSheetState.hide()
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = uiState.successUpdate, block = {
        uiState.successUpdate?.consumeOnce {
            keyboardController?.hide()
            focusManager.clearFocus()
            profileImage = Uri.EMPTY
        }
    })

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            errorProfileImage = null
            profileImage = result.uriContent
            profileImagePath = result.getUriFilePath(context).orEmpty()
            coroutineScope.launch {
                modalSheetState.hide()
            }
        } else {
            result.error
        }
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            val cropOptions = CropImageContractOptions(uri, CropImageOptions())
            imageCropLauncher.launch(cropOptions)
        }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        sheetElevation = Theme.dimension.size_8dp,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            TopBar(title = "Mau upload foto dari mana?", icon = id.teman.app.R.drawable.ic_round_close) {
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimension.size_16dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = UiColor.white,
                            shape = RoundedCornerShape(Theme.dimension.size_4dp)
                        )
                        .shadow(elevation = Theme.dimension.size_1dp)
                        .padding(Theme.dimension.size_16dp)
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        }
                ) {
                    GlideImage(
                        imageModel = rAppModule.drawable.ic_gallery,
                        modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)
                            .size(Theme.dimension.size_72dp)
                    )
                    Text(modifier = Modifier.align(CenterHorizontally),
                        text = "Galeri",
                        style = UiFont.poppinsP3SemiBold,
                        color = UiColor.neutral900
                    )
                }
                Column(
                    modifier = Modifier
                        .background(
                            color = UiColor.white,
                            shape = RoundedCornerShape(Theme.dimension.size_4dp)
                        )
                        .shadow(elevation = Theme.dimension.size_1dp)
                        .padding(Theme.dimension.size_16dp)
                        .clickable {
                            navigator.navigate(
                                LargeCameraScreenDestination(
                                    cameraSpec = CameraSpec(
                                        "Unggah Foto Profil Kamu",
                                        false,
                                        CameraType.PROFILE
                                    )
                                )
                            )
                        }
                ) {
                    GlideImage(
                        imageModel = rAppModule.drawable.ic_camera,
                        modifier = Modifier
                            .padding(horizontal = Theme.dimension.size_16dp)
                            .size(Theme.dimension.size_72dp)
                    )
                    Text(modifier = Modifier.align(CenterHorizontally),
                        text = "Kamera",
                        style = UiFont.poppinsP3SemiBold,
                        color = UiColor.neutral900
                    )
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    CenterTopAppBar(
                        elevation = Theme.dimension.size_0dp,
                        backgroundColor = Color.Transparent,
                        title = {
                            Text(
                                text = "Ubah Profil",
                                style = UiFont.poppinsH5SemiBold,
                                textAlign = TextAlign.Center
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                navigator.popBackStack()
                            }) {
                                Icon(Icons.Filled.ArrowBack, "backIcon")
                            }
                        },
                    )
                },
                content = {
                    Column(
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_32dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            )
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Foto Profil",
                            style = UiFont.poppinsP2SemiBold
                        )
                        Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
                        Row {
                            GlideImage(
                                imageModel = if (profileImage == Uri.EMPTY) profileState.profileSpec?.image.orEmpty() else profileImage,
                                modifier = Modifier
                                    .size(Theme.dimension.size_48dp)
                                    .clip(CircleShape),
                                failure = {
                                    Image(
                                        painter = painterResource(id = rAppModule.drawable.ic_person_mamoji),
                                        contentDescription = "failed"
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                            CustomChip(
                                title = "Upload Foto",
                                backgroundColor = UiColor.tertiaryBlue800,
                                borderColor = UiColor.tertiaryBlue800,
                                contentColor = UiColor.tertiaryBlue800,
                                textColor = UiColor.white,
                                onClick = {
                                    coroutineScope.launch {
                                        modalSheetState.show()
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(Theme.dimension.size_32dp))
                        EnterNameFieldWidget(profileState.profileSpec?.name.orEmpty()) {
                            nameUpdated = it
                        }
                        Spacer(modifier = Modifier.height(Theme.dimension.size_24dp))
                        EnterPhoneNumberWidget(profileState.profileSpec?.number.orEmpty())
                        if (uiState.loading) {
                            Dialog(
                                onDismissRequest = { },
                                DialogProperties(
                                    dismissOnBackPress = false,
                                    dismissOnClickOutside = false
                                )
                            ) {
                                CustomLoading(modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                },
                bottomBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        TemanFilledButton(
                            content = "Simpan",
                            buttonType = ButtonType.Large,
                            activeColor = UiColor.primaryRed500,
                            activeTextColor = Color.White,
                            isEnabled = nameUpdated.isNotNullOrEmpty() || profileImage != Uri.EMPTY,
                            borderRadius = Theme.dimension.size_30dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = Theme.dimension.size_16dp,
                                    vertical = Theme.dimension.size_24dp
                                ),
                            onClicked = {
                                if (nameUpdated != profileState.profileSpec?.name || profileImage != Uri.EMPTY) {
                                    editProfileViewModel.updateProfile(nameUpdated, profileImage, profileImagePath)
                                }
                            }
                        )
                    }
                }
            )
        })
}

@Composable
fun EnterNameFieldWidget(name: String, onChange: (String) -> Unit) {
    var updatedName by remember { mutableStateOf(name) }
    Text(
        "Nama",
        style = UiFont.poppinsP2Medium
    )
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(Theme.dimension.size_56dp),
        value = updatedName,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = UiColor.neutral100,
            cursorColor = UiColor.black,
            unfocusedBorderColor = UiColor.neutral100
        ),
        onValueChange = {
            updatedName = it
            onChange(updatedName)
        },
    )
}

@Composable
fun EnterPhoneNumberWidget(phoneNumber: String) {
    Text(
        "Nomor HP",
        style = UiFont.poppinsP2Medium
    )
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(Theme.dimension.size_56dp),
        value = phoneNumber,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        enabled = false,
        onValueChange = {},
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = UiColor.neutral100,
            cursorColor = UiColor.black,
            unfocusedBorderColor = UiColor.neutral100
        ),
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(TextFieldDefaults.MinHeight)
                    .background(color = UiColor.neutral100)
            ) {
                Text(
                    "+62", style = UiFont.poppinsCaptionSemiBold,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}