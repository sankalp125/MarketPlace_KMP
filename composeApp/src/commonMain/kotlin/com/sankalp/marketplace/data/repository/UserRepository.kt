package com.sankalp.marketplace.data.repository

import com.sankalp.marketplace.data.api.MarketPlaceApi
import com.sankalp.marketplace.data.models.MultipartRequest
import com.sankalp.marketplace.data.models.UpdateProfileRequest
import com.sankalp.marketplace.utils.AuthEvent
import com.sankalp.marketplace.utils.AuthEventBus
import com.sankalp.marketplace.utils.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository(
    private val api : MarketPlaceApi,
    private val tokenStorage : TokenStorage
) {
    suspend fun getProfile() = api.getProfile()
    suspend fun getMyProducts() = api.getMyProducts()
    suspend fun updateProfile(request : UpdateProfileRequest) = api.updateProfile(request)
    suspend fun updateProfilePicture(request : MultipartRequest) = api.updateProfilePicture(request)
    fun logOut() {
        tokenStorage.clearTokens()
        CoroutineScope(Dispatchers.Main).launch {
            AuthEventBus.sendEvent(AuthEvent.LoggedOut)
        }
    }
}