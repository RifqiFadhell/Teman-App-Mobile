package id.teman.app.ui.search

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.search.SearchUiModel
import id.teman.app.ui.ordermapscreen.map.bitmapDescriptor
import id.teman.app.ui.search.common.SearchDestinationSectionItem
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@Destination
fun SearchLocationScreen(
    navigator: DestinationsNavigator,
    viewModel: SearchLocationViewModel = hiltViewModel(),
    title: String = "Lokasi Kamu",
    defaultLatLng: LatLng,
    resultNavigator: ResultBackNavigator<PlaceDetailSpec>
) {
    var search by remember { mutableStateOf("") }
    var isPinByMappedResult by remember { mutableStateOf(false) }
    var pinPlaceResult by remember { mutableStateOf<PlaceDetailSpec?>(null) }
    val searchUiState = viewModel.searchUiState

    LaunchedEffect(key1 = searchUiState.successGetPlaceDetail, block = {
        searchUiState.successGetPlaceDetail?.consumeOnce {
            resultNavigator.navigateBack(it)
        }
    })

    LaunchedEffect(key1 = searchUiState.pinLocationName, block = {
        searchUiState.pinLocationName?.consumeOnce {
            isPinByMappedResult = true
            pinPlaceResult = it
            search = it.formattedAddress
        }
    })

    val uiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)) }
    val properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true
            )
        )
    }
    var markerPosition by rememberSaveable { mutableStateOf(defaultLatLng) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 30f)
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    properties = properties,
                    uiSettings = uiSettings,
                    cameraPositionState = cameraPositionState,
                    onMapClick = {
                        markerPosition = it
                        viewModel.getLocationName(it)
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(it, 30f, 0f, 0f)
                                )
                            )
                        }
                    }
                ) {
                    Marker(
                        state = MarkerState(position = markerPosition),
                        icon = bitmapDescriptor(context, R.drawable.ic_destination_location)
                    )
                }
                LazyColumn(modifier = Modifier.padding(top = Theme.dimension.size_28dp)) {
                    item {
                        if (title.isNotBlank()) {
                            Text(
                                title,
                                style = UiFont.poppinsP3SemiBold,
                                modifier = Modifier.padding(
                                    start = Theme.dimension.size_16dp,
                                    end = Theme.dimension.size_16dp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
                    }
                    item {
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(horizontal = Theme.dimension.size_16dp)
                                .background(
                                    color = UiColor.white,
                                    shape = RoundedCornerShape(Theme.dimension.size_8dp)
                                )
                                .fillMaxWidth(),
                            value = search,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            shape = RoundedCornerShape(Theme.dimension.size_8dp),
                            onValueChange = { newValue ->
                                search = newValue
                                viewModel.searchDebounced(newValue)
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = UiColor.neutral100,
                                cursorColor = UiColor.black,
                                unfocusedBorderColor = UiColor.neutral100
                            ),
                            placeholder = {
                                Text(modifier = Modifier, text = "Search")
                            },
                            trailingIcon = {
                                GlideImage(
                                    R.drawable.ic_search,
                                    modifier = Modifier
                                        .size(Theme.dimension.size_24dp)
                                        .clickable {
                                            viewModel.searchLocation(search)
                                        }
                                )
                            }
                        )
                    }
                    items(searchUiState.availableLocationList) { item ->
                        SearchDestinationSectionItem(
                            item = SearchUiModel.SectionDestination(
                                item.structuredFormatting?.title.orEmpty(),
                                item.structuredFormatting?.description.orEmpty(),
                                placeId = item.placeId
                            )
                        ) {
                            viewModel.getDetailLocation(it)
                        }
                    }
                }
                if (searchUiState.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
            }
        },
        bottomBar = {
            if (isPinByMappedResult) {
                TemanFilledButton(
                    content = "Lanjutkan",
                    buttonType = ButtonType.Large,
                    activeColor = UiColor.primaryRed500,
                    activeTextColor = Color.White,
                    borderRadius = Theme.dimension.size_30dp,
                    modifier = Modifier.fillMaxWidth(),
                    onClicked = {
                        if (pinPlaceResult != null) {
                            resultNavigator.navigateBack(pinPlaceResult!!)
                        }
                    }
                )
            }
            /*else {
                TemanCircleButton(
                    icon = R.drawable.ic_my_location,
                    circleBackgroundColor = UiColor.neutralGray0,
                    circleModifier = Modifier
                        .size(Theme.dimension.size_48dp)
                        .clickable {
                            (context as MainActivity).startLocationPermissionChecker()
                        },
                    iconModifier = Modifier
                        .size(Theme.dimension.size_24dp)
                )
            }*/
        }
    )
}