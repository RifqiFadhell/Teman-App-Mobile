package id.teman.app.ui.search.common

import id.teman.app.R as rAppModule
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.domain.model.search.SearchUiModel
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
fun SearchFoodSectionItem(item: SearchUiModel.SectionFood, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .padding(
                top = Theme.dimension.size_24dp,
                start = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp
            ).clickable {
                onClick(item.restaurantId)
            }
    ) {
        GlideImage(
            imageModel = rAppModule.drawable.ic_no_image,
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.dimension.size_16dp))
                .size(Theme.dimension.size_72dp)
                .aspectRatio(1f)
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Column {
            Text(
                item.storeName,
                style = UiFont.poppinsP3SemiBold.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
            Text(
                item.storeDescription,
                style = UiFont.poppinsCaptionSemiBold
                    .copy(
                        color = UiColor.neutral400,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
            Row {
                CustomImageIcon(
                    text = item.storeRating,
                    icon = rAppModule.drawable.ic_star
                )
                CustomDivider()
                Text(
                    item.storeDistance,
                    style = UiFont.poppinsCaptionSemiBold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
                CustomDivider()
                CustomImageIcon(
                    text = item.deliveryTime,
                    icon = rAppModule.drawable.ic_teman_bike
                )
            }
            Spacer(modifier = Modifier.height(Theme.dimension.size_18dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = UiColor.neutral100)
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun CustomImageIcon(
    text: String,
    @DrawableRes icon: Int
) {
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

@Composable
private fun CustomDivider() {
    Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
    Divider(
        color = UiColor.neutral100,
        modifier = Modifier
            .height(16.dp)
            .width(1.dp)
    )
    Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
}