package com.sankalp.marketplace.ui.edit_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sankalp.marketplace.data.api.NetworkResult
import com.sankalp.marketplace.data.models.DeleteProductPictureRequest
import com.sankalp.marketplace.data.models.MultipartFile
import com.sankalp.marketplace.data.models.MultipartRequest
import com.sankalp.marketplace.data.models.UpdateProductRequest
import com.sankalp.marketplace.data.repository.ProductRepo
import com.sankalp.marketplace.utils.getCurrentMillis
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProductVm(
    private val productRepo: ProductRepo
) : ViewModel() {
    private val _state = MutableStateFlow(EditProductState())
    val state = _state.asStateFlow()

    private val _effect = Channel<EditProductEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun init(productId: String) {
        _state.update { it.copy(productId = productId) }
        getProductDetails(productId)
    }

    fun onEvent(event: EditProductEvent) {
        when (event) {
            is EditProductEvent.OnPriceChange -> _state.update { it.copy(price = event.price) }
            is EditProductEvent.OnTillDateChange -> _state.update { it.copy(tillDate = event.date) }
            is EditProductEvent.OnStatusChange -> _state.update { it.copy(status = event.status) }
            EditProductEvent.OnUpdateClick -> updateProduct()
            is EditProductEvent.OnAddPicturesClick -> {
                viewModelScope.launch { _effect.send(EditProductEffect.OpenImagePicker(event.source)) }
            }
            is EditProductEvent.OnDeletePictureClick -> deletePicture(event.url)
            EditProductEvent.OnBackClick -> {
                viewModelScope.launch { _effect.send(EditProductEffect.NavigateBack) }
            }
        }
    }

    private fun getProductDetails(productId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when (val result = productRepo.productDetails(productId)) {
                    is NetworkResult.Success -> {
                        _state.update { 
                            it.copy(
                                product = result.data,
                                price = result.data.price.toString(),
                                tillDate = result.data.date,
                                status = result.data.status
                            ) 
                        }
                    }
                    is NetworkResult.Error.GeneralError -> _effect.send(EditProductEffect.ShowMessage(result.message))
                    is NetworkResult.Error.NetworkError -> _effect.send(EditProductEffect.ShowMessage("Network error"))
                    is NetworkResult.Loading -> {}
                }
            } catch (e: Exception) {
                _effect.send(EditProductEffect.ShowMessage(e.message ?: "An error occurred"))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun updateProduct() {
        viewModelScope.launch {
            _state.update { it.copy(isUpdating = true) }
            val request = UpdateProductRequest(
                productId = _state.value.productId,
                price = _state.value.price,
                date = _state.value.tillDate,
                status = _state.value.status.toString()
            )
            try {
                when (val result = productRepo.updateProduct(request)) {
                    is NetworkResult.Success -> {
                        _effect.send(EditProductEffect.ShowMessage(result.data))
                    }
                    is NetworkResult.Error.GeneralError -> _effect.send(EditProductEffect.ShowMessage(result.message))
                    is NetworkResult.Error.NetworkError -> _effect.send(EditProductEffect.ShowMessage("Network error"))
                    is NetworkResult.Loading -> {}
                }
            } catch (e: Exception) {
                _effect.send(EditProductEffect.ShowMessage(e.message ?: "An error occurred"))
            } finally {
                _state.update { it.copy(isUpdating = false) }
            }
        }
    }

    fun onImagesPicked(paths: List<String>) {
        viewModelScope.launch {
            _state.update { it.copy(addingPictures = true) }
            
            val files = paths.mapIndexed { index, path ->
                MultipartFile(
                    key = "other_pictures",
                    filePath = path,
                    fileName = "extra_image_${index}_${getCurrentMillis()}.jpg"
                )
            }

            val request = MultipartRequest(
                formFields = mapOf("productId" to _state.value.productId),
                files = files
            )

            try {
                when (val result = productRepo.addProductPictures(request)) {
                    is NetworkResult.Success -> {
                        _effect.send(EditProductEffect.ShowMessage(result.data))
                        getProductDetails(_state.value.productId)
                    }
                    is NetworkResult.Error.GeneralError -> _effect.send(EditProductEffect.ShowMessage(result.message))
                    is NetworkResult.Error.NetworkError -> _effect.send(EditProductEffect.ShowMessage("Network error"))
                    is NetworkResult.Loading -> {}
                }
            } catch (e: Exception) {
                _effect.send(EditProductEffect.ShowMessage(e.message ?: "An error occurred"))
            } finally {
                _state.update { it.copy(addingPictures = false) }
            }
        }
    }

    private fun deletePicture(url: String) {
        viewModelScope.launch {
            _state.update { it.copy(deletingPicture = url) }
            val request = DeleteProductPictureRequest(url = url)
            try {
                when (val result = productRepo.deleteProductPicture(request)) {
                    is NetworkResult.Success -> {
                        _effect.send(EditProductEffect.ShowMessage(result.data))
                        getProductDetails(_state.value.productId)
                    }
                    is NetworkResult.Error.GeneralError -> _effect.send(EditProductEffect.ShowMessage(result.message))
                    is NetworkResult.Error.NetworkError -> _effect.send(EditProductEffect.ShowMessage("Network error"))
                    is NetworkResult.Loading -> {}
                }
            } catch (e: Exception) {
                _effect.send(EditProductEffect.ShowMessage(e.message ?: "An error occurred"))
            } finally {
                _state.update { it.copy(deletingPicture = null) }
            }
        }
    }
}
