package id.teman.app.ui.food

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.common.convertToRupiah
import id.teman.app.domain.model.home.BannerHomeSpec
import id.teman.app.domain.model.home.QuickMenuModel
import id.teman.app.domain.model.restaurant.FoodCategoryItem
import id.teman.app.domain.model.restaurant.ItemRestaurantModel
import id.teman.app.domain.repository.food.FoodRepository
import id.teman.app.domain.repository.home.HomeRepository
import id.teman.app.domain.repository.wallet.WalletRepository
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val foodRepository: FoodRepository,
    private val homeRepository: HomeRepository,
    ) : ViewModel() {

    var foodUiState by mutableStateOf(FoodUiState())
        private set

    init {
        foodUiState = foodUiState.copy(loading = true)
    }

    fun getWalletBalance() {
        foodUiState = foodUiState.copy(loading = true)
        viewModelScope.launch {
            walletRepository.getWalletBalance().catch { exception ->
                foodUiState =
                    foodUiState.copy(loading = false, error = exception.message.orEmpty())
                getListCategories()
            }.collect {
                foodUiState = foodUiState.copy(loading = false, balance = it.convertToRupiah())
                getListCategories()
            }
        }
    }

    fun getListMenu() {
        foodUiState = foodUiState.copy(loading = true)
        viewModelScope.launch {
            foodRepository.getFoodMenus().catch { exception ->
                foodUiState =
                    foodUiState.copy(loading = false, error = exception.message.orEmpty())
            }.collect {
                foodUiState = foodUiState.copy(loading = false, listMenus = it)
            }
        }
    }

    private fun getListCategories() {
        foodUiState = foodUiState.copy(loading = true)
        viewModelScope.launch {
            foodRepository.getFoodCategories().catch { exception ->
                foodUiState =
                    foodUiState.copy(loading = false, error = exception.message.orEmpty())
            }.collect {
                foodUiState = foodUiState.copy(loading = false, listCategories = it)
            }
        }
    }

    fun getListRestaurantNearby(latLng: LatLng, category: String? = "", search: String? = "") {
        foodUiState = foodUiState.copy(loading = true)
        viewModelScope.launch {
            foodRepository.getListRestaurant(latLng = latLng, search = search, category = category)
                .catch { exception ->
                    foodUiState = foodUiState.copy(loading = false, error = exception.message)
                }.collect {
                foodUiState = foodUiState.copy(loading = false, listRestaurant = it)
            }
        }
    }

    fun getHomeBanner() {
        foodUiState = foodUiState.copy(loading = true)
        viewModelScope.launch {
            homeRepository.getFoodBanners().catch { exception ->
                foodUiState =
                    foodUiState.copy(loading = false, error = exception.message.orEmpty())
                getWalletBalance()
            }.collect {
                getWalletBalance()
                foodUiState = if (it.isNotEmpty()) {
                    foodUiState.copy(loading = false, listBanners = it)
                } else {
                    foodUiState.copy(loading = false)
                }
            }
        }
    }

    data class FoodUiState(
        val loading: Boolean = false,
        val error: String? = "",
        val balance: String = "Rp.0",
        val listMenus: List<QuickMenuModel> = emptyList(),
        val listCategories: List<FoodCategoryItem> = emptyList(),
        val listRestaurant: List<ItemRestaurantModel> = emptyList(),
        val listBanners: List<BannerHomeSpec>? = emptyList()
    )
}