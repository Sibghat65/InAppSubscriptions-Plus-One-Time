package com.sibghat.test.inappfullsubscription.di


import com.sibghat.test.inappfullsubscription.PremiumViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    //Premium viewmodel
    viewModel { PremiumViewModel(get(), get(), get(), get(), get()) }

}