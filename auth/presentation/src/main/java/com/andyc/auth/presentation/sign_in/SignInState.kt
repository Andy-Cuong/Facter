package com.andyc.auth.presentation.sign_in

data class SignInState(
    val isSigningIn: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
