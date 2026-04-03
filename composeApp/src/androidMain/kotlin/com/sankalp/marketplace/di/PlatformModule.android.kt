package com.sankalp.marketplace.di

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.engine.android.Android
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single {
        SharedPreferencesSettings(
            androidContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        )
    }
    single { Android.create() }
}