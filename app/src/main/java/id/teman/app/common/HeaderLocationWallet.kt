package id.teman.app.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun HeaderLocationWallet(balance: String, location: String, onLocationPickClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth(0.65f)) {
            Text("Lokasi Anda", style = UiFont.poppinsP1Medium.copy(color = UiColor.neutral900))
            Row(modifier = Modifier.clickable {
                onLocationPickClick()
            }) {
                Text(
                    location.ifEmpty { "Alamat tidak terdeteksi" },
                    style = UiFont.poppinsP2SemiBold.copy(color = UiColor.tertiaryBlue500),
                    maxLines = 1
                )
                GlideImage(
                    imageModel = R.drawable.ic_dropdown,
                    modifier = Modifier.size(Theme.dimension.size_16dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .background(
                    UiColor.tertiaryBlue50,
                    shape = RoundedCornerShape(Theme.dimension.size_30dp)
                )
                .padding(
                    vertical = Theme.dimension.size_8dp,
                    horizontal = Theme.dimension.size_12dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            GlideImage(
                R.drawable.ic_wallet,
                modifier = Modifier.size(Theme.dimension.size_16dp)
            )
            Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
            Text(balance, style = UiFont.poppinsCaptionSemiBold)
        }
    }
}