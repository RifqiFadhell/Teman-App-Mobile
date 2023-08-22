package id.teman.app.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class LoginRequest(
    val phone_number: String?,
    val fcm_token: String?
)

@Serializable
@Keep
data class RegisterRequest(
    val phone_number: String?,
    val name: String?,
    val fcm_token: String?,
    val referral_code: String? = "",
)

@Serializable
@Keep
data class VerifyOtpRequest(
    val code: String?
)