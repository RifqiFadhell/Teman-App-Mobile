package id.teman.app.data.dto.wallet.pin

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class PinWalletRequestDto(
    val pin: String?,
    val token: String? = ""
)

@Serializable
@Keep
data class OtpPinWalletRequestDto(
    val code: String?
)