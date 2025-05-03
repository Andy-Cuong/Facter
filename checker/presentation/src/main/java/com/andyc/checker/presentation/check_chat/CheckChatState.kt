package com.andyc.checker.presentation.check_chat

import androidx.compose.foundation.text.input.TextFieldState
import com.andyc.checker.domain.Message
import com.andyc.core.domain.user.User

data class CheckChatState(
    val chatId: String? = null,
    val title: String = "New check",
    val user: User? = null,
    val messageToSend: TextFieldState = TextFieldState(),
    val canSend: Boolean = false,
    val isFirstMessage: Boolean = true,
    val messages: List<Message> = emptyList(),
    val loading: Boolean = false
)
