package id.teman.app.data.dto.bill

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ItemBillResponseDto(
    val name: String?,
    val category: String?,
    val code: String?,
    val price: Int?,
)

@Keep
@Serializable
data class ItemCategoryBillDto(
    val key: String?,
    val title: String?,
    val value: String?,
    val category: String?,
    val description: String?,
    val icon: String?,
    val title_input: String?,
    val placeholder: String?,
    val information: String?,
    val title_list: String?,
)

@Keep
@Serializable
data class SuccessBuyBill(
    val description: String?,
    val status: String?,
    val sn: String? = "",
    val title: String? = "",
    val id: String? = "",
)