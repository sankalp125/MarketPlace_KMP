package com.sankalp.marketplace.ui.on_board

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import marketplaceapp.composeapp.generated.resources.Res
import marketplaceapp.composeapp.generated.resources.splash_logo1
import marketplaceapp.composeapp.generated.resources.splash_logo2
import marketplaceapp.composeapp.generated.resources.splash_logo3

class OnBoardVm : ViewModel() {
    private val _state = MutableStateFlow(OnBoardState())
    val state = _state.asStateFlow()
    private val _effect = Channel<OnBoardEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    init {
        val pages = listOf(
            OnboardingPage(
                "Choose Product",
                "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint. Velit officia consequat duis enim velit mollit.",
                Res.drawable.splash_logo3
            ),
            OnboardingPage(
                "Make Payment",
                "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint. Velit officia consequat duis enim velit mollit.",
                Res.drawable.splash_logo2
            ),
            OnboardingPage(
                "Get Your Order",
                "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint. Velit officia consequat duis enim velit mollit.",
                Res.drawable.splash_logo1
            )
        )
        _state.update {
            it.copy(
                pages = pages
            )
        }
    }
    fun onEvent(event : OnBoardEvent) {
        when(event){
            is OnBoardEvent.NavigateToLogin -> {
                viewModelScope.launch {
                    _effect.send(OnBoardEffect.NavigateToLogin)
                }
            }
        }
    }
}