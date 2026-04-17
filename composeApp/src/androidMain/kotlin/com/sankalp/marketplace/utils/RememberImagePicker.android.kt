package com.sankalp.marketplace.utils

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberImagePicker(): ImagePicker {

    val context = LocalContext.current
    val activity = context as ComponentActivity

    var callback by remember { mutableStateOf<((String?) -> Unit)?>(null) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            callback?.invoke(uri?.toString())
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                callback?.invoke(cameraUri?.toString())
            } else {
                callback?.invoke(null)
            }
        }

    fun createImageUri(): Uri {
        val file = File(activity.cacheDir, "camera_${System.currentTimeMillis()}.jpg")

        return FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.provider",
            file
        )
    }

    return remember {
        object : ImagePicker {

            override fun pickImage(
                source: ImageSource,
                onResult: (String?) -> Unit
            ) {
                callback = onResult

                when (source) {

                    ImageSource.GALLERY -> {
                        galleryLauncher.launch("image/*")
                    }

                    ImageSource.CAMERA -> {
                        val uri = createImageUri()
                        cameraUri = uri
                        cameraLauncher.launch(uri)
                    }
                }
            }
        }
    }
}