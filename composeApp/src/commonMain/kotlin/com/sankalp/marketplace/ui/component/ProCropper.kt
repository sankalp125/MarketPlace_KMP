package com.sankalp.marketplace.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@Composable
fun ProCropper(
    imagePath: String,
    onDone: (String) -> Unit
) {

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val painter = rememberAsyncImagePainter(imagePath)

    Box(
        Modifier.fillMaxSize().background(Color.Black)
    ) {

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 4f)
                        offset += pan
                    }
                }
        )

        // Overlay
        Box(
            Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        // Crop frame
        Box(
            Modifier
                .size(280.dp)
                .align(Alignment.Center)
                .border(2.dp, Color.White, CircleShape)
        )

        Button(
            onClick = {
                onDone(imagePath) // (later crop logic attach kar sakte ho)
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
        ) {
            Text("Done")
        }
    }
}