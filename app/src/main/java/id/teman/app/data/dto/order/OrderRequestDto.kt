package id.teman.app.data.dto.order

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class OrderRequestDto(
    @SerialName("pick_up_lat")
    val originLatitude: Double,
    @SerialName("pick_up_lng")
    val originLongitude: Double,
    @SerialName("pick_up_address")
    val originAddress: String,
    @SerialName("drop_off_lat")
    val destinationLatitude: Double,
    @SerialName("drop_off_lng")
    val destinationLongitude: Double,
    @SerialName("drop_off_address")
    val destinationAddress: String,
    val type: String,
    val note: String? = null,
    val pin: String? = null,
    @SerialName("payment_method")
    val paymentMethod: String? = null,
    @SerialName("receiver_name")
    val receiverName: String? = null,
    @SerialName("receiver_phone")
    val receiverPhone: String? = null,
    @SerialName("package_type")
    val packageType: String? = null,
    @SerialName("package_weight")
    val packageWeight: Int? = null,
    @SerialName("insurance")
    val insurance: Int? = null,
    @SerialName("restaurant_id")
    val restaurantId: String? = null,
    @SerialName("promotion_id")
    val promotionId: String? = null,
    val items: List<OrderRequestItemDto>? = null
)

@Keep
@Serializable
data class OrderRequestItemDto(
    @SerialName("product_id")
    val productId: String,
    val quantity: Int,
    val note: String? = null
)