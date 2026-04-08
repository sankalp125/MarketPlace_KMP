package com.sankalp.marketplace.data.repository

import com.sankalp.marketplace.data.api.MarketPlaceApi
import com.sankalp.marketplace.data.api.NetworkResult
import com.sankalp.marketplace.data.models.LoginRequest
import com.sankalp.marketplace.data.models.LoginResponse
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
}