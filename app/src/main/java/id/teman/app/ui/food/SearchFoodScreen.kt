package id.teman.app.ui.food

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.maps.model.LatLng
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.CustomLoading
import id.teman.app.domain.model.restaurant.ItemRestaurantModel
import id.teman.app.domain.model.search.SearchUiModel
import id.teman.app.domain.model.search.toSearchUiModel
import id.teman.app.ui.destinations.FoodNavScreenDestination
import id.teman.app.ui.destinations.RestaurantScreenDestination
import id.teman.app.ui.search.ClearRippleTheme
import id.teman.app.ui.search.common.SearchDestinationSectionItem
import id.teman.app.ui.search.common.SearchFoodSectionItem
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@Destination
@OptIn(ExperimentalPagerApi::class)
@Composable
fun SearchFoodScreen(
    navigator: DestinationsNavigator,
    homeViewModel: FoodMainViewModel
) {
    var search by remember { mutableStateOf("") }
    val mainUiState = homeViewModel.foodMainUiState
    val foodUiState = homeViewModel.foodUiState
    var isFood by remember { mutableStateOf(false) }

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
                                .height(Theme.dimension.size_48dp),
                            value = search,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            shape = RoundedCornerShape(Theme.dimension.size_30dp),
                            onValueChange = {
                                search = it
                                homeViewModel.searchDebounced(
                                    searchText = it,
                                    latLng = mainUiState.currentUserLocationLatLng
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
                    isFood = it == "Makanan"
                }
                TabContentWidget(
                    navigator = navigator,
                    pagerState = pagerState,
                    listRestaurant = foodUiState.listRestaurant,
                    loading = mainUiState.loading,
                    latLng = mainUiState.currentUserLocationLatLng
                )
            }
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TabWidget(pagerState: PagerState, currentPage: (String) -> Unit) {
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
            page.forEachIndexed { index, tabTitle ->
                currentPage(tabTitle)
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
                            if (index < page.size - 1) {
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
    listRestaurant: List<ItemRestaurantModel>? = emptyList(),
    latLng: LatLng
) {
    HorizontalPager(
        count = page.size,
        state = pagerState,
    ) {
        SearchFoodContentWidget(
            listRestaurant = listRestaurant,
            loading = loading,
            latLng = latLng,
            navigator = navigator
        )
    }
}

val page = listOf("Makanan")

@Composable
fun SearchFoodContentWidget(
    navigator: DestinationsNavigator,
    loading: Boolean,
    listRestaurant: List<ItemRestaurantModel>? = emptyList(),
    latLng: LatLng
) {
    val listResult: List<SearchUiModel> = listRestaurant.orEmpty().toSearchUiModel()
    if (loading) {
        Dialog(
            onDismissRequest = { },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            CustomLoading(modifier = Modifier.fillMaxSize())
        }
    } else {
        if (listResult.isEmpty()) {
            EmptySearchResult()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                itemsIndexed(listResult) { _, item ->
                    when (item) {
                        is SearchUiModel.SectionDestination -> SearchDestinationSectionItem(item) {}
                        is SearchUiModel.SectionFood -> SearchFoodSectionItem(item) {
                            navigator.navigate(
                                RestaurantScreenDestination(restaurantId = item.restaurantId, latLng = latLng)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchResult() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = Theme.dimension.size_16dp,
                vertical = Theme.dimension.size_40dp
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            imageModel = R.drawable.ic_empty_search_result,
            modifier = Modifier.size(Theme.dimension.size_200dp)
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_26dp))
        Text(
            "Ups! Yang kamu cari belum ketemu",
            textAlign = TextAlign.Center,
            style = UiFont.poppinsP3SemiBold.copy(color = UiColor.neutral900)
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
        Text(
            "Tujuan yang kamu cari belum bisa ditemukan. Coba cek kembali penulisan atau Refresh halaman ini.",
            textAlign = TextAlign.Center,
            style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral400)
        )
    }
}