package com.sankalp.marketplace.di

import com.russhwolf.settings.PreferencesSettings
import io.ktor.client.engine.java.Java
import org.koin.dsl.module
import java.util.prefs.Preferences

actual val platformModule = module {
    single { PreferencesSettings(Preferences.userRoot()) }
    single { Java.create() }
}