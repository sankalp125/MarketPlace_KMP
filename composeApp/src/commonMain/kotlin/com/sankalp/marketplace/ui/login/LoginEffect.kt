package com.sankalp.marketplace.ui.login

sealed class LoginEffect {
    data class ShowMessage(val message: String) : LoginEffect()
    data object NavigateToHome : LoginEffect()
    data object NavigateToRegister : LoginEffect()
    data class ShowBottomSheet(val type: BottomSheetType) : LoginEffect()
}