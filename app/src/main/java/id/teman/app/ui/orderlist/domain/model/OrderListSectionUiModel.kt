package id.teman.app.ui.orderlist.domain.model

import id.teman.app.R as RApp
import androidx.annotation.StringRes

sealed class OrderListSectionUiModel {
    data class Title(@StringRes val title: Int) : OrderListSectionUiModel()
    data class Item(
        val imageUrl: String,
        val title: String,
        val subtitle: String,
        val orderDate: String,
        val orderListType: OrderListType
    ) : OrderListSectionUiModel()
}

val fakeOrderList = listOf(
    OrderListSectionUiModel.Title(RApp.string.order_section_title_in_progress),
    OrderListSectionUiModel.Item(
        imageUrl = "",
        title = "T-Bike ke Lapangan Barepan, Cawas",
        subtitle = "Sedang berlangsung",
        orderDate = "",
        orderListType = OrderListType.OrderInProgress
    ),
    OrderListSectionUiModel.Title(RApp.string.order_section_title_delivered_recently),
    OrderListSectionUiModel.Item(
        imageUrl = "",
        title = "T-Food Bubur Ayam Special Bandung",
        subtitle = "17 September, 10:30 • 3 Items",
        orderDate = "",
        orderListType = OrderListType.OrderDelivered
    ),
    OrderListSectionUiModel.Item(
        imageUrl = "",
        title = "T-Bike ke Lapangan Barepan, Cawas",
        subtitle = "Pesanan Dibatalkan",
        orderDate = "",
        orderListType = OrderListType.OrderCancelled
    ),
    OrderListSectionUiModel.Item(
        imageUrl = "",
        title = "T-Bike ke Lapangan Barepan, Cawas",
        subtitle = "Sedang berlangsung",
        orderDate = "",
        orderListType = OrderListType.OrderInProgress
    ),
)