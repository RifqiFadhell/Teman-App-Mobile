package id.teman.app.ui.ordermapscreen.inprogress.bottomsheet

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.convertToRupiah
import id.teman.app.common.isNotNullOrEmpty
import id.teman.app.ui.onboarding.otp.PinView
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

typealias walletType = String
typealias pinCode = String

@Composable
fun BottomSheetPaymentMethod(
    totalPrice: Double,
    statusPin: Boolean,
    viewModel: BottomSheetPaymentViewModel = hiltViewModel(),
    onPaymentMethodClick: (walletType, pinCode) -> Unit
) {
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getWalletBalance()
    }
    val uiState = viewModel.uiState
    var walletPin by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }
    var selectedPaymentMethod by remember { mutableStateOf("cash") }
    val context = LocalContext.current

    Box {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = UiColor.primaryRed500
            )
        } else {
            Column(
                modifier = Modifier.padding(
                    horizontal = Theme.dimension.size_16dp,
                    vertical = Theme.dimension.size_32dp
                )
            ) {
                Text(
                    "Pilih Metode Pembayaran",
                    textAlign = TextAlign.Center,
                    style = UiFont.poppinsH5Bold
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
                PaymentMethodItem(
                    icon = R.drawable.ic_wallet,
                    title = "T-Kantong",
                    isSelected = selectedPaymentMethod == "wallet",
                    showLowBalance = totalPrice >= uiState.totalWalletBalance,
                    totalBalance = uiState.totalWalletBalance,
                    totalOrderPrice = totalPrice,
                    onClick = {
                        selectedPaymentMethod = "wallet"
                    }
                )
                AnimatedVisibility(visible = selectedPaymentMethod == "wallet") {
                    CustomTextField(
                        title = walletPin, hint = "Masukkan 6 digit Kantong pin Kamu",
                        modifier = Modifier.padding(
                            top = Theme.dimension.size_4dp,
                            bottom = Theme.dimension.size_12dp
                        ),
                        errorMsg = if (!isValid && walletPin.length < 6) "Silahkan input pin anda dan minimal 6 digit" else null,
                        keyboardType = KeyboardType.Number,
                        onTextChanged = {
                            if (it.isDigitsOnly()) {
                                walletPin = it
                            }
                        },
                        maxLines = 6
                    )
                }
                Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
                PaymentMethodItem(
                    isSelected = selectedPaymentMethod == "cash",
                    icon = R.drawable.ic_cash_payment,
                    title = "Tunai",
                    onClick = {
                        selectedPaymentMethod = "cash"
                    })

                TemanFilledButton(
                    content = "Ubah Pembayaran",
                    buttonType = ButtonType.Large,
                    activeColor = UiColor.primaryRed500,
                    activeTextColor = Color.White,
                    borderRadius = Theme.dimension.size_30dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Theme.dimension.size_16dp,
                            vertical = Theme.dimension.size_24dp
                        ),
                    onClicked = {
                        if (selectedPaymentMethod == "cash") {
                            onPaymentMethodClick("cash", "")
                        } else {
                            if (statusPin) {
                                if (walletPin.length >= 6) {
                                    onPaymentMethodClick("wallet", walletPin)
                                } else {
                                    isValid = false
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Kamu belum buat pin. Silahkan buat pin terlebih dahulu",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    @DrawableRes icon: Int,
    title: String,
    showLowBalance: Boolean = false,
    totalBalance: Double = 0.0,
    totalOrderPrice: Double = 0.0,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) UiColor.neutral50 else Color.White
            )
            .clickable {
                if (showLowBalance) {
                    Toast
                        .makeText(context, "Balance is low, please top up", Toast.LENGTH_SHORT)
                        .show()
                    return@clickable
                }
                onClick()
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Theme.dimension.size_44dp)
                .clip(CircleShape)
                .background(color = UiColor.white)
                .border(
                    border = BorderStroke(Theme.dimension.size_1dp, color = UiColor.neutral100),
                    shape = CircleShape
                )
        ) {
            GlideImage(
                imageModel = icon,
                modifier = Modifier
                    .size(Theme.dimension.size_24dp)
                    .align(Alignment.Center),
            )
        }
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Column {
            Row {
                Text(title, style = UiFont.poppinsP2SemiBold)
                if (isSelected) {
                    Spacer(modifier = Modifier.width(Theme.dimension.size_4dp))
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = "",
                        tint = UiColor.success500
                    )
                }
            }
            if (showLowBalance) {
                Text(
                    "Total: ${totalOrderPrice.convertToRupiah()}",
                    style = UiFont.poppinsCaptionSemiBold
                )
            }
        }
        if (showLowBalance) {
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Text(
                    totalBalance.convertToRupiah(),
                    style = UiFont.poppinsCaptionSmallSemiBold.copy(color = UiColor.primaryRed500)
                )
                Text(
                    "You have low balance",
                    style = UiFont.poppinsCaptionSmallSemiBold.copy(color = UiColor.primaryRed500)
                )
            }
        }
    }
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    title: String, hint: String,
    errorMsg: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextChanged: (String) -> Unit,
    maxLines: Int = Int.MAX_VALUE
) {
    Column(
        modifier = modifier
            .padding(
                top = Theme.dimension.size_4dp
            )
            .fillMaxWidth()
    ) {
        Text(
            hint,
            style = UiFont.poppinsP2Medium,
            modifier = Modifier.padding(vertical = Theme.dimension.size_4dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            PinView(
                pinText = title,
                onPinTextChange = {
                    onTextChanged(it)
                },
                digitCount = 6,
                isHideText = false
            )
        }

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