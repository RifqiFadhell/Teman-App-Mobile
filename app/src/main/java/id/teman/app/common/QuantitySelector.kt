package id.teman.app.common

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.teman.app.R
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun QuantitySelector(
    count: Int,
    decreaseItemCount: () -> Unit,
    increaseItemCount: () -> Unit,
    modifier: Modifier = Modifier,
    isFromSummary: Boolean = false
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = "Jumlah",
                style = UiFont.poppinsP2Medium,
                color = UiColor.neutral900,
                modifier = Modifier
                    .padding(start = Theme.dimension.size_16dp)
                    .align(Alignment.CenterVertically)
            )
        }
        Row {
            if (isFromSummary) {
                Crossfade(
                    targetState = count,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "$it",
                        style = MaterialTheme.typography.subtitle2,
                        fontSize = 18.sp,
                        color = UiColor.neutral900,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(min = 24.dp)
                    )
                }
            } else {
                TemanCircleButton(
                    icon = R.drawable.ic_min_qty,
                    circleModifier = Modifier
                        .size(Theme.dimension.size_48dp)
                        .clickable {
                            decreaseItemCount()
                        },
                    iconModifier = Modifier.size(Theme.dimension.size_24dp),
                    circleBackgroundColor = Color.White
                )
                Crossfade(
                    targetState = count,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "$it",
                        style = MaterialTheme.typography.subtitle2,
                        fontSize = 18.sp,
                        color = UiColor.neutral900,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(min = 24.dp)
                    )
                }
                TemanCircleButton(
                    icon = R.drawable.ic_plus_qty,
                    circleModifier = Modifier
                        .size(Theme.dimension.size_48dp)
                        .clickable {
                            increaseItemCount()
                        },
                    iconModifier = Modifier.size(Theme.dimension.size_24dp),
                    circleBackgroundColor = Color.White
                )
            }
        }
    }
}