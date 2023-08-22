package id.teman.app.utils

import android.content.res.Resources
import android.util.TypedValue
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

fun getChatCurrentTime(date: String?): String {
    if (date.isNullOrEmpty()) return ""
    val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    val formattedDate = apiFormat.parse(date)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("GMT+14")
    return formattedDate?.let { format.format(formattedDate) }.orEmpty()
}

fun getCurrentTimeFormat(date: String?, formatted: String = "EE, dd MMMM yyyy"): String {
    if (date.isNullOrEmpty()) return ""
    val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    val formattedDate = apiFormat.parse(date)
    val format = SimpleDateFormat(formatted, Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("GMT+14")
    return formattedDate?.let { format.format(formattedDate) }.orEmpty()
}

fun dpToPx(resources: Resources, dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).roundToInt()
}

fun getDurationTime(secondDuration: Long): String {
    val hour = secondDuration / 3600
    val minute = (secondDuration % 3600) / 60
    return if (secondDuration < 60) {
        "< 1 min"
    } else if (hour < 1) {
        "$minute min"
    } else {
        "$hour hour $minute min"
    }
}

fun minutesToReadableText(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    val resultBuilder = StringBuilder()
    if (hours > 0) {
        resultBuilder.append("$hours jam")
    }
    if (remainingMinutes > 0) {
        resultBuilder.append("$minutes menit")
    }

    return resultBuilder.toString()
}

fun String.convertUtiIso8601ToTimeOnly(): String {
    val utcFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    val utcZoned = ZonedDateTime.parse(this, utcFormatter)
    val localFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val localZoned = utcZoned.withZoneSameInstant(ZoneId.systemDefault())
    val hour = if (localZoned.hour < 12) "AM" else "PM"
    val format = localZoned.format(localFormatter)
    return "$format $hour"
}

fun String.convertUtcIso8601ToLocalTimeAgo(): String {
    val utcFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    val utcZoned = ZonedDateTime.parse(this, utcFormatter)
    val localFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val localZoned = utcZoned.withZoneSameInstant(ZoneId.systemDefault())
    val finalTime = localZoned.format(localFormatter)

    val now = ZonedDateTime.now()
    val days = now.toLocalDate().until(localZoned.toLocalDate(), ChronoUnit.DAYS)
    val dayOfWeek = localZoned.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val hour = if (localZoned.hour < 12) "AM" else "PM"

    return when (days) {
        0L -> "Hari ini $finalTime"
        1L -> "Kemarin $finalTime"
        in 2..6 -> "Hari $dayOfWeek, $finalTime"
        else -> {
            val fullDate = localZoned.format(DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm"))
            "$fullDate $hour"
        }
    }
}

fun String.convertToNotificationDate(): String {
    val utcFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    val utcZoned = ZonedDateTime.parse(this, utcFormatter)
    val localFormatter = DateTimeFormatter.ofPattern("dd MMM")
    val localZoned = utcZoned.withZoneSameInstant(ZoneId.systemDefault())
    return localZoned.format(localFormatter)
}

fun String.addCharEveryFour(char: String): String {
    val result = StringBuilder()
    var i = 0
    for (c in this) {
        result.append(c)
        i++
        if (i % 4 == 0 && i != this.length) {
            result.append(char)
        }
    }
    return result.toString()
}

fun decimalFormat(value: Double) : String {
    val format = DecimalFormat("###.#")
    return format.format(value)
}