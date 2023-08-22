package id.teman.app.domain.usecase.order

import com.google.android.gms.maps.model.LatLng
import id.teman.app.common.LatLngSerializer
import id.teman.app.data.dto.order.OrderRequestDto
import id.teman.app.data.dto.order.OrderRequestItemDto
import id.teman.app.domain.model.order.FoodOrderRequestSpec
import id.teman.app.domain.model.order.OrderDetailSpec
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.domain.model.order.mapper.toOrderDetailSpec
import id.teman.app.domain.repository.order.OrderRepository
import id.teman.app.domain.usecase.UseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

class SearchDriverUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) : UseCase<SearchDriverRequestSpec, Flow<OrderDetailSpec>> {
    override suspend fun execute(params: SearchDriverRequestSpec): Flow<OrderDetailSpec> {
        return orderRepository.searchDriver(
            OrderRequestDto(
                originAddress = params.originAddress,
                originLongitude = params.originLatLng.longitude,
                originLatitude = params.originLatLng.latitude,
                destinationAddress = params.destinationAddress,
                destinationLongitude = params.destinationLatLng.longitude,
                destinationLatitude = params.destinationLatLng.latitude,
                type = params.orderType.value,
                note = params.notes,
                paymentMethod = params.paymentMethod,
                pin = params.pin,
                receiverName = params.receiverName,
                receiverPhone = params.receiverPhone,
                packageType = params.packageType,
                packageWeight = params.packageWeight,
                restaurantId = params.restaurantOrder?.restaurantId,
                items = params.restaurantOrder?.orderedItems?.map {
                    OrderRequestItemDto(
                        productId = it.productId,
                        note = it.note,
                        quantity = it.quantity
                    )
                }, promotionId = params.promoId,
                insurance = params.insurance
            )
        ).map { it.toOrderDetailSpec() }
    }
}

@Serializable
data class SearchDriverRequestSpec(
    val orderType: OrderRequestType,
    @Serializable(with = LatLngSerializer::class)
    val originLatLng: LatLng,
    @Serializable(with = LatLngSerializer::class)
    val destinationLatLng: LatLng,
    val originAddress: String,
    val destinationAddress: String,
    val notes: String? = null,
    val paymentMethod: String,
    val pin: String? = null,
    val receiverName: String? = null,
    val receiverPhone: String? = null,
    val packageType: String? = null,
    val packageWeight: Int? = null,
    val restaurantOrder: FoodOrderRequestSpec? = null,
    val promoId: String? = "",
    val insurance: Int? = null,
)