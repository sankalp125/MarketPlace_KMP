package com.sankalp.marketplace.data.repository

import com.sankalp.marketplace.data.api.MarketPlaceApi
import com.sankalp.marketplace.utils.TokenStorage

class UserRepository(
    private val api : MarketPlaceApi,
    private val tokenStorage : TokenStorage
) {
}