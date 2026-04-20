package com.sankalp.marketplace.ui.register

import com.sankalp.marketplace.data.models.CityResponse
import com.sankalp.marketplace.data.models.CountryResponse
import com.sankalp.marketplace.data.models.StatesResponse

data class RegisterState(
    val loadingCountries : Boolean = false,
    val loadingStates : Boolean = false,
    val loadingCities : Boolean = false,
    val isPasswordVisible : Boolean = false,
    val isRegistering : Boolean = false,
    val countries : List<CountryResponse> = emptyList(),
    val states : List<StatesResponse> = emptyList(),
    val cities : List<CityResponse> = emptyList(),
    val selectedImage : String? = null,
    val name : String = "",
    val email : String = "",
    val password : String = "",
    val confirmPassword : String = "",
    val mobileNumber : String = "",
    val selectedCountry : CountryResponse? = null,
    val selectedState : StatesResponse? = null,
    val selectedCity : CityResponse? = null,
)
