package id.teman.app.domain.model.restaurant

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import id.teman.app.common.orFalse
import id.teman.app.common.orZero
import id.teman.app.data.dto.restaurant.Category
import id.teman.app.data.dto.restaurant.DetailRestaurantResponseDto
import id.teman.app.data.dto.restaurant.Hour
import id.teman.app.data.dto.restaurant.ItemMenuOrderDetailDto
import id.teman.app.data.dto.restaurant.ItemMenuRestaurant
import id.teman.app.data.dto.restaurant.Restaurant
import kotlin.math.roundToInt

/*
Item Restaurant
*/
data class ItemRestaurantModel(
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val status: String,
    val photoRestaurant: String,
    val categories: String,
    val finalRating: String,
    val rating: String,
    val totalRating: String,
    val openHour: String,
    val distance: String,
    val timeEstimation: String,
)

fun List<Restaurant>?.convertToListRestaurant(): List<ItemRestaurantModel> {
    return this?.map {
        val status = if (it.status == "open") {
            "Buka"
        } else {
            "Tutup"
        }

        ItemRestaurantModel(
            id = it.id.orEmpty(),
            name = it.name.orEmpty(),
            description = it.description.orEmpty(),
            address = it.address.orEmpty(),
            status = status,
            photoRestaurant = it.restaurant_photo?.url.orEmpty(),
            categories = it.categories?.joinToString(",").orEmpty(),
            finalRating = "${it.rating} dari ${it.total_rating} Ulasan",
            rating = it.rating.toString(),
            totalRating = it.total_rating.toString(),
            openHour = it.hours?.getOrNull(0)?.start.orEmpty(),
            distance = "${it.distance}Km",
            timeEstimation = "${it.duration.orZero().roundToInt()} Menit"
        )
    }.orEmpty()
}

/*
CATEGORY
*/


data class FoodCategoryItem(
    val id: String,
    val name: String,
    val icon: String
)

fun List<Category>?.toFoodCategoryItem(): List<FoodCategoryItem> {
    return this?.map {
        FoodCategoryItem(
            id = it.id,
            name = it.name,
            icon = it.icon,
        )
    }.orEmpty()
}

/*
DETAIL RESTAURANT
*/

data class DetailRestaurantModel(
    val id: String,
    val name: String,
    val description: String,
    val latLng: LatLng,
    val address: String,
    val status: String,
    val photoRestaurant: String,
    val categories: String,
    val finalRating: String,
    val rating: String,
    val totalRating: String,
    val openHour: List<OpenHour>,
    val distance: String,
    val timeEstimation: String,
    val listProduct: List<MenuRestaurant>
)

data class OpenHour(
    val id: String,
    val isFull: Boolean,
    val isOpen: Boolean,
    val day: String,
    val start: String,
    val end: String
)

@kotlinx.parcelize.Parcelize
data class MenuRestaurant(
    val id: String,
    val name: String,
    val description: String,
    val productPhoto: String,
    val strikeTrough: Boolean,
    val price: Double,
    val promoPrice: Double,
    val totalSales: String,
    val categories: String,
    val titleMenu: MenuTitle,
    val qty: Int,
    val notes: String
) : Parcelable

@kotlinx.parcelize.Parcelize
data class MenuDetailOrder(
    val id: String,
    val name: String,
    val description: String,
    val productPhoto: String,
    val strikeTrough: Boolean,
    val price: Double,
    val categories: String,
    val qty: Int,
    val notes: String = ""
) : Parcelable

enum class MenuTitle(
    val title: String,
    val order: Int
) {
    PROMO("Promo", 0),
    BEST_SELLING("Terlaris", 1),
    MENU("Daftar Menu", 2)
}

fun DetailRestaurantResponseDto.toDetailRestaurantModel(): DetailRestaurantModel {

    return DetailRestaurantModel(
        id = id.orEmpty(),
        name = name.orEmpty(),
        description = description.orEmpty(),
        latLng = LatLng(lat.orZero(), lng.orZero()),
        address = address.orEmpty(),
        status = status.orEmpty(),
        photoRestaurant = restaurant_photo?.url.orEmpty(),
        categories = categories?.joinToString(",").orEmpty(),
        finalRating = "$total_rating Ulasan",
        rating = rating.toString(),
        totalRating = total_rating.toString(),
        openHour = hours.orEmpty().toOpenHour(),
        distance = "${distance.orZero()} Km",
        timeEstimation = "${convertDecimal(duration.orZero())} Min",
        listProduct = products.orEmpty().toMenuRestaurant(),
    )
}

fun convertDecimal(value: Double): Double {
    val number: Double = value
    val number3digits: Double = String.format("%.3f", number).toDouble()
    val number2digits: Double = String.format("%.2f", number3digits).toDouble()
    return String.format("%.1f", number2digits).toDouble()
}

fun List<ItemMenuRestaurant>?.toMenuRestaurant(): List<MenuRestaurant> {
    return this?.map {

        val title = if (it.is_promo.orFalse()) {
            MenuTitle.PROMO
        } else if (it.total_sales.orZero() > 25) {
            MenuTitle.BEST_SELLING
        } else {
            MenuTitle.MENU
        }
        MenuRestaurant(
            id = it.id.orEmpty(),
            name = it.name.orEmpty(),
            description = it.description.orEmpty(),
            productPhoto = it.product_photo?.url.orEmpty(),
            strikeTrough = it.is_promo.orFalse(),
            price = it.markup_price.orZero(),
            promoPrice = it.markup_promo_price.orZero(),
            totalSales = it.total_sales.toString(),
            categories = it.category?.name.orEmpty(),
            titleMenu = title,
            qty = 0,
            notes = it.note.orEmpty()
        )
    }.orEmpty()
}

fun List<ItemMenuOrderDetailDto>?.toMenuOrderDetail(): List<MenuDetailOrder> {
    return this?.map {
        MenuDetailOrder(
            id = it.id.orEmpty(),
            name = it.product?.name.orEmpty(),
            description = it.product?.description.orEmpty(),
            productPhoto = it.product_photo?.url.orEmpty(),
            strikeTrough = it.product?.is_promo.orFalse(),
            price = it.markup_price.orZero(),
            categories = it.product?.category?.name.orEmpty(),
            qty = it.quantity.orZero(),
            notes = it.note.orEmpty()
        )
    }.orEmpty()
}

fun List<Hour>?.toOpenHour(): List<OpenHour> {
    return this?.map {
        OpenHour(
            id = it.id.orEmpty(),
            isFull = it.is_full.orFalse(),
            isOpen = it.is_open.orFalse(),
            day = it.day.orEmpty(),
            start = it.start.orEmpty(),
            end = it.end.orEmpty(),
        )
    }.orEmpty()
}

