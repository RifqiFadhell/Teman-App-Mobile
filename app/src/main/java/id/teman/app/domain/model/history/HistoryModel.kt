package id.teman.app.domain.model.history

import androidx.annotation.DrawableRes
import id.teman.app.R
import id.teman.app.common.convertToRupiah
import id.teman.app.common.orFalse
import id.teman.app.common.orZero
import id.teman.app.data.dto.HistoryItem
import id.teman.app.data.dto.ItemRestaurant
import id.teman.app.data.dto.restaurant.ItemMenuRestaurant
import id.teman.app.domain.model.ServiceType
import id.teman.app.domain.model.TypeOrder
import id.teman.app.domain.model.order.OrderPaymentSpec
import id.teman.app.domain.model.order.mapper.toOrderPaymentSpec
import id.teman.app.domain.model.restaurant.MenuRestaurant
import id.teman.app.domain.model.restaurant.MenuTitle
import id.teman.app.domain.model.restaurant.toMenuRestaurant
import id.teman.app.ui.orderlist.domain.model.OrderListType
import id.teman.app.ui.ordermapscreen.initiate.note
import id.teman.app.utils.getCurrentTimeFormat


data class HistoryModel(
    val id: String = "",
    val title: String= "",
    val date: String= "",
    val status: String= "",
    val address: String= "",
    val destination: String= "",
    val pickUpTime: String= "",
    val dropOffTime: String= "",
    val price: Double= 0.0,
    val image: HistoryIcon = HistoryIcon.BIKE,
    val totalItem: String? = "",
    val restoName: String= "",
    val packageWeight: String= "",
    val packageType: String= "",
    val rating: Int = 0,
    val ratingNotes: String = "",
    val driverName: String= "",
    val driverPhoto: String= "",
    val driverLicence: String= "",
    val paymentMethod: String= "",
    val sectionTitle: StatusOrder = StatusOrder.ON_PROCESS,
    val orderListType: OrderListType = OrderListType.OrderInProgress,
    val paymentBreakdown: List<OrderPaymentSpec> = emptyList(),
    val listMenu: List<MenuRestaurant> = emptyList(),
    val numberOrder: String= "",
    val notes: String = "",
    val type: String = ""
)

data class ItemFood(
    val name: String,
    val qty: String,
    val price: String
)

enum class StatusOrder(
    val title: String,
    val order: Int
) {
    ON_PROCESS("Sedang Proses", 0),
    PAST("Riwayat", 1),
}

enum class HistoryIcon(
    @DrawableRes val icon: Int
) {
    BIKE(R.drawable.ic_mini_bike),
    CAR(R.drawable.ic_mini_car),
    SEND(R.drawable.ic_mini_send),
    FOOD(R.drawable.ic_mini_food),
}

fun List<HistoryItem>?.toHistoryModel(): List<HistoryModel> {
    return this?.map {
        with(it) {
            val section = if (status == "requesting" || status == "accepted" || status == "process" || status == "onroute") StatusOrder.ON_PROCESS else StatusOrder.PAST
            val image: HistoryIcon
            val serviceType : ServiceType
                when(type) {
                TypeOrder.BIKE.value -> {
                    image = HistoryIcon.BIKE
                    serviceType = ServiceType.BIKE
                }
                TypeOrder.CAR.value -> {
                    image = HistoryIcon.CAR
                    serviceType = ServiceType.CAR
                }
                TypeOrder.FOOD.value -> {
                    image = HistoryIcon.FOOD
                    serviceType = ServiceType.FOOD
                }
                TypeOrder.SEND.value -> {
                    image = HistoryIcon.SEND
                    serviceType = ServiceType.SEND
                }
                else -> {
                    image = HistoryIcon.BIKE
                    serviceType = ServiceType.BIKE
                }
            }
            val totalItem = 0
            for (x in 0 until items?.size.orZero()) {
                totalItem + items?.get(x)?.quantity.orZero()
            }
            val titleOrder = if (type == TypeOrder.FOOD.value) restaurant?.name else drop_off_address
            val orderStatus = when(status) {
                "requesting" -> OrderListType.OrderRequested
                "accepted" -> OrderListType.OrderAccepted
                "process" -> OrderListType.OrderInProgress
                "canceled" -> OrderListType.OrderCancelled
                "onroute" -> OrderListType.OrderOnRoute
                "rejected" -> OrderListType.OrderRejected
                "arrived" -> OrderListType.OrderArrived
                "finished" -> OrderListType.OrderDelivered
                else -> OrderListType.OrderInProgress
            }
            HistoryModel(
                id = id.orEmpty(),
                title = "$serviceType-${titleOrder.orEmpty()}",
                date = getCurrentTimeFormat(updated_at),
                status = status.orEmpty(),
                address = if (type == TypeOrder.FOOD.value) titleOrder.orEmpty() else pick_up_address.orEmpty(),
                destination = drop_off_address.orEmpty(),
                price = fare.orZero(),
                image = image,
                rating = rating?.rate.orZero(),
                ratingNotes = rating?.note.orEmpty(),
                totalItem = if (type == TypeOrder.FOOD.value) totalItem.toString() else "",
                restoName = restaurant?.name.orEmpty(),
                packageWeight = package_weight.toString(),
                packageType = package_type.orEmpty(),
                sectionTitle = section,
                paymentMethod = payment_method.orEmpty(),
                numberOrder = number.orEmpty(),
                orderListType = orderStatus,
                paymentBreakdown = breakdown.toOrderPaymentSpec(),
                driverLicence = driver?.vehicle_number.orEmpty(),
                driverName = driver?.user?.name.orEmpty(),
                driverPhoto = driver?.driver_photo?.url.orEmpty(),
                listMenu = items.toMenuRestaurant(),
                notes = note.orEmpty(),
                type = type.orEmpty(),
                pickUpTime = pick_up_at.orEmpty(),
                dropOffTime = drop_off_at.orEmpty(),
            )
        }
    }.orEmpty()
}

fun List<ItemRestaurant>?.toMenuRestaurant(): List<MenuRestaurant> {
    return this?.map {
        MenuRestaurant(
            id = it.id.orEmpty(),
            name = it.product?.name.orEmpty(),
            description = it.product?.description.orEmpty(),
            productPhoto = it.product?.product_photo?.url.orEmpty(),
            strikeTrough = it.product?.is_promo.orFalse(),
            price = it.price.orZero(),
            promoPrice = it.product?.promo_price.orZero(),
            totalSales = "",
            categories = "",
            titleMenu = MenuTitle.MENU,
            qty = it.quantity.orZero(),
            notes = it.note.orEmpty()
        )
    }.orEmpty()
}