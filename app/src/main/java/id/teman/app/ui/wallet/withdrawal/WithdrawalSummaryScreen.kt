package id.teman.app.ui.wallet.withdrawal

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.common.convertToRupiah
import id.teman.app.domain.model.wallet.withdrawal.WalletDataTransferSpec
import id.teman.app.ui.destinations.WithdrawPinConfirmationScreenDestination
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun WithdrawalSummaryScreen(navigator: DestinationsNavigator, spec: WalletDataTransferSpec) {
    val pathEffect = remember {
        PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    }
    Scaffold(
        topBar = {
            CenteredTopNavigation(title = "Penarikan Kantong") {
                navigator.popBackStack()
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Theme.dimension.size_16dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = UiColor.neutralGray0,
                            shape = RoundedCornerShape(Theme.dimension.size_16dp)
                        )
                        .padding(
                            Theme.dimension.size_16dp
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bank", style = UiFont.poppinsP2Medium)
                        Text(spec.bankName, style = UiFont.poppinsP2SemiBold)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Nomor Rekening", style = UiFont.poppinsP2Medium)
                        Text(spec.accountNumber, style = UiFont.poppinsP2SemiBold)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pemilik Rekening", style = UiFont.poppinsP2Medium)
                        Text(spec.accountName, style = UiFont.poppinsP2SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(Theme.dimension.size_40dp))
                Text("Rincian Pesanan", style = UiFont.poppinsP2SemiBold)
                Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tarik Kantong", style = UiFont.poppinsP2Medium)
                    Text(spec.withdrawalAmount.convertToRupiah(), style = UiFont.poppinsP2Medium)
                }
                Canvas(
                    Modifier
                        .padding(vertical = Theme.dimension.size_20dp)
                        .fillMaxWidth()
                        .height(Theme.dimension.size_2dp)
                ) {
                    drawLine(
                        color = UiColor.neutral200,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = pathEffect
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total yang dibayarkan", style = UiFont.poppinsP2SemiBold)
                    Text(spec.withdrawalAmount.convertToRupiah(), style = UiFont.poppinsP2SemiBold)
                }
            }
        },
        bottomBar = {
            TemanFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        horizontal = Theme.dimension.size_16dp,
                        vertical = Theme.dimension.size_16dp
                    ),
                content = "Konfirmasi Penarikan",
                buttonType = ButtonType.Medium,
                activeColor = UiColor.primaryRed500,
                borderRadius = Theme.dimension.size_30dp,
                activeTextColor = UiColor.white
            ) {
                navigator.navigate(WithdrawPinConfirmationScreenDestination(spec))
            }
        }
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun CenteredTopNavigation(
    modifier: Modifier = Modifier,
    title: String,
    onBackButtonClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(Theme.dimension.size_16dp)
    ) {
        GlideImage(
            imageModel = R.drawable.ic_arrow_back,
            modifier = Modifier
                .size(Theme.dimension.size_24dp)
                .clickable {
                    onBackButtonClick()
                }
        )
        Text(
            title, style = UiFont.poppinsH5SemiBold.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ), modifier = Modifier.align(Alignment.Center)
        )
    }
}