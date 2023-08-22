package id.teman.app.ui.ordermapscreen.initiate.send

import android.os.Parcelable
import id.teman.app.domain.model.location.PlaceDetailSpec
import kotlinx.parcelize.Parcelize

@Parcelize
data class FormDetailSpec(
    val origin: PlaceDetailSpec,
    val destination: PlaceDetailSpec,
    val nearbyArea: String? = null,
    val note: String? = null,
    val receiverName: String,
    val receiverPhoneNumber: String,
    val packageType: String,
    val packageWeight: Int = 0,
    val insurance: Int = 0,
) : Parcelable

data class SendDetailSpec(
    val note: String? = null,
    val receiverName: String,
    val receiverPhoneNumber: String,
    val packageType: String = "",
    val packageWeight: Int = 0
)

@Parcelize
data class PromoSpec(
    val id: String = "",
    val titlePromo: String = "Pilih Promo"
): Parcelable