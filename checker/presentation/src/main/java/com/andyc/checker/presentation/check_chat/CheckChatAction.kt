package com.andyc.checker.presentation.check_chat

sealed interface CheckChatAction {
    data object OnNavigateBack: CheckChatAction
    data class OnSendMessage(val content: String): CheckChatAction
    data object OnLoadChatClick: CheckChatAction
}