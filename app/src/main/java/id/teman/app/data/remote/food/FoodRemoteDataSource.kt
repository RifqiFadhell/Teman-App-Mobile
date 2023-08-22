package id.teman.app.data.remote.food

import com.google.android.gms.maps.model.LatLng
import id.teman.app.data.dto.MenusItem
import id.teman.app.data.dto.restaurant.Category
import id.teman.app.data.dto.restaurant.DetailRestaurantResponseDto
import id.teman.app.data.dto.restaurant.RestaurantResponseDto
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface FoodRemoteDataSource {
    fun getListRestaurant(latLang: LatLng, search: String? = "", category: String? = ""): Flow<RestaurantResponseDto>
    fun getFoodMenus(): Flow<List<MenusItem>>
    fun getFoodCategories(): Flow<List<Category>>
    fun getDetailRestaurant(id: String, latLang: LatLng): Flow<DetailRestaurantResponseDto>
}

class DefaultFoodRemoteDataSource(private val httpClient: ApiServiceInterface): FoodRemoteDataSource {

    override fun getListRestaurant(
        latLang: LatLng,
        search: String?,
        category: String?
    ): Flow<RestaurantResponseDto> =
        handleRequestOnFlow {
            httpClient.getListRestaurant(latLang.latitude, latLang.longitude, search, category)
        }

    override fun getFoodMenus(): Flow<List<MenusItem>> =
        handleRequestOnFlow {
            httpClient.getFoodMenus().orEmpty()
        }

    override fun getFoodCategories(): Flow<List<Category>> =
        handleRequestOnFlow {
            httpClient.getMenuCategories().data.orEmpty()
        }

    override fun getDetailRestaurant(id: String, latLang: LatLng,): Flow<DetailRestaurantResponseDto> =
        handleRequestOnFlow {
            httpClient.getDetailRestaurant(id, latLang.latitude, latLang.longitude,)
        }
}