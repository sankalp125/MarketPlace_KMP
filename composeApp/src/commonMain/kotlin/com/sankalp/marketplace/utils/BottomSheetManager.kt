package com.sankalp.marketplace.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class BottomSheetManager {
    var sheetContent by mutableStateOf<(@Composable () -> Unit)?>(null)
        private set

    var isVisible by mutableStateOf(false)
        private set

    fun show(content: @Composable () -> Unit) {
        sheetContent = content
        isVisible = true
    }

    fun hide() {
        isVisible = false
        sheetContent = null
    }
}

// Easily remember karo
@Composable
fun rememberBottomSheetManager(): BottomSheetManager {
    return remember { BottomSheetManager() }
}