package com.sankalp.marketplace.ui.dashboard

import com.sankalp.marketplace.data.models.CategoryResponse

sealed interface DashBoardEvent {
    data class OnNavItemClick(val navItem : NavItem) : DashBoardEvent
    data class OnSearchQueryChange(val query : String) : DashBoardEvent
    data object OnClearSearchQuery : DashBoardEvent
    data class OnCategoryClick(val category : CategoryResponse) : DashBoardEvent
    data class OnProductClick(val prodId : String) : DashBoardEvent
}