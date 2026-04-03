package com.sankalp.marketplace.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthEventBus {

    private val _events = MutableSharedFlow<AuthEvent>(
        extraBufferCapacity = 1
    )
    val events = _events.asSharedFlow()

    suspend fun sendEvent(event: AuthEvent) {
        _events.emit(event)
    }
}