package com.sankalp.marketplace.di

import com.sankalp.marketplace.data.api.MarketPlaceApi
import com.sankalp.marketplace.data.api.createHttpClient
import com.sankalp.marketplace.ui.splash.SplashVm
import com.sankalp.marketplace.utils.TokenStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::TokenStorage)
    single { createHttpClient(get(), get()) }
    singleOf(::MarketPlaceApi)
    singleOf(::SplashVm)
}