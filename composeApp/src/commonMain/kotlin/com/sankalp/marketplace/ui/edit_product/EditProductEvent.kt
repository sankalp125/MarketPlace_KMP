package com.sankalp.marketplace.ui.edit_product

import com.sankalp.marketplace.utils.ImageSource

sealed interface EditProductEvent {
    data class OnPriceChange(val price: String) : EditProductEvent
    data class OnTillDateChange(val date: String) : EditProductEvent
    data class OnStatusChange(val status: Boolean) : EditProductEvent
    data object OnUpdateClick : EditProductEvent
    data class OnAddPicturesClick(val source: ImageSource) : EditProductEvent
    data class OnDeletePictureClick(val url: String) : EditProductEvent
    data object OnBackClick : EditProductEvent
}
