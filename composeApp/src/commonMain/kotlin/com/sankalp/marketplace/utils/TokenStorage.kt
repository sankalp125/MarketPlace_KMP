package com.sankalp.marketplace.utils

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.toFlowSettings

@OptIn(ExperimentalSettingsApi::class)
class TokenStorage(private val settings: Settings) {
    private val flowSettings = (settings as ObservableSettings).toFlowSettings()

    companion object {
        private const val ACCESS_TOKEN_KEY  = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    // ─── Access Token ──────────────────────────────────────

    fun saveAccessToken(token: String) =
        settings.putString(ACCESS_TOKEN_KEY, token)

    fun getAccessToken(): String? =
        settings.getStringOrNull(ACCESS_TOKEN_KEY)

    fun getAccessTokenFlow() =
        flowSettings.getStringOrNullFlow(ACCESS_TOKEN_KEY)

    // ─── Refresh Token ─────────────────────────────────────

    fun saveRefreshToken(token: String) =
        settings.putString(REFRESH_TOKEN_KEY, token)

    fun getRefreshToken(): String? =
        settings.getStringOrNull(REFRESH_TOKEN_KEY)

    // ─── Both ──────────────────────────────────────────────

    fun saveTokens(accessToken: String, refreshToken: String) {
        saveAccessToken(accessToken)
        saveRefreshToken(refreshToken)
    }

    // ─── Logout ────────────────────────────────────────────

    fun clearTokens() {
        settings.remove(ACCESS_TOKEN_KEY)
        settings.remove(REFRESH_TOKEN_KEY)
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null
}