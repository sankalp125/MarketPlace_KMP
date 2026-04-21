package com.sankalp.marketplace.utils

actual fun readFileBytes(path: String): ByteArray {
    return java.io.File(path).readBytes()
}