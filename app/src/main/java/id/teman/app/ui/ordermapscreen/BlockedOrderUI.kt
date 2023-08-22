package id.teman.app.ui.ordermapscreen

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BoxScope.BlockedOrderUI(orderType: String) {
    Card(
        elevation = Theme.dimension.size_6dp,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp, topEnd = Theme.dimension.size_32dp
        ),
        backgroundColor = UiColor.white
    ) {
        Column(modifier = Modifier.padding(Theme.dimension.size_16dp)) {
            Text(
                "Terjadi Kesalahan", style = UiFont.poppinsH5Bold, textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        top = Theme.dimension.size_32dp, bottom = Theme.dimension.size_16dp,
                        start = Theme.dimension.size_16dp, end = Theme.dimension.size_16dp
                    )
            )
            GlideImage(
                imageModel = R.drawable.ic_empty_driver,
                modifier = Modifier.aspectRatio(2 / 1f),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Fit
                )
            )
            Text(
                "Saat ini kamu sudah memesan order T-$orderType. Silahkan menyelesaikan order sebelumnya dahulu",
                style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500),
                modifier = Modifier.padding(
                    top = Theme.dimension.size_20dp, bottom = Theme.dimension.size_32dp,
                    start = Theme.dimension.size_16dp, end = Theme.dimension.size_16dp
                )
            )
        }
    }
}