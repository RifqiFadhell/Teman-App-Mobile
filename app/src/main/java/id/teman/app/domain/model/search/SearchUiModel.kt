package id.teman.app.domain.model.search

import id.teman.app.data.dto.location.GooglePredictionDto
import id.teman.app.domain.model.restaurant.ItemRestaurantModel

sealed class SearchUiModel {
    data class SectionDestination(
        val storeName: String,
        val storeAddress: String,
        val placeId: String? = ""
    ) : SearchUiModel()

    data class SectionFood(
        val restaurantId: String,
        val imageUrl: String,
        val storeName: String,
        val storeDescription: String,
        val storeRating: String,
        val storeDistance: String,
        val deliveryTime: String
    ) : SearchUiModel()
}

fun List<ItemRestaurantModel>.toSearchUiModel(): List<SearchUiModel.SectionFood> {
    return this.map {
        SearchUiModel.SectionFood(
            imageUrl = it.photoRestaurant,
            storeName = it.name,
            storeDescription = it.description,
            storeRating = it.rating,
            storeDistance = it.distance,
            deliveryTime = it.timeEstimation,
            restaurantId = it.id
        )
    }
}

fun List<GooglePredictionDto>.toSearchLocationUiModel(): List<SearchUiModel.SectionDestination> {
    return this.map {
        SearchUiModel.SectionDestination(
            storeName = it.structuredFormatting?.title.orEmpty(),
            storeAddress = it.description.orEmpty(),
            placeId = it.placeId.orEmpty()
        )
    }
}