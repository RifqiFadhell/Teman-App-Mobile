package id.teman.app.ui.ordermapscreen.initiate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.graphics.ColorFilter
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.common.ResponsiveText
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.domain.model.order.isTransportation
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

typealias note = String

@Composable
fun OrderInitiationForm(
    originDetailSpec: PlaceDetailSpec? = null,
    destinationDetailSpec: PlaceDetailSpec? = null,
    orderRequestType: OrderRequestType,
    isContinueButtonActive: Boolean,
    onOriginClick: (String) -> Unit,
    onDestinationClick: (String) -> Unit,
    onContinueClicked: (note) -> Unit
) {
    val title =
        if (orderRequestType.isTransportation()) "Mau kemana hari ini?" else "Mau kirim barang kemana?"
    val originTitle =
        if (orderRequestType.isTransportation()) "Mau dijemput dimana?" else "Titik Pengambilan Barang"
    val originHint =
        if (orderRequestType.isTransportation()) "Ketik Titik Jemput disini ya" else "Masukkan titik pengambilan"
    val destinationTitle =
        if (orderRequestType.isTransportation()) "Turun dimana nih?" else "Titik Tujuan Pengiriman"
    val destinationHint =
        if (orderRequestType.isTransportation()) "Ketik Tujuan disini ya..." else "Masukkan tujuan pengiriman"

    var valueText by remember { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(Theme.dimension.size_24dp)
    ) {
        Text(
            title,
            style = UiFont.poppinsH5Bold,
            modifier = Modifier.padding(bottom = Theme.dimension.size_20dp)
        )
        RowDestinationItem(
            itemTitle = originTitle,
            itemHint = originHint,
            value = originDetailSpec?.formattedAddress.orEmpty(),
            modifier = Modifier.clickable { onOriginClick(originTitle) }
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
        RowDestinationItem(
            itemTitle = destinationTitle,
            itemHint = destinationHint,
            value = destinationDetailSpec?.formattedAddress.orEmpty(),
            modifier = Modifier.clickable { onDestinationClick(destinationTitle) }
        )
        if (orderRequestType != OrderRequestType.SEND) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(Theme.dimension.size_4dp),
                        color = Color.Transparent
                    ),
                value = valueText,
                placeholder = {
                    Text(
                        "Catatan Untuk rider",
                        style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral400)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = UiColor.neutral100,
                    cursorColor = UiColor.black,
                    unfocusedBorderColor = UiColor.neutral100
                ),
                onValueChange = {
                    valueText = it
                },
            )
        }
        Spacer(modifier = Modifier.height(Theme.dimension.size_28dp))
        TemanFilledButton(
            content = "Lanjutkan",
            buttonType = ButtonType.Large,
            activeColor = UiColor.primaryRed500,
            activeTextColor = Color.White,
            isEnabled = isContinueButtonActive,
            borderRadius = Theme.dimension.size_30dp,
            modifier = Modifier.fillMaxWidth(),
            onClicked = {
                onContinueClicked(valueText)
            }
        )
    }

}

@Composable
fun RowDestinationItem(
    modifier: Modifier = Modifier,
    itemTitle: String,
    itemHint: String,
    value: String = ""
) {
    val valueTextStyle = if (value.isEmpty()) UiFont.poppinsH5SemiBold
        .copy(color = UiColor.neutral300) else UiFont.poppinsH5Medium
        .copy(color = UiColor.neutral900)
    val valueText = value.ifEmpty { itemHint }
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
    ) {
        GlideImage(
            imageModel = R.drawable.location,
            imageOptions = ImageOptions(),
            modifier = Modifier.size(Theme.dimension.size_16dp)
        )
        Spacer(Modifier.width(Theme.dimension.size_14dp))
        Column {
            Text(
                itemTitle,
                style = UiFont.poppinsP2SemiBold.copy(color = UiColor.neutral300)
            )
            Spacer(
                modifier = Modifier.height(Theme.dimension.size_10dp),
            )
            ResponsiveText(
                text = valueText,
                textStyle = valueTextStyle,
                maxLines = 3
            )
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