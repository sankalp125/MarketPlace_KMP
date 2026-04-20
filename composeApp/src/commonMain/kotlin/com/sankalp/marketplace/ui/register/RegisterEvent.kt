package com.sankalp.marketplace.ui.register

import com.sankalp.marketplace.data.models.CityResponse
import com.sankalp.marketplace.data.models.CountryResponse
import com.sankalp.marketplace.data.models.StatesResponse
import com.sankalp.marketplace.utils.ImageSource

sealed interface RegisterEvent {
    data object OnPickImageClick : RegisterEvent
    data class OpenImagePicker(val source : ImageSource) : RegisterEvent
    data class OnImageSelected(val imagePath : String) : RegisterEvent
    data class OnUserNameChange(val userName : String) : RegisterEvent
    data class OnEmailChange(val email : String) : RegisterEvent
    data class OnPasswordChange(val password : String) : RegisterEvent
    data class OnConfirmPasswordChange(val confirmPassword : String) : RegisterEvent
    data class OnMobileNumberChange(val mobileNumber : String) : RegisterEvent
    data class OnCountryChange(val country : CountryResponse) : RegisterEvent
    data class OnStateChange(val state : StatesResponse) : RegisterEvent
    data class OnCityChange(val city : CityResponse) : RegisterEvent
    data object OnPasswordVisibilityToggle : RegisterEvent
    data object OnBackClick : RegisterEvent
    data object OnRegisterClick : RegisterEvent
}