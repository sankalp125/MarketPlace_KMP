package com.sankalp.marketplace.ui.on_board

sealed interface OnBoardEvent {
    data object NavigateToLogin : OnBoardEvent
}