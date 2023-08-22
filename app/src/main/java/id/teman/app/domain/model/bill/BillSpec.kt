package id.teman.app.domain.model.bill

import android.os.Parcelable
import id.teman.app.common.convertToRupiah
import id.teman.app.common.orZero
import id.teman.app.data.dto.bill.ItemBillResponseDto
import id.teman.app.data.dto.bill.ItemCategoryBillDto
import id.teman.app.data.dto.bill.SuccessBuyBill
import kotlinx.parcelize.Parcelize

data class CategoryBillSpec(
    val name: String,
    val value: String,
    val key: String,
    val icon: String,
    val titleInput: String,
    val caption: String,
    val placeHolder: String,
    val information: String,
    val categoryKey: String,
)

fun List<ItemCategoryBillDto>?.toListCategorySpec(): List<CategoryBillSpec> {
    return this?.map {
        CategoryBillSpec(
            name = it.title.orEmpty(),
            value = it.value.orEmpty(),
            key = it.key.orEmpty(),
            icon = it.icon.orEmpty(),
            titleInput = it.title_input.orEmpty(),
            caption = it.title_list.orEmpty(),
            placeHolder = it.placeholder.orEmpty(),
            information = it.information.orEmpty(),
            categoryKey = it.category.orEmpty()
        )
    }.orEmpty()
}

data class BillSpec(
    val name: String,
    val provider: String,
    val code: String,
    val price: String
)

fun List<ItemBillResponseDto>?.toListBillSpec(): List<BillSpec> {
    return this?.map {
        BillSpec(
            it.name.orEmpty(),
            it.category.orEmpty(),
            it.code.orEmpty(),
            it.price?.orZero()?.toDouble()?.convertToRupiah().orEmpty()
        )
    }.orEmpty()
}

@Parcelize
data class ProviderSpec(
    val icon: String = "",
    val titleBar: String = "",
    val titleInput: String = "",
    val placeHolder: String = "",
    val caption: String = "",
    val key: String = "",
    val information: String = "",
    val categoryKey: String = ""
) : Parcelable

data class SuccessBillSpec(
    val description: String?,
    val status: String?,
    val statusConverted: String?,
    val sn: String? = "",
    val title: String? = "",
    val id: String? = "",
    val caption: String? = "",
    val button: String? = "",
)

fun SuccessBuyBill.toSuccessBillSpec(): SuccessBillSpec {
    with(this) {
        val statusTransaction: String
        val caption: String
        val button: String
        if (status == "success") {
            statusTransaction = "Berhasil"
            caption = "Terimakasih telah percaya kepada kami"
            button = "Kembali ke T-Bill"
        } else {
            statusTransaction = "DiProses"
            caption = "Transaksi DiProses silahkan Muat Ulang"
            button = "Muat Ulang Status"
        }
        return SuccessBillSpec(
            description,
            status,
            statusTransaction,
            sn,
            title,
            id,
            caption,
            button
        )
    }
}

fun String.convertErrorBill(): String {
    return when (this) {
        "400" -> "Kode Pin yang kamu masukkan Salah"
        "402" -> "Saldo kamu tidak cukup, silahkan Isi Ulang Kantong"
        else -> "Terjadi kesalahan di sisi kami."
    }
}



