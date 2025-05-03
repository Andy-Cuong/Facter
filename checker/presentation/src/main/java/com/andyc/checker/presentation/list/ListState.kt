package com.andyc.checker.presentation.list

import com.andyc.checker.presentation.list.model.ChatUi
import com.andyc.core.domain.user.User

data class ListState(
    val currentUser: User? = null,
    val chatHistory: List<ChatUi> = emptyList()
)
