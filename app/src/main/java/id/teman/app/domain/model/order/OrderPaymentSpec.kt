package id.teman.app.domain.model.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderPaymentSpec(
    val name: String,
    val price: Double,
    val breakdownType: BreakdownType,
    val quantity: String = "",
    val notes: String = ""
) : Parcelable

enum class BreakdownType(val value: String) {
    FOOD("food"),
    FARE("driver_fare"),
    SAFETY("safety"),
    ADMIN("admin"),
    OTHER("other"),
    DISCOUNT("discount");

    companion object {
        fun from(value: String?) = when (value) {
            FOOD.value -> FOOD
            FARE.value -> FARE
            SAFETY.value -> SAFETY
            ADMIN.value -> ADMIN
            else -> OTHER
        }
    }
}