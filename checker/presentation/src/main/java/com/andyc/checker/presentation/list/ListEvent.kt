package com.andyc.checker.presentation.list

sealed interface ListEvent {
    data object SignedOut: ListEvent
    data object FetchedChatHistory: ListEvent
    data object FailedToFetchChatHistory: ListEvent
    data object DeletedChat: ListEvent
    data object FailedToDeleteChat: ListEvent
}