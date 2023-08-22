package id.teman.app.domain.model.wallet

import android.os.Parcelable
import id.teman.app.R
import id.teman.app.common.convertToRupiah
import id.teman.app.common.orZero
import id.teman.app.data.dto.wallet.WalletHistoryTransactionDetail
import kotlinx.parcelize.Parcelize

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
    val icon: Int,
    val button: String? = "",
    val serialNumber: String? = "",
    val caption: String? = "",
    val category: String? = "",
    val number: String? = "",
) : Parcelable

fun WalletHistoryTransactionDetail.toWalletDetailItem(): WalletItemDetailSpec {
    val icon = when (type) {
        "topup", "pay", "transfer" -> R.drawable.teman_wallet_history_new
        "bike" -> R.drawable.ic_teman_bike
        "food" -> R.drawable.ic_teman_car
        "send" -> R.drawable.ic_teman_send
        "bills" -> R.drawable.ic_teman_bills
        else -> R.drawable.teman_wallet_history_new
    }

    val statusTransaction: String
    val caption: String
    val button: String
    when (status) {
        "success" -> {
            statusTransaction = "Berhasil"
            caption = "Terimakasih telah percaya kepada kami"
            button = "Kembali ke T-Bill"
        }
        "failed" -> {
            statusTransaction = "Gagal"
            caption = "Transaksi Gagal silahkan kembali lagi"
            button = "Kembali ke T-Bill"
        }
        else -> {
            statusTransaction = "DiProses"
            caption = "Transaksi DiProses silahkan Muat Ulang"
            button = "Muat Ulang Status"
        }
    }

    val finalAmount =
        if (type == "topup" && status == "success" || type == "topup" && status == "pending") {
            "+${amount.orZero().convertToRupiah()}"
        } else if (type == "topup" && status == "failed") {
            amount.orZero().convertToRupiah()
        } else {
            amount.orZero().convertToRupiah()
        }

    return WalletItemDetailSpec(
        id = id.orEmpty(),
        type = type.orEmpty(),
        createdAt = created_at.orEmpty(),
        updatedAt = updated_at.orEmpty(),
        status = status.orEmpty(),
        amount = finalAmount,
        title = title.orEmpty(),
        description = statusTransaction,
        provider = provider.orEmpty(),
        url = url.orEmpty(),
        icon = icon,
        button = button,
        caption = caption,
        serialNumber = sn, category = category, number = customer_no
    )
}

fun List<WalletHistoryTransactionDetail>?.toWalletListDetailSpec(): List<WalletItemDetailSpec> {
    return this?.map {
        val icon = when (it.type) {
            "topup", "pay", "transfer" -> R.drawable.teman_wallet_history_new
            "bike" -> R.drawable.ic_teman_bike
            "food" -> R.drawable.ic_teman_car
            "send" -> R.drawable.ic_teman_send
            "bills" -> R.drawable.ic_teman_bills
            else -> R.drawable.teman_wallet_history_new
        }
        val statusTransaction: String
        val caption: String
        val button: String
        when (it.status) {
            "success" -> {
                statusTransaction = "Berhasil"
                caption = "Terimakasih telah percaya kepada kami"
                button = "Kembali"
            }
            "failed" -> {
                statusTransaction = "Gagal"
                caption = "Transaksi Gagal silahkan kembali lagi"
                button = "Kembali"
            }
            else -> {
                statusTransaction = "Tertunda"
                caption = "Transaksi Tertunda silahkan Muat Ulang"
                button = "Muat Ulang Status"
            }
        }
        val finalAmount =
            if (it.type == "topup" && it.status == "success" || it.type == "topup" && it.status == "pending") {
                "+${it.amount.orZero().convertToRupiah()}"
            } else if (it.type == "topup" && it.status == "failed") {
                it.amount.orZero().convertToRupiah()
            } else {
                it.amount.orZero().convertToRupiah()
            }

        WalletItemDetailSpec(
            id = it.id.orEmpty(),
            type = it.type.orEmpty(),
            createdAt = it.created_at.orEmpty(),
            updatedAt = it.updated_at.orEmpty(),
            status = it.status.orEmpty(),
            amount = finalAmount,
            title = it.title.orEmpty(),
            description = statusTransaction,
            provider = it.provider.orEmpty(),
            url = it.url.orEmpty(),
            icon = icon,
            button = button,
            caption = caption,
            serialNumber = it.sn,
            category = it.category, number = it.customer_no
        )
    }.orEmpty()
}