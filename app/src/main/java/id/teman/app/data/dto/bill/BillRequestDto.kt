package id.teman.app.data.dto.bill

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ListPulsaRequestDto(
    val phone_number: String?
)

@Keep
@Serializable
data class BuyPulsaRequestDto(
    val phone_number: String?,
    val code: String?,
    val pin: String?
)

@Keep
@Serializable
data class ListBillRequestDto(
    val category: String?,
    val customer_no: String? = "",
)

@Keep
@Serializable
data class BuyBillRequestDto(
    val customer_no: String?,
    val category: String?,
    val code: String?,
    val pin: String?
)