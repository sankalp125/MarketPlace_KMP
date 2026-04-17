package com.sankalp.marketplace.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sankalp.marketplace.data.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterVm(
    private val authRepo : AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _effect = Channel<RegisterEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: RegisterEvent){
        when(event){
            is RegisterEvent.OnPickImageClick -> {
                viewModelScope.launch {
                    _effect.send(RegisterEffect.OpenBottomSheet)
                }
            }
            is RegisterEvent.OnRegisterClick -> {
                // TODO : Register user
            }
            is RegisterEvent.OnImageSelected -> {
                _state.update {
                    it.copy(
                        selectedImage = event.imagePath
                    )
                }
            }
            is RegisterEvent.OpenImagePicker -> {
                viewModelScope.launch {
                    _effect.send(RegisterEffect.OpenImagePicker(event.source))
                }
            }
        }
    }
}