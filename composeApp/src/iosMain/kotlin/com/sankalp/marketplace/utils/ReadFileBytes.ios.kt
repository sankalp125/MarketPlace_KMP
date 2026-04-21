package com.sankalp.marketplace.utils

import platform.Foundation.*

actual fun readFileBytes(path: String): ByteArray {
    val data = NSData.dataWithContentsOfFile(path) ?: return ByteArray(0)
    return data.toByteArray()
}