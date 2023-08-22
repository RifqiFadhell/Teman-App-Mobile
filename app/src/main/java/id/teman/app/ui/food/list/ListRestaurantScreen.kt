package id.teman.app.ui.food.list

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.google.android.gms.maps.model.LatLng
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.R
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.EmptyState
import id.teman.app.common.TopBar
import id.teman.app.common.isNotNullOrEmpty
import id.teman.app.ui.destinations.RestaurantScreenDestination
import id.teman.app.ui.food.FoodMainViewModel
import id.teman.app.ui.food.FoodViewModel
import id.teman.app.ui.food.ItemRestaurant
import id.teman.app.ui.theme.Theme

@Composable
@Destination
fun ListRestaurantScreen(
    navigator: DestinationsNavigator,
    viewModel: FoodViewModel = hiltViewModel(),
    foodViewModel: FoodMainViewModel,
    title: String, latLng: LatLng, category: String? = "", search: String? = ""
) {
    val uiState = viewModel.foodUiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getListRestaurantNearby(latLng, category = category, search = search)
    }
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_DESTROY) {
        foodViewModel.resetDataBack()
    }
    val caption = if (search.isNotNullOrEmpty()) {
        "“Resto $search” yang kamu cari belum bisa ditemukan. Coba cek kembali penulisan atau Refresh halaman ini."
    } else {
        "Resto yang kamu cari belum bisa Tersedia"
    }
    Scaffold(
        topBar = {
            TopBar(title = title) {
                navigator.popBackStack()
            }
        }, content = {
            LazyColumn(modifier = Modifier.padding(Theme.dimension.size_16dp)) {
                items(uiState.listRestaurant.size) { index ->
                    ItemRestaurant(spec = uiState.listRestaurant[index]) {
                        navigator.navigate(RestaurantScreenDestination(it, latLng = latLng))
                    }
                }
            }
            if (uiState.listRestaurant.isEmpty()) EmptyState(icon = R.drawable.ic_no_restaurant, title = "Ups! Yang kamu cari belum ketemu", description = caption)
        })
}