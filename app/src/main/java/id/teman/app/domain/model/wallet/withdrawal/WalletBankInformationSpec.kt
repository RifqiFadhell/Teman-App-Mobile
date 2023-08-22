package id.teman.app.domain.model.wallet.withdrawal

import android.os.Parcelable
import androidx.annotation.Keep
import id.teman.app.data.dto.wallet.withdrawal.ItemBankDto
import id.teman.app.data.dto.wallet.withdrawal.WalletBankAccountDto

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class WalletBankInformationSpec(
    val bankName: String,
    val accountNumber: String,
    val accountName: String
)

fun WalletBankAccountDto.toWalletBankInformationSpec(): WalletBankInformationSpec {
    return WalletBankInformationSpec(
        bankName = bankName.orEmpty(),
        accountName = accountName.orEmpty(),
        accountNumber = accountNumber.orEmpty()
    )
}

@Serializable
data class ItemBankSpec(
    val bankName: String,
    val bankCode: String
)

fun List<ItemBankDto>?.convertToListBank(): List<ItemBankSpec> {
    return this?.map {
        ItemBankSpec(
            bankName = it.bankName.orEmpty(),
            bankCode = it.bankCode.orEmpty()
        )
    }.orEmpty()
}

@Serializable
data class WalletDataTransferSpec(
    val bankName: String,
    val accountNumber: String,
    val accountName: String,
    val withdrawalAmount: Double
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

@Parcelize
data class WalletItemDetailSpec(
    val id: String,
    val type: String,
    val createdAt: String,
    val updatedAt: String,
    val status: String,
    val amount: String,
    val title: String,
    val description: String,
    val provider: String,
    val url: String,
    val button: String? = "",
    val serialNumber: String? = "",
    val caption: String? = "",
    val category: String? = "",
    val number: String? = "",
) : Parcelable