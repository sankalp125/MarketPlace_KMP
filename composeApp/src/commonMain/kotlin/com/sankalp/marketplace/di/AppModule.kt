package com.sankalp.marketplace.di

import com.sankalp.marketplace.data.api.MarketPlaceApi
import com.sankalp.marketplace.data.api.createHttpClient
import com.sankalp.marketplace.data.repository.AuthRepository
import com.sankalp.marketplace.ui.login.LoginVm
import com.sankalp.marketplace.ui.on_board.OnBoardVm
import com.sankalp.marketplace.ui.register.RegisterVm
import com.sankalp.marketplace.ui.splash.SplashVm
import com.sankalp.marketplace.utils.TokenStorage
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::TokenStorage)
    single { createHttpClient(get(), get()) }
    singleOf(::MarketPlaceApi)

    // Repository
    singleOf(::AuthRepository)

    // viewModel
    viewModelOf(::SplashVm)
    viewModelOf(::OnBoardVm)
    viewModelOf(::LoginVm)
    viewModelOf(::RegisterVm)
}