package id.teman.app.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BaseResponse(
    val statusCode: Int? = null,
    val message: String? = null,
    val error: String? = null
)