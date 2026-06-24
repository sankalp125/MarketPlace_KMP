package com.sankalp.marketplace.di

import com.sankalp.marketplace.data.api.MarketPlaceApi
import com.sankalp.marketplace.data.api.createHttpClient
import com.sankalp.marketplace.data.repository.AuthRepository
import com.sankalp.marketplace.data.repository.ProductRepo
import com.sankalp.marketplace.data.repository.UserRepository
import com.sankalp.marketplace.ui.dashboard.DashBoardVm
import com.sankalp.marketplace.ui.details.ProductDetailsVm
import com.sankalp.marketplace.ui.edit_product.EditProductVm
import com.sankalp.marketplace.ui.login.LoginVm
import com.sankalp.marketplace.ui.on_board.OnBoardVm
import com.sankalp.marketplace.ui.register.RegisterVm
import com.sankalp.marketplace.ui.splash.SplashVm
import com.sankalp.marketplace.utils.TokenStorage
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val SESSION_SCOPE = named("SESSION_SCOPE")

val appModule = module {
    singleOf(::TokenStorage)

    // Auth related (Global)
    viewModelOf(::SplashVm)
    viewModelOf(::OnBoardVm)

    // Session-dependent components
    scope(SESSION_SCOPE) {
        scoped { createHttpClient(get(), get()) }
        scopedOf(::MarketPlaceApi)
        scopedOf(::AuthRepository)
        scopedOf(::UserRepository)
        scopedOf(::ProductRepo)
        
        scopedOf(::LoginVm)
        scopedOf(::RegisterVm)
        scopedOf(::DashBoardVm)
        scopedOf(::ProductDetailsVm)
        scopedOf(::EditProductVm)
    }
}
