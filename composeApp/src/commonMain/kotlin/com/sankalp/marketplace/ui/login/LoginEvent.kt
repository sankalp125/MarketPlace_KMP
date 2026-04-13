package com.sankalp.marketplace.ui.login

sealed interface LoginEvent {
    data class OnUserNameChange(val userName : String) : LoginEvent
    data class OnPasswordChange(val password : String) : LoginEvent
    data object OnLoginClick : LoginEvent
    data object OnRegisterClick : LoginEvent
    data object OnPasswordVisibilityToggle : LoginEvent
    data object OnForgotPasswordClick : LoginEvent
    data object OnForgotPasswordFlowCancel : LoginEvent
    data class OnForgotPasswordEmailChange(val email : String) : LoginEvent
    data class OnForgotPasswordOTPChange(val otp : String) : LoginEvent
    data class OnForgotPasswordPasswordChange(val password : String) : LoginEvent
    data class OnForgotPasswordConfirmPasswordChange(val confirmPassword : String) : LoginEvent
    data object OnSubmitEmail : LoginEvent
    data object OnSubmitOTP : LoginEvent
    data object OnSubmitPassword : LoginEvent
}