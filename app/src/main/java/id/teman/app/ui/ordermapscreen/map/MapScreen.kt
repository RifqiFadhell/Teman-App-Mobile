package id.teman.app.ui.ordermapscreen.map

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.view.animation.LinearInterpolator
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import id.teman.app.R
import id.teman.app.common.calculateSnappedLatLng
import id.teman.app.ui.ordermapscreen.TransportViewModel
import id.teman.coreui.typography.UiColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MapScreen(viewModel: TransportViewModel, defaultLatLng: LatLng, isCar: Boolean) {
    val uiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = false)) }
    val properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = false
            )
        )
    }
    val driverVehicleMarkerDrawable by remember { derivedStateOf { if (isCar) R.drawable.ic_teman_car else R.drawable.ic_motor_bike } }
    val transportUiState = viewModel.transportUiState
    val context = LocalContext.current

    var markerPosition by rememberSaveable { mutableStateOf(defaultLatLng) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 30f)
    }
    var driverPosition by rememberSaveable { mutableStateOf<LatLng?>(null) }
    var driverBearing by rememberSaveable { mutableStateOf(0.0f) }

    var polylines by rememberSaveable { mutableStateOf<List<LatLng>>(emptyList()) }
    var destination by rememberSaveable { mutableStateOf<LatLng?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val metrics = LocalConfiguration.current
    val width = metrics.screenWidthDp

    LaunchedEffect(key1 = transportUiState.removePolyline, block = {
        transportUiState.removePolyline?.consumeOnce {
            polylines = emptyList()
            driverBearing = 0.0f
            driverPosition = null
            destination = null
        }
    })
    LaunchedEffect(key1 = transportUiState.originMarker, block = {
        transportUiState.originMarker?.consumeOnce { latLng ->
            coroutineScope.launch {
                markerPosition = latLng
                delay(300)
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newCameraPosition(
                        CameraPosition(latLng, 30f, 0f, 0f)
                    )
                )
                cameraPositionState.move(CameraUpdateFactory.scrollBy(0f, 400f))
            }
        }
    })

    LaunchedEffect(key1 = transportUiState.driverLatLng, block = {
        transportUiState.driverLatLng?.consumeOnce { updatePos ->
            driverPosition?.let {
                if (polylines.isNotEmpty()) {
                    val snapLatLng = calculateSnappedLatLng(polylines, updatePos)
                    animateMarkerPosition(it, snapLatLng) { newLatLng, rotation ->
                        driverPosition = newLatLng
                    }
                } else {
                    driverPosition = updatePos
                }
            } ?: run {
                driverPosition = updatePos
            }

        }
    })

    LaunchedEffect(key1 = transportUiState.driverBearing, block = {
        transportUiState.driverBearing?.consumeOnce {
            driverBearing = it
        }
    })

    LaunchedEffect(key1 = transportUiState.mapPolyline, block = {
        transportUiState.mapPolyline?.consumeOnce { spec ->
            polylines = emptyList()
            if (spec.points.isNotEmpty()) {
                coroutineScope.launch {
                    polylines = spec.points
                    if (driverPosition == null) {
                        markerPosition = spec.points.first()
                    }
                    destination = spec.points.last()
                    val bounds = spec.bounds ?: LatLngBounds.Builder().apply {
                        spec.points.forEach { spec -> include(spec) }
                    }.build()
                    cameraPositionState.animate(CameraUpdateFactory.scrollBy(0f, 300f))
                    delay(500)
                    val routePadding = width * 0.7
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            routePadding.toInt()
                        )
                    )

                }
            }
        }
    })

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = properties,
        uiSettings = uiSettings,
        cameraPositionState = cameraPositionState,
    ) {
        if (transportUiState.destinationFormPlaceDetail != null) {
            Marker(
                state = MarkerState(transportUiState.destinationFormPlaceDetail.locationLatLng),
                icon = bitmapDescriptor(context, R.drawable.ic_destination_location)
            )
        } else {
            destination?.let { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    icon = bitmapDescriptor(context, R.drawable.ic_destination_location)
                )
            }
        }
        if (polylines.isNotEmpty()) {
            Polyline(
                points = polylines,
                color = UiColor.blue,
                jointType = JointType.ROUND,
                width = 25f
            )
        }
        driverPosition?.let {
            Marker(
                state = MarkerState(position = it),
                anchor = Offset(0.5f, 0.5f),
                flat = true,
                rotation = driverBearing,
                icon = bitmapDescriptor(context, driverVehicleMarkerDrawable)
            )
        } ?: run {
            Marker(
                state = MarkerState(position = markerPosition),
                icon = bitmapDescriptor(context, R.drawable.ic_origin_location)
            )
        }
        transportUiState.nearbyDrivers.map {
            Marker(
                state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                anchor = Offset(0.5f, 0.5f),
                flat = true,
                rotation = it.bearing,
                icon = bitmapDescriptor(context, driverVehicleMarkerDrawable)
            )
        }
    }
}

fun bitmapDescriptor(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

fun animateMarkerPosition(origin: LatLng, destination: LatLng, onAnimate: (LatLng, Float) -> Unit) {
    val animator = ValueAnimator()
    animator.setFloatValues(0f, 1f)
    animator.duration = 2500
    animator.interpolator = LinearInterpolator()
    var currentPosition = origin
    animator.addUpdateListener { animation ->
        val newPosition: LatLng =
            SphericalUtil.interpolate(origin, destination, animation.animatedFraction.toDouble())
        val bearing = calculateBearing(currentPosition, newPosition)
        currentPosition = newPosition
        onAnimate(newPosition, bearing)
    }
    animator.start()
}

fun calculateBearing(start: LatLng, end: LatLng): Float {
//    val bearing = SphericalUtil.computeHeading(start, end)
//    return (bearing * 180 / Math.PI).toFloat()
    val locationA = Location("locationA").apply {
        start.latitude
        start.longitude
    }
    val locationB = Location("locationB").apply {
        end.latitude
        end.longitude
    }
    return locationA.bearingTo(locationB)
}