package com.sankalp.marketplace.ui.on_board

sealed class OnBoardEffect {
    data object NavigateToLogin : OnBoardEffect()
}