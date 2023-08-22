package id.teman.app.ui.orderlist.domain.model

sealed class OrderHistorySectionUiModel {
    data class Title(val text: String) : OrderHistorySectionUiModel()
    data class Item(
        val imageUrl: String,
        val title: String,
        val subtitle: String,
        val orderDate: String,
        val orderListType: OrderListType,
        val totalOrderPayment: String,
        val showOrderButton: Boolean,
        val showRatingButton: Boolean
    ) : OrderHistorySectionUiModel()
}

val fakeOrderHistoryList = listOf(
    OrderHistorySectionUiModel.Title("17 September 2022"),
    OrderHistorySectionUiModel.Item(
        imageUrl = "",
        title = "T-Bike ke Lapangan Barepan, Cawas",
        subtitle = "Sedang berlangsung",
        orderDate = "",
        orderListType = OrderListType.OrderDelivered,
        totalOrderPayment = "Rp103.000",
        showOrderButton = true,
        showRatingButton = true
    ),
    OrderHistorySectionUiModel.Item(
        imageUrl = "",
        title = "T-Bike ke Lapangan Barepan, Cawas",
        subtitle = "Sedang berlangsung",
        orderDate = "",
        orderListType = OrderListType.OrderDelivered,
        totalOrderPayment = "Rp103.000",
        showOrderButton = true,
        showRatingButton = true
    ),
    OrderHistorySectionUiModel.Title("15 September 2022"),
    OrderHistorySectionUiModel.Item(
        imageUrl = "",
        title = "T-Bike ke Lapangan Barepan, Cawas",
        subtitle = "Sedang berlangsung",
        orderDate = "",
        orderListType = OrderListType.OrderDelivered,
        totalOrderPayment = "Rp83.000",
        showOrderButton = true,
        showRatingButton = false
    ),
)