package id.teman.app.ui.ordermapscreen.initiate.send

import androidx.annotation.DrawableRes
import id.teman.app.R

data class PackageUISpec(
    val title: String,
    @DrawableRes val icon: Int? = null
)

val chipPackages = listOf(
    PackageUISpec("Makanan", R.drawable.ic_send_chip_food),
    PackageUISpec("Baju", R.drawable.ic_send_chip_clothes),
    PackageUISpec("Dokumen", R.drawable.ic_send_chip_document),
    PackageUISpec("Obat obatan", R.drawable.ic_send_chip_medicine),
    PackageUISpec("Buku", R.drawable.ic_send_chip_book),
    PackageUISpec("Lainnya"),
)

data class InsuranceUISpec(
    val title: String, val value: Int)

val chipInsurances = listOf(
    InsuranceUISpec("Rp1.000", 1000),
    InsuranceUISpec("Rp2.000", 2000),
    InsuranceUISpec("Rp3.000", 3000),

)
