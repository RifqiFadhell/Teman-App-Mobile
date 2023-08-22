package id.teman.app.domain.model.location

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import id.teman.app.common.orZero
import id.teman.app.data.dto.location.AddressComponentsDto
import id.teman.app.data.dto.location.PlaceResponseDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceDetailSpec(
    val locationLatLng: LatLng,
    val formattedAddress: String,
    val shortLocationAddress: String
) : Parcelable

fun PlaceResponseDto.toPlaceDetailSpec(): PlaceDetailSpec? {
    if (result == null) return null
    return PlaceDetailSpec(
        locationLatLng = LatLng(
            result.geometry?.location?.lat.orZero(),
            result.geometry?.location?.lng.orZero()
        ),
        formattedAddress = "${result.name}, ${result.formattedAddress.orEmpty()}",
        shortLocationAddress = result.addressComponents.orEmpty().createShortLocationAddress()
    )
}

fun List<AddressComponentsDto>.createShortLocationAddress(): String {
    if (this.isEmpty()) return ""

    val province =
        filter { it.types.orEmpty().contains("administrative_area_level_1") }.map { it.shortName }
            .firstOrNull()
    val city =
        filter { it.types.orEmpty().contains("administrative_area_level_2") }.map { it.shortName }
            .firstOrNull()
    val district =
        filter { it.types.orEmpty().contains("administrative_area_level_3") }.map { it.shortName }
            .firstOrNull()
    val subdistrict =
        filter { it.types.orEmpty().contains("administrative_area_level_4") }.map { it.shortName }
            .firstOrNull()

    val chosenFirstWord = city.orEmpty().ifEmpty { province.orEmpty().ifEmpty { "" } }
    val chosenSecondWord = district.orEmpty().ifEmpty { subdistrict.orEmpty().ifEmpty { "" } }

    return "$chosenSecondWord, $chosenFirstWord"
}