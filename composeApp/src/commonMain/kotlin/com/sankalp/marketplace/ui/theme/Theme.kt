package com.sankalp.marketplace.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppColors {
    // Primary Green
    val Green10  = Color(0xFF002204)
    val Green20  = Color(0xFF004408)
    val Green30  = Color(0xFF00660D)
    val Green40  = Color(0xFF008813)
    val Green80  = Color(0xFF7DDC7F)
    val Green90  = Color(0xFF9AEEA0)
    val Green95  = Color(0xFFB8FFB6)
    val Green99  = Color(0xFFF0FFF0)

    // Secondary
    val Teal40   = Color(0xFF006A60)
    val Teal80   = Color(0xFF4EDBD0)
    val Teal90   = Color(0xFF70F7EC)

    // Neutral
    val Grey10   = Color(0xFF1A1C1A)
    val Grey20   = Color(0xFF2F312E)
    val Grey90   = Color(0xFFE1E3DF)
    val Grey95   = Color(0xFFEFF1EC)
    val Grey99   = Color(0xFFFBFDF7)

    // Error
    val Red10    = Color(0xFF410002)
    val Red40    = Color(0xFFBA1A1A)
    val Red80    = Color(0xFFFFB4AB)
    val Red90    = Color(0xFFFFDAD6)
}

// ─── Light Color Scheme ────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary            = AppColors.Green40,
    onPrimary          = Color.White,
    primaryContainer   = AppColors.Green90,
    onPrimaryContainer = AppColors.Green10,

    secondary          = AppColors.Teal40,
    onSecondary        = Color.White,
    secondaryContainer = AppColors.Teal90,
    onSecondaryContainer = AppColors.Green10,

    background         = AppColors.Grey99,
    onBackground       = AppColors.Grey10,

    surface            = AppColors.Grey99,
    onSurface          = AppColors.Grey10,
    surfaceVariant     = AppColors.Grey90,
    onSurfaceVariant   = AppColors.Grey20,

    error              = AppColors.Red40,
    onError            = Color.White,
    errorContainer     = AppColors.Red90,
    onErrorContainer   = AppColors.Red10,

    outline            = Color(0xFF727972)
)

// ─── Dark Color Scheme ─────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary            = AppColors.Green80,
    onPrimary          = AppColors.Green20,
    primaryContainer   = AppColors.Green30,
    onPrimaryContainer = AppColors.Green90,

    secondary          = AppColors.Teal80,
    onSecondary        = AppColors.Green10,
    secondaryContainer = AppColors.Teal40,
    onSecondaryContainer = AppColors.Teal90,

    background         = AppColors.Grey10,
    onBackground       = AppColors.Grey90,

    surface            = AppColors.Grey10,
    onSurface          = AppColors.Grey90,
    surfaceVariant     = AppColors.Grey20,
    onSurfaceVariant   = AppColors.Grey90,

    error              = AppColors.Red80,
    onError            = AppColors.Red10,
    errorContainer     = AppColors.Red40,
    onErrorContainer   = AppColors.Red90,

    outline            = Color(0xFF8C9389)
)

// ─── App Theme ─────────────────────────────────────────────

@Composable
expect fun AppTheme(
    content: @Composable () -> Unit
)

// ─── Common Theme Logic ────────────────────────────────────

@Composable
fun AppThemeContent(
    darkTheme: Boolean,
    dynamicColorScheme: ColorScheme? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = dynamicColorScheme ?: if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}
