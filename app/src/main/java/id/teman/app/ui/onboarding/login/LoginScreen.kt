package id.teman.app.ui.onboarding.login

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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.common.CustomLoading
import id.teman.app.common.TextInputFieldIcon
import id.teman.app.common.orZero
import id.teman.app.data.dto.LoginRequest
import id.teman.app.domain.usecase.user.LoginUseCase
import id.teman.app.domain.usecase.user.RequestOtpUseCase
import id.teman.app.preference.Preference
import id.teman.app.ui.destinations.OtpScreenDestination
import id.teman.app.ui.destinations.RegisterScreenDestination
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Destination
@Composable
fun LoginScreen(navigator: DestinationsNavigator, viewModel: LoginViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    RenderView(navigator = navigator, state = state, viewModel = viewModel)
}

@Composable
fun RenderView(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel,
    state: LoginViewModel.LoginUiEvent
) {
    var isShowError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    LoginView(navigator = navigator, viewModel = viewModel, isShowError, errorMessage)
    when (state) {
        is LoginViewModel.LoginUiEvent.Loading -> {
            if (state.isShow) {
                Dialog(
                    onDismissRequest = { },
                    DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                ) {
                    CustomLoading(modifier = Modifier.fillMaxSize())
                }
            }
        }
        is LoginViewModel.LoginUiEvent.SendOtpSuccess -> {
            navigator.navigate(OtpScreenDestination(state.phoneNumber))
        }
        is LoginViewModel.LoginUiEvent.FetchError -> {
            isShowError = true
            errorMessage = state.message.orEmpty()
        }
    }
}

@Composable
fun LoginView(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel,
    isShowError: Boolean?,
    errorMessage: String?
) {
    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberScrollState()
    var phone by remember { mutableStateOf("") }
    Scaffold(scaffoldState = scaffoldState,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                GlideImage(
                    imageModel = R.drawable.teman_logo,
                    modifier = Modifier
                        .padding(top = Theme.dimension.size_16dp)
                        .size(150.dp)
                        .align(Alignment.CenterHorizontally),
                    imageOptions = ImageOptions(contentScale = ContentScale.Fit)
                )
                Text(
                    text = "Selamat datang, Teman!", modifier = Modifier
                        .padding(
                            top = Theme.dimension.size_16dp,
                            start = Theme.dimension.size_16dp,
                            end = Theme.dimension.size_16dp
                        )
                        .align(Alignment.CenterHorizontally), style = UiFont.cabinH2sBold
                )
                Text(
                    text = "Welcome back, you’ve been missed!",
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
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Phone
                    ),
                    onChange = {
                        phone = it
                    },
                    modifier = Modifier.padding(
                        start = Theme.dimension.size_16dp,
                        end = Theme.dimension.size_16dp,
                        top = Theme.dimension.size_48dp
                    ),
                    placeholders = "812345678",
                    error = isShowError,
                    message = errorMessage
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TemanFilledButton(
                    content = "Masuk",
                    buttonType = ButtonType.Large,
                    activeColor = UiColor.primaryRed500,
                    activeTextColor = Color.White,
                    isEnabled = viewModel.validateNumber(phone),
                    borderRadius = Theme.dimension.size_30dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Theme.dimension.size_16dp),
                    onClicked = {
                        viewModel.login(phone)
                    }
                )
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        text = "Belum memiliki akun ? ",
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_8dp,
                                start = Theme.dimension.size_16dp,
                                bottom = Theme.dimension.size_16dp
                            ),
                        style = UiFont.poppinsP2Medium
                    )
                    Text(
                        text = "Daftar sekarang",
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_8dp,
                                end = Theme.dimension.size_16dp
                            )
                            .clickable {
                                navigator.navigate(RegisterScreenDestination())
                            },
                        style = UiFont.poppinsP2SemiBold,
                        color = Color.Blue
                    )
                }
            }
        })
}


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val requestOtpUseCase: RequestOtpUseCase,
    private val firebaseInstallations: FirebaseInstallations,
    private val preference: Preference
) : ViewModel() {

    var state = MutableStateFlow<LoginUiEvent>(LoginUiEvent.Loading(false))

    fun validateNumber(phoneNumber: String?): Boolean {
        return phoneNumber?.length.orZero() > 7
    }

    private fun emitState(state: LoginUiEvent) {
        viewModelScope.launch {
            this@LoginViewModel.state.emit(state)
        }
    }

    fun login(phoneNumber: String?) {
        emitState(LoginUiEvent.Loading(true))
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
                val allowedNumber = convertToAllowedNumber(phoneNumber.orEmpty())
                loginUseCase.execute(LoginRequest(allowedNumber, fcm_token = token))
                    .catch { exception ->
                        val message = if (exception.message.isNullOrBlank()) "Mohon Coba Lagi"
                        else exception.message
                        emitState(LoginUiEvent.Loading(false))
                        emitState(LoginUiEvent.FetchError(message))
                    }.collect { accessToken ->
                        preference.setBearerToken(accessToken)
                        requestOtp(allowedNumber)
                    }
            }
        })
    }

    private fun convertToAllowedNumber(phoneNumber: String): String {
        return if (phoneNumber.getOrNull(0).toString() == "0") {
            phoneNumber.replaceFirst("0", "62")
        } else if (phoneNumber.getOrNull(0).toString() == "6" && phoneNumber.getOrNull(1)
                .toString() == "2"
        ) {
            phoneNumber.replaceFirst("0", "62")
        } else {
            "62$phoneNumber"
        }
    }

    private fun requestOtp(phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            requestOtpUseCase.execute().catch {
                emitState(LoginUiEvent.Loading(false))
                emitState(LoginUiEvent.FetchError(it.message))
            }.collect {
                emitState(LoginUiEvent.SendOtpSuccess(phoneNumber))
            }
        }
    }

    sealed class LoginUiEvent {
        data class Loading(val isShow: Boolean) : LoginUiEvent()
        data class FetchError(val message: String?) : LoginUiEvent()
        data class SendOtpSuccess(val phoneNumber: String) : LoginUiEvent()
    }
}