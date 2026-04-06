package com.sankalp.marketplace.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sankalp.marketplace.utils.TokenStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SplashVm(
    private val tokenStorage : TokenStorage
): ViewModel(){
    private val _effect = Channel<SplashEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    init {
        checkNavigation()
    }

    private fun checkNavigation(){
        viewModelScope.launch {
            delay(2000L)
            val isLoggedIn = tokenStorage.isLoggedIn()
            when {
                isLoggedIn -> {
                    _effect.send(SplashEffect.NavigateToDashboard)
                }
                else -> {
                    _effect.send(SplashEffect.NavigateToOnBoard)
                }
            }
        }
    }
}