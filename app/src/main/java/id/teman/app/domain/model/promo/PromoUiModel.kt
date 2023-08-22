package id.teman.app.domain.model.promo

import id.teman.app.common.convertToRupiah
import id.teman.app.common.orZero
import id.teman.app.data.dto.CouponPromoItem
import id.teman.app.domain.model.TypeOrder
import id.teman.app.utils.decimalFormat
import id.teman.app.utils.getCurrentTimeFormat

data class PromoUiModel(
    val code: String,
    val imageUrl: String,
    val title: String,
    val type: String,
    val status: String,
    val expired: String,
    val discountValue: String,
    val discountType: String,
    val sectionTitle: PromoFeature
)

fun List<CouponPromoItem>?.toPromoModel(): List<PromoUiModel> {
    return this?.map { item ->
        with(item) {
            val category = when(type) {
                TypeOrder.BIKE.value -> PromoFeature.BIKE
                TypeOrder.CAR.value -> PromoFeature.CAR
                TypeOrder.SEND.value -> PromoFeature.SEND
                TypeOrder.FOOD.value -> PromoFeature.FOOD
                else -> PromoFeature.ALL
            }
            val value = when(discount_type) {
                "amount" -> discount_value?.convertToRupiah()
                "percent" -> "${decimalFormat(discount_value.orZero())} %"
                else -> discount_value?.convertToRupiah()
            }

            PromoUiModel(
                code = id.orEmpty(),
                imageUrl = image?.url.orEmpty(),
                title = "$title $value",
                type = type.orEmpty(),
                status = status.orEmpty(),
                expired = getCurrentTimeFormat(end_at),
                discountValue = value.orEmpty(),
                discountType = discount_type.orEmpty(),
                sectionTitle = category,
            )
        }

    }.orEmpty()
}

enum class PromoFeature(
    val title: String
) {
    ALL("Semua Promo"),
    FOOD("T-Food"),
    BIKE("T-Bike"),
    CAR("T-Car"),
    SEND("T-Send"),
}