package id.teman.app.ui.ordermapscreen.initiate.send

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.common.ResponsiveText
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Composable
fun OrderSendChip(
    item: PackageUISpec,
    textModifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = UiColor.neutral50,
    contentColor: Color = UiColor.neutral900,
    textColor: Color = UiColor.neutral500,
    onClick: () -> Unit
) {
    Chip(
        modifier = Modifier.padding(end = Theme.dimension.size_4dp),
        onClick = onClick,
        border = BorderStroke(
            ChipDefaults.OutlinedBorderSize,
            borderColor
        ),
        colors = ChipDefaults.chipColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        ),
        content = {
            Row {
                if (item.icon != null) {
                    GlideImage(
                        imageModel = item.icon,
                        modifier = Modifier.size(Theme.dimension.size_16dp),
                        imageOptions = ImageOptions(
                            colorFilter = ColorFilter.tint(textColor)
                        )
                    )
                    Spacer(modifier = Modifier.padding(end = Theme.dimension.size_12dp))
                }
                ResponsiveText(
                    item.title,
                    textStyle = UiFont.poppinsP2SemiBold
                        .copy(
                            color = textColor, platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                    modifier = textModifier,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Composable
fun OrderSendChipInsurance(
    item: InsuranceUISpec,
    textModifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = UiColor.neutral50,
    contentColor: Color = UiColor.neutral900,
    textColor: Color = UiColor.neutral500,
    onClick: () -> Unit
) {
    Chip(
        modifier = Modifier.padding(end = Theme.dimension.size_4dp),
        onClick = onClick,
        border = BorderStroke(
            ChipDefaults.OutlinedBorderSize,
            borderColor
        ),
        colors = ChipDefaults.chipColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        ),
        content = {
            Row {
                ResponsiveText(
                    item.title,
                    textStyle = UiFont.poppinsP2SemiBold
                        .copy(
                            color = textColor, platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                    modifier = textModifier,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}