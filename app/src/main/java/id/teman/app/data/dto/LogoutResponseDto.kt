package id.teman.app.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LogoutResponseDto(
    val message: String? = null
)