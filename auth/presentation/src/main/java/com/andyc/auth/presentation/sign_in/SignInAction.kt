package com.andyc.auth.presentation.sign_in

import com.andyc.core.presentation.ui.UiText

sealed interface SignInAction {
    data object OnSignInClick: SignInAction
}
