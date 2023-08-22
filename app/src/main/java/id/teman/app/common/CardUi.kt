package id.teman.app.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButtonString
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun CardItemCommon(
    modifier: Modifier = Modifier,
    circleBackgroundColor: Color = UiColor.neutralGray0,
    icon: String,
    title: String,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = modifier.noRippleClickable {
                onClick()
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            TemanCircleButtonString(
                icon = icon,
                circleBackgroundColor = circleBackgroundColor,
                circleModifier = Modifier
                    .size(Theme.dimension.size_48dp),
                iconModifier = Modifier
                    .size(Theme.dimension.size_24dp)
            )
            Text(
                title,
                style = UiFont.poppinsP3SemiBold,
                modifier = Modifier
                    .weight(8f)
                    .padding(start = Theme.dimension.size_16dp)
            )

            GlideImage(
                imageModel = R.drawable.ic_right_arrow,
                modifier = Modifier
                    .weight(1f)
                    .width(Theme.dimension.size_6dp)
                    .height(Theme.dimension.size_12dp)
            )
        }
        Divider(
            color = UiColor.neutral100, thickness = 1.dp,
            modifier = Modifier.padding(
                start = Theme.dimension.size_64dp,
                top = Theme.dimension.size_16dp
            )
        )
    }
}
