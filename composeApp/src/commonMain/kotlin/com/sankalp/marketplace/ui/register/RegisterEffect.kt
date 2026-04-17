package com.sankalp.marketplace.ui.register

import com.sankalp.marketplace.utils.ImageSource

sealed class RegisterEffect {
    data object NavigateToLogin : RegisterEffect()
    data object OpenBottomSheet : RegisterEffect()
    data class OpenImagePicker(val source : ImageSource) : RegisterEffect()
}