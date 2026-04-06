package com.sankalp.marketplace.ui.splash

sealed class SplashEffect {
    data object NavigateToOnBoard : SplashEffect()
    data object NavigateToDashboard : SplashEffect()
}