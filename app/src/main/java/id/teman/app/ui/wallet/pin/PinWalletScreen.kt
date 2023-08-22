package id.teman.app.ui.wallet.pin

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.common.ButtonType
import id.teman.app.common.CustomLoading
import id.teman.app.common.TopBar
import id.teman.app.common.isNotNullOrEmpty
import id.teman.app.domain.model.bill.convertErrorBill
import id.teman.app.domain.model.wallet.pin.ValueCaptionPinWalletScreen
import id.teman.app.domain.model.wallet.pin.getValuePinWalletScreen
import id.teman.app.ui.destinations.BillScreenDestination
import id.teman.app.ui.destinations.OtpScreenDestination
import id.teman.app.ui.destinations.PinWalletScreenDestination
import id.teman.app.ui.destinations.ProviderScreenDestination
import id.teman.app.ui.destinations.WalletScreenDestination
import id.teman.app.ui.onboarding.otp.PinView
import id.teman.app.ui.ordermapscreen.done.BillFinished
import id.teman.app.ui.ordermapscreen.done.OrderFinishedSpec
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.parcelize.Parcelize

@Composable
@Destination
fun PinWalletScreen(
    navigator: DestinationsNavigator,
    pinWalletViewModel: PinWalletViewModel = hiltViewModel(),
    pinScreenSpec: PinScreenSpec,
    token: String? = ""
) {
    val uiState = pinWalletViewModel.pinWalletUiState
    var isOrderCompleted by remember { mutableStateOf(false) }
    var serialNumber by remember { mutableStateOf("") }
    var statusTransaction by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var transactionId by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var button by remember { mutableStateOf("") }

    LaunchedEffect(key1 = uiState.successBuyBill, block = {
        uiState.successBuyBill?.consumeOnce {
            serialNumber = it.sn.orEmpty()
            statusTransaction = it.statusConverted.orEmpty()
            status = it.status.orEmpty()
            caption = it.caption.orEmpty()
            button = it.button.orEmpty()
            transactionId = it.id.orEmpty()
            isOrderCompleted = true
        }
    })
    LaunchedEffect(key1 = uiState.successUpdateBill, block = {
        uiState.successUpdateBill?.consumeOnce {
            serialNumber = it.serialNumber.orEmpty()
            status = it.status
            statusTransaction = it.description
            transactionId = it.id
            caption = it.caption.orEmpty()
            button = it.button.orEmpty()
        }
    })

    if (isOrderCompleted) {
        Box(modifier = Modifier.fillMaxSize()) {
            BillFinished(spec = OrderFinishedSpec(
                title = "Pembayaran ${pinScreenSpec.title} $statusTransaction",
                caption = caption,
                button = button,
                serialNumber = serialNumber,
                number = pinScreenSpec.number,
                icon = pinScreenSpec.icon,
                category = pinScreenSpec.title,
                price = pinScreenSpec.price
            ), alignment = Alignment.Center, onButtonClick = {
                if (status != "success") {
                    pinWalletViewModel.updateStatusBill(transactionId)
                } else {
                    navigator.navigate(BillScreenDestination) {
                        popUpTo(ProviderScreenDestination.route) {
                            inclusive = true
                        }
                    }
                }
            })
        }
    } else {
        RenderState(
            uiState = uiState,
            navigator = navigator,
            getValuePinWalletScreen(pinScreenSpec.useFor),
            pinWalletViewModel,
            pinScreenSpec,
            token
        )
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RenderState(
    uiState: PinWalletViewModel.PinWalletUiState,
    navigator: DestinationsNavigator,
    spec: ValueCaptionPinWalletScreen,
    pinWalletViewModel: PinWalletViewModel,
    pinScreenSpec: PinScreenSpec,
    token: String? = ""
) {
    val (pinValue, onPinValueChange) = remember { mutableStateOf("") }

    LaunchedEffect(key1 = uiState.successSetPin, block = {
        uiState.successSetPin?.consumeOnce {
            navigator.navigate(WalletScreenDestination) {
                popUpTo(PinWalletScreenDestination.route) {
                    inclusive = false
                }
            }
        }
    })

    LaunchedEffect(key1 = uiState.successRequestOtp, block = {
        uiState.successRequestOtp?.consumeOnce {
            navigator.navigate(OtpScreenDestination(phoneNumber = uiState.number, isFrom = "pin"))
        }
    })

    Scaffold(
        topBar = {
            TopBar(title = spec.titleBar) {
                navigator.popBackStack()
            }
        },
        content = {
            Column {
                Column {
                    Text(
                        text = spec.title, modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_48dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            )
                            .align(Alignment.CenterHorizontally), style = UiFont.cabinH2sBold
                    )
                    Text(
                        text = spec.subtitle,
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
                            },
                            digitCount = 6,
                            isHideText = false
                        )
                    }
                    if (uiState.error.isNotNullOrEmpty()) {
                        Text(
                            text = uiState.error?.convertErrorBill().orEmpty(),
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
                    Text(
                        text = spec.warning,
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_8dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            )
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                       pinWalletViewModel.requestOtpReset()
                            },
                        textAlign = TextAlign.Center,
                        style = UiFont.poppinsP2Medium.copy(color = if (spec.useFor != "update") UiColor.tertiaryBlue500 else UiColor.neutral600)
                    )
                }
                if (uiState.loading) {
                    Dialog(
                        onDismissRequest = { },
                        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
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
                    content = spec.buttonTitle,
                    buttonType = ButtonType.Large,
                    activeColor = UiColor.primaryRed500,
                    activeTextColor = Color.White,
                    isEnabled = pinValue.length == 6,
                    borderRadius = Theme.dimension.size_30dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Theme.dimension.size_16dp,
                            vertical = Theme.dimension.size_24dp
                        ),
                    onClicked = {
                        when (spec.useFor) {
                            "update" -> pinWalletViewModel.updatePinWallet(pinValue, token)
                            "bill" -> pinWalletViewModel.buyBillValidation(
                                pinScreenSpec.number,
                                pinScreenSpec.code,
                                pinValue,
                                pinScreenSpec.category
                            )
                            "pulsa" -> pinWalletViewModel.buyPulsaValidation(
                                pinScreenSpec.number,
                                pinScreenSpec.code,
                                pinValue
                            )
                        }
                    }
                )
            }
        })
}

@Parcelize
data class PinScreenSpec(
    val useFor: String = "update",
    val number: String = "",
    val category: String = "",
    val code: String = "",
    val title: String = "",
    val icon: String = "",
    val price: String = "",
) : Parcelable