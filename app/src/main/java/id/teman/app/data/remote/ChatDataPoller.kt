package id.teman.app.data.remote

import id.teman.app.domain.model.chat.ChatMessageSpec
import id.teman.app.domain.repository.chat.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

interface ChatPoller {
    fun poller(
        delay: Long,
        isActive: Boolean
    ): Flow<List<ChatMessageSpec>>
}

class ChatDataPoller(
    private val userId: String,
    private val requestId: String,
    private val chatRepository: ChatRepository
) : ChatPoller {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun poller(
        delay: Long,
        isActive: Boolean,
    ): Flow<List<ChatMessageSpec>> {
        return channelFlow {
            while (!isClosedForSend) {
                if (!isActive) {
                    close()
                    return@channelFlow
                }
                delay(delay)
                chatRepository.getChatMessages(
                    currentUserId = userId,
                    requestId = requestId,
                ).catch { exception ->
                    close(exception)
                }.collect {
                    send(it)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

}