package id.teman.app.domain.usecase.order

import com.google.android.gms.maps.model.LatLng
import id.teman.app.domain.model.order.FoodOrderRequestSpec
import id.teman.app.domain.model.order.OrderEstimationResponseSpec
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.domain.model.order.mapper.toOrderEstimationResponseSpec
import id.teman.app.domain.model.order.mapper.toOrderRequestDto
import id.teman.app.domain.repository.order.OrderRepository
import id.teman.app.domain.usecase.UseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetOrderEstimationUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) : UseCase<OrderEstimationRequestSpec, Flow<OrderEstimationResponseSpec>> {

    override suspend fun execute(params: OrderEstimationRequestSpec): Flow<OrderEstimationResponseSpec> =
        orderRepository.getOrderDetailEstimation(params.toOrderRequestDto())
            .map { orderResponse ->
                orderResponse.toOrderEstimationResponseSpec(params)
            }
}

data class OrderEstimationRequestSpec(
    val orderType: OrderRequestType,
    val originLatLng: LatLng,
    val destinationLatLng: LatLng,
    val originAddress: String,
    val destinationAddress: String,
    val notes: String? = null,
    val paymentMethod: String = "cash",
    val receiverName: String? = null,
    val receiverPhone: String? = null,
    val packageType: String? = null,
    val packageWeight: Int? = null,
    val insurance: Int? = null,
    val restaurantOrder: FoodOrderRequestSpec? = null,
    val promoId: String = ""
)