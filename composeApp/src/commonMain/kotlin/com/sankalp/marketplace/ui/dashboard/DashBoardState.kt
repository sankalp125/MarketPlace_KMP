package com.sankalp.marketplace.ui.dashboard

import com.sankalp.marketplace.data.models.CategoryResponse
import com.sankalp.marketplace.data.models.CityResponse
import com.sankalp.marketplace.data.models.CountryResponse
import com.sankalp.marketplace.data.models.ProductListResponse
import com.sankalp.marketplace.data.models.StatesResponse

data class DashBoardState(
    val selectedNavItem : NavItem = NavItem.HOME,
    val loadingProducts : Boolean = false,
    val loadingCategories : Boolean = false,
    val loadingCountries : Boolean = false,
    val loadingStates : Boolean = false,
    val loadingCities : Boolean = false,
    val categories : List<CategoryResponse> = emptyList(),
    val countryList : List<CountryResponse> = emptyList(),
    val stateList : List<StatesResponse> = emptyList(),
    val cityList : List<CityResponse> = emptyList(),
    val searchQuery : String = "",
    val selectedCategory : CategoryResponse? = null,
    val productList : List<ProductListResponse> = emptyList(),
    val filteredProducts : List<ProductListResponse> = emptyList(),
    val productMainImage : String = "",
    val productName : String = "",
    val productPrice : String = "",
    val productDesc : String = "",
    val productCategory : CategoryResponse? = null,
    val selectedCurrency : String = "",
    val currencyList : List<String> = listOf("INR"),
    val productCountry : CountryResponse? = null,
    val productState : StatesResponse? = null,
    val productCity : CityResponse? = null,
    val productTillDate : String = "",
    val otherImages : List<String> = emptyList(),
    val addingProduct : Boolean = false
)
