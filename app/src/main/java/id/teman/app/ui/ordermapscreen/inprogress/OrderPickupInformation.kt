package id.teman.app.ui.ordermapscreen.inprogress

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.common.convertToRupiah
import id.teman.app.common.noRippleClickable
import id.teman.app.dialog.GeneralActionButton
import id.teman.app.dialog.GeneralDialogPrompt
import id.teman.app.domain.model.order.BreakdownType
import id.teman.app.domain.model.order.OrderDetailSpec
import id.teman.app.domain.model.order.OrderPaymentSpec
import id.teman.app.domain.model.user.DriverMitraType
import id.teman.app.ui.ordermapscreen.inprogress.bottomsheet.PaymentItemRow
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import id.teman.app.R as RAppModule

@Composable
fun OrderPickupInformation(
    orderItem: OrderDetailSpec,
    onChatClicked: () -> Unit,
) {
    val context = LocalContext.current
    var isShowCallDriverDialog by remember { mutableStateOf(false) }

    if (isShowCallDriverDialog) {
        GeneralDialogPrompt(
            title = "Telepon Rider?",
            subtitle = "Kamu akan terkena tarif pulsa sesuai dengan operator yang digunakan",
            actionButtons = {
                GeneralActionButton(
                    text = "Batal",
                    textColor = UiColor.primaryRed500,
                    isFirstAction = true
                ) {
                    isShowCallDriverDialog = false
                }
                GeneralActionButton(
                    text = "Telepon",
                    textColor = UiColor.tertiaryBlue500,
                    isFirstAction = false
                ) {
                    isShowCallDriverDialog = false

                    val uri = Uri.parse("tel:" + orderItem.driverPhoneNumber)
                    val intent = Intent(Intent.ACTION_DIAL, uri)
                    context.startActivity(intent)
                }
            },
            onDismissRequest = { isShowCallDriverDialog = false }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimension.size_16dp)
    ) {
        DriverInformation(orderItem)
        DriverInteractionOptionsUI(
            onCallDriverClicked = { isShowCallDriverDialog = true },
            onChatClick = onChatClicked
        )
        OrderDetailInformation(orderItem)
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun OrderDetailInformation(item: OrderDetailSpec) {
    var expandableState by rememberSaveable { mutableStateOf(false) }
    val rotateState by animateFloatAsState(targetValue = if (expandableState) 180f else 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Theme.dimension.size_26dp,
                    bottom = Theme.dimension.size_32dp
                )
                .noRippleClickable {
                    expandableState = !expandableState
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Lihat detail pesanan", style = UiFont.poppinsP3SemiBold)
            Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
            GlideImage(
                imageModel = RAppModule.drawable.ic_arrow_down,
                modifier = Modifier
                    .size(Theme.dimension.size_18dp)
                    .rotate(rotateState)
            )
        }
        AnimatedVisibility(
            visible = expandableState,
            enter = fadeIn() + expandVertically(
                animationSpec = tween(durationMillis = 700, easing = LinearOutSlowInEasing)
            ),
            exit = fadeOut() + shrinkVertically(
                animationSpec = tween(
                    durationMillis = 700, easing = LinearOutSlowInEasing
                )
            )
        ) {
            Column {
                Text(
                    "Rincian Pesanan",
                    style = UiFont.poppinsH5Bold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
                Spacer(Modifier.height(Theme.dimension.size_20dp))
                if (item.orderType == DriverMitraType.FOOD) {
                    item.listMenu.map {
                        PaymentItemRow(
                            item = OrderPaymentSpec(
                                it.name,
                                it.price,
                                BreakdownType.FOOD,
                                quantity = it.qty.toString(),
                                notes = it.notes
                            )
                        )
                    }
                }
                Spacer(Modifier.height(Theme.dimension.size_20dp))
                item.paymentBreakdown.map {
                    PaymentItemRow(item = it)
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Theme.dimension.size_8dp)
                ) {
                    Text(
                        "Total yang dibayarkan",
                        style = UiFont.poppinsP3SemiBold.copy(color = UiColor.neutral900)
                    )
                    Text(
                        item.totalPrice.convertToRupiah(),
                        style = UiFont.poppinsP3SemiBold.copy(
                            color = UiColor.neutral900
                        )
                    )
                }
            }
        }

    }
}

@Composable
fun DriverInteractionOptionsUI(onChatClick: () -> Unit, onCallDriverClicked: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Theme.dimension.size_32dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(end = Theme.dimension.size_16dp)
                .weight(1f)
                .border(
                    shape = RoundedCornerShape(Theme.dimension.size_100dp),
                    border = BorderStroke(Theme.dimension.size_1dp, color = UiColor.neutral100)
                )
                .padding(
                    horizontal = Theme.dimension.size_16dp,
                    vertical = Theme.dimension.size_12dp
                )
                .noRippleClickable {
                    onChatClick()
                }
        ) {
            Text(
                "Kirim Pesan ke Rider...",
                style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500)
            )
            GlideImage(
                imageModel = RAppModule.drawable.ic_arrow_right,
                modifier = Modifier.size(Theme.dimension.size_20dp)
            )
        }
        TemanCircleButton(
            icon = id.teman.app.R.drawable.ic_call_filled,
            iconModifier = Modifier
                .width(Theme.dimension.size_18dp)
                .height(Theme.dimension.size_22dp),
            circleBackgroundColor = UiColor.tertiaryBlue500,
            circleModifier = Modifier
                .size(Theme.dimension.size_48dp)
                .clickable {
                    onCallDriverClicked()
                }
        )
    }
}

@Composable
fun DriverInformation(item: OrderDetailSpec) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Theme.dimension.size_32dp),
        verticalAlignment = Alignment.Top
    ) {
        GlideImage(
            imageModel = item.driverPhoto,
            modifier = Modifier
                .clip(CircleShape)
                .size(Theme.dimension.size_44dp),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Fit
            )
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Theme.dimension.size_18dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    item.driverName,
                    style = UiFont.poppinsP2SemiBold.copy(color = UiColor.neutral900)
                )
                if (item.isDriverVaccine) {
                    Spacer(modifier = Modifier.width(Theme.dimension.size_10dp))
                    GlideImage(
                        imageModel = RAppModule.drawable.ic_verified_filled,
                        modifier = Modifier
                            .padding(top = Theme.dimension.size_4dp)
                            .size(Theme.dimension.size_14dp),
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Fit,
                            alignment = Alignment.BottomCenter,
                            colorFilter = ColorFilter.tint(color = UiColor.tertiaryBlue500)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
            Text(
                buildDriverVehicleDescription(
                    item.driverVehicleLicenseNumber,
                    "${item.driverVehicleBrand} ${item.driverVehicleType}"
                ),
                style = UiFont.poppinsP2Medium.copy(
                    color = UiColor.neutral500
                )
            )
        }
        if (item.driverRating > 0.0) {
            Row {
                GlideImage(
                    imageModel = RAppModule.drawable.ic_star,
                    modifier = Modifier
                        .padding(top = Theme.dimension.size_4dp)
                        .size(Theme.dimension.size_18dp),
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(color = UiColor.tertiaryBlue500)
                    )
                )
                Text(
                    String.format("%.1f", item.driverRating), style = UiFont.poppinsP3SemiBold,
                    modifier = Modifier.padding(start = Theme.dimension.size_4dp)
                )
            }
        }
    }
}

fun buildDriverVehicleDescription(licensePlate: String, motorType: String): String {
    return when {
        licensePlate.isNotBlank() && motorType.isNotBlank() -> {
            "$licensePlate • $motorType"
        }

        licensePlate.isNotBlank() && motorType.isBlank() -> licensePlate
        licensePlate.isBlank() && motorType.isNotBlank() -> motorType
        else -> "No Driver Information"
    }
}