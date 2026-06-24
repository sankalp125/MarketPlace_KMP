package com.sankalp.marketplace.ui.edit_product

import com.sankalp.marketplace.data.models.ProductDetailsResponse

data class EditProductState(
    val productId: String = "",
    val isLoading: Boolean = false,
    val product: ProductDetailsResponse? = null,
    val price: String = "",
    val tillDate: String = "",
    val status: Boolean = true,
    val isUpdating: Boolean = false,
    val addingPictures: Boolean = false,
    val deletingPicture: String? = null
)
