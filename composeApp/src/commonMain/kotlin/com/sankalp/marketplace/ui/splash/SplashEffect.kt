package com.sankalp.marketplace.ui.splash

sealed interface SplashEffect {
    data object NavigateToOnBoard : SplashEffect
    data object NavigateToDashboard : SplashEffect
}