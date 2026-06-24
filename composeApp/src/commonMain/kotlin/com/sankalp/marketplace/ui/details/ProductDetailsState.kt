package com.sankalp.marketplace.ui.details

import com.sankalp.marketplace.data.models.ProductDetailsResponse

data class ProductDetailsState(
    val productId: String = "",
    val isMyProduct: Boolean = false,
    val isLoading: Boolean = false,
    val product: ProductDetailsResponse? = null,
    val deletingProduct: Boolean = false,
    val error: String? = null
)
