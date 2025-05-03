package com.andyc.checker.presentation.check_chat

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andyc.checker.domain.Chat
import com.andyc.checker.domain.ChatRepository
import com.andyc.checker.domain.CheckRepository
import com.andyc.checker.domain.Message
import com.andyc.checker.domain.MessageRole
import com.andyc.core.domain.auth.AuthRepository
import com.andyc.core.domain.util.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

private const val TAG = "CheckChatViewModel"

class CheckChatViewModel(
    savedStateHandle: SavedStateHandle,
    authRepository: AuthRepository,
    private val checkRepository: CheckRepository,
    private val chatRepository: ChatRepository
): ViewModel() {

    private val chatId: String? = savedStateHandle[CheckChatDestination.CHAT_ID_ARG]

    var state by mutableStateOf(CheckChatState())
        private set

    private val _eventChannel = Channel<CheckChatEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        when (chatId) {
            null -> {
                viewModelScope.launch {
                    _eventChannel.send(CheckChatEvent.FailedToLoadChat)
                }
            }
            "new" -> {}
            else -> {
                state = state.copy(chatId = chatId)
                loadChat(chatId)
            }
        }

        authRepository.getCurrentUser().onEach { user ->
            state = state.copy(user = user)
        }.launchIn(viewModelScope)

        snapshotFlow { state.messageToSend.text }.onEach { message ->
            state = state.copy(
                canSend = message.isNotBlank()
            )
        }.launchIn(viewModelScope)
    }

    fun onAction(action: CheckChatAction) {
        when (action) {
            CheckChatAction.OnNavigateBack -> Unit
            CheckChatAction.OnLoadChatClick -> {
                state.chatId?.let { loadChat(it) }
            }
            is CheckChatAction.OnSendMessage -> sendMessage(action.content)
        }
    }

    private fun loadChat(chatId: String) {
        viewModelScope.launch {
            when (val result = chatRepository.getChat(chatId)) {
                is Result.Error -> {
                    _eventChannel.send(CheckChatEvent.FailedToLoadChat)
                }
                is Result.Success -> {
                    state = state.copy(
                        chatId = result.data.id,
                        isFirstMessage = false,
                        title = result.data.title,
                        messages = result.data.messages
                    )
                    _eventChannel.send(CheckChatEvent.ChatLoaded)
                }
            }
        }
    }

    private fun sendMessage(content: String) {
        val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
        state = state.copy(
            messageToSend = TextFieldState(),
            messages = state.messages + listOf(
                Message(
                    role = MessageRole.USER,
                    sentEpochSecond = now,
                    content = content
                )
            ),
            loading = true
        )

        var updatedChat = if (state.isFirstMessage) {
            // Chat item for first message
            Chat(
                userId = state.user!!.id,
                title = state.title,
                messages = state.messages
            )
        } else {
            // Chat item for continuing message
            Chat(
                id = state.chatId!!,
                userId = state.user!!.id,
                title = state.title,
                messages = state.messages
            )
        }
        
        viewModelScope.launch {
            if (state.isFirstMessage) {
                when (val titleResult = checkRepository.generateChatTitle(updatedChat)) {
                    is Result.Error -> {
                        Log.e(TAG, "Failed to generate chat title: ${titleResult.error}")
                        val titleIndexEnd = content.indexOf(' ', 15)
                        val title = if (titleIndexEnd < 0) {
                            content
                        } else {
                            content.substring(0, titleIndexEnd)
                        }
                            state = state.copy(title = title)
                        }
                    is Result.Success -> {
                        state = state.copy(title = titleResult.data)
                    }
                }
                state = state.copy(isFirstMessage = false)
            }

            updatedChat = updatedChat.copy(title = state.title)
            upsertChat(updatedChat)

            when (val sendResult = checkRepository.chatCompletion(updatedChat)) {
                is Result.Error -> {
                    Log.e(TAG, "Failed to get answer: ${sendResult.error}")
                    _eventChannel.send(CheckChatEvent.FailedToGetResponse)
                }
                is Result.Success -> {
                    updatedChat = updatedChat.copy(messages = updatedChat.messages + listOf(sendResult.data))
                    upsertChat(updatedChat)
                }
            }

            state = state.copy(loading = false)
        }
    }

    private suspend fun upsertChat(chat: Chat) {
        when (val result = chatRepository.upsertChat(chat)) {
            is Result.Error -> {
                _eventChannel.send(CheckChatEvent.FailedToUpsertChat)
            }
            is Result.Success -> {
                loadChat(result.data)
            }
        }
    }
}