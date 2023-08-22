package id.teman.app.ui.search

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.maps.model.LatLng
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.data.dto.location.GooglePredictionDto
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.restaurant.ItemRestaurantModel
import id.teman.app.ui.sharedviewmodel.MainViewModel
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@OptIn(ExperimentalPagerApi::class)
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    viewModel: SearchLocationViewModel = hiltViewModel(),
    homeViewModel: MainViewModel,
    resultNavigator: ResultBackNavigator<PlaceDetailSpec>
) {
    var search by remember { mutableStateOf("") }
    val mainUiState = homeViewModel.locationUiState
    val uiState = viewModel.searchUiState
    var isFood by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = uiState.successGetPlaceDetail, block = {
        uiState.successGetPlaceDetail?.consumeOnce {
            resultNavigator.navigateBack(it)
        }
    })

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.Transparent,
                contentPadding = PaddingValues(0.dp),
                elevation = 0.dp,
                modifier = Modifier.padding(Theme.dimension.size_16dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row {
                        IconButton(onClick = {
                            navigator.popBackStack()
                        }) {
                            Icon(Icons.Filled.ArrowBack, "backIcon")
                        }
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            value = search,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            shape = RoundedCornerShape(Theme.dimension.size_30dp),
                            onValueChange = {
                                search = it
                                viewModel.searchDebounced(
                                    searchText = it,
                                    latLng = mainUiState.currentUserLocationLatLng,
                                    isFood = isFood
                                )
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = UiColor.neutral100,
                                cursorColor = UiColor.black,
                                unfocusedBorderColor = UiColor.neutral100
                            ),
                            trailingIcon = {
                                IconButton(onClick = { search = "" }) {
                                    Icon(
                                        Icons.Default.Close, "Close Icon",
                                        modifier = Modifier.size(Theme.dimension.size_24dp)
                                    )
                                }
                            },
                            leadingIcon = {
                                GlideImage(
                                    R.drawable.ic_search,
                                    modifier = Modifier.size(Theme.dimension.size_24dp)
                                )
                            }
                        )
                    }
                }
            }
        },
        content = {
            val pagerState = rememberPagerState()
            Column {
                TabWidget(pagerState) {
                    isFood = it == 1
                }
                TabContentWidget(
                    navigator = navigator,
                    pagerState = pagerState,
                    locationList = uiState.availableLocationList,
                    listRestaurant = uiState.listRestaurant,
                    loading = uiState.loading,
                    latLng = mainUiState.currentUserLocationLatLng
                ) {
                    viewModel.getDetailLocation(it)
                }
            }
        }
    )
}

object ClearRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor(): Color = Color.Transparent

    @Composable
    override fun rippleAlpha() = RippleAlpha(
        draggedAlpha = 0.0f,
        focusedAlpha = 0.0f,
        hoveredAlpha = 0.0f,
        pressedAlpha = 0.0f,
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TabWidget(pagerState: PagerState, currentPage: (Int) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    CompositionLocalProvider(LocalRippleTheme provides ClearRippleTheme) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    color = UiColor.blue,
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            pages.forEachIndexed { index, tabTitle ->
                Tab(
                    selected = pagerState.currentPage == index,
                    content = {
                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                tabTitle,
                                modifier = Modifier.align(Alignment.CenterVertically),
                                textAlign = TextAlign.Center,
                                style = UiFont.poppinsCaptionSemiBold.copy(color = Color.Black)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (index < pages.size - 1) {
                                Divider(
                                    color = UiColor.neutral100,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(bottom = Theme.dimension.size_12dp),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                        currentPage(index)
                    }
                )
            }
        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TabContentWidget(
    navigator: DestinationsNavigator,
    pagerState: PagerState,
    loading: Boolean,
    locationList: List<GooglePredictionDto>? = emptyList(),
    listRestaurant: List<ItemRestaurantModel>? = emptyList(),
    latLng: LatLng? = null,
    onClickedItem : (String) -> Unit?
) {
    HorizontalPager(
        count = pages.size,
        state = pagerState,
    ) { page: Int ->
        SearchContentWidget(
            searchPosition = page,
            locationList = locationList,
            listRestaurant = listRestaurant,
            loading = loading,
            latLng = latLng,
            navigator = navigator
        ) {
            onClickedItem(it)
        }
    }
}

val pages = listOf("Destinasi", "Makanan")