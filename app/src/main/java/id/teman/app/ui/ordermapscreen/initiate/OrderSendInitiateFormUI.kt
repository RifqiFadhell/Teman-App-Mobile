@file:OptIn(ExperimentalPermissionsApi::class)

package id.teman.app.ui.ordermapscreen.initiate

import android.content.Intent
import android.provider.ContactsContract.CommonDataKinds
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.isNotNullOrEmpty
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.ui.destinations.SearchLocationScreenDestination
import id.teman.app.ui.destinations.WebViewScreenDestination
import id.teman.app.ui.ordermapscreen.initiate.send.FormDetailSpec
import id.teman.app.ui.ordermapscreen.initiate.send.OrderPackageInsurance
import id.teman.app.ui.ordermapscreen.initiate.send.OrderPackageType
import id.teman.app.ui.ordermapscreen.initiate.send.WeightTextFormField
import id.teman.app.ui.sharedviewmodel.MainViewModel
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun OrderSendInitiateFormScreen(
    origin: PlaceDetailSpec,
    destination: PlaceDetailSpec,
    resultNavigator: ResultBackNavigator<FormDetailSpec>,
    mainViewModel: MainViewModel,
    resultSearchLocation: ResultRecipient<SearchLocationScreenDestination, PlaceDetailSpec>,
    navigator: DestinationsNavigator
) {
    val verticalState = rememberScrollState()

    val nearbyArea by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var receiverName by remember { mutableStateOf("") }
    var receiverPhone by remember { mutableStateOf("") }
    var packageType by remember { mutableStateOf("") }
    var insuranceType by remember { mutableStateOf(0) }
    var selectedWeight by remember { mutableStateOf(0) }

    var receiverErrorMsg by remember { mutableStateOf<String?>(null) }
    var receiverPhoneErrorMsg by remember { mutableStateOf<String?>(null) }
    var packageTypeErrorMsg by remember { mutableStateOf<String?>(null) }
    var packageTypeInsuranceErrorMsg by remember { mutableStateOf<String?>(null) }
    var weightErrorMsg by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val contactListLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val contactUri = result.data?.data
            val projection = arrayOf(CommonDataKinds.Phone.NUMBER)

            contactUri?.let { uri ->
                context.contentResolver.query(uri, projection, null, null, null)
                    .use { cursor ->
                        if (cursor != null && cursor.moveToFirst()) {
                            val numberIndex = cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER)
                            receiverPhone = cursor.getString(numberIndex).removeNonPhone()
                        }
                    }
            }
        }

    val uiState = mainViewModel.locationUiState

    resultSearchLocation.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                mainViewModel.saveDataLocationSend(
                    placeDetailSpecSend = uiState.placeDetailSpecSend?.copy(
                        locationLatLng = it.value.locationLatLng,
                        formattedAddress = it.value.formattedAddress
                    )
                )
            }
        }
    }

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        if (uiState.placeDetailSpecSend == null) {
            mainViewModel.saveDataLocationSend(destination)
        }
    }

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_DESTROY) {
        mainViewModel.saveDataLocationSend(null)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = Theme.dimension.size_20dp,
                        start = Theme.dimension.size_16dp
                    )
            ) {
                GlideImage(
                    imageModel = R.drawable.ic_arrow_back,
                    modifier = Modifier.size(Theme.dimension.size_24dp).clickable {
                        navigator.popBackStack()
                    }
                )
                Text(
                    "Konfirmasi Detail Pesanan",
                    style = UiFont.poppinsP3SemiBold,
                    modifier = Modifier.padding(
                        start = Theme.dimension.size_16dp
                    )
                )
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = Theme.dimension.size_16dp
                    )
                    .fillMaxSize()
                    .verticalScroll(verticalState)
                    .padding(
                        top = Theme.dimension.size_40dp,
                        bottom = Theme.dimension.size_72dp
                    )

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Antar Ke Alamat", style = UiFont.poppinsP2SemiBold)
                    Text("Ubah", style = UiFont.poppinsP2Medium, modifier = Modifier.clickable {
                        uiState.placeDetailSpecSend?.locationLatLng?.let {
                            navigator.navigate(
                                SearchLocationScreenDestination(
                                    "Mau antar kemana?",
                                    defaultLatLng = it
                                )
                            )
                        }
                    })
                }
                uiState.placeDetailSpecSend?.let { loc -> SelectedPlaceUI(loc) }
                FormTextField(
                    title = "Catatan (opsional)",
                    hint = "Masukkan catatan",
                    onTextChanged = {
                        notes = it
                    })
                Text(
                    "Daftar Penerima",
                    style = UiFont.poppinsP2SemiBold,
                    modifier = Modifier.padding(top = Theme.dimension.size_40dp)
                )
                FormTextField(
                    title = "Nama Penerima",
                    hint = "Masukkan nama penerima",
                    errorMsg = receiverErrorMsg,
                    onTextChanged = {
                        receiverName = it
                        receiverErrorMsg = null
                    })
                EnterPhoneNumberWidget(value = receiverPhone, onEnterNumber = {
                    receiverPhone = it
                    receiverPhoneErrorMsg = null
                }, errorMessage = receiverPhoneErrorMsg, onContactClick = {
                    val intent = Intent(Intent.ACTION_PICK).apply {
                        type = CommonDataKinds.Phone.CONTENT_TYPE
                    }
                    contactListLauncher.launch(intent)
                })
                OrderPackageType(errorMessage = packageTypeErrorMsg, onClick = {
                    packageType = it
                    packageTypeErrorMsg = null
                })
                if (mainViewModel.checkIfInsuranceShow()) {
                    OrderPackageInsurance(errorMessage = packageTypeInsuranceErrorMsg, onClick = {
                        insuranceType = it
                        packageTypeInsuranceErrorMsg = null
                    })
                } else {
                    Text(
                        "*Paket kamu sudah kami lindungi s.d Rp500.000.",
                        style = UiFont.poppinsP1Medium,
                        modifier = Modifier.padding(top = Theme.dimension.size_16dp)
                    )
                    Text(
                        "Syarat & ketentuan berlaku",
                        style = UiFont.poppinsP1Medium,
                        modifier = Modifier.clickable {
                            navigator.navigate(
                                WebViewScreenDestination(
                                    "Syarat Ketentuan Asuransi",
                                    "https://www.temanofficial.co.id/insurance"
                                )
                            )
                        },
                        textDecoration = TextDecoration.Underline,
                        color = UiColor.primaryRed500
                    )
                }
                Divider(
                    modifier = Modifier
                        .padding(vertical = Theme.dimension.size_16dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = UiColor.neutral100)
                )
                WeightTextFormField(
                    value = selectedWeight,
                    errorMessage = weightErrorMsg,
                    onSelected = {
                        selectedWeight = it
                        weightErrorMsg = null
                    })
                Column(
                    modifier = Modifier
                        .padding(top = Theme.dimension.size_16dp)
                        .fillMaxWidth()
                        .background(
                            color = UiColor.primaryYellow50,
                            shape = RoundedCornerShape(Theme.dimension.size_16dp)
                        )
                        .padding(Theme.dimension.size_16dp)
                ) {
                    Text(
                        "Layanan akan disesuaikan dengan berat barang",
                        style = UiFont.poppinsCaptionSemiBold
                    )
                    Text(
                        "T-Send Bike : Maks 15 kg\nDimensi : Maks 50x50x50cm",
                        style = UiFont.poppinsCaptionMedium,
                        modifier = Modifier.padding(vertical = Theme.dimension.size_4dp)
                    )
                }
            }
        },
        bottomBar = {
            TemanFilledButton(
                content = "Lanjutkan",
                buttonType = ButtonType.Large,
                activeColor = UiColor.primaryRed500,
                activeTextColor = Color.White,
                isEnabled = true,
                borderRadius = Theme.dimension.size_30dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Theme.dimension.size_16dp,
                        vertical = Theme.dimension.size_8dp
                    )
            ) {
                if (receiverName.isNotEmpty() && receiverPhone.isNotEmpty()
                    && packageType.isNotEmpty() && selectedWeight > 0
                ) {
                    mainViewModel.saveDataLocationSend(null)
                    uiState.placeDetailSpecSend?.let {
                        FormDetailSpec(
                            origin = origin,
                            destination = it,
                            nearbyArea = nearbyArea,
                            note = notes,
                            receiverName = receiverName,
                            receiverPhoneNumber = receiverPhone,
                            packageType = packageType,
                            packageWeight = selectedWeight,
                            insurance = insuranceType
                        )
                    }?.let {
                        resultNavigator.navigateBack(
                            it
                        )
                    }
                } else {
                    if (receiverName.isEmpty()) {
                        receiverErrorMsg = "Harap isi nama penerima"
                    }
                    if (receiverPhone.isEmpty()) {
                        receiverPhoneErrorMsg = "Harap isi nomor penerima"
                    }
                    if (packageType.isEmpty()) {
                        packageTypeErrorMsg = "Harap isi jenis package pengiriman"
                    }
                    if (insuranceType <= 0) {
                        packageTypeInsuranceErrorMsg = "Harap isi jenis garansi pengiriman"
                    }
                    if (selectedWeight <= 0) {
                        weightErrorMsg = "Harap isi beban package"
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun SelectedPlaceUI(spec: PlaceDetailSpec) {
    Row(
        modifier = Modifier
            .padding(
                top = Theme.dimension.size_24dp,
                start = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TemanCircleButton(
            icon = R.drawable.ic_location,
            iconModifier = Modifier
                .width(Theme.dimension.size_18dp)
                .height(Theme.dimension.size_22dp),
            circleBackgroundColor = UiColor.tertiaryBlue50,
            circleModifier = Modifier.size(Theme.dimension.size_52dp)
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Text(
            spec.formattedAddress,
            style = UiFont.poppinsP3SemiBold.copy(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    title: String, hint: String,
    errorMsg: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextChanged: (String) -> Unit,
) {
    var textFieldValue by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .padding(
                top = Theme.dimension.size_16dp
            )
            .fillMaxWidth()
    ) {
        Text(title, style = UiFont.poppinsP2Medium)
        Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
        OutlinedTextField(
            value = textFieldValue,
            placeholder = {
                Text(hint, style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500))
            },
            shape = RoundedCornerShape(Theme.dimension.size_4dp),
            onValueChange = {
                textFieldValue = it
                onTextChanged(it)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = keyboardType
            ),
            visualTransformation = visualTransformation
        )

        if (errorMsg.isNotNullOrEmpty()) {
            Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
            Text(
                errorMsg!!, style = UiFont.poppinsCaptionMedium.copy(
                    color = UiColor.primaryRed500
                )
            )
        }
    }
}

@Composable
private fun EnterPhoneNumberWidget(
    value: String,
    errorMessage: String? = "",
    onEnterNumber: (String) -> Unit,
    onContactClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(top = Theme.dimension.size_16dp)
    ) {
        Text(
            "Nomor HP",
            style = UiFont.poppinsP2Medium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(0.9f),
                value = value,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                textStyle = UiFont.poppinsP2Medium,
                onValueChange = onEnterNumber,
                isError = errorMessage.isNotNullOrEmpty(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = UiColor.neutral100,
                    cursorColor = UiColor.black,
                    unfocusedBorderColor = UiColor.neutral100
                ),
                maxLines = 1,
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
            GlideImage(R.drawable.ic_phone_book,
                modifier = Modifier
                    .padding(start = Theme.dimension.size_4dp)
                    .weight(0.15f)
                    .size(Theme.dimension.size_36dp)
                    .clickable {
                        onContactClick()
                    }
            )
        }
        errorMessage?.let { value ->
            Text(
                value,
                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.primaryRed500),
                modifier = Modifier.padding(top = Theme.dimension.size_4dp)
            )
        }
    }

    // todo: remove this later
//    fun openSearchContact() {
//        when (permissionState.status) {
//            is PermissionStatus.Denied -> {
//                permissionState.launchPermissionRequest()
//            }
//
//            is PermissionStatus.Granted -> {
//                if (contactList.isNotEmpty()) {
//                    showDialog = true
//                } else {
//                    coroutineScope.launch {
//                        withContext(Dispatchers.IO) {
//                            val cursor = context.contentResolver.query(
//                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                                arrayOf(
//                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//                                    ContactsContract.CommonDataKinds.Phone.NUMBER
//                                ),
//                                ContactsContract.Contacts.HAS_PHONE_NUMBER,
//                                null,
//                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
//                            )
//
//                            if (cursor != null && cursor.moveToFirst()) {
//                                do {
//                                    val name =
//                                        cursor.getString(
//                                            cursor.getColumnIndex(
//                                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
//                                            )
//                                        )
//                                    val phoneNumber =
//                                        cursor.getString(
//                                            cursor.getColumnIndex(
//                                                ContactsContract.CommonDataKinds.Phone.NUMBER
//                                            )
//                                        )
//                                    contactList.add(
//                                        ContactDetail(
//                                            name,
//                                            phoneNumber
//                                        )
//                                    )
//                                } while (cursor.moveToNext())
//                            }
//                            cursor?.close()
//                            if (contactList.isNotEmpty()) {
//                                showDialog = true
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}

fun String.removeNonPhone(): String {
    if (this.isBlank()) return ""
    return this.replace("+62", "")
        .replace("0", "")
        .replace("-", "")
        .replace("\\s".toRegex(), "")
}