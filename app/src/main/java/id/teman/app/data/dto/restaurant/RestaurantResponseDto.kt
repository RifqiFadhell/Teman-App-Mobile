package id.teman.app.data.dto.restaurant

import androidx.annotation.Keep
import id.teman.app.data.dto.DetailImageGeneral
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class RestaurantResponseDto(
    val page: Int? = null,
    val total: Int? = null,
    val pageCount: Int? = null,
    val count: Int? = null,
    val data: List<Restaurant>? = null
)

@Serializable
@Keep
data class Restaurant(
    val id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val name: String? = null,
    val description: String? = null,
    val address: String? = null,
    val phone_number: String? = null,
    val email: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val status: String? = null,
    val rating: Int? = null,
    val user_id: String? = null,
    val restaurant_photo_id: String? = null,
    val verified: Boolean? = null,
    val total_sales: Int? = null,
    val is_promo: Boolean? = null,
    val total_rating: Int? = null,
    val categories: List<String>? = null,
    val optional_address: String? = null,
    val postal_code: String? = null,
    val type: String? = null,
    val distance: Double? = null,
    val duration: Double? = null,
    val hours: List<Hour>? = null,
    val restaurant_photo: RestaurantPhoto? = null
)

@Serializable
@Keep
data class Hour(
    val id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val is_full: Boolean? = null,
    val is_open: Boolean? = null,
    val day: String? = null,
    val start: String? = null,
    val end: String? = null
)

@Serializable
@Keep
data class RestaurantPhoto(
    val id: String? = null,
    val filename: String? = null,
    val url: String? = null
)

@Serializable
@Keep
data class Category(
    val id: String,
    val created_at: String,
    val updated_at: String,
    val name: String,
    val icon: String,
)

@Serializable
@Keep
data class CategoryResponseDto(
    val data: List<Category>?,
    val count: Int,
    val total: Int,
    val page: Int,
    val pageCount: Int
)

@Serializable
@Keep
data class DetailRestaurantResponseDto(
    val id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val user_id: String? = null,
    val name: String? = null,
    val type: String? = null,
    val description: String? = null,
    val address: String? = null,
    val optional_address: String? = null,
    val postal_code: String? = null,
    val phone_number: String? = null,
    val email: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val status: String? = null,
    val rating: Int? = null,
    val verified: Boolean? = null,
    val total_sales: Int? = null,
    val total_rating: Int? = null,
    val is_promo: Boolean? = null,
    val categories: List<String>? = null,
    val hours: List<Hour>? = null,
    val restaurant_photo: DetailImageGeneral? = null,
    val distance: Double? = null,
    val duration: Double? = null,
    val products: List<ItemMenuRestaurant>? = null
)

@Serializable
@Keep
data class ItemMenuOrderDetailDto(
    val id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val restaurant_id: String? = null,
    val note: String? = null,
    val quantity: Int? = null,
    val markup_price: Double?,
    val price: Double? = null,
    val product: ItemMenuRestaurant? = null,
    val product_photo: DetailImageGeneral? = null
)

@Serializable
@Keep
data class ItemMenuRestaurant(
    val id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val restaurant_id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val promo_price: Double? = null,
    val markup_price: Double? = null,
    val markup_promo_price: Double? = null,
    val total_sales: Int? = null,
    val is_active: Boolean? = null,
    val is_promo: Boolean? = null,
    val category_id: String? = null,
    val note: String? = null,
    val category: ItemMenuCategory? = null,
    val product_photo: DetailImageGeneral? = null
)

@Serializable
@Keep
data class ItemMenuCategory(
    val id: String,
    val created_at: String,
    val updated_at: String,
    val restaurant_id: String,
    val name: String,
    val description: String
)