package com.andyc.checker.network.di

import com.andyc.checker.domain.CheckRepository
import com.andyc.checker.network.XaiCheckRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val checkerNetworkModule = module {
    singleOf(::XaiCheckRepository).bind<CheckRepository>()
}