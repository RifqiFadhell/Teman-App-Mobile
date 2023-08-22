package id.teman.app.domain.model.order.mapper

import id.teman.app.common.convertToKilometre
import id.teman.app.common.orFalse
import id.teman.app.common.orZero
import id.teman.app.data.dto.order.OrderDetailResponseDto
import id.teman.app.data.dto.order.OrderRequestDto
import id.teman.app.data.dto.order.OrderRequestItemDto
import id.teman.app.data.dto.order.OrderResponseDto
import id.teman.app.data.dto.order.PriceBreakdown
import id.teman.app.domain.model.order.BreakdownType
import id.teman.app.domain.model.order.OrderDetailSpec
import id.teman.app.domain.model.order.OrderEstimationResponseSpec
import id.teman.app.domain.model.order.OrderPaymentSpec
import id.teman.app.domain.model.order.TransportRequestType
import id.teman.app.domain.model.restaurant.toMenuOrderDetail
import id.teman.app.domain.model.restaurant.toMenuRestaurant
import id.teman.app.domain.model.user.DriverMitraType
import id.teman.app.domain.usecase.order.OrderEstimationRequestSpec
import id.teman.app.utils.decimalFormat
import kotlin.math.roundToInt

fun OrderEstimationRequestSpec.toOrderRequestDto(): OrderRequestDto {
    return OrderRequestDto(
        originLatitude = originLatLng.latitude,
        originLongitude = originLatLng.longitude,
        originAddress = originAddress,
        destinationLatitude = destinationLatLng.latitude,
        destinationLongitude = destinationLatLng.longitude,
        destinationAddress = destinationAddress,
        type = orderType.value,
        note = notes,
        paymentMethod = paymentMethod,
        receiverName = receiverName,
        receiverPhone = receiverPhone,
        packageType = packageType,
        packageWeight = packageWeight,
        insurance = insurance,
        restaurantId = restaurantOrder?.restaurantId,
        items = restaurantOrder?.orderedItems?.map {
            OrderRequestItemDto(
                productId = it.productId,
                note = it.note,
                quantity = it.quantity
            )
        },
        promotionId = promoId
    )
}

fun OrderResponseDto.toOrderEstimationResponseSpec(params: OrderEstimationRequestSpec): OrderEstimationResponseSpec {
    return OrderEstimationResponseSpec(
        distance = distance.orZero().convertToKilometre(),
        totalPrice = fare.orZero(),
        paymentBreakdown = breakdown.toOrderPaymentSpec(),
        originAddress = params.originAddress,
        originLatLng = params.originLatLng,
        destinationAddress = params.destinationAddress,
        destinationLatLng = params.destinationLatLng,
        paymentMethod = params.paymentMethod,
        notes = params.notes.orEmpty(),
        receiverName = params.receiverName,
        receiverPhoneNumber = params.receiverPhone,
        packageWeight = params.packageWeight,
        packageType = params.packageType,
        insurance = params.insurance,
    )
}

fun List<PriceBreakdown>?.toOrderPaymentSpec(): List<OrderPaymentSpec> {
    return this?.map {
        OrderPaymentSpec(
            name = it.name.orEmpty(),
            price = it.value.orZero(),
            breakdownType = BreakdownType.from(it.type)
        )
    }.orEmpty()
}

fun OrderDetailResponseDto.toOrderDetailSpec(): OrderDetailSpec {
    return OrderDetailSpec(
        requestId = id.orEmpty(),
        paymentMethod = paymentMethod.orEmpty(),
        driverPhoto = driver?.driverPhoto?.url.orEmpty(),
        driverName = driver?.user?.name.orEmpty(),
        driverPhoneNumber = driver?.user?.phoneNumber.orEmpty(),
        isDriverVaccine = driver?.vaccine.orFalse(),
        driverVehicleLicenseNumber = driver?.vehicleNumber.orEmpty(),
        driverVehicleBrand = driver?.vehicleBrand.orEmpty(),
        driverVehicleType = driver?.vehicleType.orEmpty(),
        driverRating = driver?.rating.orZero(),
        driverLatitude = driver?.lat.orZero(),
        driverLongitude = driver?.lng.orZero(),
        driverBearing = driver?.orientation ?: 0f,
        orderType = DriverMitraType.from(type),
        totalPrice = fare.orZero(),
        distance = distance.orZero(),
        duration = duration.orZero().roundToInt(),
        pickupAddress = originAddress.orEmpty(),
        pickupLatitude = originLatitude.orZero(),
        pickupLongitude = originLongitude.orZero(),
        destinationAddress = destinationAddress.orEmpty(),
        destinationLatitude = destinationLatitude.orZero(),
        destinationLongitude = destinationLongitude.orZero(),
        orderStatus = TransportRequestType.from(status),
        paymentBreakdown = breakdown.toOrderPaymentSpec(),
        notes = note.orEmpty(),
        listMenu = items.toMenuOrderDetail(),
        packageType = packageType,
        packageWeight = packageWeight.orZero().roundToInt(),
        insurance = insurance.orZero().roundToInt()
    )
}