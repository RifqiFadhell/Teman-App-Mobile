package id.teman.app.domain.model.reward

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import id.teman.app.R
import id.teman.app.common.orZero
import id.teman.app.data.dto.reward.Reward
import id.teman.app.data.dto.reward.RewardRedeemed
import id.teman.app.data.dto.reward.RewardTransaction
import id.teman.app.utils.getCurrentTimeFormat
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class ItemReward(
    val id: String,
    val title: String,
    val description: String,
    val point: Int,
    val type: String,
    val startDate: String,
    val endDate: String,
    val url: String
)

fun List<Reward>?.toListRewards(): List<ItemReward> {
    return this?.map {
        ItemReward(
            id = it.id.orEmpty(),
            title = it.title.orEmpty(),
            description = it.description.orEmpty(),
            point = it.point.orZero(),
            type = it.type.orEmpty(),
            startDate = getCurrentTimeFormat(it.start_at),
            endDate = getCurrentTimeFormat(it.end_at),
            url = it.image?.url.orEmpty(),
        )
    }.orEmpty()
}

@Serializable
@Keep
data class ItemRewardRedeemed(
    val id: String,
    val status: String,
    val reward: ItemReward
)

fun List<RewardRedeemed>?.toListRewardRedeemed(): List<ItemRewardRedeemed> {
    return this?.map {
        ItemRewardRedeemed(
            id = it.id.orEmpty(),
            status = it.status.orEmpty(),
            reward = ItemReward(
                id = it.reward?.id.orEmpty(),
                title = it.reward?.title.orEmpty(),
                description = it.reward?.description.orEmpty(),
                point = it.reward?.point.orZero(),
                type = it.reward?.type.orEmpty(),
                startDate = getCurrentTimeFormat(it.reward?.start_at),
                endDate = getCurrentTimeFormat(it.reward?.end_at),
                url = it.reward?.image?.url.orEmpty(),
            )
        )
    }.orEmpty()
}

fun List<RewardTransaction>?.toHistoryPoint(): List<ItemRewardTransaction> {
    return this?.map {
        val icon = if (it.key == "customer_point" || it.key == "register_point") {
            R.drawable.ic_giftcard
        } else {
            R.drawable.ic_money_withdraw
        }
        ItemRewardTransaction(
            id = it.id.orEmpty(),
            title = it.title.orEmpty(),
            description = it.description.orEmpty(),
            amount = it.amount.orZero(),
            key = it.key.orEmpty(),
            date = getCurrentTimeFormat(it.created_at.orEmpty()),
            icon = icon,
        )
    }.orEmpty()
}

@Serializable
@Keep
data class ItemRewardTransaction(
    val id: String,
    val title: String,
    val description: String,
    val amount: Int,
    val key: String,
    val date: String,
    @DrawableRes val icon: Int
)

