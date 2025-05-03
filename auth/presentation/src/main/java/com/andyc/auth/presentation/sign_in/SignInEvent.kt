package com.andyc.auth.presentation.sign_in

import com.andyc.core.presentation.ui.UiText

sealed interface SignInEvent {
    data class Error(val error: UiText): SignInEvent
    data object SignInSuccess: SignInEvent
}