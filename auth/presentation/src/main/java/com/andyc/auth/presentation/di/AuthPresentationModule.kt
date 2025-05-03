package com.andyc.auth.presentation.di

import com.andyc.auth.presentation.sign_in.SignInViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::SignInViewModel)
}