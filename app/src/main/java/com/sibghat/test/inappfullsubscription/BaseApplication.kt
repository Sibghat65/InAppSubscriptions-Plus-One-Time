package com.sibghat.test.inappfullsubscription

import android.app.Application
import com.sibghat.test.inappfullsubscription.di.repositoryModule
import com.sibghat.test.inappfullsubscription.di.useCaseModule
import com.sibghat.test.inappfullsubscription.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class BaseApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            modules(listOf(repositoryModule, useCaseModule, viewModelModule))
        }
    }
}