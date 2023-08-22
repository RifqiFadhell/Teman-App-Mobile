package id.teman.app.data.dto.order

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class OrderResponseDto(
    val distance: Double? = null,
    val duration: Double? = null,
    val fare: Double? = null,
    val breakdown: List<PriceBreakdown>? = null,
    val items: List<FoodOrderItemDto>? = null
)

@Keep
@Serializable
data class PriceBreakdown(
    val name: String? = null,
    val type: String? = null,
    val value: Double? = null
)

@Keep
@Serializable
data class FoodOrderItemDto(
    @SerialName("product_id")
    val productId: String? = null,
    val quantity: Int? = null,
    val note: String? = null,
    val price: Double? = null
)