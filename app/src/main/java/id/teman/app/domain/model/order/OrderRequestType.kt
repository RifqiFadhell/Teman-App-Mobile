package id.teman.app.domain.model.order

enum class OrderRequestType(val value: String) {
    BIKE("bike"),
    SEND("send"),
    FOOD("food"),
    CAR("car");
}

fun OrderRequestType.convertTitle(): String {
    return when(this) {
        OrderRequestType.BIKE -> "T-Bike Safe"
        OrderRequestType.SEND -> "T-Send"
        OrderRequestType.CAR -> "T-Car Safe"
        OrderRequestType.FOOD -> "T-Food"
    }
}

fun OrderRequestType.isFood() = this == OrderRequestType.FOOD
fun OrderRequestType.isTransportation() =
    this == OrderRequestType.BIKE || this == OrderRequestType.CAR

fun OrderRequestType.isSend() = this == OrderRequestType.SEND