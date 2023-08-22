package id.teman.app.ui.search

import android.location.Address
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.common.orFalse
import id.teman.app.data.dto.location.GooglePredictionDto
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.restaurant.ItemRestaurantModel
import id.teman.app.domain.repository.food.FoodRepository
import id.teman.app.domain.repository.location.LocationRepository
import id.teman.app.utils.Event
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val foodRepository: FoodRepository,
    private val geocoder: Geocoder,
) : ViewModel() {

    var searchUiState by mutableStateOf(SearchLocationUiState())
        private set

    private var searchJob: Job? = null

    fun searchDebounced(searchText: String, latLng: LatLng? = null, isFood: Boolean? = false) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(700)
            if (isFood.orFalse()) {
                latLng?.let { getListRestaurantNearby(latLng = it, search = searchText) }
            } else {
                searchLocation(searchText, latLng)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (searchJob != null) {
            searchJob?.cancel()
        }
    }

    fun getLocationName(value: LatLng) {
        searchUiState = searchUiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.Default) {
            if (Geocoder.isPresent()) {
                val addresses: MutableList<Address>
                try {
                    addresses = geocoder.getFromLocation(
                        value.latitude,
                        value.longitude,
                        5
                    ) as ArrayList<Address>

                    if (addresses.isNotEmpty()) {
                        val city = addresses.getOrNull(0)?.subAdminArea.orEmpty()
                        val subAdminArea = addresses.getOrNull(0)?.subLocality.orEmpty()
                        val spec = PlaceDetailSpec(
                            LatLng(value.latitude, value.longitude),
                            addresses.getOrNull(0)?.getAddressLine(0).orEmpty(),
                            "$subAdminArea, $city"
                        )
                        searchUiState = searchUiState.copy(
                            loading = false,
                            emptyLocationPredictions = true,
                            availableLocationList = emptyList(),
                            pinLocationName = Event(spec)
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                searchUiState = searchUiState.copy(loading = false)
            }
        }
    }

    fun searchLocation(query: String, latLng: LatLng? = null) {
        searchUiState = searchUiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.getLocation(latLng = latLng.toString(), query = query)
                .catch { exception ->
                    searchUiState = searchUiState.copy(
                        loading = false,
                        error = exception.message.orEmpty()
                    )
                }
                .collect {
                    searchUiState = if (it.predictions.isNullOrEmpty()) {
                        searchUiState.copy(
                            loading = false,
                            emptyLocationPredictions = true,
                            availableLocationList = emptyList()
                        )
                    } else {
                        searchUiState.copy(
                            loading = false,
                            emptyLocationPredictions = false,
                            availableLocationList = it.predictions
                        )
                    }
                }
        }
    }

    private fun getListRestaurantNearby(latLng: LatLng, category: String? = "", search: String? = "") {
        searchUiState = searchUiState.copy(loading = true)
        viewModelScope.launch {
            foodRepository.getListRestaurant(latLng = latLng, search = search, category = category)
                .catch { exception ->
                    searchUiState = searchUiState.copy(loading = false, error = exception.message)
                }.collect {
                    searchUiState = searchUiState.copy(loading = false, listRestaurant = it)
                }
        }
    }

    fun getDetailLocation(placeId: String) {
        searchUiState = searchUiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.getDetailLocation(placeId)
                .catch { exception ->
                    searchUiState = searchUiState.copy(
                        loading = false,
                        error = exception.message.orEmpty()
                    )
                }
                .collect {
                    searchUiState = if (it != null) {
                        searchUiState.copy(
                            loading = false,
                            successGetPlaceDetail = Event(it)
                        )
                    } else {
                        searchUiState.copy(loading = false)
                    }
                }
        }
    }

    data class SearchLocationUiState(
        val loading: Boolean = false,
        val error: String? = null,
        val availableLocationList: List<GooglePredictionDto> = emptyList(),
        val emptyLocationPredictions: Boolean = false,
        val successGetPlaceDetail: Event<PlaceDetailSpec>? = null,
        val listRestaurant: List<ItemRestaurantModel> = emptyList(),
        val pinLocationName: Event<PlaceDetailSpec>? = null
    )
}