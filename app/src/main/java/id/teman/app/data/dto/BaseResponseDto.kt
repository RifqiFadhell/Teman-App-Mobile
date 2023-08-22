package id.teman.app.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class BaseResponseDto(
    val statusCode: Int?,
    val message: String?,
    val error: String?
)