package com.sankalp.marketplace.ui.login

data class LoginState(
    val userName : String = "",
    val password : String = "",
    val loggingIn : Boolean = false,
    val isPasswordVisible : Boolean = false,
    val forgotPasswordEmail : String = "",
    val forgotPasswordOTP : String = "",
    val forgotPasswordPassword : String = "",
    val forgotPasswordConfirmPassword : String = "",
    val sendingOtp : Boolean = false,
    val changingPassword : Boolean = false
)
