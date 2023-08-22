package id.teman.app.ui.food

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.common.orFalse
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.order.FoodOrderItemRequestSpec
import id.teman.app.domain.model.order.FoodOrderRequestSpec
import id.teman.app.domain.model.order.OrderPaymentSpec
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.domain.model.restaurant.DetailRestaurantModel
import id.teman.app.domain.model.restaurant.ItemRestaurantModel
import id.teman.app.domain.model.restaurant.MenuRestaurant
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.domain.repository.food.FoodRepository
import id.teman.app.domain.usecase.order.GetOrderEstimationUseCase
import id.teman.app.domain.usecase.order.OrderEstimationRequestSpec
import id.teman.app.domain.usecase.order.SearchDriverRequestSpec
import id.teman.app.manager.UserManager
import id.teman.app.manager.UserState
import id.teman.app.preference.Preference
import id.teman.app.ui.ordermapscreen.initiate.send.PromoSpec
import id.teman.app.ui.sharedviewmodel.MainViewModel
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@HiltViewModel
class FoodMainViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val orderEstimationUseCase: GetOrderEstimationUseCase,
    private val json: Json,
    private val userManager: UserManager,
    private val preference: Preference
) : ViewModel() {

    var foodMainUiState by mutableStateOf(FoodLocationUiState())
        private set

    var foodUiState by mutableStateOf(FoodUiState())
        private set
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            userManager.start()
            userManager.observeUserState().collect { userState ->
                if (userState is UserState.Revoked) {
                    foodUiState = foodUiState.copy(logoutEvent = Event(Unit))
                }
            }
        }
    }

    fun updateData(location: MainViewModel.LocationUiState, spec: FoodScreenSpec) {
        foodMainUiState = foodMainUiState.copy(
            userLocation = location.userLocation,
            currentUserLocationLatLng = location.currentUserLocationLatLng,
            placeDetailSpec = location.placeDetailSpec,
            spec = spec
        )
    }

    fun updateLocationSpec(spec: PlaceDetailSpec) {
        foodMainUiState = foodMainUiState.copy(
            userLocation = spec.shortLocationAddress,
            currentUserLocationLatLng = spec.locationLatLng,
            placeDetailSpec = spec,
            FoodChangeEvent = Event(spec)
        )
    }

    fun searchDebounced(searchText: String, latLng: LatLng) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            getListRestaurantNearby(latLng = latLng, search = searchText)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (searchJob != null) {
            searchJob?.cancel()
        }
    }

    fun getUserProfile() {
        viewModelScope.launch {
            val userInfoJson = preference.getUserInfo.first()
            if (userInfoJson.isNotBlank()) {
                val userInfo = json.decodeFromString<UserInfo>(userInfoJson)
                foodUiState = foodUiState.copy(userInfo = userInfo)
            }
        }
    }

    private fun getListRestaurantNearby(latLng: LatLng, category: String? = "", search: String? = "") {
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

    fun getDetailRestaurant(id: String, latLang: LatLng) {
        foodUiState = foodUiState.copy(loading = true)
        viewModelScope.launch {
            foodRepository.getDetailRestaurant(id, latLang).catch { exception ->
                foodUiState = foodUiState.copy(loading = false, error = exception.message)
            }.collect {
                foodUiState = foodUiState.copy(
                    loading = false,
                    detailRestaurant = it,
                    listMenu = if (foodUiState.listMenu.isNullOrEmpty()) it.listProduct else foodUiState.listMenu
                )
            }
        }
    }

    fun getEstimation(userLocation: String, userLatLng: LatLng, promoId: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            foodUiState.detailRestaurant?.let { restaurant ->
                foodUiState = foodUiState.copy(loading = true)
                orderEstimationUseCase.execute(
                    OrderEstimationRequestSpec(
                        orderType = OrderRequestType.FOOD,
                        originLatLng = userLatLng,
                        originAddress = userLocation,
                        destinationLatLng = restaurant.latLng,
                        destinationAddress = restaurant.address,
                        notes = "",
                        restaurantOrder = FoodOrderRequestSpec(
                            restaurantId = restaurant.id,
                            orderedItems = foodUiState.listMenu.orEmpty().map {
                                FoodOrderItemRequestSpec(
                                    productId = it.id,
                                    quantity = it.qty,
                                    note = ""
                                )
                            }
                        ), promoId = promoId
                    )
                ).catch {
                    foodUiState = foodUiState.copy(loading = false)
                }.collect {
                    foodUiState = foodUiState.copy(
                        loading = false,
                        paymentSpec = it.paymentBreakdown,
                        totalPriceServer = it.totalPrice
                    )
                }
            }
        }
    }

    fun increaseItem(id: String, notes: String = "") {
        val list = foodUiState.listMenu.orEmpty()
        val currentCount = list.first { it.id == id }.qty
        updateQtyItem(id = id, count = currentCount + 1, notes = notes)
    }

    fun decreaseItem(id: String) {
        val list = foodUiState.listMenu.orEmpty()
        val currentCount = list.first { it.id == id }.qty
        if (currentCount > 0) {
            updateQtyItem(id = id, count = currentCount - 1, notes = "")
        }
    }

    private fun updateQtyItem(id: String, count: Int, notes: String = "") {
        val list = foodUiState.listMenu.orEmpty()
        foodUiState = foodUiState.copy(listMenu = list.map {
            if (it.id == id) {
                it.copy(qty = count, notes = notes)
            } else {
                it
            }
        })
        buttonContinue()
    }

    fun updateNotes(id: String, notes: String) {
        val list = foodUiState.listMenu.orEmpty()
        foodUiState = foodUiState.copy(listMenu = list.map {
            if (it.id == id) {
                it.copy(notes = notes)
            } else {
                it
            }
        })
        buttonContinue()
    }

    private fun getTotalDiscount(): Double {
        val list = foodUiState.listMenu.orEmpty()
        return list.sumOf { if (it.qty > 0 && it.strikeTrough) (it.price - it.promoPrice) * it.qty else 0.0}
    }

    private fun buttonContinue() {
        val list = foodUiState.listMenu.orEmpty()
        foodUiState = foodUiState.copy(
            totalPrice = list.sumOf {
                if (it.qty > 0 && it.strikeTrough) it.promoPrice * it.qty else if (it.qty > 0) it.price * it.qty else 0.0
            },
            totalDiscount = getTotalDiscount()
        )
    }

    fun resetDataBack() {
        val spec = foodMainUiState.spec
        foodUiState = foodUiState.copy(listMenu = emptyList(), totalDiscount = 0.0, totalPrice = 0.0)
        foodMainUiState = foodMainUiState.copy(spec = spec?.copy(isRestaurantFromMain = false, isListRestaurantFromMain = false))
    }

    fun getIsFromMain(): Boolean {
        return foodMainUiState.spec?.isRestaurantFromMain.orFalse()
    }

    fun getIsListFromMain(): Boolean {
        return foodMainUiState.spec?.isListRestaurantFromMain.orFalse()
    }

    fun redirectToMap(spec: SearchDriverRequestSpec) {
        foodUiState = foodUiState.copy(navigateToMap = Event(spec))
    }

    fun updateUsePromo(spec: PromoSpec) {
        foodUiState = foodUiState.copy(usePromo = spec)
    }
}

data class FoodUiState(
    val loading: Boolean = false,
    val error: String? = "",
    val userInfo: UserInfo? = null,
    val detailRestaurant: DetailRestaurantModel? = null,
    var listMenu: List<MenuRestaurant>? = emptyList(),
    var totalPrice: Double = 0.0,
    var totalPriceServer: Double = 0.0,
    var totalDiscount: Double = 0.0,
    val navigateToMap: Event<SearchDriverRequestSpec>? = null,
    val paymentSpec: List<OrderPaymentSpec> = emptyList(),
    val listRestaurant: List<ItemRestaurantModel> = emptyList(),
    val logoutEvent: Event<Unit>? = null,
    val usePromo: PromoSpec? = null,
    val placeDetailSpec: PlaceDetailSpec? = null,
    val notes: String = ""
)

data class FoodLocationUiState(
    val loading: Boolean = false,
    val error: String? = "",
    val userLocation: String = "Lokasi belum terdeteksi",
    val currentUserLocationLatLng: LatLng = LatLng(-6.172131022973852, 107.0425259263954),
    val placeDetailSpec: PlaceDetailSpec? = null,
    val spec: FoodScreenSpec? = null,
    val FoodChangeEvent: Event<PlaceDetailSpec>? = null
)