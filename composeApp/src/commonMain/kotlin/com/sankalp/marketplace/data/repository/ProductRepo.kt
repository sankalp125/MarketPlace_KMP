package com.sankalp.marketplace.data.repository

import com.sankalp.marketplace.data.api.MarketPlaceApi
import com.sankalp.marketplace.data.api.NetworkResult
import com.sankalp.marketplace.data.models.CityResponse
import com.sankalp.marketplace.data.models.CountryResponse
import com.sankalp.marketplace.data.models.MultipartRequest
import com.sankalp.marketplace.data.models.StatesResponse

class ProductRepo(
    private val api : MarketPlaceApi
) {
    suspend fun getCategories() = api.getCategories()
    suspend fun getProductList() = api.getProductsList()
    suspend fun addProduct(request: MultipartRequest) = api.addProduct(request)
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
}