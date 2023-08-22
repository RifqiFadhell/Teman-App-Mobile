package id.teman.app.data.dto.user

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class SimpleUserDetailDto(
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @SerialName("user_photo")
    val photo: UserPhotoDto? = null,
    val id: String? = null,
    val name: String? = null,
)

@Keep
@Serializable
data class UserPhotoDto(
    val url: String? = null
)