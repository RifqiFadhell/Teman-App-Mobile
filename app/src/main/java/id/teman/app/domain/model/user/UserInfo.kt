package id.teman.app.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val phoneNumber: String,
    val isVerified: Boolean,
    val kycStatus: UserKycStatus,
    val pinStatus: Boolean,
    val userPhoto: String,
    val point: Double? = 0.0,
    val referralCode: String? = "",
    val notification: Boolean = true
)

@Serializable
enum class UserKycStatus(val status: String) {
    UNPROCESSED("unprocessed"),
    REQUESTING("requesting"),
    REJECTED("rejected"),
    APPROVED("approved");

    companion object {
        fun from(value: String?) = when (value) {
            UNPROCESSED.status -> UNPROCESSED
            REQUESTING.status -> REQUESTING
            REJECTED.status -> REJECTED
            APPROVED.status -> APPROVED
            else -> UNPROCESSED
        }
    }
}