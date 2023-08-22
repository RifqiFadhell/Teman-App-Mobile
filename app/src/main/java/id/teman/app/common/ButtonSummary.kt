package id.teman.app.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
fun ButtonContinue(totalPrice: Double, discount: Double, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Theme.dimension.size_16dp)
            .heightIn(min = Theme.dimension.size_52dp)
            .background(
                UiColor.primaryRed500,
                shape = RoundedCornerShape(Theme.dimension.size_32dp)
            )
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(
            modifier = Modifier
                .weight(0.7f)
                .padding(start = Theme.dimension.size_20dp)
        ) {
            Text(
                title,
                modifier = Modifier.padding(top = if (discount > 0.0) Theme.dimension.size_8dp else 0.dp),
                style = UiFont.poppinsCaptionMedium.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    ),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.LastLineBottom
                    )
                ),
                color = UiColor.white,
            )
            if (discount > 0.0) {
                Text("Kamu hemat ${discount.convertToRupiah()}",
                    modifier = Modifier.padding(
                        top = Theme.dimension.size_2dp,
                        bottom = Theme.dimension.size_8dp,
                    ),
                    style = UiFont.poppinsCaptionMedium.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    color = UiColor.white
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(0.3f)) {
            Text(totalPrice.convertToRupiah(),
                style = UiFont.poppinsH5SemiBold.copy(color = UiColor.white),
                modifier = Modifier
            )
            GlideImage(
                R.drawable.ic_next_button,
                modifier = Modifier
                    .weight(1f)
                    .width(Theme.dimension.size_6dp)
                    .height(Theme.dimension.size_18dp)
                    .size(Theme.dimension.size_18dp),
                imageOptions = ImageOptions(contentScale = ContentScale.Fit)
            )
        }
    }
}