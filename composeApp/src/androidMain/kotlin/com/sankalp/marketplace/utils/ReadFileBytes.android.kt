package com.sankalp.marketplace.utils

import android.content.Context
import java.io.File
import androidx.core.net.toUri

lateinit var appContext: Context

actual fun readFileBytes(path: String): ByteArray {
    return try {
        val uri = path.toUri()
        // content:// URI handle karo
        if (path.startsWith("content://")) {
            appContext.contentResolver
                .openInputStream(uri)
                ?.use { it.readBytes() }
                ?: ByteArray(0)
        } else {
            // Normal file path
            File(path).readBytes()
        }
    } catch (e: Exception) {
        ByteArray(0)
    }
}