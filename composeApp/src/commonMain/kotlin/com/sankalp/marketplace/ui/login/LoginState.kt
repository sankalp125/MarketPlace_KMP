package com.sankalp.marketplace.ui.login

data class LoginState(
    val userName : String = "",
    val password : String = "",
    val loggingIn : Boolean = false,
    val isPasswordVisible : Boolean = false
)
