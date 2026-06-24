package com.sankalp.marketplace.ui.details

sealed class ProductDetailsEffect {
    data object NavigateBack : ProductDetailsEffect()
    data class NavigateToEditProduct(val productId: String) : ProductDetailsEffect()
    data class ShowMessage(val message: String) : ProductDetailsEffect()
    data object ProductDeleted : ProductDetailsEffect()
}
