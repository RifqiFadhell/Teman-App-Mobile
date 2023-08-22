package id.teman.app.ui.ordermapscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.orFalse
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.domain.model.promo.PromoFeature
import id.teman.app.domain.usecase.order.SearchDriverRequestSpec
import id.teman.app.ui.destinations.ChatScreenDestination
import id.teman.app.ui.destinations.OrderSendInitiateFormScreenDestination
import id.teman.app.ui.destinations.SearchLocationScreenDestination
import id.teman.app.ui.destinations.SelectPromoScreenDestination
import id.teman.app.ui.ordermapscreen.common.ErrorWidgetUI
import id.teman.app.ui.ordermapscreen.done.OrderFinished
import id.teman.app.ui.ordermapscreen.initiate.InactiveOrderUI
import id.teman.app.ui.ordermapscreen.initiate.send.FormDetailSpec
import id.teman.app.ui.ordermapscreen.initiate.send.PromoSpec
import id.teman.app.ui.ordermapscreen.inprogress.EmptyDriverUi
import id.teman.app.ui.ordermapscreen.inprogress.InProgressButtonContent
import id.teman.app.ui.ordermapscreen.inprogress.LoadingCard
import id.teman.app.ui.ordermapscreen.inprogress.OrderDestinationHeader
import id.teman.app.ui.ordermapscreen.inprogress.bottomsheet.BottomSheetOrderArrivedDetail
import id.teman.app.ui.ordermapscreen.inprogress.bottomsheet.BottomSheetOrderEstimationDetail
import id.teman.app.ui.ordermapscreen.inprogress.bottomsheet.BottomSheetPaymentMethod
import id.teman.app.ui.ordermapscreen.map.MapScreen
import id.teman.app.ui.sharedviewmodel.MainViewModel
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Destination
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderProcessScreen(
    navigator: DestinationsNavigator,
    resultSearchLocation: ResultRecipient<SearchLocationScreenDestination, PlaceDetailSpec>,
    resultSendDetailForm: ResultRecipient<OrderSendInitiateFormScreenDestination, FormDetailSpec>,
    resultSelectPromo: ResultRecipient<SelectPromoScreenDestination, PromoSpec>,
    orderRequestType: OrderRequestType,
    foodOrders: SearchDriverRequestSpec? = null,
    viewModel: TransportViewModel = hiltViewModel(),
    mainViewModel: MainViewModel,
) {
    val uiState = viewModel.transportUiState
    val currentLocationUiState = mainViewModel.locationUiState

    var isFirstTimeCalled by rememberSaveable { mutableStateOf(true) }

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        if (isFirstTimeCalled) {
            viewModel.setOrderType(orderRequestType)
            currentLocationUiState.placeDetailSpec?.let {
                viewModel.updateOriginInactiveUi(it)
            }
            isFirstTimeCalled = false
        }
        viewModel.initUserOrderStatus(foodOrders)
        currentLocationUiState.destinationFormPlaceDetail.let {
            if (it != null) {
                viewModel.updateDestinationInactiveUi(it)
            }
        }
    }
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_PAUSE) {
        viewModel.stopEmitData()
    }

    var isSearchOriginAddress by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = { false }
    )
    resultSearchLocation.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                if (isSearchOriginAddress) {
                    viewModel.updateOriginInactiveUi(it.value)
                } else {
                    viewModel.updateDestinationInactiveUi(it.value)
                }
            }
        }
    }

    resultSelectPromo.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                viewModel.updateUsePromo(it.value)
                if (orderRequestType == OrderRequestType.SEND) {
                    coroutineScope.launch {
                        modalSheetState.hide()
                    }
                    viewModel.getTSendEstimationDetail()
                } else {
                    viewModel.getOrderEstimationDetail()
                }
            }
        }
    }

    resultSendDetailForm.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                viewModel.setFormSendDetail(
                    it.value
                )
            }
        }
    }

    LaunchedEffect(key1 = uiState.openBottomSheet, block = {
        uiState.openBottomSheet?.consumeOnce { shouldOpen ->
            launch {
                delay(400)
                if (shouldOpen) {
                    modalSheetState.show()
                } else {
                    modalSheetState.hide()
                }
            }
        }
    })

    BackHandler {
        if (uiState.orderEstimationUI != null && uiState.isShoPaymentMethodUI && modalSheetState.isVisible) {
            coroutineScope.launch { modalSheetState.hide() }
        } else {
            navigator.popBackStack()
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        sheetElevation = Theme.dimension.size_8dp,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            if (uiState.orderEstimationUI != null) {
                if (uiState.isShoPaymentMethodUI) {
                    BottomSheetPaymentMethod(
                        totalPrice = uiState.orderEstimationUI.totalPrice,
                        uiState.userInfo?.pinStatus == true
                    ) { walletType, pinCode ->
                        viewModel.changePaymentMethod(walletType, pinCode)
                    }
                } else {
                    BottomSheetOrderEstimationDetail(
                        uiState.orderEstimationUI,
                        onChangePaymentMethod = {
                            viewModel.showPaymentMethod()
                        },
                        onContinue = {
                            coroutineScope.launch {
                                viewModel.searchDriver()
                                modalSheetState.hide()
                            }
                        },
                        type = orderRequestType,
                        titlePromo = uiState.usePromo?.titlePromo.orEmpty()
                    ) {
                        navigator.navigate(SelectPromoScreenDestination(
                            type = when(orderRequestType) {
                                OrderRequestType.BIKE -> PromoFeature.BIKE
                                OrderRequestType.CAR -> PromoFeature.CAR
                                OrderRequestType.SEND -> PromoFeature.SEND
                                OrderRequestType.FOOD -> PromoFeature.FOOD
                            }
                        ))
                    }
                }
            } else if (uiState.orderArrivedUI != null) {
                BottomSheetOrderArrivedDetail(
                    enableButtonRatingDriver = !uiState.successRatingDriver.orFalse(),
                    enableButtonRatingResto = !uiState.successRatingResto.orFalse(),
                    item = uiState.orderArrivedUI,
                    onRatingClick = { rating, feedback ->
                        viewModel.sendRating(rating, feedback)
                    }, onRatingRestoClick = { rating, feedback ->
                        viewModel.sendRatingResto(rating, feedback)
                    })
            } else {
                Box(modifier = Modifier.height(Theme.dimension.size_1dp))
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                MapScreen(
                    viewModel,
                    currentLocationUiState.currentUserLocationLatLng,
                    orderRequestType == OrderRequestType.CAR
                )
                when {
                    uiState.showOriginDestinationCard -> {
                        InactiveOrderUI(
                            isContinueButtonActive = uiState.originFormPlaceDetail != null && uiState.destinationFormPlaceDetail != null,
                            onContinueButton = {
                                if (orderRequestType == OrderRequestType.SEND) {
                                    navigator.navigate(
                                        OrderSendInitiateFormScreenDestination(
                                            origin = uiState.originFormPlaceDetail!!,
                                            destination = uiState.destinationFormPlaceDetail!!
                                        )
                                    )
                                } else {
                                    viewModel.getOrderEstimationDetail(it)
                                }
                            },
                            onOriginClick = {
                                isSearchOriginAddress = true
                                navigator.navigate(
                                    SearchLocationScreenDestination(
                                        it,
                                        mainViewModel.locationUiState.currentUserLocationLatLng
                                    )
                                )
                            },
                            onDestinationClick = {
                                isSearchOriginAddress = false
                                navigator.navigate(
                                    SearchLocationScreenDestination(
                                        it,
                                        mainViewModel.locationUiState.currentUserLocationLatLng
                                    )
                                )
                            },
                            originDetailSpec = uiState.originFormPlaceDetail,
                            destinationDetailSpec = uiState.destinationFormPlaceDetail,
                            orderRequestType = viewModel.orderType.value
                        )
                    }
                    uiState.orderOnGoingUI != null -> {
                        InProgressButtonContent(
                            orderItem = uiState.orderOnGoingUI,
                            onChatClicked = {
                                navigator.navigate(
                                    ChatScreenDestination(
                                        item = uiState.orderOnGoingUI
                                    )
                                )
                            },
                        )
                        OrderDestinationHeader(
                            item = uiState.orderOnGoingUI
                        )
                    }
                    uiState.orderFinishedUI -> {
                        coroutineScope.launch {
                            modalSheetState.hide()
                        }
                        OrderFinished {
                            navigator.popBackStack()
                        }
                    }
                    uiState.emptyDriver -> {
                        EmptyDriverUi(uiState.errorRestaurantRequestOrder) {
                            if (foodOrders != null) {
                                viewModel.onRestaurantOrders(foodOrders)
                            } else {
                                viewModel.searchDriver()
                            }
                        }
                    }
                    uiState.isLoading -> LoadingCard(uiState.searchRequestInProcess) {
                        viewModel.cancelOrderTrip(it)
                    }
                    uiState.otherOrderTypeActive != null -> BlockedOrderUI(orderType = uiState.otherOrderTypeActive)
                    uiState.isShowErrorUI -> ErrorWidgetUI {
                        viewModel.retryOrderPage(foodOrders)
                    }
                }
                if (mainViewModel.locationUiState.loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = UiColor.primaryRed500
                        )
                    }
                }
            }
        }
    )
}