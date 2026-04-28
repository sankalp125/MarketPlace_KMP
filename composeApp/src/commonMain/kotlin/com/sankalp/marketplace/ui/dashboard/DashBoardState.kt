package com.sankalp.marketplace.ui.dashboard

import com.sankalp.marketplace.data.models.CategoryResponse
import com.sankalp.marketplace.data.models.ProductListResponse

data class DashBoardState(
    val selectedNavItem : NavItem = NavItem.HOME,
    val loadingProducts : Boolean = false,
    val loadingCategories : Boolean = false,
    val categories : List<CategoryResponse> = emptyList(),
    val searchQuery : String = "",
    val selectedCategory : CategoryResponse? = null,
    val productList : List<ProductListResponse> = emptyList(),
    val filteredProducts : List<ProductListResponse> = emptyList()
)
