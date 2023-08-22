package id.teman.app.domain.model.location

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

data class LocationDirectionSpec(
    val points: List<LatLng>,
    val bounds: LatLngBounds? = null
)