package com.andyc.auth.presentation.sign_in

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andyc.core.domain.auth.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

class SignInViewModel(
    authRepository: AuthRepository
): ViewModel() {

    var state by mutableStateOf(SignInState())
        private set

    private val _eventChannel = Channel<SignInEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        authRepository.getCurrentUser().onEach { user ->
            Log.d("SignInViewModel", "User is $user")
            if (user != null) {
                _eventChannel.send(SignInEvent.SignInSuccess)
            }
            state = state.copy(isSigningIn = false)
        }
            .launchIn(viewModelScope)
    }

    fun onAction(action: SignInAction) {
        when (action) {
            SignInAction.OnSignInClick -> signIn()
        }
    }

    private fun signIn() {
        state = state.copy(
            isSigningIn = true
        )
        // The rest is handled by the FirebaseSignInActivity
    }
}