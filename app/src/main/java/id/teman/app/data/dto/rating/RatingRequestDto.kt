package id.teman.app.data.dto.rating

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RatingRequestDto(
    val rate: Int,
    val note: String
)