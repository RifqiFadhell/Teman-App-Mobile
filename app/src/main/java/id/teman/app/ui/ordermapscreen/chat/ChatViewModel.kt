package id.teman.app.ui.ordermapscreen.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.domain.model.chat.ChatMessageSpec
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.domain.repository.chat.ChatRepository
import id.teman.app.preference.Preference
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val json: Json,
    private val preference: Preference
) : ViewModel() {

    var isChatPollerActive by mutableStateOf(true)
        private set

    var chatUiState by mutableStateOf(ChatUiSpec())
        private set

    fun stopEmitData() {
        isChatPollerActive = false
    }

    fun initData(requestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatUiState = chatUiState.copy(loading = true)
            var userInfo: UserInfo? = null
            val userInfoJson = preference.getUserInfo.first()
            if (userInfoJson.isNotBlank()) {
                userInfo = json.decodeFromString(userInfoJson)
            }
            if (!isChatPollerActive) isChatPollerActive = true

            while (isChatPollerActive) {
                delay(3000)
                chatRepository.getChatMessages(userInfo!!.id, requestId)
                    .catch { chatUiState = chatUiState.copy(loading = false) }
                    .collect {
                        chatUiState = chatUiState.copy(loading = false, chatMessages = it)
                    }
            }
        }
    }

    fun sendMessage(requestId: String, text: String) {
        val userInfoJson = runBlocking { preference.getUserInfo.first() }
        if (userInfoJson.isNotBlank()) {
            val userInfo = json.decodeFromString<UserInfo>(userInfoJson)
            viewModelScope.launch(Dispatchers.IO) {
                chatRepository.sendChatMessage(userInfo.id, requestId, text)
                    .catch { exception ->
                        /* no-op */
                        print(exception)
                    }.collect { item ->
                        val chatMessage = chatUiState.chatMessages.toMutableList()
                        chatMessage.add(item)
                        chatUiState = chatUiState.copy(loading = false, chatMessages = chatMessage)
                    }
            }
        }
    }

}

data class ChatUiSpec(
    val loading: Boolean = false,
    val chatMessages: List<ChatMessageSpec> = emptyList(),
)