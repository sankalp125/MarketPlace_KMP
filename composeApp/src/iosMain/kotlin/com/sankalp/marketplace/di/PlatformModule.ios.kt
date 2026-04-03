package com.sankalp.marketplace.di

import com.russhwolf.settings.NSUserDefaultsSettings
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule = module {
    single {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }
    single { Darwin.create() }
}