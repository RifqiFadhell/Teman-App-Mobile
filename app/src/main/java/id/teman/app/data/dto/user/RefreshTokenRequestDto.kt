package id.teman.app.data.dto.user

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class RefreshTokenRequestDto(
    val token: String
)