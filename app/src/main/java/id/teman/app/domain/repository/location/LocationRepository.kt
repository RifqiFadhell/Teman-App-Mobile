package id.teman.app.domain.repository.location

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import id.teman.app.BuildConfig
import id.teman.app.common.orZero
import id.teman.app.data.dto.location.GooglePredictionsDto
import id.teman.app.data.remote.location.LocationRemoteDataSource
import id.teman.app.domain.model.location.LocationDirectionSpec
import id.teman.app.domain.model.location.PlaceDetailSpec
import id.teman.app.domain.model.location.toPlaceDetailSpec
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class LocationRepository @Inject constructor(
    private val locationRemoteDataSource: LocationRemoteDataSource
) {
    suspend fun getLocation(latLng: String, query: String): Flow<GooglePredictionsDto> =
        locationRemoteDataSource.getListLocationMaps(latLng = latLng, query = query)

    suspend fun getDetailLocation(locationId: String): Flow<PlaceDetailSpec?> =
        locationRemoteDataSource.getDetailLocation(locationId).map { it.toPlaceDetailSpec() }

    suspend fun getMapDirection(
        origin: String, destination: String,
        vehicleType: String, mode: String = "driving"
    ): Flow<LocationDirectionSpec> =
        flow {
            val mapApiKey = BuildConfig.MAPS_API_KEY
            locationRemoteDataSource.getMapDirection(
                origin,
                destination,
                mapApiKey,
                mode,
                vehicleType
            )
                .catch { exception -> throw exception }
                .collect {
                    val legsDetail = it.routes?.getOrNull(0)?.legs
                    val points = legsDetail?.getOrNull(0)?.steps
                    val northeast = it.routes?.getOrNull(0)?.bounds?.northeast
                    val southwest = it.routes?.getOrNull(0)?.bounds?.southwest
                    val latLngBounds = if (northeast != null && southwest != null) LatLngBounds(
                        LatLng(southwest.lat!!, southwest.lng!!),
                        LatLng(northeast.lat!!, northeast.lng!!)
                    ) else null
                    if (!points.isNullOrEmpty()) {
                        val connectedLatLng = arrayListOf<LatLng>()
                        points.forEach { step ->
                            connectedLatLng.add(
                                LatLng(
                                    step.startLocation?.lat.orZero(),
                                    step.startLocation?.lng.orZero()
                                )
                            )

                            val polyline = PolyUtil.decode(step.polyline?.points)
                            connectedLatLng.addAll(polyline)
                            connectedLatLng.add(
                                LatLng(
                                    step.endLocation?.lat.orZero(),
                                    step.endLocation?.lng.orZero()
                                )
                            )
                        }
                        emit(
                            LocationDirectionSpec(
                                connectedLatLng,
                                latLngBounds
                            )
                        )
                    }
                }
        }
}