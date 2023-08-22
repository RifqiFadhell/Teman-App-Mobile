@file:OptIn(ExperimentalTextApi::class)

package id.teman.app.ui.food.summary

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.ButtonContinue
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.TopBar
import id.teman.app.common.convertToRupiah
import id.teman.app.common.orFalse
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.order.BreakdownType
import id.teman.app.domain.model.order.FoodOrderItemRequestSpec
import id.teman.app.domain.model.order.FoodOrderRequestSpec
import id.teman.app.domain.model.order.OrderPaymentSpec
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.domain.model.promo.PromoFeature
import id.teman.app.domain.model.restaurant.MenuRestaurant
import id.teman.app.domain.usecase.order.SearchDriverRequestSpec
import id.teman.app.ui.bill.list.DetailItemRow
import id.teman.app.ui.destinations.SearchLocationScreenDestination
import id.teman.app.ui.destinations.SelectPromoScreenDestination
import id.teman.app.ui.food.FoodMainViewModel
import id.teman.app.ui.myaccount.CardItem
import id.teman.app.ui.ordermapscreen.initiate.send.PromoSpec
import id.teman.app.ui.ordermapscreen.inprogress.bottomsheet.BottomSheetPaymentMethod
import id.teman.app.ui.ordermapscreen.inprogress.bottomsheet.PaymentItemRow
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import id.teman.coreui.typography.UiFont.poppinsP2SemiBold
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Destination
fun SummaryFoodScreen(
    navigator: DestinationsNavigator,
    spec: SummaryFoodSpec,
    resultSearchLocation: ResultRecipient<SearchLocationScreenDestination, PlaceDetailSpec>,
    resultSelectPromo: ResultRecipient<SelectPromoScreenDestination, PromoSpec>,
    foodViewModel: FoodMainViewModel
) {
    val foodUiState = foodViewModel.foodUiState
    val locationUiState = foodViewModel.foodMainUiState

    var selectedLocationAddress by rememberSaveable { mutableStateOf(locationUiState.userLocation) }
    var selectedLocationLatLng by rememberSaveable { mutableStateOf(locationUiState.currentUserLocationLatLng) }
    var isApiCalled by rememberSaveable { mutableStateOf(false) }
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        if (!isApiCalled) {
            foodViewModel.getUserProfile()
            foodViewModel.getEstimation(
                selectedLocationAddress,
                selectedLocationLatLng
            )
            isApiCalled = true
        }
    }

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    var paymentMethod by rememberSaveable { mutableStateOf("cash") }
    var walletPinCode by rememberSaveable { mutableStateOf("") }

    resultSearchLocation.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                selectedLocationAddress = it.value.shortLocationAddress
                selectedLocationLatLng = it.value.locationLatLng
                foodViewModel.getEstimation(it.value.shortLocationAddress, it.value.locationLatLng)
            }
        }
    }
    resultSelectPromo.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                foodViewModel.updateUsePromo(it.value)
                foodViewModel.getEstimation(
                    selectedLocationAddress,
                    selectedLocationLatLng,
                    promoId = it.value.id
                )
            }
        }
    }

    BackHandler {
        if (modalSheetState.isVisible) {
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
            BottomSheetPaymentMethod(
                totalPrice = foodUiState.totalPrice,
                statusPin = foodUiState.userInfo?.pinStatus.orFalse()
            ) { walletType, pinCode ->
                paymentMethod = walletType
                walletPinCode = pinCode
                 coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
        }, content = {
            Scaffold(
                topBar = {
                    TopBar(title = spec.nameRestaurant) {
                        navigator.popBackStack()
                    }
                }, content = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (foodUiState.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = UiColor.primaryRed500
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.padding(
                                    bottom = Theme.dimension.size_80dp,
                                    start = Theme.dimension.size_16dp,
                                    end = Theme.dimension.size_16dp,
                                )
                            ) {
                                item {
                                    Spacer(Modifier.height(Theme.dimension.size_20dp))
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Antar ke Alamat", style = poppinsP2SemiBold)
                                        Text(
                                            "Ubah",
                                            style = UiFont.poppinsP2Medium.copy(color = UiColor.tertiaryBlue500),
                                            modifier = Modifier.clickable {
                                                navigator.navigate(
                                                    SearchLocationScreenDestination(
                                                        "Mau antar kemana?",
                                                        defaultLatLng = selectedLocationLatLng
                                                    )
                                                )
                                            }
                                        )
                                    }
                                    Spacer(Modifier.height(Theme.dimension.size_20dp))
                                }
                                item {
                                    Row(
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        TemanCircleButton(
                                            icon = R.drawable.ic_location,
                                            circleBackgroundColor = Color.Transparent,
                                            circleModifier = Modifier
                                                .size(Theme.dimension.size_48dp)
                                                .border(
                                                    BorderStroke(1.dp, color = UiColor.neutral100),
                                                    shape = CircleShape
                                                ),
                                            iconModifier = Modifier
                                                .size(Theme.dimension.size_24dp)
                                        )
                                        Column {
                                            Text(
                                                selectedLocationAddress,
                                                style = poppinsP2SemiBold.copy(color = UiColor.neutral900),
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .padding(start = Theme.dimension.size_16dp)
                                            )
                                            Text(
                                                selectedLocationAddress,
                                                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .padding(start = Theme.dimension.size_16dp),
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                                item {
                                    Spacer(Modifier.height(Theme.dimension.size_36dp))
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Daftar Pesanan", style = poppinsP2SemiBold)
                                        Text(
                                            "+Tambah",
                                            style = UiFont.poppinsP2Medium.copy(color = UiColor.tertiaryBlue500),
                                            modifier = Modifier.clickable {
                                                navigator.popBackStack()
                                            }
                                        )
                                    }
                                    Spacer(Modifier.height(Theme.dimension.size_16dp))
                                }
                                val list = foodUiState.listMenu.orEmpty()
                                items(list) { productItem ->
                                    if (productItem.qty > 0) {
                                        ItemMenuSummary(
                                            modifier = Modifier.padding(
                                                horizontal = Theme.dimension.size_0dp,
                                                vertical = Theme.dimension.size_8dp
                                            ),
                                            spec = productItem
                                        )
                                    }
                                }
                                item {
                                    Text(
                                        modifier = Modifier.padding(top = Theme.dimension.size_16dp),
                                        text = "Rincian Pesanan",
                                        style = poppinsP2SemiBold.copy(
                                            platformStyle = PlatformTextStyle(
                                                includeFontPadding = false
                                            )
                                        )
                                    )
                                    Spacer(Modifier.height(Theme.dimension.size_20dp))
                                    foodUiState.paymentSpec.map { item -> PaymentItemRow(item = item) }
                                    if (foodUiState.totalDiscount > 0.0) {
                                        PaymentItemRow(OrderPaymentSpec("Diskon Makanan", foodUiState.totalDiscount, BreakdownType.DISCOUNT))
                                    }
                                    DetailItemRow(title = "Total", value = foodUiState.totalPriceServer.convertToRupiah())
                                }
                                item {
                                    Spacer(Modifier.height(Theme.dimension.size_20dp))
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Metode Pembayaran", style = poppinsP2SemiBold)
                                        Text(
                                            "Ubah",
                                            style = UiFont.poppinsP2Medium.copy(color = UiColor.tertiaryBlue500),
                                            modifier = Modifier.clickable {
                                                coroutineScope.launch {
                                                    modalSheetState.show()
                                                }
                                            }
                                        )
                                    }
                                    Spacer(Modifier.height(Theme.dimension.size_20dp))
                                }
                                item {
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.padding(bottom = Theme.dimension.size_12dp)
                                    ) {
                                        TemanCircleButton(
                                            icon = if (paymentMethod == "cash") R.drawable.ic_cash_payment else R.drawable.ic_wallet,
                                            circleBackgroundColor = Color.Transparent,
                                            circleModifier = Modifier
                                                .size(Theme.dimension.size_48dp)
                                                .border(
                                                    BorderStroke(1.dp, color = UiColor.neutral100),
                                                    shape = CircleShape
                                                ),
                                            iconModifier = Modifier
                                                .size(Theme.dimension.size_24dp)
                                        )
                                        Column {
                                            Text(
                                                paymentMethod.replaceFirstChar { it.uppercase() },
                                                style = poppinsP2SemiBold.copy(color = UiColor.neutral900),
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .padding(start = Theme.dimension.size_16dp)
                                            )
                                            Text(
                                                if (paymentMethod == "cash") "Bayar di tempat tujuan" else "Bayar secara instan pakai T-Kantong",
                                                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .padding(start = Theme.dimension.size_16dp),
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("Pilih Promo Tersedia", style = poppinsP2SemiBold)
                                    }
                                    CardItem(
                                        modifier = Modifier
                                            .padding(top = Theme.dimension.size_16dp),
                                        title = foodUiState.usePromo?.titlePromo.orEmpty().ifEmpty { "Pilih Promo" },
                                        icon = R.drawable.ic_promo
                                    ) {
                                        navigator.navigate(SelectPromoScreenDestination(type = PromoFeature.FOOD))
                                    }
                                }
                            }
                        }
                    }
                }, bottomBar = {
                    if (foodUiState.totalPrice > 0.0) {
                        ButtonContinue(
                            totalPrice = foodUiState.totalPriceServer,
                            discount = foodUiState.totalDiscount,
                            title = "Order T-Food"
                        ) {
                            foodUiState.detailRestaurant?.let { restaurant ->
                                val listMenu = ArrayList<MenuRestaurant>()
                                foodUiState.listMenu?.forEach { item ->
                                    if (item.qty > 0) {
                                        listMenu.add(item)
                                    }
                                }
                                foodViewModel.redirectToMap(
                                    SearchDriverRequestSpec(
                                        orderType = OrderRequestType.FOOD,
                                        originLatLng = restaurant.latLng,
                                        originAddress = restaurant.address,
                                        destinationLatLng = selectedLocationLatLng,
                                        destinationAddress = selectedLocationAddress,
                                        paymentMethod = paymentMethod,
                                        pin = walletPinCode,
                                        receiverName = foodUiState.userInfo?.name.orEmpty(),
                                        restaurantOrder = FoodOrderRequestSpec(
                                            restaurantId = restaurant.id,
                                            orderedItems = listMenu.map {
                                                FoodOrderItemRequestSpec(
                                                    productId = it.id,
                                                    quantity = it.qty,
                                                    note = it.notes
                                                )
                                            }
                                        ), promoId = foodUiState.usePromo?.id.orEmpty()
                                    )
                                )
                            }
                        }
                    }
                })
        }
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ItemMenuSummary(
    modifier: Modifier,
    spec: MenuRestaurant
) {
    Card(
        modifier = modifier,
        elevation = Theme.dimension.size_1dp
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimension.size_16dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        spec.name,
                        style = poppinsP2SemiBold.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        spec.description,
                        style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row {
                        Text(
                            if (spec.strikeTrough) spec.promoPrice.convertToRupiah() else spec.price.convertToRupiah(),
                            style = poppinsP2SemiBold.copy(color = UiColor.tertiaryBlue500),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (spec.strikeTrough) Text(
                            spec.price.convertToRupiah(),
                            style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral200),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
                GlideImage(
                    imageModel = spec.productPhoto,
                    modifier = Modifier
                        .size(Theme.dimension.size_64dp)
                        .clip(RoundedCornerShape(Theme.dimension.size_6dp)),
                    failure = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_no_image),
                            contentDescription = "failed"
                        )
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Theme.dimension.size_16dp,
                        end = Theme.dimension.size_42dp,
                        top = Theme.dimension.size_4dp,
                        bottom = Theme.dimension.size_8dp
                    )
            ) {
                Text("Jumlah", style = UiFont.poppinsP2Medium)
                Text(
                    spec.qty.toString(), style = poppinsP2SemiBold.copy(
                        color = UiColor.neutral900
                    )
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(Theme.dimension.size_4dp),
                        color = Color.Transparent
                    ),
                value = spec.notes.ifEmpty { "Catatan Makanan" },
                enabled = false,
                placeholder = {

                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = UiColor.neutral100,
                    cursorColor = UiColor.black,
                    unfocusedBorderColor = UiColor.neutral100
                ),
                onValueChange = {

                },
            )
        }
    }
}