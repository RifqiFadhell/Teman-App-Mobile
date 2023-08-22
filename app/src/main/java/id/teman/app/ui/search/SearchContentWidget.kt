package id.teman.app.ui.search

import id.teman.app.R as rAppModule
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.model.LatLng
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.common.CustomLoading
import id.teman.app.data.dto.location.GooglePredictionDto
import id.teman.app.domain.model.restaurant.ItemRestaurantModel
import id.teman.app.domain.model.search.SearchUiModel
import id.teman.app.domain.model.search.toSearchLocationUiModel
import id.teman.app.domain.model.search.toSearchUiModel
import id.teman.app.ui.destinations.FoodNavScreenDestination
import id.teman.app.ui.food.FoodScreenSpec
import id.teman.app.ui.search.common.SearchDestinationSectionItem
import id.teman.app.ui.search.common.SearchFoodSectionItem
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun SearchContentWidget(
    navigator: DestinationsNavigator,
    searchPosition: Int,
    loading: Boolean,
    locationList: List<GooglePredictionDto>? = emptyList(),
    listRestaurant: List<ItemRestaurantModel>? = emptyList(),
    latLng: LatLng? = null,
    onClickedItem : (String) -> Unit
) {
    val listResult: List<SearchUiModel> = if (searchPosition == 0) {
        locationList.orEmpty().toSearchLocationUiModel()
    } else {
        listRestaurant.orEmpty().toSearchUiModel()
    }

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
                        is SearchUiModel.SectionDestination -> SearchDestinationSectionItem(item) {
                            onClickedItem(it)
                        }
                        is SearchUiModel.SectionFood -> SearchFoodSectionItem(item) {
                            navigator.navigate(
                                FoodNavScreenDestination(
                                    FoodScreenSpec(
                                        isRestaurantFromMain = true,
                                        latLng = latLng,
                                        restaurantId = it
                                    )
                                )
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
            imageModel = rAppModule.drawable.ic_empty_search_result,
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