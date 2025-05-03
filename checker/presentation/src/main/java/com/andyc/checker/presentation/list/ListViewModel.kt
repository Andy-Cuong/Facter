package com.andyc.checker.presentation.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andyc.checker.domain.ChatRepository
import com.andyc.checker.presentation.list.mapper.toChatUi
import com.andyc.core.domain.auth.AuthRepository
import com.andyc.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ListViewModel(
    val authRepository: AuthRepository,
    val chatRepository: ChatRepository
): ViewModel() {
    var state by mutableStateOf(ListState())
        private set

    private val _eventChannel = Channel<ListEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        authRepository.getCurrentUser().onEach { user ->
            state = state.copy(currentUser = user)
            if (user == null) {
                _eventChannel.send(ListEvent.SignedOut)
            }
        }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            delay(500) // Wait for chat repository to initialize
            while (true) {
                fetchChatHistory()
                delay(5000) // TODO: Can Flow be used instead?
            }
        }
    }

    fun onAction(action: ListAction) {
        when (action) {
            ListAction.OnSignOutClick -> authRepository.signOut()
            ListAction.OnDeleteUserClick -> authRepository.deleteUser()
            is ListAction.OnChatClick -> Unit // Handled by NavHost
            is ListAction.OnDeleteChatClick -> deleteChat(action.chatId)
            ListAction.OnFetchChatHistory -> fetchChatHistory()
        }
    }

    private fun deleteChat(chatId: String) {
        viewModelScope.launch {
            when (chatRepository.deleteChat(chatId)) {
                is Result.Success -> {
                    _eventChannel.send(ListEvent.DeletedChat)
                    fetchChatHistory()
                }
                is Result.Error -> {
                    _eventChannel.send(ListEvent.FailedToDeleteChat)
                    fetchChatHistory()
                }
            }
        }
    }

    private fun fetchChatHistory() {
        viewModelScope.launch {
            when (val result = chatRepository.getChatHistory()) {
                is Result.Success -> {
                    state = state.copy(
                        chatHistory = result.data
                            .sortedByDescending { chat -> chat.messages.last().sentEpochSecond }
                            .map { chat -> chat.toChatUi() }
                    )
                    _eventChannel.send(ListEvent.FetchedChatHistory)
                }
                is Result.Error -> {
                    _eventChannel.send(ListEvent.FailedToFetchChatHistory)
                }
            }
        }
    }
}