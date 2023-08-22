package id.teman.app.ui.home

import android.annotation.SuppressLint
import id.teman.app.R as rAppModule
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.google.android.gms.maps.model.LatLng
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.PromoContent
import id.teman.app.common.QuickMenuItem
import id.teman.app.common.TemanSectionTitleIcon
import id.teman.app.common.noRippleClickable
import id.teman.app.domain.model.home.QuickMenuModel
import id.teman.app.domain.model.home.QuickMenuUIModel
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.domain.model.restaurant.ItemRestaurantModel
import id.teman.app.ui.destinations.BillScreenDestination
import id.teman.app.ui.destinations.FoodNavScreenDestination
import id.teman.app.ui.destinations.MyAccountScreenDestination
import id.teman.app.ui.destinations.OrderProcessScreenDestination
import id.teman.app.ui.destinations.PinWalletScreenDestination
import id.teman.app.ui.destinations.SearchLocationScreenDestination
import id.teman.app.ui.destinations.SearchScreenDestination
import id.teman.app.ui.destinations.WalletScreenDestination
import id.teman.app.ui.destinations.WebViewScreenDestination
import id.teman.app.ui.food.FoodScreenSpec
import id.teman.app.ui.sharedviewmodel.MainViewModel
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.wallet.pin.PinScreenSpec
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@RootNavGraph(true)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
    searchRecipient: ResultRecipient<SearchLocationScreenDestination, PlaceDetailSpec>,
    searchRecipientLocation: ResultRecipient<SearchScreenDestination, PlaceDetailSpec>,
) {
    val uiState = mainViewModel.locationUiState
    val homeUiState = homeViewModel.homeUiState

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        homeViewModel.getRemoteUserProfile()
        homeViewModel.getWalletBalance()
        mainViewModel.getListRestaurantNearby()
    }
    searchRecipient.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val place = it.value
                mainViewModel.changeHomeLocationAddress(place)
            }
        }
    }

    searchRecipientLocation.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val place = it.value
                mainViewModel.updateDestination(place)
            }
        }
    }
    Scaffold(
        modifier = Modifier.padding(
            bottom = Theme.dimension.size_24dp
        )
    ) {
        Box {
            LazyColumn(
                modifier = Modifier.padding(

                )
            ) {
                item {
                    HeaderSection(navigator,
                        uiState.userLocation,
                        balance = homeUiState.balance,
                        point = homeUiState.point,
                        profile = homeUiState.imageProfile,
                        onClick = {
                            navigator.navigate(
                                SearchLocationScreenDestination(
                                    defaultLatLng = mainViewModel.locationUiState.currentUserLocationLatLng
                                )
                            )
                        },
                        onWalletClick = {
                            if (homeViewModel.isPinWasSet()) navigator.navigate(
                                WalletScreenDestination
                            ) else navigator.navigate(
                                PinWalletScreenDestination(
                                    PinScreenSpec(useFor = "update")
                                )
                            )
                        })
                }
                item {
                    QuickMenuContent(
                        userName = homeUiState.userName.orEmpty(),
                        menus = homeUiState.listMenus, onClick = {
                            when (it.key) {
                                QuickMenuUIModel.Teman_Bike.key -> navigator.navigate(
                                    OrderProcessScreenDestination(
                                        orderRequestType = OrderRequestType.BIKE
                                    )
                                )
                                QuickMenuUIModel.Teman_Car.key -> navigator.navigate(
                                    OrderProcessScreenDestination(
                                        orderRequestType = OrderRequestType.CAR
                                    )
                                )
                                QuickMenuUIModel.Teman_Food.key -> {
                                    navigator.navigate(FoodNavScreenDestination(FoodScreenSpec()))
                                }
                                QuickMenuUIModel.Teman_Bills.key -> {
                                    navigator.navigate(BillScreenDestination)
                                }
                                QuickMenuUIModel.Teman_Send.key -> navigator.navigate(
                                    OrderProcessScreenDestination(
                                        orderRequestType = OrderRequestType.SEND
                                    )
                                )
                                QuickMenuUIModel.Teman_Other.key -> Unit
                            }
                        })
                }
                item {
                    homeUiState.listBanners?.let { banners ->
                        PromoContent(banners) { url, title ->
                            navigator.navigate(WebViewScreenDestination(url = url, title = title))
                        }
                    }
                }
                if (!uiState.listRestaurant.isNullOrEmpty()) {
                    item {
                        TemanSectionTitleIcon(
                            title = "Teman Food",
                            icon = rAppModule.drawable.ic_teman_food,
                            modifier = Modifier.padding(
                                top = Theme.dimension.size_32dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            )
                        )
                        NearbyFoodTitle(navigator, uiState.currentUserLocationLatLng)
                        LazyRow(modifier = Modifier.padding(top = Theme.dimension.size_20dp)) {
                            val list = uiState.listRestaurant.take(5)
                            items(list.size) { index ->
                                NearbyFoodList(
                                    navigator,
                                    list[index],
                                    uiState.currentUserLocationLatLng
                                )
                            }
                        }
                    }
                }
            }
            if (uiState.loading) {
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
}

@Composable
private fun HeaderSection(
    navigator: DestinationsNavigator,
    street: String,
    balance: String,
    point: String,
    profile: String,
    onClick: () -> Unit,
    onWalletClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(UiColor.customRed)
            .padding(
                top = Theme.dimension.size_24dp,
                start = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp,
                bottom = Theme.dimension.size_8dp
            )
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.65f)
            ) {
                Text("Lokasi Anda", style = UiFont.poppinsP1Medium.copy(color = UiColor.whiteLow))
                Row(modifier = Modifier.clickable {
                    onClick()
                }) {
                    Text(
                        street.ifEmpty { "Alamat tidak terdeteksi" },
                        style = UiFont.poppinsP2SemiBold.copy(color = UiColor.white)
                    )
                    GlideImage(
                        imageModel = rAppModule.drawable.ic_dropdown,
                        modifier = Modifier.size(Theme.dimension.size_16dp),
                        imageOptions = ImageOptions(colorFilter = ColorFilter.tint(UiColor.white))
                    )
                }
            }
            Column(modifier = Modifier
                .background(
                    UiColor.white,
                    shape = RoundedCornerShape(Theme.dimension.size_12dp)
                )
                .padding(
                    vertical = Theme.dimension.size_6dp,
                    horizontal = Theme.dimension.size_12dp
                )
                .clickable {
                    onWalletClick()
                }
                .fillMaxWidth(4.2f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    GlideImage(
                        rAppModule.drawable.ic_wallet,
                        modifier = Modifier.size(Theme.dimension.size_16dp),
                        imageOptions = ImageOptions(colorFilter = ColorFilter.tint(UiColor.customRed))
                    )
                    Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                    Text(balance, style = UiFont.poppinsCaptionSemiBold)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    GlideImage(
                        rAppModule.drawable.ic_giftcard,
                        modifier = Modifier.size(Theme.dimension.size_16dp),
                        imageOptions = ImageOptions(colorFilter = ColorFilter.tint(UiColor.customRed))
                    )
                    Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                    Text("$point Poin", style = UiFont.poppinsCaptionSemiBold)
                }
            }
        }
        Spacer(modifier = Modifier.height(Theme.dimension.size_24dp))
        Row {
            Box(
                Modifier
                    .weight(9f)
                    .border(
                        width = Theme.dimension.size_2dp,
                        color = UiColor.neutral100,
                        shape = RoundedCornerShape(Theme.dimension.size_30dp)
                    )
                    .padding(
                        vertical = Theme.dimension.size_16dp,
                        horizontal = Theme.dimension.size_24dp
                    )
                    .noRippleClickable {
                        navigator.navigate(SearchScreenDestination)
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlideImage(
                        rAppModule.drawable.ic_search,
                        modifier = Modifier.size(Theme.dimension.size_24dp),
                        imageOptions = ImageOptions(colorFilter = ColorFilter.tint(UiColor.white))
                    )
                    Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
                    Text(
                        "Cari Layanan TEMAN...",
                        style = UiFont.poppinsP2Medium.copy(color = UiColor.white)
                    )
                }
            }
            Spacer(Modifier.width(Theme.dimension.size_16dp))
            GlideImage(
                imageModel = profile,
                modifier = Modifier
                    .size(Theme.dimension.size_48dp)
                    .clip(CircleShape)
                    .noRippleClickable {
                        navigator.navigate(MyAccountScreenDestination)
                    },
                failure = {
                    Image(
                        painter = painterResource(id = rAppModule.drawable.ic_person_mamoji),
                        contentDescription = "failed"
                    )
                }
            )
        }
    }
}

@Composable
private fun QuickMenuContent(
    userName: String,
    onClick: (QuickMenuModel) -> Unit,
    menus: List<QuickMenuModel>?
) {
    val listState = rememberLazyGridState()
    Column(
        modifier = Modifier
    ) {
        Column(
            modifier = Modifier
                .height(Theme.dimension.size_20dp)
                .fillMaxWidth()
                .background(color = UiColor.customRed)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        shape = RoundedCornerShape(
                            topStart = Theme.dimension.size_16dp,
                            topEnd = Theme.dimension.size_16dp
                        ), color = UiColor.white
                    )
            )
        }
        Text(
            "Hai $userName", style = UiFont.poppinsH3SemiBold, modifier = Modifier.padding(
                start = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp
            )
        )
        Text(
            "Butuh bantuan apa hari ini?",
            style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500),
            modifier = Modifier.padding(
                start = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp
            )
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_32dp))
            LazyHorizontalGrid(
            modifier = Modifier
                .padding(horizontal = Theme.dimension.size_42dp)
                .height(220.dp)
                .fillMaxWidth(),
            rows = GridCells.Fixed(2),
            state = listState,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!menus.isNullOrEmpty()) {
                items(menus.size) { index ->
                    val item = menus[index]
                    QuickMenuItem(item, onClick = {
                        onClick(item)
                    })
                }
            }
        }
    }
}

@Composable
private fun NearbyFoodTitle(navigator: DestinationsNavigator, latLng: LatLng) {
    Column(
        modifier = Modifier.padding(
            top = Theme.dimension.size_20dp,
            start = Theme.dimension.size_16dp,
            end = Theme.dimension.size_16dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Resto Terdekat",
                style = UiFont.poppinsP2Medium.copy(fontWeight = FontWeight.W700)
            )
            Text(
                "Lihat Semua",
                style = UiFont.poppinsCaptionMedium.copy(
                    fontWeight = FontWeight.W700,
                    color = UiColor.secondary500
                ),
                modifier = Modifier.clickable {
                    navigator.navigate(
                        FoodNavScreenDestination(
                            FoodScreenSpec(
                                isListRestaurantFromMain = true,
                                latLng = latLng
                            )
                        )
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
        Text(
            "Coba makanan terbaik dari Resto berikut ini", style = UiFont.poppinsCaptionMedium.copy(
                color = UiColor.neutral400
            )
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun NearbyFoodList(navigator: DestinationsNavigator, spec: ItemRestaurantModel, latLng: LatLng) {
    Card(
        shape = RoundedCornerShape(Theme.dimension.size_12dp),
        modifier = Modifier
            .padding(start = Theme.dimension.size_16dp)
            .clickable {
                navigator.navigate(
                    FoodNavScreenDestination(
                        FoodScreenSpec(
                            isRestaurantFromMain = true,
                            latLng = latLng,
                            restaurantId = spec.id
                        )
                    )
                )
            },
        elevation = 0.dp
    ) {
        Box(modifier = Modifier.size(Theme.dimension.size_144dp)) {
            GlideImage(
                imageModel = spec.photoRestaurant,
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop
                ),
                modifier = Modifier.fillMaxSize(),
                failure = {
                    Image(
                        painter = painterResource(id = rAppModule.drawable.ic_no_image),
                        contentDescription = "failed",
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            )
            Card(
                backgroundColor = Color(0x8C0A0A0A),
                elevation = 0.dp,
                shape = RoundedCornerShape(Theme.dimension.size_8dp),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9A9A9A),
                            Color(0x00888888)
                        )
                    ),
                ),
                modifier = Modifier
                    .blur(115.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .padding(Theme.dimension.size_12dp)
                    .align(Alignment.TopEnd)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0x66FFFFFF),
                                Color(0x1AFFFFFF)
                            )
                        ),
                        shape = RoundedCornerShape(Theme.dimension.size_8dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(
                        vertical = Theme.dimension.size_4dp,
                        horizontal = Theme.dimension.size_6dp
                    )
                ) {
                    GlideImage(
                        imageModel = rAppModule.drawable.ic_star,
                        modifier = Modifier.size(Theme.dimension.size_20dp)
                    )
                    Spacer(modifier = Modifier.width(Theme.dimension.size_4dp))
                    Text(
                        spec.rating,
                        style = UiFont.poppinsCaptionMedium.copy(
                            color = Color.White,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                        )
                    )
                }
            }
        }
    }
}