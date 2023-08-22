package id.teman.app.domain.repository.chat

import id.teman.app.data.dto.chat.ChatRequestDto
import id.teman.app.data.remote.chat.ChatRemoteDataSource
import id.teman.app.domain.model.chat.ChatMessageSpec
import id.teman.app.utils.convertUtcIso8601ToLocalTimeAgo
import id.teman.app.utils.convertUtiIso8601ToTimeOnly
import id.teman.app.utils.getChatCurrentTime
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ChatRepository @Inject constructor(
    private val chatRemoteDataSource: ChatRemoteDataSource
) {

    suspend fun getChatMessages(
        currentUserId: String,
        requestId: String
    ): Flow<List<ChatMessageSpec>> = flow {
        chatRemoteDataSource.getChatMessages(requestId)
            .catch { exception -> throw exception }
            .collect {
                it.data?.let { messages ->
                    val chatMessages = messages.map { message ->
                        ChatMessageSpec(
                            isSelfMessage = message.userId == currentUserId,
                            sendTime = message.sentTime.orEmpty().convertUtiIso8601ToTimeOnly(),
                            message = message.text.orEmpty()
                        )
                    }
                    emit(chatMessages)
                }
            }
    }

    suspend fun sendChatMessage(
        currentUserId: String,
        requestId: String,
        message: String
    ): Flow<ChatMessageSpec> =
        chatRemoteDataSource.sendMessage(requestId, ChatRequestDto(message)).map {
            ChatMessageSpec(
                isSelfMessage = currentUserId == it.userId,
                sendTime = getChatCurrentTime(it.sentTime),
                message = it.text.orEmpty().convertUtcIso8601ToLocalTimeAgo()
            )
        }
}