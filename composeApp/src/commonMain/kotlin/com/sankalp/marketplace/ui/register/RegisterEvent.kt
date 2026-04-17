package com.sankalp.marketplace.ui.register

import com.sankalp.marketplace.utils.ImageSource

sealed interface RegisterEvent {
    data object OnPickImageClick : RegisterEvent
    data object OnRegisterClick : RegisterEvent
    data class OpenImagePicker(val source : ImageSource) : RegisterEvent
    data class OnImageSelected(val imagePath : String) : RegisterEvent
}