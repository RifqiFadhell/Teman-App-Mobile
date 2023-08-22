package id.teman.app.domain.model.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class FoodOrderRequestSpec(
    val restaurantId: String,
    val orderedItems: List<FoodOrderItemRequestSpec>
) : Parcelable

@Parcelize
@Serializable
data class FoodOrderItemRequestSpec(
    val productId: String,
    val quantity: Int,
    val note: String? = null
) : Parcelable