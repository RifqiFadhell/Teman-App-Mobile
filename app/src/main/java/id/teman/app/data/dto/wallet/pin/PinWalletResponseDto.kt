package id.teman.app.data.dto.wallet.pin

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class OtpWalletDto(
    val status: Int?,
    val message: String?,
    val attemption: Int?,
    val wait_in_seconds: Int?,
)

@Keep
@Serializable
data class UpdatePinWalletDto(
    val message: String?
)

@Keep
@Serializable
data class VerifyOtpWalletDto(
    val token: String?,
    val message: String?
)
