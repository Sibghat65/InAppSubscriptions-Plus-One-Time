package com.sibghat.test.inappfullsubscription.di


import com.sibghat.test.inappfullsubscription.domain.usecases.ConnectBillingUseCase
import com.sibghat.test.inappfullsubscription.domain.usecases.ConsumePurchaseUseCase
import com.sibghat.test.inappfullsubscription.domain.usecases.PurchaseProductUseCase
import com.sibghat.test.inappfullsubscription.domain.usecases.QueryActivePurchasesUseCase
import com.sibghat.test.inappfullsubscription.domain.usecases.QueryProductsUseCase
import com.sibghat.test.inappfullsubscription.domain.usecases.TerminateConnectionUseCase
import org.koin.dsl.module

val useCaseModule = module {

    //InApp UseCase
    factory { ConnectBillingUseCase(get()) }
    factory { QueryProductsUseCase(get()) }
    factory { PurchaseProductUseCase(get()) }
    factory { QueryActivePurchasesUseCase(get()) }
    factory { ConsumePurchaseUseCase(get()) }
    factory { TerminateConnectionUseCase(get()) }
}