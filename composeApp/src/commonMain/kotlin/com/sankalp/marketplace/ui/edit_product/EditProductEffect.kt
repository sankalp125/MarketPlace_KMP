package com.sankalp.marketplace.ui.edit_product

import com.sankalp.marketplace.utils.ImageSource

sealed class EditProductEffect {
    data object NavigateBack : EditProductEffect()
    data class ShowMessage(val message: String) : EditProductEffect()
    data class OpenImagePicker(val source: ImageSource) : EditProductEffect()
}
