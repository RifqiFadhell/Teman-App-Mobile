package id.teman.app.domain.repository.order

import com.google.android.gms.maps.model.LatLng
import id.teman.app.common.orZero
import id.teman.app.data.dto.order.OrderDetailResponseDto
import id.teman.app.data.dto.order.OrderRequestDto
import id.teman.app.data.dto.order.OrderResponseDto
import id.teman.app.data.dto.rating.RatingRequestDto
import id.teman.app.data.remote.order.OrderRemoteDataSource
import id.teman.app.domain.model.order.OrderDetailSpec
import id.teman.app.domain.model.order.TransportRequestType
import id.teman.app.domain.model.order.mapper.toOrderDetailSpec
import id.teman.app.domain.model.user.DriverInfo
import id.teman.app.domain.model.user.toDriverInfo
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class OrderRepository @Inject constructor(
    private val orderRemoteDataSource: OrderRemoteDataSource
) {

    suspend fun getOrderDetailEstimation(request: OrderRequestDto): Flow<OrderResponseDto> =
        orderRemoteDataSource.getOrderDetailEstimation(request)

    suspend fun searchDriver(request: OrderRequestDto): Flow<OrderDetailResponseDto> =
        orderRemoteDataSource.searchDriver(request)

    suspend fun getActiveOrder(): Flow<OrderDetailSpec> =
        orderRemoteDataSource.getActiveOrder().map { it.toOrderDetailSpec() }

    suspend fun sendRating(requestId: String, note: String, rate: Int): Flow<Int> =
        orderRemoteDataSource.sendRating(RatingRequestDto(rate, note), requestId = requestId)
            .map { it.rate.orZero() }

    suspend fun sendRatingResto(requestId: String, note: String, rate: Int): Flow<Int> =
        orderRemoteDataSource.sendRatingResto(RatingRequestDto(rate, note), requestId = requestId)
            .map { it.rate.orZero() }

    suspend fun updateOrderStatus(
        requestId: String,
        status: TransportRequestType
    ): Flow<OrderDetailSpec> =
        orderRemoteDataSource.updateOrderStatus(requestId, status).map { it.toOrderDetailSpec() }

    suspend fun getSnappedRoad(originPoints: String): Flow<LatLng> = flow {
        orderRemoteDataSource
            .getSnappedRoad(originPoints).catch { exception -> throw exception }
            .collect { snappedPoint ->
                val firstPoints = snappedPoint.snappedPoints?.firstOrNull()

                firstPoints?.let {
                    val latitude = it.location?.latitude.orZero()
                    val longitude = it.location?.longitude.orZero()

                    if (latitude != 0.0 && longitude != 0.0) {
                        emit(LatLng(latitude, longitude))
                    } else {
                        throw Exception()
                    }
                } ?: run {
                    throw Exception()
                }
            }
    }

    suspend fun getNearbyDrivers(
        latitude: Double,
        longitude: Double,
        orderType: String
    ): Flow<List<DriverInfo>> =
        orderRemoteDataSource.getNearbyDrivers(latitude, longitude, orderType)
            .map { drivers -> drivers.map { it.toDriverInfo() } }
}