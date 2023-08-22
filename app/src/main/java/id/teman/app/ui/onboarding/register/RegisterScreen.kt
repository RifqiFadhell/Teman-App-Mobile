package id.teman.app.ui.onboarding.register

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.common.CustomLoading
import id.teman.app.common.TextInputField
import id.teman.app.common.TextInputFieldIcon
import id.teman.app.common.isNotNullOrEmpty
import id.teman.app.data.dto.RegisterRequest
import id.teman.app.domain.usecase.user.RegisterUseCase
import id.teman.app.preference.Preference
import id.teman.app.ui.destinations.OtpScreenDestination
import id.teman.app.ui.destinations.WebViewScreenDestination
import id.teman.app.ui.sharedviewmodel.MainViewModel
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Destination(
    route = "referral",
    deepLinks = [
        DeepLink(
            uriPattern = "https://www.temanofficial.co.id/referral?={code}"
        )
    ]
)
@Composable
fun RegisterScreen(
    navigator: DestinationsNavigator,
    viewModel: RegisterViewModel = hiltViewModel(),
    mainViewModel: MainViewModel,
    code: String = ""
) {
    val state by viewModel.state.collectAsState()
    RenderView(navigator = navigator, viewModel = viewModel, state = state, code = mainViewModel.locationUiState.successGetReferral.orEmpty())
}

@Composable
fun RenderView(
    navigator: DestinationsNavigator,
    viewModel: RegisterViewModel,
    state: RegisterViewModel.RegisterUiEvent,
    code: String = ""
) {
    var isShowError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val referral by remember { mutableStateOf(code) }
    RegisterView(navigator = navigator, viewModel = viewModel, isShowError, errorMessage, code = referral)
    when (state) {
        is RegisterViewModel.RegisterUiEvent.Loading -> {
            if (state.isShow) {
                Dialog(
                    onDismissRequest = { },
                    DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                ) {
                    CustomLoading(modifier = Modifier.fillMaxSize())
                }
            }
        }
        is RegisterViewModel.RegisterUiEvent.RegisterSuccess -> {
            navigator.navigate(OtpScreenDestination(state.phoneNumber))
        }
        is RegisterViewModel.RegisterUiEvent.FetchError -> {
            isShowError = true
            errorMessage = state.message.orEmpty()
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegisterView(
    navigator: DestinationsNavigator, viewModel: RegisterViewModel, isShowError: Boolean?,
    errorMessage: String?, code: String = ""
) {
    val scaffoldState = rememberScaffoldState()
    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var referral by remember { mutableStateOf(code) }
    val scrollState = rememberScrollState()
    Scaffold(scaffoldState = scaffoldState,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(bottom = Theme.dimension.size_375dp)
            ) {
                GlideImage(
                    imageModel = R.drawable.teman_logo,
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.CenterHorizontally),
                    imageOptions = ImageOptions(contentScale = ContentScale.Fit)
                )
                Text(
                    text = "Kenalan Dulu yuk!", modifier = Modifier
                        .padding(
                            start = Theme.dimension.size_16dp,
                            end = Theme.dimension.size_16dp
                        )
                        .align(Alignment.CenterHorizontally), style = UiFont.cabinH2sBold
                )
                Text(
                    text = "Masukkan nomor HP dan nama kamu biar Teman bantuin daftar ke aplikasi",
                    modifier = Modifier
                        .padding(
                            top = Theme.dimension.size_8dp,
                            start = Theme.dimension.size_16dp,
                            end = Theme.dimension.size_16dp
                        )
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    style = UiFont.poppinsP2Medium
                )
                TextInputFieldIcon(
                    title = "Nomor Telepon",
                    textBox = "+62",
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Phone
                    ),
                    onChange = {
                        phone = it
                    },
                    modifier = Modifier.padding(
                        start = Theme.dimension.size_16dp,
                        end = Theme.dimension.size_16dp,
                        top = Theme.dimension.size_30dp
                    ),
                    placeholders = "812345678",
                    error = isShowError,
                    message = "Nomor Sudah Terdaftar"
                )
                TextInputField(
                    title = "Nama",
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    onChange = {
                        name = it
                    },
                    modifier = Modifier.padding(
                        start = Theme.dimension.size_16dp,
                        end = Theme.dimension.size_16dp,
                        top = Theme.dimension.size_20dp
                    ),
                    placeholders = "Budi"
                )
                TextInputField(
                    title = "Referral *jika ada",
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    onChange = {
                        referral = it

                    },
                    modifier = Modifier.padding(
                        start = Theme.dimension.size_16dp,
                        end = Theme.dimension.size_16dp,
                        top = Theme.dimension.size_20dp
                    ),
                    placeholders = "TMN23",
                    text = referral,
                    isEnabled = code.isEmpty()
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TemanFilledButton(
                    content = "Daftar",
                    buttonType = ButtonType.Large,
                    activeColor = UiColor.primaryRed500,
                    activeTextColor = Color.White,
                    isEnabled = phone.isNotEmpty(),
                    borderRadius = Theme.dimension.size_30dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Theme.dimension.size_16dp),
                    onClicked = {
                        viewModel.register(name, phone, referral)
                    }
                )
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        text = "Dengan Mendaftar, kamu menyetujui ",
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_8dp,
                                start = Theme.dimension.size_16dp,
                                bottom = Theme.dimension.size_16dp
                            ),
                        style = UiFont.poppinsCaptionMedium
                    )
                    Text(
                        text = "Terms & Conditions",
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_8dp,
                                end = Theme.dimension.size_16dp
                            )
                            .clickable {
                                navigator.navigate(
                                    WebViewScreenDestination(
                                        "Terms & Conditions",
                                        "https://www.temanofficial.co.id/term-condition"
                                    )
                                )
                            },
                        style = UiFont.poppinsCaptionSemiBold,
                        color = Color.Blue
                    )
                }
            }
        })
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val firebaseInstallations: FirebaseInstallations,
    private val preference: Preference
) : ViewModel() {

    var state = MutableStateFlow<RegisterUiEvent>(RegisterUiEvent.Loading(false))

    private fun emitState(state: RegisterUiEvent) {
        viewModelScope.launch {
            this@RegisterViewModel.state.emit(state)
        }
    }

    fun register(name: String?, phoneNumber: String?, referral: String? = "") {
        emitState(RegisterUiEvent.Loading(true))
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            viewModelScope.launch {
                delay(1000)
                val deviceId = preference.getDeviceId.first()
                if (deviceId.isBlank()) {
                    firebaseInstallations.id.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            launch { preference.setDeviceId(task.result) }
                        }
                    }
                }
                val final = if (phoneNumber?.getOrNull(0).toString() == "0") {
                    phoneNumber?.replaceFirst("0", "62")
                } else if (phoneNumber?.getOrNull(0).toString() == "6" && phoneNumber?.getOrNull(1)
                        .toString() == "2"
                ) {
                    phoneNumber?.replaceFirst("0", "62")
                } else {
                    "62$phoneNumber"
                }

                registerUseCase.execute(RegisterRequest(final, name, fcm_token = token.orEmpty(), referral_code = referral))
                    .catch {
                        emitState(RegisterUiEvent.Loading(false))
                        emitState(RegisterUiEvent.FetchError(it.message))
                    }.collect {
                        emitState(RegisterUiEvent.Loading(false))
                        emitState(RegisterUiEvent.RegisterSuccess(final.orEmpty()))
                    }
            }
        })
    }

    sealed class RegisterUiEvent {
        data class Loading(val isShow: Boolean) : RegisterUiEvent()
        data class FetchError(val message: String?) : RegisterUiEvent()
        data class RegisterSuccess(val phoneNumber: String) : RegisterUiEvent()
    }
}