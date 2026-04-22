package com.sankalp.marketplace.ui.dashboard

sealed class DashBoardEffect {
    data object NavigateToLogin : DashBoardEffect()
    data class NavigateToProductDetails(val prodId : String) : DashBoardEffect()
    data class NavigateToEditProduct(val prodId : String) : DashBoardEffect()
    data class ShowMessage(val message : String) : DashBoardEffect()
}