package com.andyc.checker.data.di

import com.andyc.checker.data.FirestoreChatRepository
import com.andyc.checker.domain.ChatRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val checkerDataModule = module {
    singleOf(::FirestoreChatRepository).bind<ChatRepository>()
}