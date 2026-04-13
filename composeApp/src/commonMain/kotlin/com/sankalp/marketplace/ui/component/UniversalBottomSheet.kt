package com.sankalp.marketplace.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sankalp.marketplace.utils.BottomSheetManager
import com.sankalp.marketplace.utils.WindowSize
import com.sankalp.marketplace.utils.getWindowSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalBottomSheet(
    manager: BottomSheetManager,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val windowSize = getWindowSize()

    LaunchedEffect(manager.isVisible) {
        if (manager.isVisible) sheetState.show()
        else sheetState.hide()
    }

    if (manager.isVisible && manager.sheetContent != null) {
        ModalBottomSheet(
            onDismissRequest = {
                onDismiss()
                manager.hide()
            },
            sheetState     = sheetState,
            tonalElevation = 8.dp,
            // ─── Screen size ke according width ───────────
            modifier  = when (windowSize) {
                WindowSize.Compact  -> Modifier.fillMaxWidth()
                WindowSize.Medium   -> Modifier.fillMaxWidth(0.7f)   // 70% width
                WindowSize.Expanded -> Modifier.fillMaxWidth(0.4f)   // 40% width — center automatic
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                // Content
                manager.sheetContent?.invoke()
            }
        }
    }
}