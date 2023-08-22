package id.teman.app.domain.model.order

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderEstimationResponseSpec(
    val distance: String,
    val totalPrice: Double,
    val paymentBreakdown: List<OrderPaymentSpec>,
    val originAddress: String,
    val destinationAddress: String,
    val originLatLng: LatLng,
    val destinationLatLng: LatLng,
    val paymentMethod: String,
    val notes: String,
    val pin: String? = null,
    val receiverName: String? = null,
    val receiverPhoneNumber: String? = null,
    val packageType: String? = null,
    val packageWeight: Int? = null,
    val insurance: Int? = null,
) : Parcelable