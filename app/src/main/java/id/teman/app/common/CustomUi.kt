package id.teman.app.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
fun CustomImageIcon(
    text: String,
    @DrawableRes icon: Int
) {
    Row {
        GlideImage(
            imageModel = icon,
            modifier = Modifier.size(Theme.dimension.size_14dp)
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_6dp))
        Text(
            text,
            style = UiFont.poppinsCaptionSemiBold.copy(
                color = Color.Black,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                ),
            )
        )
    }
}

@Composable
fun CustomDivider() {
    Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
    Divider(
        color = UiColor.neutral100,
        modifier = Modifier
            .height(Theme.dimension.size_36dp)
            .width(1.dp)
    )
    Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun TemanLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(Theme.dimension.size_38dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            imageModel = R.drawable.ic_top_icon,
            modifier = Modifier
                .size(Theme.dimension.size_40dp)
                .align(Alignment.CenterVertically),
            imageOptions = ImageOptions(contentScale = ContentScale.Fit)
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
        Text(
            "Teman",
            modifier = Modifier.align(Alignment.CenterVertically),
            style = UiFont.cabinH2Bold.copy(
                color = UiColor.primaryRed500,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            textAlign = TextAlign.Center
        )
    }
}