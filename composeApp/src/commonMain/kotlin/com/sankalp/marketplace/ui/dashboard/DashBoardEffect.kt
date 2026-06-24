package com.sankalp.marketplace.ui.dashboard

import com.sankalp.marketplace.utils.ImageSource

sealed class DashBoardEffect {
    data object NavigateToLogin : DashBoardEffect()
    data class NavigateToProductDetails(val prodId : String, val isMyProduct: Boolean = false) : DashBoardEffect()
    data class NavigateToEditProduct(val prodId : String) : DashBoardEffect()
    data class ShowMessage(val message : String) : DashBoardEffect()
    data class ShowBottomSheet(val type : BottomSheetType) : DashBoardEffect()
    data class OpenImagePicker(val source : ImageSource, val type : BottomSheetType) : DashBoardEffect()
}