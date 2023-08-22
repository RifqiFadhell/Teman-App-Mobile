package id.teman.app.data.dto

import androidx.annotation.Keep
import id.teman.app.data.dto.order.PriceBreakdown
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class LoginDto(
    val accessToken: String?
)

@Serializable
@Keep
data class RequestOtpDto(
    val message: String?,
    val attemption: Int? = 0,
    val wait_in_seconds: String?,
)

@Serializable
@Keep
data class RequestOtpResetDto(
    val message: String?,
    val token: String?
)

@Serializable
@Keep
data class UserDto(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val user: UserDataDto
)

@Serializable
@Keep
data class UserDataDto(
    val id: String? = null,
    val email: String? = null,
    val name: String? = null,
    val role: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    val verified: Boolean? = null,
    val kyc: Boolean? = null,
    @SerialName("pin_status")
    val pinStatus: Boolean? = null,
    @SerialName("kyc_status")
    val kycStatus: String? = null,
    @SerialName("user_photo")
    val userPhoto: DetailImageGeneral? = null,
    val point: Double? = 0.0,
    @SerialName("referral_code")
    val referral: String? = null,
    val notification: Boolean = true
)

@Serializable
@Keep
data class HomeMenusDto(
    val data: List<MenusItem>?,
    val count: Int?,
    val total: Int?,
    val page: Int?,
    val pageCount: Int?
)

@Serializable
@Keep
data class MenusItem(
    val key: String,
    val title: String,
    val value: Boolean,
    val category: String,
    val icon: String,
)

@Serializable
@Keep
data class HomeBannerDto(
    val data: List<HomeBannerItem>?
)

@Serializable
@Keep
data class CouponPromoDto(
    val data: List<CouponPromoItem>?,
    val count: Int?,
    val total: Int?,
    val page: Int?,
    val pageCount: Int?,
)

@Serializable
@Keep
data class CouponPromoItem(
    val id: String?,
    val created_at: String?,
    val updated_at: String?,
    val start_at: String?,
    val end_at: String?,
    val title: String?,
    val type: String?,
    val status: String?,
    val discount_value: Double?,
    val discount_type: String?,
    val discount_max: Int?,
    val image: DetailImageGeneral?
)

@Serializable
@Keep
data class HomeBannerItem(
    val id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val title: String? = null,
    val scale: Int? = null,
    val status: String? = null,
    val image: DetailImageGeneral? = null,
    val url: String? = null
)

@Serializable
@Keep
data class DetailImageGeneral(
    val id: String? = null,
    val fieldname: String? = null,
    val url: String? = null
)

@Serializable
@Keep
data class HistoryOrders(
    val data: List<HistoryItem>?
)

@Serializable
@Keep
data class HistoryItem(
    val id: String?,
    val number: String?,
    val created_at: String?,
    val updated_at: String?,
    val driver_id: String?,
    val user_id: String?,
    val type: String?,
    val payment_method: String?,
    val pick_up_lat: Double?,
    val pick_up_lng: Double?,
    val pick_up_at: String?,
    val pick_up_address: String?,
    val drop_off_lat: Double?,
    val drop_off_lng: Double?,
    val drop_off_at: String?,
    val drop_off_address: String?,
    val drop_off_description: String?,
    val duration: Double?,
    val distance: Double?,
    val fare: Double?,
    val status: String?,
    val note: String?,
    val rating: Rating?,
    val receiver_name: String?,
    val receiver_phone: String?,
    val package_type: String?,
    val package_weight: Double?,
    val rejects: List<String>?,
    val promotion_id: String?,
    val restaurant_id: String?,
    val user: HistoryUserInfo,
    val driver: HistoryDriverInfo?,
    val restaurant: RestaurantInfo?,
    val breakdown: List<PriceBreakdown>?,
    val items: List<ItemRestaurant>?,
)

@Serializable
@Keep
data class Rating(
    val id: String?,
    val note: String?,
    val rate: Int?
)

@Serializable
@Keep
data class ItemRestaurant(
    val id: String?,
    val note: String?,
    val quantity: Int?,
    val price: Double?,
    val product: ProductItemRestaurant?
)

@Serializable
@Keep
data class ProductItemRestaurant(
    val id: String?,
    val name: String?,
    val description: String?,
    val price: Double?,
    val promo_price: Double?,
    val is_promo: Boolean?,
    val product_photo: DetailImageGeneral?
)

@Serializable
@Keep
data class HistoryUserInfo(
    val id: String?,
    val name: String?,
    val phone_number: String?,
    val user_photo: DetailImageGeneral?,
)

@Serializable
@Keep
data class HistoryDriverInfo(
    val id: String?,
    val created_at: String?,
    val updated_at: String?,
    val lat: Double?,
    val lng: Double?,
    val orientation: Double?,
    val rating: Double?,
    val last_movement: String?,
    val status: String?,
    val type: String?,
    val verified: String?,
    val vaccine: String?,
    val city: String?,
    val driver_license_number: String?,
    val vehicle_brand: String?,
    val vehicle_type: String?,
    val vehicle_year : String?,
    val vehicle_number: String?,
    val vehicle_fuel: String?,
    val driver_photo: DetailImageGeneral?,
    val user: DriverInfo?,
)

@Serializable
@Keep
data class DriverInfo(
    val id: String?,
    val name: String?,
    val phone_number: String?,
)

@Serializable
@Keep
data class RestaurantInfo(
    val id: String?,
    val name: String?,
    val phone_number: String?,
    val address: String?,
    val optional_address: String?,
    val status: String?,
    val rating: Double?
)

@Serializable
@Keep
data class BreakdownFareItem(
    val name: String?,
    val type: String?,
    val value: Double?,
)