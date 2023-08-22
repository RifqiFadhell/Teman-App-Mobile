package id.teman.app.data.dto.location

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class PlaceResponseDto(
    val result: PlacesResultDto? = null
)

@Keep
@Serializable
data class PlacesResultDto(
    val name: String? = null,
    val geometry: GeometryDto? = null,
    @SerialName("formatted_address")
    val formattedAddress: String? = null,
    @SerialName("address_components")
    val addressComponents: List<AddressComponentsDto>? = null
)

@Keep
@Serializable
data class AddressComponentsDto(
    @SerialName("short_name")
    val shortName: String? = null,
    val types: List<String>? = null
)

@Keep
@Serializable
data class GeometryDto(
    val location: LocationDto? = null,
    val viewport: ViewPortDto? = null
)

@Keep
@Serializable
data class ViewPortDto(
    val northeast: LocationDto? = null,
    val southwest: LocationDto? = null
)