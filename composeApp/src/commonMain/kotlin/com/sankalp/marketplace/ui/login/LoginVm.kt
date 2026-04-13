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
            is LoginEvent.OnForgotPasswordClick -> {
                viewModelScope.launch {
                    _effect.send(
                        LoginEffect.ShowBottomSheet(
                            type = BottomSheetType.EMAIL
                        )
                    )
                }
            }
            is LoginEvent.OnForgotPasswordFlowCancel -> {
                _state.update {
                    it.copy(
                        forgotPasswordEmail = "",
                        forgotPasswordOTP = "",
                        forgotPasswordPassword = "",
                        forgotPasswordConfirmPassword = ""
                    )
                }
            }
            is LoginEvent.OnForgotPasswordEmailChange -> {
                _state.update {
                    it.copy(
                        forgotPasswordEmail = event.email
                    )
                }
            }
            is LoginEvent.OnForgotPasswordOTPChange -> {
                _state.update {
                    it.copy(
                        forgotPasswordOTP = event.otp
                    )
                }
            }
            is LoginEvent.OnForgotPasswordPasswordChange -> {
                _state.update {
                    it.copy(
                        forgotPasswordPassword = event.password
                    )
                }
            }
            is LoginEvent.OnForgotPasswordConfirmPasswordChange -> {
                _state.update {
                    it.copy(
                        forgotPasswordConfirmPassword = event.confirmPassword
                    )
                }
            }
            is LoginEvent.OnSubmitEmail -> {
                requestPasswordReset()
            }
            is LoginEvent.OnSubmitOTP -> {
                viewModelScope.launch {
                    if (_state.value.forgotPasswordOTP.isBlank()) {
                        _effect.send(LoginEffect.ShowMessage("OTP required"))
                        return@launch
                    }
                    if (_state.value.forgotPasswordOTP.length != 6){
                        _effect.send(LoginEffect.ShowMessage("Please enter a valid OTP"))
                        return@launch
                    }
                    _effect.send(
                        LoginEffect.ShowBottomSheet(
                            type = BottomSheetType.PASSWORD
                        )
                    )
                }
            }
            is LoginEvent.OnSubmitPassword -> {
                // TODO :- Call Reset Password API
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            // Validation
            if (_state.value.userName.isBlank()) {
                _effect.send(LoginEffect.ShowMessage("Email required"))
                return@launch
            }
            if (!isValidEmail(_state.value.userName)) {
                _effect.send(LoginEffect.ShowMessage("Please enter a Valid email"))
                return@launch
            }
            if (_state.value.password.isBlank()) {
                _effect.send(LoginEffect.ShowMessage("Password required"))
                return@launch
            }
            if (_state.value.password.length < 6) {
                _effect.send(LoginEffect.ShowMessage("Password must be larger than 6 characters"))
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
                        _effect.send(LoginEffect.ShowMessage(result.message))
                    }
                    is NetworkResult.Error.NetworkError -> {
                        _effect.send(LoginEffect.ShowMessage("Please check your internet connection!"))
                    }
                    is NetworkResult.Loading -> Unit
                }
            } catch (e: Exception) {
                _effect.send(LoginEffect.ShowMessage(e.message ?: "Something went wrong!"))
            } finally {
                _state.update { it.copy(loggingIn = false) }
            }
        }
    }

    private fun requestPasswordReset(){
        viewModelScope.launch {
            if (_state.value.forgotPasswordEmail.isBlank()){
                _effect.send(LoginEffect.ShowMessage("Email required"))
                return@launch
            }
            if (!isValidEmail(_state.value.forgotPasswordEmail)){
                _effect.send(LoginEffect.ShowMessage("Please enter a valid email"))
                return@launch
            }
            _state.update { it.copy(sendingOtp = true) }
            try {
                when(val result = authRepo.requestForgetPassword(
                    email = _state.value.forgotPasswordEmail
                )){
                    is NetworkResult.Success -> {
                        _effect.send(
                            LoginEffect.ShowBottomSheet(
                                type = BottomSheetType.OTP
                            )
                        )
                        _effect.send(LoginEffect.ShowMessage(result.data.message))
                    }
                    is NetworkResult.Error.GeneralError -> {
                        _effect.send(LoginEffect.ShowMessage(result.message))
                    }
                    is NetworkResult.Error.NetworkError -> {
                        _effect.send(LoginEffect.ShowMessage("Please check your internet connection!"))
                    }
                    is NetworkResult.Loading -> Unit
                }
            }catch (e : Exception){
                _effect.send(LoginEffect.ShowMessage(e.message ?: "Something went wrong!"))
            }finally {
                _state.update { it.copy(sendingOtp = false) }
            }
        }
    }

    private fun resetPassword(){
        viewModelScope.launch {
            if (_state.value.forgotPasswordPassword.isBlank()){
                _effect.send(LoginEffect.ShowMessage("Password required"))
                return@launch
            }
            if (_state.value.forgotPasswordPassword.length < 6){
                _effect.send(LoginEffect.ShowMessage("Password must be larger than 6 characters"))
                return@launch
            }
            if (_state.value.forgotPasswordConfirmPassword.isBlank()){
                _effect.send(LoginEffect.ShowMessage("Confirm Password required"))
                return@launch
            }
            if (_state.value.forgotPasswordPassword != _state.value.forgotPasswordConfirmPassword){
                _effect.send(LoginEffect.ShowMessage("Password and Confirm Password must be same"))
                return@launch
            }
            _state.update {
                it.copy(changingPassword = true)
            }
            try {
                when(val result = authRepo.requestResetPassword(
                    email = _state.value.forgotPasswordEmail,
                    otp = _state.value.forgotPasswordOTP,
                    newPassword = _state.value.forgotPasswordPassword
                )){
                    is NetworkResult.Success -> {
                        _effect.send(LoginEffect.ShowMessage(result.data.message))
                        _state.update {
                            it.copy(
                                forgotPasswordEmail = "",
                                forgotPasswordOTP = "",
                                forgotPasswordPassword = "",
                                forgotPasswordConfirmPassword = ""
                            )
                        }
                        _effect.send(
                            LoginEffect.ShowBottomSheet(
                                type = BottomSheetType.NONE
                            )
                        )
                    }
                    is NetworkResult.Error.GeneralError -> {
                        _effect.send(LoginEffect.ShowMessage(result.message))
                    }
                    is NetworkResult.Error.NetworkError -> {
                        _effect.send(LoginEffect.ShowMessage("Please check your internet connection!"))
                    }
                    is NetworkResult.Loading -> Unit
                }
            }catch (e : Exception){
                _effect.send(LoginEffect.ShowMessage(e.message ?: "Something went wrong!"))
            }finally {
                _state.update {
                    it.copy(changingPassword = false)
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email)
    }

}