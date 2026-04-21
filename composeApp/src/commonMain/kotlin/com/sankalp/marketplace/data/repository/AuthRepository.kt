package com.sankalp.marketplace.data.repository

import com.sankalp.marketplace.data.api.MarketPlaceApi
import com.sankalp.marketplace.data.api.NetworkResult
import com.sankalp.marketplace.data.models.CityResponse
import com.sankalp.marketplace.data.models.CountryResponse
import com.sankalp.marketplace.data.models.ForgotPasswordRequest
import com.sankalp.marketplace.data.models.ForgotPasswordResponse
import com.sankalp.marketplace.data.models.LoginRequest
import com.sankalp.marketplace.data.models.LoginResponse
import com.sankalp.marketplace.data.models.MultipartFile
import com.sankalp.marketplace.data.models.MultipartRequest
import com.sankalp.marketplace.data.models.PasswordResetRequest
import com.sankalp.marketplace.data.models.PasswordResetResponse
import com.sankalp.marketplace.data.models.RegisterResponse
import com.sankalp.marketplace.data.models.StatesResponse
import com.sankalp.marketplace.utils.TokenStorage

class AuthRepository(
    private val api : MarketPlaceApi,
    private val tokenStorage : TokenStorage
) {
    suspend fun login(
        userName : String,
        password : String
    ) : NetworkResult<LoginResponse> {
        val result = api.login(LoginRequest(userName, password))
        if (result is NetworkResult.Success){
            tokenStorage.saveTokens(
                result.data.accessToken,
                result.data.refreshToken
            )
        }
        return result
    }

    suspend fun requestForgetPassword(
        email : String
    ) : NetworkResult<ForgotPasswordResponse> {
        val result = api.requestForgetPassword(ForgotPasswordRequest(email = email))
        return result
    }

    suspend fun requestResetPassword(
        email : String,
        otp : String,
        newPassword : String
    ) : NetworkResult<PasswordResetResponse> {
        val result = api.requestPasswordReset(
            PasswordResetRequest(
                email = email,
                otp = otp,
                newPassword = newPassword
            )
        )
        return result
    }

    suspend fun requestCountries() : NetworkResult<List<CountryResponse>> {
        val result = api.requestCountriesList()
        return result
    }

    suspend fun requestStates(countryCode : String) : NetworkResult<List<StatesResponse>> {
        val result = api.requestStatesList(countryCode)
        return result
    }

    suspend fun requestCities(stateCode : String) : NetworkResult<List<CityResponse>> {
        val result = api.requestCitiesList(stateCode)
        return result
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        mobileNo: String,
        countryCode: String,
        stateCode: String,
        cityCode: String,
        image: String? = null
    ): NetworkResult<RegisterResponse> {

        val formFields = buildMap {
            put("name", name)
            put("email", email)
            put("password", password)
            put("mobileNo", mobileNo)
            put("country", countryCode)
            put("state", stateCode)
            put("city", cityCode)
        }

        val files = image?.let {
            listOf(
                MultipartFile(
                    key = "photo",
                    filePath = it,
                    fileName = "profile_image.jpg"
                )
            )
        }.orEmpty()

        return api.registerUser(
            MultipartRequest(
                formFields = formFields,
                files = files
            )
        )
    }
}