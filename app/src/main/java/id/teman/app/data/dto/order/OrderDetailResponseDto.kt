package id.teman.app.data.dto.order

import androidx.annotation.Keep
import id.teman.app.data.dto.RestaurantInfo
import id.teman.app.data.dto.driver.DriverBasicInfoDto
import id.teman.app.data.dto.restaurant.ItemMenuOrderDetailDto
import id.teman.app.data.dto.user.SimpleUserDetailDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class OrderDetailResponseDto(
    val id: String? = null,
    @SerialName("payment_method")
    val paymentMethod: String? = null,
    @SerialName("pick_up_lat")
    val originLatitude: Double? = null,
    @SerialName("pick_up_lng")
    val originLongitude: Double? = null,
    @SerialName("pick_up_address")
    val originAddress: String? = null,
    @SerialName("drop_off_lat")
    val destinationLatitude: Double? = null,
    @SerialName("drop_off_lng")
    val destinationLongitude: Double? = null,
    @SerialName("drop_off_address")
    val destinationAddress: String? = null,
    val type: String? = null,
    val duration: Double? = null,
    @SerialName("real_distance")
    val distance: Double? = null,
    val fare: Double? = null,
    val breakdown: List<PriceBreakdown>? = emptyList(),
    val status: String? = null,
    val note: String? = null,
    @SerialName("receiver_name")
    val receiverName: String? = null,
    @SerialName("receiver_phone")
    val receiverPhone: String? = null,
    @SerialName("package_type")
    val packageType: String? = null,
    @SerialName("package_weight")
    val packageWeight: Double? = null,
    @SerialName("insurance")
    val insurance: Double? = null,
    val rejects: List<String>? = null,
    @SerialName("promotion_id")
    val promotionId: String? = null,
    @SerialName("restaurant_id")
    val restaurantId: String? = null,
    val user: SimpleUserDetailDto? = null,
    val driver: DriverBasicInfoDto? = null,
    val restaurant: RestaurantInfo? = null,
    val items: List<ItemMenuOrderDetailDto>? = emptyList()
)