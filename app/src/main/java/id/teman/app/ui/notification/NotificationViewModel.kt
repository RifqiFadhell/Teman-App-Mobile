package id.teman.app.ui.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.domain.repository.notification.NotificationRepository
import id.teman.app.ui.notification.domain.model.NotificationUiSpec
import id.teman.app.utils.Event
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    var uiState by mutableStateOf(NotificationUIState())
        private set

    fun getNotifications() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            notificationRepository.getNotifications()
                .catch { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        exception = Event(exception.message.orEmpty())
                    )
                }.collect { notification ->
                    uiState = uiState.copy(isLoading = false, successGetNotification = notification)
                }
        }
    }

    fun readNotification(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationRepository.readNotification(id)
                .catch { exception -> }
                .collect {
                    /* no-op */
                    val notification = uiState.successGetNotification.toMutableList()
                    val index = notification.indexOfFirst { it.id == id }
                    notification[index] = notification[index].copy(isNotificationOpen = true)
                    uiState = uiState.copy(successGetNotification = notification)
                }
        }
    }

    fun readAllNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationRepository.readAllNotification()
                .catch { }
                .collect {
                    uiState =
                        uiState.copy(successGetNotification = uiState.successGetNotification.map {
                            it.copy(isNotificationOpen = true)
                        })
                }
        }
    }
}

data class NotificationUIState(
    val isLoading: Boolean = false,
    val successGetNotification: List<NotificationUiSpec> = emptyList(),
    val exception: Event<String>? = null
)