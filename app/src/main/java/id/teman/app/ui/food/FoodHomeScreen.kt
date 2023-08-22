package id.teman.app.ui.food

import android.os.Parcelable
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.google.android.gms.maps.model.LatLng
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.HeaderLocationWallet
import id.teman.app.common.PromoContent
import id.teman.app.common.QuickMenuItem
import id.teman.app.common.StarRatting
import id.teman.app.common.noRippleClickable
import id.teman.app.common.orZero
import id.teman.app.domain.model.RatingSpec
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.restaurant.ItemRestaurantModel
import id.teman.app.ui.destinations.ListRestaurantScreenDestination
import id.teman.app.ui.destinations.RestaurantScreenDestination
import id.teman.app.ui.destinations.SearchFoodScreenDestination
import id.teman.app.ui.destinations.SearchLocationScreenDestination
import id.teman.app.ui.destinations.WebViewScreenDestination
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButtonHome
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.parcelize.Parcelize

@Destination
@Composable
fun FoodHomeScreen(
    navigator: DestinationsNavigator,
    mainViewModel: FoodMainViewModel,
    searchRecipient: ResultRecipient<SearchLocationScreenDestination, PlaceDetailSpec>,
    foodViewModel: FoodViewModel = hiltViewModel()
) {
    val uiState = foodViewModel.foodUiState
    val locationState = mainViewModel.foodMainUiState

    searchRecipient.onNavResult {
        when (it) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                val place = it.value
                mainViewModel.updateLocationSpec(place)
            }
        }
    }

    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        foodViewModel.getWalletBalance()
        foodViewModel.getListMenu()
        foodViewModel.getHomeBanner()
        foodViewModel.getListRestaurantNearby(locationState.currentUserLocationLatLng)
        if (mainViewModel.getIsFromMain()) {
            locationState.spec?.latLng?.let {
                navigator.navigate(
                    RestaurantScreenDestination(
                        locationState.spec.restaurantId.orEmpty(),
                        it
                    )
                )
            }
        } else if (mainViewModel.getIsListFromMain()) {
            navigator.navigate(ListRestaurantScreenDestination(title = "Restoran Terdekat", latLng = locationState.currentUserLocationLatLng, category = "nearby"))
        }
    }

    Scaffold(
        modifier = Modifier.padding(
            bottom = Theme.dimension.size_24dp
        )
    ) {
        LazyColumn(
            modifier = Modifier.padding(
                top = Theme.dimension.size_24dp,
                start = Theme.dimension.size_16dp, end = Theme.dimension.size_16dp
            )
        ) {
            item {
                HeaderLocationWallet(
                    balance = uiState.balance,
                    location = locationState.userLocation
                ) {
                    navigator.navigate(
                        SearchLocationScreenDestination(
                            defaultLatLng = locationState.currentUserLocationLatLng
                        )
                    )
                }
            }
            item {
                Row(modifier = Modifier.padding(top = Theme.dimension.size_16dp)) {
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
                                navigator.navigate(SearchFoodScreenDestination)
                            }
                    ) {
                        Row(verticalAlignment = CenterVertically) {
                            GlideImage(
                                R.drawable.ic_search,
                                modifier = Modifier.size(Theme.dimension.size_24dp)
                            )
                            Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
                            Text(
                                "Lagi pengen makan apa nih",
                                style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral400)
                            )
                        }
                    }
                }
            }
            item {
                PromoContent(uiState.listBanners.orEmpty()) { url, title ->
                    navigator.navigate(WebViewScreenDestination(url = url, title = title))
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    repeat(uiState.listMenus.size.orZero()) { index ->
                        val item = uiState.listMenus[index]
                        QuickMenuItem(item, onClick = {
                            navigator.navigate(ListRestaurantScreenDestination(title = item.title,
                            latLng = locationState.currentUserLocationLatLng, category = item.key ))
                        })
                    }
                }
            }
            item {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Theme.dimension.size_16dp)
                    ) {
                        Text(
                            "Eksplor aneka Kuliner",
                            style = UiFont.poppinsP2Medium.copy(fontWeight = FontWeight.W700)
                        )
                    }
                }
            }
            item {
                LazyRow(modifier = Modifier.padding(top = Theme.dimension.size_16dp)) {
                    items(uiState.listCategories.size) {
                        val item = uiState.listCategories[it]
                        ItemMenuFood(title = item.name, icon = item.icon) {
                            navigator.navigate(ListRestaurantScreenDestination(title = item.name,
                                latLng = locationState.currentUserLocationLatLng, category = item.name))
                        }
                    }
                }
            }
            item {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Theme.dimension.size_16dp)
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
                                navigator.navigate(ListRestaurantScreenDestination(title = "Restoran Terdekat", latLng = locationState.currentUserLocationLatLng, category = "nearby"))
                            }
                        )
                    }
                }
            }
            items(uiState.listRestaurant.size) { index ->
                ItemRestaurant(spec = uiState.listRestaurant[index]) {
                    navigator.navigate(
                        RestaurantScreenDestination(
                            it,
                            latLng = locationState.currentUserLocationLatLng
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ItemMenuFood(title: String, icon: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = Theme.dimension.size_24dp)
            .noRippleClickable {
                onClick()
            }) {
        TemanCircleButtonHome(
            iconServer = icon,
            iconModifier = Modifier.size(Theme.dimension.size_56dp),
            circleModifier = Modifier.size(Theme.dimension.size_64dp)
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_10dp))
        Text(text = title, style = UiFont.poppinsCaptionMedium)
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ItemRestaurant(spec: ItemRestaurantModel, onClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = Theme.dimension.size_20dp)
            .border(
                BorderStroke(
                    Theme.dimension.size_1dp,
                    color = UiColor.neutral50
                ),
                shape = RoundedCornerShape(Theme.dimension.size_16dp)
            )
            .padding(Theme.dimension.size_4dp)
            .clickable {
                onClick(spec.id)
            }
    ) {
        Box(modifier = Modifier) {
            GlideImage(
                modifier = Modifier
                    .aspectRatio(3f / 1f)
                    .clip(RoundedCornerShape(Theme.dimension.size_12dp)),
                imageModel = spec.photoRestaurant,
                failure = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_person_mamoji),
                        contentDescription = "failed"
                    )
                }
            )
            Row(modifier = Modifier.align(Alignment.BottomStart)) {
                Card(
                    backgroundColor = UiColor.tertiaryBlue500,
                    elevation = 0.dp,
                    shape = RoundedCornerShape(Theme.dimension.size_8dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = UiColor.tertiaryBlue500,
                    ),
                    modifier = Modifier
                        .blur(115.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                        .padding(
                            start = Theme.dimension.size_12dp,
                            top = Theme.dimension.size_12dp,
                            bottom = Theme.dimension.size_12dp,
                            end = Theme.dimension.size_4dp
                        )
                        .background(
                            UiColor.tertiaryBlue500,
                            shape = RoundedCornerShape(Theme.dimension.size_8dp)
                        )
                ) {
                    Row(
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(
                            vertical = Theme.dimension.size_4dp,
                            horizontal = Theme.dimension.size_6dp
                        )
                    ) {
                        Text(
                            modifier = Modifier.height(Theme.dimension.size_20dp),
                            textAlign = TextAlign.Center,
                            text = spec.distance,
                            style = UiFont.poppinsCaptionMedium.copy(
                                color = Color.White,
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                ),
                            )
                        )
                    }
                }
                Card(
                    backgroundColor = UiColor.primaryRed500,
                    elevation = 0.dp,
                    shape = RoundedCornerShape(Theme.dimension.size_8dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = UiColor.primaryRed500,
                    ),
                    modifier = Modifier
                        .blur(115.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                        .padding(
                            start = Theme.dimension.size_4dp,
                            top = Theme.dimension.size_12dp,
                            bottom = Theme.dimension.size_12dp
                        )
                        .background(
                            UiColor.primaryRed500,
                            shape = RoundedCornerShape(Theme.dimension.size_8dp)
                        )
                ) {
                    Row(
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(
                            vertical = Theme.dimension.size_4dp,
                            horizontal = Theme.dimension.size_6dp
                        )
                    ) {
                        GlideImage(
                            imageModel = R.drawable.ic_teman_bike,
                            modifier = Modifier.size(Theme.dimension.size_20dp),
                            imageOptions = ImageOptions(colorFilter = ColorFilter.tint(UiColor.white))
                        )
                        Text(
                            spec.timeEstimation,
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
        Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                "${spec.name}, ${spec.address}",
                modifier = Modifier
                    .padding(horizontal = Theme.dimension.size_12dp)
                    .fillMaxWidth(0.8f),
                style = UiFont.poppinsSubHSemiBold,
                maxLines = 1
            )
            StarRatting(spec = RatingSpec(UiColor.black, spec.rating))
        }
        Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
        Row(modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp)) {
            Text(
                "$",
                style = UiFont.poppinsCaptionSemiBold
            )
            Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
            Text(
                spec.categories,
                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral300)
            )
        }
        Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
    }
}

@Parcelize
data class FoodScreenSpec(
    val isRestaurantFromMain: Boolean? = false,
    val isListRestaurantFromMain: Boolean? = false,
    val latLng: LatLng? = null,
    val restaurantId: String? = ""
) : Parcelable