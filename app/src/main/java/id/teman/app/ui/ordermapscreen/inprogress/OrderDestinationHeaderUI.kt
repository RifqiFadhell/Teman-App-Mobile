package id.teman.app.ui.ordermapscreen.inprogress

import id.teman.app.R as RApp
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.domain.model.order.OrderDetailSpec
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BoxScope.OrderDestinationHeader(
    item: OrderDetailSpec
) {
    Card(
        shape = RoundedCornerShape(Theme.dimension.size_12dp),
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(
                vertical = Theme.dimension.size_16dp,
                horizontal = Theme.dimension.size_20dp
            )
    ) {
        Column(
            modifier = Modifier.padding(Theme.dimension.size_14dp)
        ) {
            RowItemLocation(item.pickupAddress)
            Divider(
                thickness = Theme.dimension.size_1dp,
                color = UiColor.neutral50,
                modifier = Modifier
                    .padding(
                        top = Theme.dimension.size_10dp,
                        bottom = Theme.dimension.size_10dp,
                        start = Theme.dimension.size_32dp
                    )
                    .fillMaxWidth()
                    .height(Theme.dimension.size_1dp)

            )
            RowItemLocation(item.destinationAddress)
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun RowItemLocation(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        GlideImage(
            imageModel = RApp.drawable.location,
            modifier = Modifier.size(Theme.dimension.size_16dp),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Fit
            )
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Text(
            text,
            style = UiFont.poppinsCaptionSemiBold.copy(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                ),
                color = UiColor.neutral900
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}