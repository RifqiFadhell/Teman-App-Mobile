package id.teman.app.data.dto.wallet

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class WalletBalanceDto(
    val balance: Double? = 0.0
)

@Serializable
@Keep
data class WalletHistoryTransactionDto(
    val data: List<WalletHistoryTransactionDetail>?,
    val count: Int?,
    val total: Int?,
    val page: Int?,
    val pageCount: Int?
)

@Serializable
@Keep
data class WalletHistoryTransactionDetail(
    val id: String? = null,
    val type: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val status: String? = null,
    val sn: String? = null,
    val amount: Double? = 0.0,
    val title: String? = null,
    val description: String? = null,
    val provider: String? = null,
    val url: String? = null,
    val category: String? = null,
    val customer_no: String? = null,
)
