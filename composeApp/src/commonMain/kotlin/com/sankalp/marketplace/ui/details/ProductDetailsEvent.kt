package com.sankalp.marketplace.ui.details

sealed interface ProductDetailsEvent {
    data object OnBackClick : ProductDetailsEvent
    data object OnEditClick : ProductDetailsEvent
    data object OnDeleteClick : ProductDetailsEvent
    data class OnDeleteConfirm(val productId: String) : ProductDetailsEvent
}
