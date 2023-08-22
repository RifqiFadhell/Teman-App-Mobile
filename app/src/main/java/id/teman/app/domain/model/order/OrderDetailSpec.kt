package id.teman.app.domain.model.order

import android.os.Parcelable
import id.teman.app.domain.model.restaurant.MenuDetailOrder
import id.teman.app.domain.model.restaurant.MenuRestaurant
import id.teman.app.domain.model.user.DriverMitraType
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderDetailSpec(
    val requestId: String,
    val driverPhoto: String,
    val driverName: String,
    val driverPhoneNumber: String,
    val isDriverVaccine: Boolean,
    val driverVehicleLicenseNumber: String,
    val driverVehicleBrand: String,
    val driverVehicleType: String,
    val driverRating: Double,
    val orderType: DriverMitraType,
    val paymentMethod: String,
    val totalPrice: Double,
    val distance: Double,
    val duration: Int,
    val pickupAddress: String,
    val pickupLatitude: Double,
    val pickupLongitude: Double,
    val destinationAddress: String,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val orderStatus: TransportRequestType,
    val paymentBreakdown: List<OrderPaymentSpec>,
    val notes: String,
    val packageWeight: Int? = null,
    val packageType: String? = null,
    val insurance: Int? = null,
    val driverLatitude: Double,
    val driverLongitude: Double,
    val driverBearing: Float,
    val listMenu: List<MenuDetailOrder> = emptyList()
) : Parcelable