package id.teman.app.ui.notification.domain.model

import id.teman.app.R as RApp
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import id.teman.app.common.orFalse
import id.teman.app.data.dto.notification.NotificationDto
import id.teman.app.utils.convertToNotificationDate
import id.teman.coreui.typography.UiColor

enum class NotificationType(
    @DrawableRes val icon: Int,
    val backgroundColor: Color,
    val iconColor: Color
) {
    Promo(RApp.drawable.ic_promo, UiColor.primaryRed50, UiColor.primaryRed500),
    Bills(RApp.drawable.ic_notification_bills, UiColor.tertiaryBlue50, UiColor.tertiaryBlue500),
    Rating(RApp.drawable.ic_star, UiColor.success50, UiColor.success500),
}

data class NotificationUiSpec(
    val type: NotificationType,
    val title: String,
    val subtitle: String,
    val date: String,
    val isNotificationOpen: Boolean,
    val url: String,
    val id: String
)

fun NotificationDto.toListNotificationSpec(): List<NotificationUiSpec> {
    if (data.isNullOrEmpty() && pageCount == 0) return emptyList()
    return data?.map {
        NotificationUiSpec(
            id = it.id.orEmpty(),
            title = it.title.orEmpty(),
            subtitle = it.description.orEmpty(),
            date = it.createdAt.orEmpty().convertToNotificationDate(),
            isNotificationOpen = it.read.orFalse(),
            url = it.url.orEmpty(),
            type = if (it.type == "update" || it.type == "rating") NotificationType.Rating else NotificationType.Rating
        )
    }.orEmpty()
}