package id.teman.app.ui.orderlist.domain.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import id.teman.app.R
import id.teman.coreui.typography.UiColor

enum class OrderListType(
    @StringRes val orderStatus: Int,
    val orderStatusColor: Color
) {
    OrderInProgress(R.string.order_status_in_progress, UiColor.blue),
    OrderArrived(R.string.order_status_arrived, UiColor.blue),
    OrderAccepted(R.string.order_status_accepted, UiColor.blue),
    OrderOnRoute(R.string.order_status_on_route, UiColor.blue),
    OrderRequested(R.string.order_status_requesting, UiColor.blue),
    OrderDelivered(R.string.order_status_delivered, UiColor.success500),
    OrderCancelled(R.string.order_status_cancelled, UiColor.error500),
    OrderRejected(R.string.order_status_rejected, UiColor.error500),
}