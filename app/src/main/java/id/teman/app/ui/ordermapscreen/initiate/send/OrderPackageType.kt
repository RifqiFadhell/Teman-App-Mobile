package id.teman.app.ui.ordermapscreen.initiate.send

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import id.teman.app.common.isNotNullOrEmpty
import id.teman.app.common.orZero
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun OrderPackageType(onClick: (String) -> Unit, errorMessage: String? = null) {
    var selectedPackage by remember { mutableStateOf<Int?>(null) }
    val packages by remember { mutableStateOf(chipPackages) }
    Text(
        "Paketnya Berupa Apa?",
        style = UiFont.poppinsP2SemiBold,
        modifier = Modifier.padding(top = Theme.dimension.size_16dp)
    )
    Row {
        packages.take(3).forEachIndexed { index, item ->
            OrderSendChip(
                item = item,
                textColor = if (selectedPackage == index) UiColor.tertiaryBlue500 else UiColor.neutral500,
                borderColor = if (selectedPackage == index) UiColor.tertiaryBlue500 else UiColor.neutral500,
            ) {
                selectedPackage = index
                onClick(item.title)
            }
        }
    }
    Row {
        packages.takeLast(3).forEachIndexed { index, item ->
            OrderSendChip(
                item = item,
                textColor = if (selectedPackage.orZero() - 3 == index) UiColor.tertiaryBlue500 else UiColor.neutral500,
                borderColor = if (selectedPackage.orZero() - 3 == index) UiColor.tertiaryBlue500 else UiColor.neutral500
            ) {
                selectedPackage = index + 3
                onClick(item.title)
            }
        }
    }
    if (errorMessage.isNotNullOrEmpty()) {
        Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
        Text(
            errorMessage!!, style = UiFont.poppinsCaptionMedium.copy(
                color = UiColor.primaryRed500
            )
        )
    }
}

@Composable
fun OrderPackageInsurance(onClick: (Int) -> Unit, errorMessage: String? = null) {
    var selectedPackage by remember { mutableStateOf<Int?>(null) }
    val packages by remember { mutableStateOf(chipInsurances) }
    Text(
        "Garansi Pengantaran*",
        style = UiFont.poppinsP2SemiBold,
        modifier = Modifier.padding(top = Theme.dimension.size_16dp)
    )
    Row {
        packages.take(3).forEachIndexed { index, item ->
            OrderSendChipInsurance(
                item = item,
                textColor = if (selectedPackage == index) UiColor.tertiaryBlue500 else UiColor.neutral500,
                borderColor = if (selectedPackage == index) UiColor.tertiaryBlue500 else UiColor.neutral500,
            ) {
                selectedPackage = index
                onClick(item.value)
            }
        }
    }
    if (errorMessage.isNotNullOrEmpty()) {
        Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
        Text(
            errorMessage!!, style = UiFont.poppinsCaptionMedium.copy(
                color = UiColor.primaryRed500
            )
        )
    }

}