package id.teman.app.ui.ordermapscreen.inprogress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import id.teman.app.common.ButtonType
import id.teman.app.domain.model.order.OrderDetailSpec
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BoxScope.LoadingCard(order: OrderDetailSpec? = null, onCancel: (String) -> Unit) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .background(
                color = Color.White, shape = RoundedCornerShape(
                    topEnd = Theme.dimension.size_32dp,
                    topStart = Theme.dimension.size_32dp
                )
            )
    ) {
        Text(
            "Sedang Mencari Driver...", style = UiFont.poppinsH3sBold, modifier = Modifier.padding(
                top = Theme.dimension.size_32dp,
                bottom = Theme.dimension.size_20dp,
                start = Theme.dimension.size_24dp,
                end = Theme.dimension.size_24dp
            )
        )
        LinearProgressIndicator(
            color = UiColor.tertiaryBlue500,
            modifier = Modifier
                .fillMaxWidth()
        )
        if (order != null) {
            TemanFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = Theme.dimension.size_24dp,
                        horizontal = Theme.dimension.size_24dp
                    ),
                content = "Batalkan Order",
                buttonType = ButtonType.Medium,
                activeTextColor = Color.White,
                borderRadius = Theme.dimension.size_30dp,
                isEnabled = true,
                activeColor = UiColor.primaryRed500,
                onClicked = {
                    onCancel(order.requestId)
                }
            )
        }
    }
}