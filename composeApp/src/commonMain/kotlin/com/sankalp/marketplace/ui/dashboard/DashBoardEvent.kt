package com.sankalp.marketplace.ui.dashboard

import com.sankalp.marketplace.data.models.CategoryResponse
import com.sankalp.marketplace.data.models.CityResponse
import com.sankalp.marketplace.data.models.CountryResponse
import com.sankalp.marketplace.data.models.StatesResponse
import com.sankalp.marketplace.utils.ImageSource

sealed interface DashBoardEvent {
    data class OnNavItemClick(val navItem : NavItem) : DashBoardEvent
    data class OnSearchQueryChange(val query : String) : DashBoardEvent
    data object OnClearSearchQuery : DashBoardEvent
    data class OnCategoryClick(val category : CategoryResponse) : DashBoardEvent
    data class OnProductClick(val prodId : String) : DashBoardEvent
    data class OnMainImagePickRequest(val type : BottomSheetType): DashBoardEvent
    data class OnMainImagePicked(val imagePath : String) : DashBoardEvent
    data class OpenImagePicker(val source : ImageSource, val type : BottomSheetType) : DashBoardEvent
    data class OnProductNameChange(val name : String) : DashBoardEvent
    data class OnProductPriceChange(val price : String) : DashBoardEvent
    data class OnProductDescChange(val desc : String) : DashBoardEvent
    data class OnProductCategoryChange(val category : CategoryResponse) : DashBoardEvent
    data class OnProductCountryChange(val country : CountryResponse) : DashBoardEvent
    data class OnProductStateChange(val state : StatesResponse) : DashBoardEvent
    data class OnProductCityChange(val city : CityResponse) : DashBoardEvent
    data class OnProductCurrencyChange(val currency : String) : DashBoardEvent
    data class OnProductTillDateChange(val date : String) : DashBoardEvent
    data class OnProductImagePickRequest(val type : BottomSheetType) : DashBoardEvent
    data class OnProductImagePicked(val imagePath : String) : DashBoardEvent
    data class OnProductImageDeleteRequest(val index : Int) : DashBoardEvent
    data object OnAddProductClick : DashBoardEvent
}