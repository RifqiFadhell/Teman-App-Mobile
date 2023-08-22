package id.teman.app.data.dto.chat

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ChatRequestDto(
    val text: String
)