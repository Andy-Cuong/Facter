package com.andyc.facter

import android.app.Application
import com.andyc.auth.presentation.di.authPresentationModule
import com.andyc.checker.data.di.checkerDataModule
import com.andyc.checker.network.di.checkerNetworkModule
import com.andyc.checker.presentation.di.checkerPresentationModule
import com.andyc.core.data.di.coreDataModule
import com.andyc.facter.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FacterApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FacterApp)
            modules(
                appModule,
                authPresentationModule,
                checkerPresentationModule,
                coreDataModule,
                checkerDataModule,
                checkerNetworkModule,
            )
        }
    }
}