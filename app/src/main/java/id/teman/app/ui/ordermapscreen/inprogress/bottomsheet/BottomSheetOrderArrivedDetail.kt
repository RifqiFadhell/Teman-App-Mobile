package id.teman.app.ui.ordermapscreen.inprogress.bottomsheet

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.teman.app.domain.model.order.OrderDetailSpec
import id.teman.app.domain.model.user.DriverMitraType
import id.teman.app.ui.ordermapscreen.done.OrderDetailRatingForm
import id.teman.app.ui.ordermapscreen.done.OrderDetailRestoRatingForm
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiFont

@Composable
fun BottomSheetOrderArrivedDetail(
    item: OrderDetailSpec,
    onRatingClick: (Int, String) -> Unit,
    onRatingRestoClick: (Int, String) -> Unit,
    enableButtonRatingDriver: Boolean = true,
    enableButtonRatingResto: Boolean = true,
) {
    LazyColumn(
        modifier = Modifier.padding(
            horizontal = Theme.dimension.size_16dp,
            vertical = Theme.dimension.size_32dp
        )
    ) {
        item {
            OrderDetailRatingForm(onRatingClick = onRatingClick, enableButtonRatingDriver)
            if (item.orderType == DriverMitraType.FOOD) {
                Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
                OrderDetailRestoRatingForm(onRatingClick = onRatingRestoClick, enableButtonRatingResto)
            }
        }
        item {
            Text(
                "Lihat detail pesanan",
                style = UiFont.poppinsP3SemiBold,
                modifier = Modifier.padding(
                    bottom = Theme.dimension.size_20dp,
                    top = Theme.dimension.size_32dp
                )
            )
        }
        item {
            ContentDetailOriginDestinationSectionItem(
                itemTitle = "Titik Penjemputan",
                itemHint = item.pickupAddress
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
            ContentDetailOriginDestinationSectionItem(
                itemTitle = "Titik Tujuan",
                itemHint = item.destinationAddress
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
        }
        item {
            ContentDetailPaymentSection(item.paymentBreakdown, item.paymentMethod, totalPrice = item.totalPrice) {

            }
        }

    }
}