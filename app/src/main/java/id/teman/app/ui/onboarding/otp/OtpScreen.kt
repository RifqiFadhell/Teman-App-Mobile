package id.teman.app.ui.onboarding.otp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.common.CustomLoading
import id.teman.app.data.dto.VerifyOtpRequest
import id.teman.app.domain.repository.user.UserRepository
import id.teman.app.domain.usecase.user.RequestOtpUseCase
import id.teman.app.domain.usecase.user.VerifyOtpUseCase
import id.teman.app.ui.destinations.HomeScreenDestination
import id.teman.app.ui.destinations.OtpScreenDestination
import id.teman.app.ui.destinations.PinWalletScreenDestination
import id.teman.app.ui.destinations.ProviderScreenDestination
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.app.ui.wallet.pin.PinScreenSpec
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@Destination
@Composable
fun OtpScreen(
    navigator: DestinationsNavigator,
    viewModel: OtpViewModel = hiltViewModel(),
    phoneNumber: String,
    isFrom: String = "Login"
) {
    val state by viewModel.state.collectAsState()
    RenderView(
        state = state,
        navigator = navigator,
        viewModel = viewModel,
        phoneNumber,
        isFrom = isFrom
    )
}

@Composable
private fun RenderView(
    state: OtpViewModel.OtpUiEvent,
    navigator: DestinationsNavigator,
    viewModel: OtpViewModel,
    phoneNumber: String,
    isFrom: String
) {
    var isError by remember { mutableStateOf(false) }
    var attempts by remember { mutableStateOf(0) }
    viewModel.countDown(30)
    RenderOtpPage(
        viewModel = viewModel,
        showError = isError,
        attempts = attempts,
        phoneNumber = phoneNumber,
        isFrom = isFrom
    )
    when (state) {
        is OtpViewModel.OtpUiEvent.Loading -> {
            if (state.isShow) {
                Dialog(
                    onDismissRequest = { },
                    DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                ) {
                    CustomLoading(modifier = Modifier.fillMaxSize())
                }
            }
        }
        is OtpViewModel.OtpUiEvent.SuccessVerifyOtp -> {
            navigator.navigate(HomeScreenDestination) {
                popUpTo(OtpScreenDestination.route) {
                    inclusive = false
                }
            }
        }
        is OtpViewModel.OtpUiEvent.SendOtpSuccess -> {
            attempts = state.count
            val count: Int = if (attempts < 1) {
                30
            } else if (attempts < 2) {
                60
            } else {
                360
            }
            viewModel.countDown(count)
        }
        is OtpViewModel.OtpUiEvent.FetchError -> {
            isError = true
        }
        is OtpViewModel.OtpUiEvent.SuccessVerifyOtpReset -> {
            navigator.navigate(
                PinWalletScreenDestination(
                    PinScreenSpec(useFor = "update"), token = state.token
                )
            ) {
                popUpTo(OtpScreenDestination.route) {
                    inclusive = false
                }
            }
        }
    }
}

@Composable
private fun RenderOtpPage(
    viewModel: OtpViewModel,
    showError: Boolean,
    attempts: Int,
    phoneNumber: String,
    isFrom: String,
) {
    val scaffoldState = rememberScaffoldState()
    val (pinValue, onPinValueChange) = remember {
        mutableStateOf("")
    }
    Scaffold(
        scaffoldState = scaffoldState,
        content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                GlideImage(
                    imageModel = R.drawable.teman_logo,
                    modifier = Modifier
                        .padding(top = Theme.dimension.size_16dp)
                        .size(150.dp)
                        .align(Alignment.CenterHorizontally),
                    imageOptions = ImageOptions(contentScale = ContentScale.Fit)
                )
                Text(
                    text = "Autentikasi OTP", modifier = Modifier
                        .padding(
                            top = Theme.dimension.size_16dp,
                            start = Theme.dimension.size_16dp,
                            end = Theme.dimension.size_16dp
                        )
                        .align(Alignment.CenterHorizontally), style = UiFont.cabinH2sBold
                )
                Text(
                    text = "Pesan dengan Kode Otp telah dikirimkan ke nomor telepon $phoneNumber",
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Theme.dimension.size_24dp,
                            end = Theme.dimension.size_24dp,
                            top = Theme.dimension.size_40dp
                        )
                ) {
                    PinView(
                        pinText = pinValue,
                        onPinTextChange = {
                            onPinValueChange(it)
                            if (it.length == 4) {
                                if (isFrom == "pin") viewModel.verifyOtpReset(it) else viewModel.verifyOtp(
                                    it
                                )
                            }
                        }
                    )
                }
                if (showError) {
                    Text(
                        text = "Kode OTP yang kamu masukkan Salah. Coba cek kembali Kode yang kami kirimkan.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = Theme.dimension.size_8dp,
                                start = Theme.dimension.size_16dp,
                                bottom = Theme.dimension.size_16dp
                            ),
                        style = UiFont.poppinsCaptionMedium,
                        color = UiColor.error500,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (viewModel.ticks > 0) {
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text(
                            text = "Permintaan ulang kode dalam: ",
                            modifier = Modifier
                                .padding(
                                    top = Theme.dimension.size_8dp,
                                    start = Theme.dimension.size_16dp,
                                    bottom = Theme.dimension.size_16dp
                                ),
                            style = UiFont.poppinsCaptionSmallMedium
                        )
                        Text(
                            text = "${viewModel.ticks}",
                            modifier = Modifier
                                .padding(
                                    top = Theme.dimension.size_8dp,
                                    end = Theme.dimension.size_16dp
                                ),
                            style = UiFont.poppinsCaptionSmallSemiBold,
                            color = Color.Blue
                        )
                    }
                } else {
                    TemanFilledButton(
                        content = "Minta Kode Sekali Lagi",
                        buttonType = ButtonType.Large,
                        activeColor = UiColor.primaryRed500,
                        activeTextColor = Color.White,
                        isEnabled = pinValue.length < 4 && attempts < 3,
                        borderRadius = Theme.dimension.size_30dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Theme.dimension.size_16dp,
                                vertical = Theme.dimension.size_24dp
                            ),
                        onClicked = {
                            if (isFrom == "pin") viewModel.requestOtpReset() else viewModel.requestOtp()
                        }
                    )
                }
            }
        })
}

@Composable
fun PinView(
    pinText: String,
    onPinTextChange: (String) -> Unit,
    digitColor: Color = MaterialTheme.colors.onBackground,
    digitCount: Int = 4,
    isHideText: Boolean? = false
) {
    val scope = rememberCoroutineScope()
    val (cursorSymbol, setCursorSymbol) = remember { mutableStateOf("") }

    BasicTextField(
        value = pinText,
        onValueChange = {
            if (it.length <= digitCount) {
                onPinTextChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = if (pinText.length == 4) ImeAction.Done else ImeAction.Next
        ),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(digitCount) { index ->
                    val isCursorVisible = pinText.length == index

                    LaunchedEffect(key1 = cursorSymbol, isCursorVisible) {
                        if (isCursorVisible) {
                            scope.launch {
                                delay(350)
                                setCursorSymbol(if (cursorSymbol.isEmpty()) "|" else "")
                            }
                        }
                    }

                    val text = if (index >= pinText.length) "" else pinText[index].toString()
                    Text(
                        text = if (isCursorVisible) cursorSymbol else text,
                        color = digitColor,
                        modifier = Modifier
                            .size(Theme.dimension.size_52dp)
                            .border(
                                width = 1.dp,
                                color = UiColor.neutral100,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(Theme.dimension.size_16dp),
                        style = UiFont.poppinsP3SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(Theme.dimension.size_2dp))
                }
            }
        })
}

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val requestOtpUseCase: RequestOtpUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    var state = MutableStateFlow<OtpUiEvent>(OtpUiEvent.Loading(false))
    var ticks by mutableStateOf(0)

    private fun emitState(state: OtpUiEvent) {
        viewModelScope.launch {
            this@OtpViewModel.state.emit(state)
        }
    }

    fun countDown(time: Int) {
        viewModelScope.launch {
            ticks = 0
            ticks = time
            while (true) {
                delay(1.seconds)
                ticks--
                if (ticks < 0) break
            }
        }
    }

    fun verifyOtp(otp: String) {
        emitState(OtpUiEvent.Loading(true))
        viewModelScope.launch {
            verifyOtpUseCase.execute(otp).catch {
                emitState(OtpUiEvent.FetchError(it.message))
                emitState(OtpUiEvent.Loading(false))
            }.collect {
                emitState(OtpUiEvent.Loading(false))
                emitState(OtpUiEvent.SuccessVerifyOtp)
            }
        }
    }

    fun verifyOtpReset(otp: String) {
        emitState(OtpUiEvent.Loading(true))
        viewModelScope.launch {
            userRepository.verifyOtpReset(VerifyOtpRequest(otp)).catch {
                emitState(OtpUiEvent.FetchError(it.message))
                emitState(OtpUiEvent.Loading(false))
            }.collect {
                emitState(OtpUiEvent.Loading(false))
                emitState(OtpUiEvent.SuccessVerifyOtpReset(it))
            }
        }
    }

    fun requestOtpReset() {
        emitState(OtpUiEvent.Loading(true))
        viewModelScope.launch {
            userRepository.sendOtpReset().catch {
                emitState(OtpUiEvent.FetchError(it.message))
                emitState(OtpUiEvent.Loading(false))
            }.collect {
                emitState(OtpUiEvent.Loading(false))
                emitState(OtpUiEvent.SendOtpSuccess(it))
            }
        }
    }

    fun requestOtp() {
        emitState(OtpUiEvent.Loading(true))
        viewModelScope.launch {
            requestOtpUseCase.execute().catch {
                emitState(OtpUiEvent.Loading(false))
                emitState(OtpUiEvent.FetchError(it.message))
            }.collect {
                emitState(OtpUiEvent.Loading(false))
                emitState(OtpUiEvent.SendOtpSuccess(it))
            }
        }
    }

    sealed class OtpUiEvent {
        data class Loading(val isShow: Boolean) : OtpUiEvent()
        data class FetchError(val message: String?) : OtpUiEvent()
        data class SendOtpSuccess(val count: Int) : OtpUiEvent()
        data class SuccessVerifyOtpReset(val token: String) : OtpUiEvent()
        object SuccessVerifyOtp : OtpUiEvent()
    }
}