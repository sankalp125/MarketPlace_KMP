package com.sankalp.marketplace.ui.login

sealed interface LoginEvent {
    data class OnUserNameChange(val userName : String) : LoginEvent
    data class OnPasswordChange(val password : String) : LoginEvent
    data object OnLoginClick : LoginEvent
    data object OnRegisterClick : LoginEvent
    data object OnPasswordVisibilityToggle : LoginEvent
}