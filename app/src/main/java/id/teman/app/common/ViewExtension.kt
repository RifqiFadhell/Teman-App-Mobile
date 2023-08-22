package id.teman.app.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun Int.convertToDp(): Dp {
    val configuration = LocalConfiguration.current
    return if (configuration.screenWidthDp <= 360) {
        (this * .875).dp
    } else {
        this.dp
    }
}

@Composable
fun Int.convertToSp(): TextUnit {
    val configuration = LocalConfiguration.current
    return if (configuration.screenWidthDp <= 360) {
        (this * .875).sp
    } else {
        this.sp
    }
}

fun createPartFromString(stringData: String): RequestBody {
    return stringData.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun Boolean?.orFalse(): Boolean = this ?: false

fun Int?.orZero(): Int = this ?: 0

fun Double?.orZero(): Double = this ?: 0.0

fun Long?.orZero(): Long = this ?: 0L

fun String?.isNotNullOrEmpty(): Boolean = this != null && this.isNotEmpty()

fun Double.convertToKilometre(): String {
    if (this < 1000) return "$this m"
    val result = (this / 1000f).toFloat()
    return "${String.format("%.2f", result)} Km"
}

fun Double.convertToRupiah(): String {
    val localeID = Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    numberFormat.minimumFractionDigits = 0
    return numberFormat.format(this)
}