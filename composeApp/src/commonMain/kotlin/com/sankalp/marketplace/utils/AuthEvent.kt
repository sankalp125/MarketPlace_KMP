package com.sankalp.marketplace.utils

sealed class AuthEvent {
    object SessionExpired : AuthEvent()
    object LoggedOut : AuthEvent()
}