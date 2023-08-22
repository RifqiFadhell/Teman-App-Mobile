package id.teman.app.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.domain.model.home.QuickMenuFoodUIModel
import id.teman.app.domain.model.home.QuickMenuModel
import id.teman.app.domain.model.home.QuickMenuUIModel
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.app.ui.theme.buttons.TemanCircleButtonHome
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun QuickMenuItem(item: QuickMenuModel, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.noRippleClickable {
            onClick()
        }) {
        TemanCircleButtonHome(
            icon = item.icon,
            iconServer = item.iconServer,
            iconModifier = Modifier.size(Theme.dimension.size_64dp),
            circleModifier = Modifier.size(Theme.dimension.size_72dp)
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_10dp))
        Text(text = item.title, style = UiFont.poppinsP2Medium)
    }
}

@Composable
fun GameMenuItem(image: String, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = Theme.dimension.size_16dp, vertical = Theme.dimension.size_16dp)
            .background(
                shape = RoundedCornerShape(Theme.dimension.size_4dp),
                color = UiColor.neutral50
            )
            .noRippleClickable {
                onClick()
            },
        elevation = Theme.dimension.size_1dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlideImage(
                imageModel = image,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = Theme.dimension.size_100dp)
                    .padding(Theme.dimension.size_2dp)
                    .clip(RoundedCornerShape(Theme.dimension.size_8dp)),
                failure = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_no_image),
                        contentDescription = "failed"
                    )
                })
            Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
            Text(text = title, style = UiFont.poppinsCaptionMedium, maxLines = 1, modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp))
            Spacer(modifier = Modifier.height(Theme.dimension.size_10dp))
        }
    }
}