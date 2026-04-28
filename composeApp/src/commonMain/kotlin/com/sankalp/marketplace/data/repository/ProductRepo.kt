package com.sankalp.marketplace.data.repository

import com.sankalp.marketplace.data.api.MarketPlaceApi

class ProductRepo(
    private val api : MarketPlaceApi
) {
    suspend fun getCategories() = api.getCategories()
    suspend fun getProductList() = api.getProductsList()
}