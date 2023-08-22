package id.teman.app.ui.theme.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.isNotNullOrEmpty
import id.teman.coreui.typography.UiColor

@Composable
fun TemanCircleButton(
    circleModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    iconServer: String? = "",
    iconColor: Color? = null,
    circleBackgroundColor: Color = UiColor.neutralGray0
) {
    Box(
        modifier = circleModifier
            .clip(CircleShape)
            .background(color = circleBackgroundColor)
    ) {
        GlideImage(
            imageModel = if (iconServer.isNotNullOrEmpty()) iconServer else icon,
            modifier = iconModifier
                .align(Alignment.Center),
            imageOptions = ImageOptions(
                colorFilter = if (iconColor != null ) ColorFilter.tint(color = iconColor) else null
            )
        )
    }
}

@Composable
fun TemanCircleButtonHome(
    circleModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    @DrawableRes icon: Int = R.drawable.ic_no_image,
    iconServer: String? = "",
    iconColor: Color? = null,
) {
    Box(
        modifier = circleModifier
            .clip(CircleShape)
    ) {
        GlideImage(
            imageModel = if (iconServer.isNotNullOrEmpty()) iconServer else icon,
            modifier = iconModifier
                .align(Alignment.Center),
            imageOptions = ImageOptions(
                colorFilter = if (iconColor != null) ColorFilter.tint(color = iconColor) else null
            ),
            failure = {
                painterResource(id = icon)
            }
        )
    }
}

@Composable
fun TemanCircleButtonString(
    circleModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    icon: String,
    iconColor: Color? = null,
    circleBackgroundColor: Color = UiColor.neutralGray0
) {
    Box(
        modifier = circleModifier
            .clip(CircleShape)
            .background(color = circleBackgroundColor)
    ) {
        GlideImage(
            imageModel = icon,
            modifier = iconModifier
                .align(Alignment.Center),
            imageOptions = ImageOptions(
                colorFilter = if (iconColor != null ) ColorFilter.tint(color = iconColor) else null
            )
        )
    }
}

@Composable
fun TemanCircleButtonCLicked(
    circleModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    iconColor: Color? = null,
    circleBackgroundColor: Color = UiColor.neutralGray0,
    onClick: () -> Unit
) {
    Box(
        modifier = circleModifier
            .clip(CircleShape)
            .background(color = circleBackgroundColor)
            .clickable { onClick() }
    ) {
        GlideImage(
            imageModel = icon,
            modifier = iconModifier
                .align(Alignment.Center),
            imageOptions = ImageOptions(
                colorFilter = if (iconColor != null ) ColorFilter.tint(color = iconColor) else null
            )
        )
    }
}