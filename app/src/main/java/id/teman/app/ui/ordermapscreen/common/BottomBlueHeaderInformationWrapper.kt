package id.teman.app.ui.ordermapscreen.common

import id.teman.app.R as RAppModule
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.common.ResponsiveText
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BottomBlueHeaderInformationWrapper(
    blueHeaderInformationContent: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = Theme.dimension.size_6dp)
            .background(
                color = UiColor.tertiaryBlue500,
                shape = RoundedCornerShape(
                    topEnd = Theme.dimension.size_32dp,
                    topStart = Theme.dimension.size_32dp
                )
            )
            .padding(
                top = Theme.dimension.size_18dp,
                bottom = Theme.dimension.size_32dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        blueHeaderInformationContent()
    }
}

@Composable
fun RowScope.DriverVaccinationInformation() {
    Spacer(modifier = Modifier.width(Theme.dimension.size_26dp))
    GlideImage(
        imageModel = RAppModule.drawable.ic_safe,
        modifier = Modifier
            .size(Theme.dimension.size_24dp)
    )
    Spacer(
        modifier = Modifier.width(Theme.dimension.size_12dp),
    )
    Text(
        "Driver Kami Sudah Tervaksinasi",
        maxLines = 1,
        modifier = Modifier.weight(1f),
        style = UiFont.poppinsP2SemiBold.copy(color = Color.White)
    )
    Spacer(modifier = Modifier.width(Theme.dimension.size_26dp))
}

@Composable
fun DriverEstimatedArrivalInformation(isDriverEnrouteToCustomer: Boolean, duration: String) {
    Row(
        modifier = Modifier.padding(
            start = Theme.dimension.size_26dp,
            end = Theme.dimension.size_26dp
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        GlideImage(
            imageModel = RAppModule.drawable.ic_time_filled,
            modifier = Modifier
                .size(Theme.dimension.size_24dp)
        )
        Text(
            if (isDriverEnrouteToCustomer) "Rider akan sampai pada" else "Hati-hati dijalan ya",
            maxLines = 1,
            modifier = Modifier.padding(start = Theme.dimension.size_14dp),
            style = UiFont.poppinsP2SemiBold.copy(color = Color.White)
        )
        if (isDriverEnrouteToCustomer) {
            ResponsiveText(
                duration,
                textStyle = UiFont.poppinsP2SemiBold.copy(color = Color.White),
                modifier = Modifier
                    .padding(start = Theme.dimension.size_10dp)
                    .background(
                        shape = RoundedCornerShape(Theme.dimension.size_100dp),
                        color = UiColor.tertiaryBlue600
                    )
                    .padding(
                        vertical = Theme.dimension.size_2dp,
                        horizontal = Theme.dimension.size_8dp
                    )
            )
        }
    }
}