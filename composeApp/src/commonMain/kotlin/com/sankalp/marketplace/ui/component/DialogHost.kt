package com.sankalp.marketplace.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sankalp.marketplace.utils.DialogManager
import com.sankalp.marketplace.utils.WindowSize
import com.sankalp.marketplace.utils.getWindowSize

@Composable
fun DialogHost(
    manager: DialogManager,
    onDismiss: () -> Unit
) {
    val windowSize = getWindowSize()

    if (manager.isVisible && manager.dialogContent != null) {

        AlertDialog(
            onDismissRequest = {
                onDismiss()
                manager.hide()
            },
            confirmButton = {},
            text = {
                manager.dialogContent?.invoke()
            },
            tonalElevation = 8.dp,
            modifier = when(windowSize){
                WindowSize.Compact -> Modifier.fillMaxWidth()
                WindowSize.Expanded -> Modifier.fillMaxWidth(0.7f)
                WindowSize.Medium -> Modifier.fillMaxWidth(0.4f)
            }
        )
    }
}