package id.teman.app.data.remote.order

import id.teman.app.BuildConfig
import id.teman.app.data.dto.driver.DriverBasicInfoDto
import id.teman.app.data.dto.location.SnappedPointsDto
import id.teman.app.data.dto.order.OrderDetailResponseDto
import id.teman.app.data.dto.order.OrderRequestDto
import id.teman.app.data.dto.order.OrderRequestStatusDto
import id.teman.app.data.dto.order.OrderResponseDto
import id.teman.app.data.dto.rating.RatingRequestDto
import id.teman.app.data.dto.rating.RatingResponseDto
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import id.teman.app.domain.model.order.TransportRequestType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface OrderRemoteDataSource {
    suspend fun getOrderDetailEstimation(request: OrderRequestDto): Flow<OrderResponseDto>
    suspend fun searchDriver(request: OrderRequestDto): Flow<OrderDetailResponseDto>
    suspend fun getActiveOrder(): Flow<OrderDetailResponseDto>
    suspend fun sendRating(rating: RatingRequestDto, requestId: String): Flow<RatingResponseDto>
    suspend fun sendRatingResto(rating: RatingRequestDto, requestId: String): Flow<RatingResponseDto>
    suspend fun updateOrderStatus(
        requestId: String,
        type: TransportRequestType
    ): Flow<OrderDetailResponseDto>

    suspend fun getSnappedRoad(originPoint: String): Flow<SnappedPointsDto>
    suspend fun getNearbyDrivers(
        latitude: Double,
        longitude: Double,
        orderType: String
    ): Flow<List<DriverBasicInfoDto>>
}

class DefaultOrderRemoteDataSource @Inject constructor(
    private val httpClient: ApiServiceInterface
) : OrderRemoteDataSource {

    override suspend fun getOrderDetailEstimation(request: OrderRequestDto): Flow<OrderResponseDto> =
        handleRequestOnFlow {
            httpClient.getOrderDetailEstimation(request)
        }

    override suspend fun searchDriver(request: OrderRequestDto): Flow<OrderDetailResponseDto> =
        handleRequestOnFlow {
            httpClient.searchDriver(request)
        }

    override suspend fun getActiveOrder(): Flow<OrderDetailResponseDto> =
        handleRequestOnFlow {
            httpClient.getActiveOrder()
        }

    override suspend fun sendRating(
        rating: RatingRequestDto,
        requestId: String
    ): Flow<RatingResponseDto> =
        handleRequestOnFlow {
            httpClient.sendRating(rating, requestId = requestId)
        }

    override suspend fun sendRatingResto(
        rating: RatingRequestDto,
        requestId: String
    ): Flow<RatingResponseDto> =
        handleRequestOnFlow {
            httpClient.sendRatingResto(rating, requestId = requestId)
        }

    override suspend fun updateOrderStatus(
        requestId: String,
        type: TransportRequestType
    ): Flow<OrderDetailResponseDto> =
        handleRequestOnFlow {
            httpClient.updateOrderStatus(requestId, OrderRequestStatusDto(type.value))
        }

    override suspend fun getSnappedRoad(originPoint: String): Flow<SnappedPointsDto> =
        handleRequestOnFlow {
            httpClient.getSnappedRoad(
                origin = originPoint,
                apiKey = BuildConfig.MAPS_API_KEY
            )
        }

    override suspend fun getNearbyDrivers(
        latitude: Double,
        longitude: Double,
        orderType: String
    ): Flow<List<DriverBasicInfoDto>> =
        handleRequestOnFlow {
            httpClient.getNearestDrivers(latitude, longitude, orderType)
        }

}