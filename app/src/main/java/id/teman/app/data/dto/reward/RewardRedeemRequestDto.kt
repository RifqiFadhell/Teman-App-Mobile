package id.teman.app.data.dto.reward

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class RewardRedeemRequestDto(
    val reward_id: String?
)