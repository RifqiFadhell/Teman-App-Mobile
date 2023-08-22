package id.teman.app.ui.ordermapscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.common.orZero
import id.teman.app.domain.model.location.LocationDirectionSpec
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.order.OrderDetailSpec
import id.teman.app.domain.model.order.OrderEstimationResponseSpec
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.domain.model.order.TransportRequestType
import id.teman.app.domain.model.user.DriverInfo
import id.teman.app.domain.model.user.DriverMitraType
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.domain.repository.location.LocationRepository
import id.teman.app.domain.repository.order.OrderRepository
import id.teman.app.domain.repository.user.UserRepository
import id.teman.app.domain.usecase.order.GetOrderEstimationUseCase
import id.teman.app.domain.usecase.order.OrderEstimationRequestSpec
import id.teman.app.domain.usecase.order.SearchDriverRequestSpec
import id.teman.app.domain.usecase.order.SearchDriverUseCase
import id.teman.app.ui.ordermapscreen.initiate.send.FormDetailSpec
import id.teman.app.ui.ordermapscreen.initiate.send.PromoSpec
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class TransportViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val orderEstimationUseCase: GetOrderEstimationUseCase,
    private val searchDriverUseCase: SearchDriverUseCase,
    private val orderRepository: OrderRepository
) : ViewModel() {

    var transportUiState by mutableStateOf(TransportUiStateSpec())
        private set

    var driverPoller by mutableStateOf(false)
        private set

    var orderType = mutableStateOf(OrderRequestType.BIKE)
        private set

    fun setOrderType(orderRequestType: OrderRequestType) {
        orderType.value = orderRequestType
    }

    init {
        viewModelScope.launch {
            userRepository.getUserProfile()
                .catch { }
                .collect {
                    transportUiState = transportUiState.copy(
                        userInfo = it
                    )
                }
        }
    }

    fun onRestaurantOrders(item: SearchDriverRequestSpec) {
        transportUiState =
            transportUiState.copy(
                isLoading = true,
                showOriginDestinationCard = false,
                errorRestaurantRequestOrder = ""
            )
        getNearbyDrivers(item.originLatLng.latitude, item.originLatLng.longitude)
        viewModelScope.launch(Dispatchers.IO) {
            searchDriverUseCase.execute(
                SearchDriverRequestSpec(
                    orderType = orderType.value,
                    originLatLng = item.originLatLng,
                    destinationLatLng = item.destinationLatLng,
                    originAddress = item.originAddress,
                    destinationAddress = item.destinationAddress,
                    paymentMethod = item.paymentMethod,
                    pin = item.pin,
                    restaurantOrder = item.restaurantOrder,
                    notes = item.notes,
                    promoId = item.promoId
                )
            ).catch { exception ->
                transportUiState =
                    transportUiState.copy(
                        isLoading = false,
                        emptyDriver = true,
                        errorRestaurantRequestOrder = exception.message.orEmpty()
                    )
            }.collect {
                transportUiState = transportUiState.copy(
                    searchRequestInProcess = it
                )
                getActiveOrderStatusIntervally()
            }
        }
    }

    fun stopEmitData() {
        driverPoller = false
    }

    override fun onCleared() {
        super.onCleared()
        driverPoller = false
        transportUiState.searchRequestInProcess?.let { order ->
            cancelOrderTrip(order.requestId)
        }
    }

    private fun updateMapLatLng(value: LatLng) {
        transportUiState = transportUiState.copy(originMarker = Event(value))
    }

    fun initUserOrderStatus(foodOrders: SearchDriverRequestSpec? = null) {
        transportUiState =
            transportUiState.copy(isLoading = true, isShowErrorUI = false, emptyDriver = false)
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserProfile()
                .catch { exception ->
                    transportUiState = transportUiState.copy(isLoading = false)
                }.collect {
                    getActiveOrderStatusIntervally(foodOrders)
                }
        }
    }

    private fun getActiveOrderStatusIntervally(foodOrders: SearchDriverRequestSpec? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            driverPoller = true
            while (driverPoller) {
                delay(2000)
                orderRepository.getActiveOrder()
                    .catch {
                        driverPoller = false
                        if (foodOrders != null) {
                            onRestaurantOrders(foodOrders)
                        } else {
                            transportUiState = transportUiState.copy(
                                isLoading = false,
                                showOriginDestinationCard = true,
                                emptyDriver = false,
                                isShowErrorUI = false
                            )
                        }
                    }.collect { orderSpec ->
                        if (orderSpec.orderType.type.isOtherOrderTypeIsActive()) {
                            driverPoller = false
                            transportUiState = transportUiState.copy(
                                isLoading = false,
                                otherOrderTypeActive = orderSpec.orderType.type
                            )
                        } else {
                            getDetailBasedOnOrderStatus(orderSpec)
                        }
                    }
            }
        }
    }

    private fun String.isOtherOrderTypeIsActive() = this != orderType.value.value

    private fun getMapDirection(
        origin: String,
        destination: String,
        spec: OrderDetailSpec? = null
    ) {
        transportUiState =
            transportUiState.copy(isLoading = true, isShowErrorUI = false)
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.getMapDirection(
                origin,
                destination,
                vehicleType = spec?.orderType?.getVehicleType() ?: orderType.value.getVehicleType()
            ).catch {
                transportUiState =
                    transportUiState.copy(isLoading = false, isShowErrorUI = true)
            }.collect { direction ->
                spec?.let { order ->
                    val originMarker = LatLng(order.pickupLatitude, order.pickupLatitude)

                    transportUiState = transportUiState.copy(
                        isLoading = false,
                        orderEstimationUI = null,
                        mapPolyline = Event(direction),
                        orderOnGoingUI = spec,
                        driverBearing = Event(order.driverBearing),
                        driverLatLng = Event(
                            LatLng(
                                order.driverLatitude,
                                order.driverLongitude
                            )
                        ),
                        originMarker = if (order.orderStatus != TransportRequestType.REQUESTING) null else Event(
                            originMarker
                        ),
                        destinationFormPlaceDetail = if (order.orderStatus != TransportRequestType.REQUESTING) null else
                            transportUiState.destinationFormPlaceDetail
                    )
                } ?: run {
                    transportUiState = transportUiState.copy(
                        isLoading = false,
                        mapPolyline = Event(direction)
                    )
                }

            }
        }
    }

    fun setFormSendDetail(spec: FormDetailSpec?) {
        transportUiState = transportUiState.copy(tSendSpec = spec)
        getTSendEstimationDetail()
    }

    fun getTSendEstimationDetail() = viewModelScope.launch(Dispatchers.IO) {
        val spec = transportUiState.tSendSpec
        if (spec != null) {
            getNearbyDrivers(
                spec.origin.locationLatLng.latitude,
                spec.origin.locationLatLng.longitude
            )
            orderEstimationUseCase.execute(
                OrderEstimationRequestSpec(
                    orderType = orderType.value,
                    originLatLng = spec.origin.locationLatLng,
                    destinationLatLng = spec.destination.locationLatLng,
                    originAddress = spec.origin.formattedAddress,
                    destinationAddress = spec.destination.formattedAddress,
                    notes = spec.note,
                    receiverName = transportUiState.tSendSpec?.receiverName,
                    receiverPhone = transportUiState.tSendSpec?.receiverPhoneNumber,
                    packageType = transportUiState.tSendSpec?.packageType,
                    packageWeight = transportUiState.tSendSpec?.packageWeight,
                    promoId = transportUiState.usePromo?.id.orEmpty(),
                    insurance = transportUiState.tSendSpec?.insurance.orZero(),
                )
            ).catch {
                transportUiState = transportUiState.copy(isLoading = false)
            }.collect {
                transportUiState = transportUiState.copy(
                    isLoading = false,
                    orderEstimationUI = it,
                    showOriginDestinationCard = false,
                    openBottomSheet = Event(true)
                )
            }
        }
    }

    fun getOrderEstimationDetail(driverNote: String = "") =
        viewModelScope.launch(Dispatchers.IO) {
            if (transportUiState.originFormPlaceDetail != null && transportUiState.destinationFormPlaceDetail != null) {
                transportUiState = transportUiState.copy(isLoading = true)
                if (driverNote.isNotEmpty()) {
                    transportUiState = transportUiState.copy(notes = driverNote)
                }
                val originLatLng = transportUiState.originFormPlaceDetail!!.locationLatLng
                val destinationLatLng = transportUiState.destinationFormPlaceDetail!!.locationLatLng
                orderEstimationUseCase.execute(
                    OrderEstimationRequestSpec(
                        orderType = orderType.value,
                        originLatLng = originLatLng,
                        destinationLatLng = destinationLatLng,
                        originAddress = transportUiState.originFormPlaceDetail!!.formattedAddress,
                        destinationAddress = transportUiState.destinationFormPlaceDetail!!.formattedAddress,
                        notes = transportUiState.notes,
                        promoId = transportUiState.usePromo?.id.orEmpty()
                    )
                ).catch {
                    transportUiState = transportUiState.copy(isLoading = false)
                }.collect {
                    transportUiState = transportUiState.copy(
                        isLoading = false,
                        orderEstimationUI = it,
                        showOriginDestinationCard = false,
                        openBottomSheet = Event(true)
                    )
                }
            }
        }

    fun getNearbyDrivers(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getNearbyDrivers(latitude, longitude, orderType.value.value)
                .catch { exception ->
                    /* no-op */
                }
                .collect {
                    transportUiState = transportUiState.copy(nearbyDrivers = it)
                }
        }
    }

    fun searchDriver() {
        transportUiState =
            transportUiState.copy(isLoading = true, showOriginDestinationCard = false)
        viewModelScope.launch(Dispatchers.IO) {
            val originLatLng = transportUiState.originFormPlaceDetail!!.locationLatLng
            val destinationLatLng = transportUiState.destinationFormPlaceDetail!!.locationLatLng
            if (transportUiState.nearbyDrivers.isEmpty()) {
                getNearbyDrivers(originLatLng.latitude, originLatLng.longitude)
            }
            searchDriverUseCase.execute(
                SearchDriverRequestSpec(
                    orderType = orderType.value,
                    originLatLng = originLatLng,
                    destinationLatLng = destinationLatLng,
                    originAddress = transportUiState.originFormPlaceDetail!!.formattedAddress,
                    destinationAddress = transportUiState.destinationFormPlaceDetail!!.formattedAddress,
                    paymentMethod = transportUiState.orderEstimationUI?.paymentMethod ?: "cash",
                    pin = transportUiState.orderEstimationUI?.pin,
                    notes = transportUiState.orderEstimationUI?.notes,
                    receiverPhone = transportUiState.orderEstimationUI?.receiverPhoneNumber,
                    receiverName = transportUiState.orderEstimationUI?.receiverName,
                    packageWeight = transportUiState.orderEstimationUI?.packageWeight,
                    packageType = transportUiState.orderEstimationUI?.packageType,
                    promoId = transportUiState.usePromo?.id.orEmpty(),
                    insurance = transportUiState.orderEstimationUI?.insurance,
                )
            ).catch {
                transportUiState =
                    transportUiState.copy(isLoading = false, emptyDriver = true)
            }.collect {
                transportUiState = transportUiState.copy(
                    searchRequestInProcess = it
                )
                getActiveOrderStatusIntervally()
            }
        }
    }

    private fun getDetailBasedOnOrderStatus(orderSpec: OrderDetailSpec) {
        when (orderSpec.orderStatus) {
            TransportRequestType.REQUESTING -> {
                if (transportUiState.nearbyDrivers.isEmpty()) {
                    getNearbyDrivers(orderSpec.pickupLatitude, orderSpec.pickupLongitude)
                }
                transportUiState = transportUiState.copy(
                    searchRequestInProcess = orderSpec,
                    isLoading = true,
                    showOriginDestinationCard = false,
                    orderEstimationUI = null
                )
            }
            TransportRequestType.ACCEPTED -> {
                transportUiState = transportUiState.copy(nearbyDrivers = emptyList())
                if (transportUiState.orderOnGoingUI == null) {
                    getMapDirection(
                        "${orderSpec.driverLatitude},${orderSpec.driverLongitude}",
                        "${orderSpec.pickupLatitude},${orderSpec.pickupLongitude}",
                        orderSpec
                    )
                } else {
                    transportUiState =
                        transportUiState.copy(
                            orderOnGoingUI = orderSpec,
                            searchRequestInProcess = null,
                            orderEstimationUI = null,
                            driverBearing = Event(orderSpec.driverBearing),
                            driverLatLng = Event(
                                LatLng(
                                    orderSpec.driverLatitude,
                                    orderSpec.driverLongitude
                                )
                            )
                        )
                }
            }
            TransportRequestType.REJECTED -> Unit
            TransportRequestType.ONROUTE -> {
                transportUiState = transportUiState.copy(nearbyDrivers = emptyList())
                if (transportUiState.orderOnGoingUI == null || transportUiState.orderOnGoingUI?.orderStatus == TransportRequestType.ACCEPTED) {
                    getMapDirection(
                        "${orderSpec.driverLatitude},${orderSpec.driverLongitude}",
                        "${orderSpec.destinationLatitude},${orderSpec.destinationLongitude}",
                        orderSpec
                    )
                } else {
                    transportUiState =
                        transportUiState.copy(
                            orderOnGoingUI = orderSpec,
                            driverBearing = Event(orderSpec.driverBearing),
                            driverLatLng = Event(
                                LatLng(
                                    orderSpec.driverLatitude,
                                    orderSpec.driverLongitude
                                )
                            )
                        )
                }

            }
            TransportRequestType.ARRIVED -> {
                driverPoller = false
                transportUiState = transportUiState.copy(
                    isLoading = false,
                    orderArrivedUI = orderSpec,
                    openBottomSheet = Event(true),
                    orderOnGoingUI = null,
                    removePolyline = Event(Unit),
                    orderEstimationUI = null
                )
            }
            else -> Unit
        }
    }

    fun retryOrderPage(foodOrders: SearchDriverRequestSpec? = null) {
        when {
            transportUiState.showOriginDestinationCard ->
                transportUiState =
                    transportUiState.copy(isShowErrorUI = false, showOriginDestinationCard = true)
            else -> {
                initUserOrderStatus(foodOrders)
            }
        }
    }

    fun updateOriginInactiveUi(spec: PlaceDetailSpec) {
        transportUiState = transportUiState.copy(originFormPlaceDetail = spec)
        calculateDirectionFromLatestLocationSearch()
    }

    fun updateDestinationInactiveUi(spec: PlaceDetailSpec) {
        transportUiState = transportUiState.copy(destinationFormPlaceDetail = spec)
        calculateDirectionFromLatestLocationSearch()
    }

    fun updateUsePromo(spec: PromoSpec) {
        transportUiState = transportUiState.copy(usePromo = spec)
    }

    private fun calculateDirectionFromLatestLocationSearch() {
        val originLatLng: LatLng? = transportUiState.originFormPlaceDetail?.let {
            LatLng(it.locationLatLng.latitude, it.locationLatLng.longitude)
        }
        val destinationLatLng: LatLng? = transportUiState.destinationFormPlaceDetail?.let {
            LatLng(it.locationLatLng.latitude, it.locationLatLng.longitude)
        }

        // only update origin marker if destination is null otherwise we should use directions api
        // to put origin marker
        if (originLatLng != null && destinationLatLng == null) {
            updateMapLatLng(originLatLng)
        }

        // call direction api if both latlng is available
        if (originLatLng != null && destinationLatLng != null) {
            val origin = "${originLatLng.latitude},${originLatLng.longitude}"
            val destination = "${destinationLatLng.latitude},${destinationLatLng.longitude}"
            getMapDirection(origin, destination)
            getNearbyDrivers(originLatLng.latitude, originLatLng.longitude)
        }
    }

    fun sendRating(rating: Int, feedback: String) {
        viewModelScope.launch(Dispatchers.IO) {
            transportUiState.orderArrivedUI?.let { spec ->
                orderRepository.sendRating(spec.requestId, feedback, rating)
                    .catch {
                        transportUiState = transportUiState.copy(isLoading = false)
                    }.collect {
                        transportUiState =
                            transportUiState.copy(
                                isLoading = false,
                                successRatingDriver = true
                            )
                        if (spec.orderType == DriverMitraType.FOOD) {
                            if (transportUiState.successRatingResto == true) {
                                setOrderFinished(spec.requestId)
                            }
                        } else {
                            setOrderFinished(spec.requestId)
                        }
                    }
            }
        }
    }

    fun sendRatingResto(rating: Int, feedback: String) {
        viewModelScope.launch(Dispatchers.IO) {
            transportUiState.orderArrivedUI?.let { spec ->
                orderRepository.sendRatingResto(spec.requestId, feedback, rating)
                    .catch {
                        transportUiState = transportUiState.copy(isLoading = false)
                    }.collect {
                        transportUiState =
                            transportUiState.copy(
                                isLoading = false,
                                successRatingDriver = true
                            )
                        if (transportUiState.successRatingDriver == true && spec.orderType == DriverMitraType.FOOD) {
                            setOrderFinished(spec.requestId)
                        }
                    }
            }
        }
    }

    fun cancelOrderTrip(id: String) {
        transportUiState = transportUiState.copy(
            isLoading = true,
        )
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.updateOrderStatus(id, TransportRequestType.CANCELED)
                .catch {
                    transportUiState = transportUiState.copy(
                        isLoading = false
                    )
                }
                .collect {
                    // reset everything and show origin card
                    transportUiState = TransportUiStateSpec(
                        showOriginDestinationCard = true,
                        removePolyline = Event(Unit)
                    )
                }
        }
    }

    private fun DriverMitraType?.getVehicleType(): String = when (this) {
        DriverMitraType.CAR -> "car"
        else -> "motorcycle"
    }

    private fun OrderRequestType.getVehicleType(): String = when (this) {
        OrderRequestType.CAR -> "car"
        else -> "motorcycle"
    }

    private suspend fun setOrderFinished(id: String) {
        orderRepository.updateOrderStatus(id, TransportRequestType.FINISHED)
            .catch {
                transportUiState =
                    transportUiState.copy(isLoading = false)
            }
            .collect {
                transportUiState =
                    transportUiState.copy(
                        isLoading = false,
                        orderFinishedUI = true,
                        orderOnGoingUI = null,
                        orderArrivedUI = null,
                        searchRequestInProcess = null,
                        destinationFormPlaceDetail = null,
                        originFormPlaceDetail = null,
                        successRatingDriver = false,
                        successRatingResto = false
                    )
            }
    }

    fun showPaymentMethod() {
        transportUiState = transportUiState.copy(isShoPaymentMethodUI = true)
    }

    fun changePaymentMethod(paymentMethod: String, pinCode: String? = null) {
        transportUiState.orderEstimationUI?.let { spec ->
            val newOrder = spec.copy(paymentMethod = paymentMethod, pin = pinCode)
            transportUiState =
                transportUiState.copy(isShoPaymentMethodUI = false, orderEstimationUI = newOrder)
        }
    }

    data class TransportUiStateSpec(
        val isLoading: Boolean = false,
        val orderArrivedUI: OrderDetailSpec? = null,
        val orderFinishedUI: Boolean = false,
        val orderEstimationUI: OrderEstimationResponseSpec? = null,
        val orderOnGoingUI: OrderDetailSpec? = null,
        val orderStatus: Pair<TransportRequestType, String>? = null,
        val originFormPlaceDetail: PlaceDetailSpec? = null,
        val destinationFormPlaceDetail: PlaceDetailSpec? = null,
        val showOriginDestinationCard: Boolean = false,
        val mapPolyline: Event<LocationDirectionSpec>? = null,
        val userInfo: UserInfo? = null,
        val originMarker: Event<LatLng>? = null,
        val openBottomSheet: Event<Boolean>? = null,
        val emptyDriver: Boolean = false,
        val errorRestaurantRequestOrder: String = "",
        val otherOrderTypeActive: String? = null,
        val driverLatLng: Event<LatLng>? = null,
        val isShowErrorUI: Boolean = false,
        val searchRequestInProcess: OrderDetailSpec? = null,
        val isShoPaymentMethodUI: Boolean = false,
        val removePolyline: Event<Unit>? = null,
        val driverBearing: Event<Float>? = null,
        val nearbyDrivers: List<DriverInfo> = emptyList(),
        val tSendSpec: FormDetailSpec? = null,
        val usePromo: PromoSpec? = null,
        val notes: String = "",
        val successRatingDriver: Boolean? = false,
        val successRatingResto: Boolean? = false
    )
}