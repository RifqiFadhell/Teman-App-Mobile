package id.teman.app.data.dto.notification

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NotificationReadResponseDto(
    val message: String
)