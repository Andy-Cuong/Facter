package com.andyc.core.data.di

import com.andyc.core.data.auth.FirebaseAuthRepository
import com.andyc.core.data.networking.HttpClientFactory
import com.andyc.core.domain.auth.AuthRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory().getClient()
    }
    singleOf(::FirebaseAuthRepository).bind<AuthRepository>()
}