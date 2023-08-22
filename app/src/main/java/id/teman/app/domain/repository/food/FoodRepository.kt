package id.teman.app.domain.repository.food

import com.google.android.gms.maps.model.LatLng
import id.teman.app.data.remote.food.FoodRemoteDataSource
import id.teman.app.domain.model.home.QuickMenuModel
import id.teman.app.domain.model.home.quickFoodMenus
import id.teman.app.domain.model.restaurant.DetailRestaurantModel
import id.teman.app.domain.model.restaurant.FoodCategoryItem
import id.teman.app.domain.model.restaurant.ItemRestaurantModel
import id.teman.app.domain.model.restaurant.convertToListRestaurant
import id.teman.app.domain.model.restaurant.toDetailRestaurantModel
import id.teman.app.domain.model.restaurant.toFoodCategoryItem
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodRepository @Inject constructor(
    private val foodRemoteDataSource: FoodRemoteDataSource
) {
    suspend fun getListRestaurant(latLng: LatLng, search: String? = "", category: String? = ""): Flow<List<ItemRestaurantModel>> =
        foodRemoteDataSource.getListRestaurant(latLng, search, category).map { it.data?.convertToListRestaurant().orEmpty() }

    suspend fun getFoodMenus(): Flow<List<QuickMenuModel>> =
        foodRemoteDataSource.getFoodMenus().map { it.quickFoodMenus() }

    suspend fun getFoodCategories(): Flow<List<FoodCategoryItem>> =
        foodRemoteDataSource.getFoodCategories().map { it.toFoodCategoryItem() }

    suspend fun getDetailRestaurant(id: String, latLang: LatLng): Flow<DetailRestaurantModel> =
        foodRemoteDataSource.getDetailRestaurant(id = id, latLang).map { it.toDetailRestaurantModel() }
}