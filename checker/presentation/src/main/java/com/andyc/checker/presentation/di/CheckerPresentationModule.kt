package com.andyc.checker.presentation.di

import com.andyc.checker.presentation.check_chat.CheckChatViewModel
import com.andyc.checker.presentation.list.ListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val checkerPresentationModule = module {
    viewModelOf(::ListViewModel)
    viewModelOf(::CheckChatViewModel)
}