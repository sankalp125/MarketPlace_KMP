package com.sankalp.marketplace.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sankalp.marketplace.data.api.NetworkResult
import com.sankalp.marketplace.data.repository.ProductRepo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsVm(
    private val productRepo: ProductRepo
) : ViewModel() {
    private val _state = MutableStateFlow(ProductDetailsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProductDetailsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun init(productId: String, isMyProduct: Boolean) {
        _state.update { it.copy(productId = productId, isMyProduct = isMyProduct) }
        getProductDetails(productId)
    }

    fun onEvent(event: ProductDetailsEvent) {
        when (event) {
            ProductDetailsEvent.OnBackClick -> {
                viewModelScope.launch { _effect.send(ProductDetailsEffect.NavigateBack) }
            }
            ProductDetailsEvent.OnEditClick -> {
                viewModelScope.launch { 
                    _effect.send(ProductDetailsEffect.NavigateToEditProduct(_state.value.productId)) 
                }
            }
            ProductDetailsEvent.OnDeleteClick -> {
                // Should show a confirmation dialog in the UI
            }
            is ProductDetailsEvent.OnDeleteConfirm -> {
                deleteProduct(event.productId)
            }
        }
    }

    private fun getProductDetails(productId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when (val result = productRepo.productDetails(productId)) {
                    is NetworkResult.Success -> {
                        _state.update { it.copy(product = result.data) }
                    }
                    is NetworkResult.Error.GeneralError -> {
                        _effect.send(ProductDetailsEffect.ShowMessage(result.message))
                    }
                    is NetworkResult.Error.NetworkError -> {
                        _effect.send(ProductDetailsEffect.ShowMessage("Network error, please try again."))
                    }
                    is NetworkResult.Loading -> {}
                }
            } catch (e: Exception) {
                _effect.send(ProductDetailsEffect.ShowMessage(e.message ?: "An error occurred."))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _state.update { it.copy(deletingProduct = true) }
            try {
                when (val result = productRepo.deleteProduct(productId)) {
                    is NetworkResult.Success -> {
                        _effect.send(ProductDetailsEffect.ShowMessage(result.data))
                        _effect.send(ProductDetailsEffect.ProductDeleted)
                    }
                    is NetworkResult.Error.GeneralError -> {
                        _effect.send(ProductDetailsEffect.ShowMessage(result.message))
                    }
                    is NetworkResult.Error.NetworkError -> {
                        _effect.send(ProductDetailsEffect.ShowMessage("Network error, please try again."))
                    }
                    is NetworkResult.Loading -> {}
                }
            } catch (e: Exception) {
                _effect.send(ProductDetailsEffect.ShowMessage(e.message ?: "An error occurred."))
            } finally {
                _state.update { it.copy(deletingProduct = false) }
            }
        }
    }
}
