package com.sankalp.marketplace.utils

interface ImagePicker {
    fun pickImage(
        source: ImageSource,
        onResult : (String?) -> Unit
    )
}