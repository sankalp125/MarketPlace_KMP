package com.sankalp.marketplace.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sankalp.marketplace.data.api.NetworkResult
import com.sankalp.marketplace.data.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginVm(
    private val authRepo : AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()
    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event : LoginEvent){
        when(event){
            is LoginEvent.OnUserNameChange -> {
                _state.update {
                    it.copy(
                        userName = event.userName
                    )
                }
            }
            is LoginEvent.OnPasswordChange -> {
                _state.update {
                    it.copy(
                        password = event.password
                    )
                }
            }
            is LoginEvent.OnLoginClick -> {
                login()
            }
            is LoginEvent.OnRegisterClick -> {
                viewModelScope.launch {
                    _effect.send(LoginEffect.NavigateToRegister)
                }
            }
            is LoginEvent.OnPasswordVisibilityToggle -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            // Validation
            if (_state.value.userName.isBlank()) {
                _effect.send(LoginEffect.ShowError("Email required hai"))
                return@launch  // ← return@launch use karo, return nahi
            }
            if (!isValidEmail(_state.value.userName)) {
                _effect.send(LoginEffect.ShowError("Valid email enter karo"))
                return@launch
            }
            if (_state.value.password.isBlank()) {
                _effect.send(LoginEffect.ShowError("Password required hai"))
                return@launch
            }
            if (_state.value.password.length < 6) {
                _effect.send(LoginEffect.ShowError("Password 6 se zyada hona chahiye"))
                return@launch
            }

            // API Call
            _state.update { it.copy(loggingIn = true) }
            try {
                when (val result = authRepo.login(
                    userName = _state.value.userName,
                    password = _state.value.password
                )) {
                    is NetworkResult.Success -> {
                        _effect.send(LoginEffect.NavigateToHome)
                    }
                    is NetworkResult.Error.GeneralError -> {
                        _effect.send(LoginEffect.ShowError(result.message))
                    }
                    is NetworkResult.Error.NetworkError -> {
                        _effect.send(LoginEffect.ShowError("Internet check karo"))
                    }
                    is NetworkResult.Loading -> Unit
                }
            } catch (e: Exception) {
                _effect.send(LoginEffect.ShowError(e.message ?: "Kuch to samasya hai"))
            } finally {
                _state.update { it.copy(loggingIn = false) }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email)
    }

}