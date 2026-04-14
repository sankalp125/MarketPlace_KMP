package com.sankalp.marketplace.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
actual fun AppTheme(
    content: @Composable () -> Unit
) {
    AppThemeContent(
        darkTheme = isSystemInDarkTheme(),
        content   = content
    )
}