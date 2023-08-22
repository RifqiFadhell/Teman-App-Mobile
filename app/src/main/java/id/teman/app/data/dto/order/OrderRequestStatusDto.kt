package id.teman.app.data.dto.order

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class OrderRequestStatusDto(
    val status: String
)