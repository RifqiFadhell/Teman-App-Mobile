package id.teman.app.ui.ordermapscreen.inprogress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import id.teman.app.domain.model.order.OrderDetailSpec
import id.teman.app.domain.model.order.TransportRequestType
import id.teman.app.ui.ordermapscreen.common.BottomBlueHeaderInformationWrapper
import id.teman.app.ui.ordermapscreen.common.DriverEstimatedArrivalInformation
import id.teman.app.ui.theme.Theme
import id.teman.app.utils.minutesToReadableText

@Composable
fun BoxScope.InProgressButtonContent(
    orderItem: OrderDetailSpec,
    modifier: Modifier = Modifier,
    onChatClicked: () -> Unit
) {
    Box(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(Theme.dimension.size_0dp)
    ) {
        BottomBlueHeaderInformationWrapper {
            DriverEstimatedArrivalInformation(
                orderItem.orderStatus == TransportRequestType.ACCEPTED,
                minutesToReadableText(orderItem.duration)
            )
        }
        Card(
            shape = RoundedCornerShape(
                topStart = Theme.dimension.size_32dp,
                topEnd = Theme.dimension.size_32dp
            ),
            modifier = Modifier
                .offset(
                    x = Theme.dimension.size_0dp,
                    y = Theme.dimension.size_56dp
                )
                .padding(bottom = Theme.dimension.size_56dp)
        ) {
            OrderPickupInformation(
                orderItem = orderItem,
                onChatClicked = onChatClicked
            )
        }
    }
}