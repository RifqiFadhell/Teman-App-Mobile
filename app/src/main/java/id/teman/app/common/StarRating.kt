package id.teman.app.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.domain.model.RatingSpec
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
fun StarRatting(spec: RatingSpec) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(
            vertical = Theme.dimension.size_4dp,
            horizontal = Theme.dimension.size_6dp
        ).fillMaxWidth(1f)
    ) {
        GlideImage(
            imageModel = R.drawable.ic_star,
            modifier = Modifier.size(Theme.dimension.size_20dp)
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_4dp))
        Text(spec.rating,
            style = UiFont.poppinsCaptionMedium.copy(
                color = spec.color,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                ),
            )
        )
    }
}