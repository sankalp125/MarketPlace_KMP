package com.sankalp.marketplace.ui.register

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

class RegisterVm(
    private val authRepo : AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _effect = Channel<RegisterEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        getCountries()
    }

    fun onEvent(event: RegisterEvent){
        when(event){
            is RegisterEvent.OnPickImageClick -> {
                viewModelScope.launch {
                    _effect.send(RegisterEffect.OpenBottomSheet)
                }
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
            is RegisterEvent.OnUserNameChange -> {
                _state.update {
                    it.copy(
                        name = event.userName
                    )
                }
            }
            is RegisterEvent.OnEmailChange -> {
                _state.update {
                    it.copy(
                        email = event.email
                    )
                }
            }
            is RegisterEvent.OnPasswordChange -> {
                _state.update {
                    it.copy(
                        password = event.password
                    )
                }
            }
            is RegisterEvent.OnConfirmPasswordChange -> {
                _state.update {
                    it.copy(
                        confirmPassword = event.confirmPassword
                    )
                }
            }
            is RegisterEvent.OnMobileNumberChange -> {
                _state.update {
                    it.copy(
                        mobileNumber = event.mobileNumber
                    )
                }
            }
            is RegisterEvent.OnCountryChange -> {
                _state.update {
                    it.copy(
                        selectedCountry = event.country
                    )
                }
                getStates(event.country.countryId)
            }
            is RegisterEvent.OnStateChange -> {
                _state.update {
                    it.copy(
                        selectedState = event.state
                    )
                }
                getCities(event.state.stateId)
            }
            is RegisterEvent.OnCityChange -> {
                _state.update {
                    it.copy(
                        selectedCity = event.city
                    )
                }
            }
            is RegisterEvent.OnBackClick -> {
                viewModelScope.launch {
                    _effect.send(RegisterEffect.NavigateToLogin)
                }
            }
            is RegisterEvent.OnPasswordVisibilityToggle -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }
            is RegisterEvent.OnRegisterClick -> {
                // TODO : Register user
            }
        }
    }

    private fun getCountries(){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loadingCountries = true
                )
            }
            try {
                when(val result = authRepo.requestCountries()){
                    is NetworkResult.Success -> {
                        _state.update { it.copy(countries = result.data) }
                    }
                    is NetworkResult.Error.GeneralError -> {
                        _effect.send(RegisterEffect.ShowMessage(result.message))
                    }
                    is NetworkResult.Error.NetworkError -> {
                        _effect.send(RegisterEffect.ShowMessage("Please check your internet connection!"))
                    }
                    is NetworkResult.Loading -> Unit
                }
            } catch (e: Exception) {
                _effect.send(RegisterEffect.ShowMessage(e.message ?: "Something went wrong!"))
            } finally {
                _state.update {
                    it.copy(
                        loadingCountries = false
                    )
                }
            }
        }
    }

    private fun getStates(countryCode: String){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loadingStates = true
                )
            }
            try {
                when(val result = authRepo.requestStates(countryCode)){
                    is NetworkResult.Success -> {
                        _state.update { it.copy(states = result.data) }
                    }
                    is NetworkResult.Error.GeneralError -> {
                        _effect.send(RegisterEffect.ShowMessage(result.message))
                    }
                    is NetworkResult.Error.NetworkError -> {
                        _effect.send(RegisterEffect.ShowMessage("Please check your internet connection!"))
                    }
                    is NetworkResult.Loading -> Unit
                }
            }catch (e : Exception){
                _effect.send(RegisterEffect.ShowMessage(e.message ?: "Something went wrong!"))
            }finally {
                _state.update {
                    it.copy(
                        loadingStates = false
                    )
                }
            }
        }
    }
    private fun getCities(stateCode: String){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loadingCities = true
                )
            }
            try {
                when(val result = authRepo.requestCities(stateCode)){
                    is NetworkResult.Success -> {
                        _state.update { it.copy(cities = result.data) }
                    }
                    is NetworkResult.Error.GeneralError -> {
                        _effect.send(RegisterEffect.ShowMessage(result.message))
                    }
                    is NetworkResult.Error.NetworkError -> {
                        _effect.send(RegisterEffect.ShowMessage("Please check your internet connection!"))
                    }
                    is NetworkResult.Loading -> Unit
                }
            }catch (e : Exception){
                _effect.send(RegisterEffect.ShowMessage(e.message ?: "Something went wrong!"))
            }finally {
                _state.update {
                    it.copy(
                        loadingCities = false
                    )
                }
            }
        }
    }
}