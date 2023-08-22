package id.teman.app.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
enum class DriverMitraType(val type: String) {
    CAR("car"),
    BIKE("bike"),
    FOOD("food"),
    SEND("send"),
    UNKNOWN("Unknown");

    companion object {
        fun from(value: String?) = when (value) {
            CAR.type -> CAR
            BIKE.type -> BIKE
            SEND.type -> SEND
            FOOD.type -> FOOD
            else -> UNKNOWN
        }
    }
}