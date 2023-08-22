package id.teman.app.ui.ordermapscreen.inprogress.bottomsheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.common.convertToRupiah
import id.teman.app.common.isNotNullOrEmpty
import id.teman.app.common.noRippleClickable
import id.teman.app.common.orZero
import id.teman.app.domain.model.order.BreakdownType
import id.teman.app.domain.model.order.OrderEstimationResponseSpec
import id.teman.app.domain.model.order.OrderPaymentSpec
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.domain.model.order.convertTitle
import id.teman.app.ui.myaccount.CardItem
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BottomSheetOrderEstimationDetail(
    spec: OrderEstimationResponseSpec,
    onChangePaymentMethod: (() -> Unit)? = null,
    onContinue: () -> Unit,
    type: OrderRequestType,
    titlePromo: String = "Pilih Promo",
    onPromoSelect: () -> Unit
) {
    val origin = if (spec.packageType != null) "Titik Pengambilan Barang" else "Titik Penjemputan"
    val destination = if (spec.packageType != null) "Titik Tujuan Pengiriman" else "Titik Tujuan"
    LazyColumn(
        modifier = Modifier.padding(
            horizontal = Theme.dimension.size_16dp,
            vertical = Theme.dimension.size_32dp
        )
    ) {
        if (spec.packageType != null) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Layanan Pengiriman",
                        style = UiFont.poppinsP3SemiBold,
                        modifier = Modifier.padding(
                            bottom = Theme.dimension.size_20dp,
                            top = Theme.dimension.size_0dp
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlideImage(
                            imageModel = R.drawable.ic_send_motor,
                            modifier = Modifier.size(Theme.dimension.size_44dp),
                            imageOptions = ImageOptions(
                                contentScale = ContentScale.Fit
                            )
                        )
                        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
                        Text(type.convertTitle(), style = UiFont.poppinsP2SemiBold)
                    }
                    Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
                }
            }
        }
        item {
            Text(
                "Detail pesanan",
                style = UiFont.poppinsP3SemiBold,
                modifier = Modifier.padding(
                    bottom = Theme.dimension.size_12dp,
                    top = Theme.dimension.size_0dp
                )
            )
            if (spec.packageType.isNotNullOrEmpty()) {
                Text(
                    "Jenis Barang",
                    style = UiFont.poppinsP2SemiBold,
                    modifier = Modifier.padding(
                        bottom = Theme.dimension.size_8dp,
                        top = Theme.dimension.size_0dp
                    )
                )
                Row {
                    InfoDetailSend(icon = R.drawable.box_fix, title = spec.packageType.orEmpty())
                }
            }
        }
        if (spec.packageType.isNotNullOrEmpty()) {
            item {
                Column {
                    Text(
                        "Berat Total",
                        style = UiFont.poppinsP2SemiBold,
                        modifier = Modifier.padding(
                            bottom = Theme.dimension.size_8dp,
                            top = Theme.dimension.size_8dp
                        )
                    )
                    Text(
                        "Maks. ${spec.packageWeight}Kg",
                        style = UiFont.poppinsCaptionMedium,
                        modifier = Modifier.padding(
                            bottom = Theme.dimension.size_4dp,
                            top = Theme.dimension.size_0dp
                        )
                    )
                }
                Divider(
                    color = UiColor.neutral50,
                    modifier = Modifier
                        .padding(top = Theme.dimension.size_8dp, bottom = Theme.dimension.size_16dp)
                        .height(Theme.dimension.size_1dp)
                        .fillMaxWidth()
                )
            }
        }
        item {
            ContentDetailOriginDestinationSectionItem(
                itemTitle = origin,
                itemHint = spec.originAddress,
                notes = spec.notes
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
            ContentDetailOriginDestinationSectionItem(
                itemTitle = destination,
                itemHint = spec.destinationAddress
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
        }
        item {
            ContentDetailPaymentSection(
                spec.paymentBreakdown,
                paymentMethod = spec.paymentMethod,
                totalPrice = spec.totalPrice,
                onContinue = onContinue,
                onChangePaymentMethod = onChangePaymentMethod,
                isShowPromo = true, titlePromo = titlePromo) {
                onPromoSelect()
            }
        }

    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ContentDetailOriginDestinationSectionItem(
    itemTitle: String,
    itemHint: String,
    notes: String = ""
) {
    Row(verticalAlignment = Alignment.Top) {
        Column {
            Spacer(modifier = Modifier.height(Theme.dimension.size_2dp))
            GlideImage(
                imageModel = R.drawable.location,
                modifier = Modifier.size(Theme.dimension.size_16dp),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Fit
                )
            )
        }
        Spacer(Modifier.width(Theme.dimension.size_14dp))
        Column {
            Text(
                itemTitle,
                style = UiFont.poppinsP2SemiBold.copy(
                    color = UiColor.neutral900,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
            Spacer(
                modifier = Modifier.height(Theme.dimension.size_10dp),
            )
            Text(
                itemHint,
                style = UiFont.poppinsP2Medium
                    .copy(
                        color = UiColor.neutral500,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                maxLines = 1
            )
            if (notes.isNotEmpty()) {
                Spacer(
                    modifier = Modifier.height(Theme.dimension.size_10dp),
                )
                Text(
                    "Catatan untuk Driver:",
                    style = UiFont.poppinsCaptionSemiBold
                        .copy(
                            color = UiColor.neutral900,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                    maxLines = 1
                )
                Text(
                    notes,
                    style = UiFont.poppinsCaptionMedium
                        .copy(
                            color = UiColor.neutral500,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                    maxLines = 2
                )
            }
            Divider(
                color = UiColor.neutral50,
                modifier = Modifier
                    .padding(top = Theme.dimension.size_20dp)
                    .height(Theme.dimension.size_1dp)
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ContentDetailPaymentSection(
    paymentSpec: List<OrderPaymentSpec>,
    paymentMethod: String,
    totalPrice: Double? = null,
    isShowPromo: Boolean = false,
    titlePromo: String = "Pilih Promo",
    onChangePaymentMethod: (() -> Unit)? = null,
    onContinue: (() -> Unit)? = null,
    onPromoSelect: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text("Metode Pembayaran", style = UiFont.poppinsP2SemiBold)
        if (onContinue != null) {
            Text(
                "Ubah",
                style = UiFont.poppinsP2Medium.copy(color = UiColor.tertiaryBlue500),
                modifier = Modifier.noRippleClickable {
                    onChangePaymentMethod?.invoke()
                })
        }
    }
    Spacer(Modifier.height(Theme.dimension.size_20dp))
    Row(
        verticalAlignment = Alignment.Top
    ) {
        TemanCircleButton(
            icon = if (paymentMethod == "cash") R.drawable.ic_cash_payment else R.drawable.ic_wallet,
            circleBackgroundColor = Color.Transparent,
            circleModifier = Modifier
                .size(Theme.dimension.size_48dp)
                .border(BorderStroke(1.dp, color = UiColor.neutral100), shape = CircleShape),
            iconModifier = Modifier
                .size(Theme.dimension.size_24dp)
        )
        Spacer(Modifier.width(Theme.dimension.size_16dp))
        Column {
            Text(
                paymentMethod.replaceFirstChar { it.uppercase() },
                style = UiFont.poppinsP2SemiBold.copy(color = UiColor.neutral900),
                maxLines = 1,
                modifier = Modifier
                    .padding(start = Theme.dimension.size_16dp)
            )
            Text(
                if (paymentMethod == "cash") "Bayar di tempat tujuan" else "Bayar secara instan pakai T-Wallet",
                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
                maxLines = 1,
                modifier = Modifier
                    .padding(start = Theme.dimension.size_16dp, bottom = Theme.dimension.size_12dp),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    if (isShowPromo) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Pilih Promo Tersedia", style = UiFont.poppinsP2SemiBold)
        }
        CardItem(
            modifier = Modifier
                .padding(top = Theme.dimension.size_16dp),
            title = titlePromo.ifEmpty { "Pilih Promo" },
            icon = R.drawable.ic_promo
        ) {
            onPromoSelect()
        }
    }
    Spacer(Modifier.height(Theme.dimension.size_30dp))
    Text(
        "Rincian Pesanan", style = UiFont.poppinsH5Bold.copy(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        )
    )
    Spacer(Modifier.height(Theme.dimension.size_20dp))
    paymentSpec.map { item -> PaymentItemRow(item) }
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text("Total Biaya", style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral900))
        Text(
            totalPrice?.convertToRupiah().orEmpty(),
            style = UiFont.poppinsP2Medium.copy(
                color = UiColor.neutral900
            )
        )
    }
    Spacer(Modifier.height(Theme.dimension.size_12dp))
    onContinue?.let {
        Spacer(Modifier.height(Theme.dimension.size_26dp))
        TemanFilledButton(
            content = "Lanjutkan",
            buttonType = ButtonType.Large,
            activeColor = UiColor.primaryRed500,
            activeTextColor = Color.White,
            isEnabled = true,
            borderRadius = Theme.dimension.size_30dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            onContinue.invoke()
        }
    } ?: run {
        Spacer(Modifier.height(Theme.dimension.size_28dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Total yang dibayarkan", style = UiFont.poppinsP2SemiBold)
            Text(totalPrice?.convertToRupiah() ?: paymentSpec.filter { it.breakdownType != BreakdownType.DISCOUNT }.sumOf { it.price }.convertToRupiah(),
                style = UiFont.poppinsP2SemiBold
            )
        }
        Spacer(Modifier.height(Theme.dimension.size_12dp))
    }
}

@Composable
fun PaymentItemRow(item: OrderPaymentSpec) {
    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            if (item.quantity.isNotEmpty()) {
                Text("${item.name} - ${item.quantity} Pcs", style = UiFont.poppinsP2Medium)
            } else {
                Text(item.name, style = UiFont.poppinsP2Medium)
            }
            Text(
                item.price.convertToRupiah(),
                style = UiFont.poppinsP2Medium.copy(
                    color = if (item.breakdownType == BreakdownType.DISCOUNT)
                        UiColor.success600 else UiColor.neutral900
                )
            )
        }
        if (item.notes.isNotEmpty()) {
            Text("Catatan : ${item.notes}", style = UiFont.poppinsP2Medium)
        }
    }
    Spacer(Modifier.height(Theme.dimension.size_12dp))
}

@Composable
fun RowScope.InfoDetailSend(icon: Int, title: String) {
    GlideImage(
        imageModel = icon,
        modifier = Modifier
            .size(Theme.dimension.size_24dp)
    )
    Spacer(
        modifier = Modifier.width(Theme.dimension.size_12dp),
    )
    Text(
        title,
        maxLines = 1,
        modifier = Modifier.weight(1f),
        style = UiFont.poppinsP2SemiBold.copy(color = UiColor.neutral500)
    )
    Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
}
