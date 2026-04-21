package com.sankalp.marketplace.utils

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val byteArray = ByteArray(size)

    if (size > 0) {
        val buffer = bytes ?: return byteArray

        byteArray.usePinned {
            memcpy(
                it.addressOf(0),
                buffer,
                size.convert()
            )
        }
    }

    return byteArray
}