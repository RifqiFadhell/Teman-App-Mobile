package id.teman.app.ui.notification

import id.teman.app.R as RApp
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.MainActivity
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.EmptyState
import id.teman.app.common.noRippleClickable
import id.teman.app.dialog.GeneralActionButton
import id.teman.app.dialog.GeneralDialogPrompt
import id.teman.app.ui.NavGraphs
import id.teman.app.ui.destinations.HomeScreenDestination
import id.teman.app.ui.notification.domain.model.NotificationUiSpec
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun NotificationScreen(
    navigator: DestinationsNavigator,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getNotifications()
    }
    val context = LocalContext.current
    BackHandler {
        navigator.navigate(HomeScreenDestination) {
            popUpTo(NavGraphs.root) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    if (uiState.successGetNotification.isEmpty()) EmptyState(icon = RApp.drawable.ic_no_notification, title = "Belum Ada Notifikasi", description = "Tenang aja, kita bakal kabari kamu ketika ada notifikasi baru.")

    var showMarkAllReadDialog by remember { mutableStateOf(false) }
    AnimatedVisibility(visible = showMarkAllReadDialog) {
        GeneralDialogPrompt(
            title = "Tandai semua telah dibaca?",
            subtitle = "Semua notifikasi akan ditandai sebagai pesan yang sudah terbaca",
            actionButtons = {
                GeneralActionButton(
                    text = "Batal",
                    textColor = UiColor.neutral900,
                    isFirstAction = true
                ) {
                    showMarkAllReadDialog = false
                }
                GeneralActionButton(
                    text = "Lanjutkan",
                    textColor = UiColor.neutral900,
                    isFirstAction = false
                ) {
                    // TODO: need call api or at least change data locally to all read
                }
            },
            onDismissRequest = { showMarkAllReadDialog = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = UiColor.primaryRed500
            )
        } else {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Theme.dimension.size_16dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Notification", style = UiFont.poppinsH3SemiBold)
                    GlideImage(
                        imageModel = RApp.drawable.ic_notification_checklist,
                        modifier = Modifier
                            .size(Theme.dimension.size_24dp)
                            .noRippleClickable {
                                if (uiState.successGetNotification.isNotEmpty()) {
                                    viewModel.readAllNotification()
                                    showMarkAllReadDialog = true
                                }
                            }
                    )
                }

                LazyColumn {
                    itemsIndexed(uiState.successGetNotification) { _, item ->
                        NotificationRowItem(item) {
                            try {
                                viewModel.readNotification(item.id)
                                (context as MainActivity).startActivity(
                                    Intent(Intent.ACTION_VIEW, item.url.toUri())
                                )
                            } catch (e: Exception) {
                                e.stackTrace
                                Toast.makeText(
                                    context,
                                    "Mohon maaf, kami tidak dapat membuka url ini",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRowItem(item: NotificationUiSpec, onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(Theme.dimension.size_8dp)
            .border(
                BorderStroke(
                    width = Theme.dimension.size_1dp,
                    color = UiColor.neutral50
                ),
                shape = RoundedCornerShape(Theme.dimension.size_16dp)
            )
            .padding(
                horizontal = Theme.dimension.size_12dp,
                vertical = Theme.dimension.size_20dp
            )
            .clickable { onClick() }
    ) {
        TemanCircleButton(
            icon = item.type.icon,
            iconModifier = Modifier.size(Theme.dimension.size_24dp),
            circleModifier = Modifier
                .align(Alignment.CenterVertically)
                .size(Theme.dimension.size_40dp),
            circleBackgroundColor = item.type.backgroundColor,
            iconColor = item.type.iconColor
        )
        Column(
            modifier = Modifier
                .padding(start = Theme.dimension.size_8dp)
                .weight(1f)
        ) {
            Text(
                item.title,
                style = UiFont.poppinsCaptionSemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                item.subtitle,
                style = UiFont.poppinsCaptionMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                item.date, style = UiFont.poppinsCaptionSmallMedium,
                modifier = Modifier.padding(start = Theme.dimension.size_4dp)
            )
            if (!item.isNotificationOpen) {
                Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(Theme.dimension.size_12dp)
                        .background(color = UiColor.blue)
                )
            }
        }
    }
}