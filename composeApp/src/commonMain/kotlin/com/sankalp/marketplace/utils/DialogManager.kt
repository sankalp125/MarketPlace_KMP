package com.sankalp.marketplace.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class DialogManager {
    var dialogContent by mutableStateOf<(@Composable () -> Unit)?>(null)
        private set

    var isVisible by mutableStateOf(false)
        private set

    fun show(content: @Composable () -> Unit) {
        dialogContent = content
        isVisible = true
    }

    fun hide() {
        isVisible = false
        dialogContent = null
    }
}
@Composable
fun rememberDialogManager(): DialogManager {
    return remember { DialogManager() }
}