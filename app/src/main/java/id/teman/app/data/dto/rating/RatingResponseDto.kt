package id.teman.app.data.dto.rating

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RatingResponseDto(
    val note: String? = null,
    val id: String? = null,
    val rate: Int? = null
)