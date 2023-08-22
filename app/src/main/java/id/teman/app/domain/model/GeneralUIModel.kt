package id.teman.app.domain.model

import androidx.compose.ui.graphics.Color

data class RatingSpec(
    val color: Color,
    val rating: String
)

enum class TypeOrder(
    val value: String
) {
    BIKE("bike"),
    CAR("car"),
    FOOD("food"),
    SEND("send"),
}

enum class ServiceType(
    val title: String
) {
    FOOD("T-Food"),
    BIKE("T-Bike"),
    CAR("T-Car"),
    SEND("T-Send"),
}