package id.teman.app.data.remote.location

import id.teman.app.BuildConfig
import id.teman.app.data.dto.location.DirectionResponseDto
import id.teman.app.data.dto.location.GooglePredictionsDto
import id.teman.app.data.dto.location.PlaceResponseDto
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface LocationRemoteDataSource {
    suspend fun getListLocationMaps(latLng: String, query: String): Flow<GooglePredictionsDto>
    suspend fun getDetailLocation(placeId: String): Flow<PlaceResponseDto>
    suspend fun getMapDirection(
        origin: String,
        destination: String,
        apiKey: String,
        mode: String,
        vehicleType: String
    ): Flow<DirectionResponseDto>
}

class DefaultLocationDataSource(private val httpClient: ApiServiceInterface) :
    LocationRemoteDataSource {

    override suspend fun getListLocationMaps(
        latLng: String,
        query: String
    ): Flow<GooglePredictionsDto> =
        handleRequestOnFlow {
            val mapsKey = BuildConfig.MAPS_API_KEY
            httpClient.getPredictions(key = mapsKey, input = query, location = latLng)
        }

    override suspend fun getDetailLocation(placeId: String): Flow<PlaceResponseDto> =
        handleRequestOnFlow {
            val mapsKey = BuildConfig.MAPS_API_KEY
            httpClient.getDetailLocation(mapsKey, placeId)
        }

    override suspend fun getMapDirection(
        origin: String,
        destination: String,
        apiKey: String,
        mode: String,
        vehicleType: String
    ): Flow<DirectionResponseDto> =
        handleRequestOnFlow {
            httpClient.getMapDirection(
                origin,
                destination,
                apiKey,
                vehicleType,
                mode,
                avoid = if (vehicleType == "motorcycle") "tolls" else null
            )
        }
}