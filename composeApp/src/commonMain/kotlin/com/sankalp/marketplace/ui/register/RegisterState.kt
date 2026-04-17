package com.sankalp.marketplace.ui.register

import coil3.Uri

data class RegisterState(
    val selectedImage : String? = null,
    val name : String = "",
    val email : String = "",
    val password : String = "",
)
