package com.sankalp.marketplace.utils

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun getWindowSize(): WindowSize {
    val windowSizeClass = calculateWindowSizeClass()  // ← No activity needed
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact  -> WindowSize.Compact
        WindowWidthSizeClass.Medium   -> WindowSize.Medium
        else                          -> WindowSize.Expanded
    }
}