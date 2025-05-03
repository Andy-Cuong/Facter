package com.andyc.checker.presentation.list

sealed interface ListAction {
    data object OnSignOutClick: ListAction
    data object OnDeleteUserClick: ListAction
    data class OnChatClick(val chatId: String): ListAction
    data class OnDeleteChatClick(val chatId: String): ListAction
    data object OnFetchChatHistory: ListAction
}