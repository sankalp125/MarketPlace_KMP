package com.sankalp.marketplace.ui.login

sealed class LoginEffect {
    data class ShowError(val message: String) : LoginEffect()
    data object NavigateToHome : LoginEffect()
    data object NavigateToRegister : LoginEffect()
}