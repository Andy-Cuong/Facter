package com.andyc.checker.presentation.check_chat

sealed interface CheckChatEvent {
    data object ChatLoaded: CheckChatEvent
    data object FailedToLoadChat: CheckChatEvent
    data object FailedToUpsertChat: CheckChatEvent
    data object FailedToGetResponse: CheckChatEvent
}