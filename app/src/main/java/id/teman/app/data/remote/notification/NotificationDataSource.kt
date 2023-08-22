package id.teman.app.data.remote.notification

import id.teman.app.data.dto.notification.NotificationDto
import id.teman.app.data.dto.notification.NotificationReadRequestDto
import id.teman.app.data.dto.notification.NotificationReadResponseDto
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface NotificationDataSource {
    suspend fun getNotificationList(): Flow<NotificationDto>
    suspend fun updateNotificationRead(id: String): Flow<NotificationReadResponseDto>
    suspend fun readAllNotification(): Flow<NotificationReadResponseDto>
}

class DefaultNotificationDataSource(
    private val httpClient: ApiServiceInterface
) : NotificationDataSource {

    override suspend fun getNotificationList(): Flow<NotificationDto> =
        handleRequestOnFlow { httpClient.getNotifications() }

    override suspend fun updateNotificationRead(id: String): Flow<NotificationReadResponseDto> =
        handleRequestOnFlow { httpClient.readNotification(NotificationReadRequestDto(id)) }

    override suspend fun readAllNotification(): Flow<NotificationReadResponseDto> =
        handleRequestOnFlow { httpClient.readAllNotification() }
}