package com.sibghat.test.inappfullsubscription.di


import com.sibghat.test.inappfullsubscription.data.remote.repositories.InAppBillingRepository
import com.sibghat.test.inappfullsubscription.domain.repositories.InAppBillingRepositoryImp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<InAppBillingRepository> {
        InAppBillingRepositoryImp(androidContext())
    }
}