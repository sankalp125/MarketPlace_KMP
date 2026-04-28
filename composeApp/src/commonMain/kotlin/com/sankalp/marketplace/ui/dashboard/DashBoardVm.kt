package com.sankalp.marketplace.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sankalp.marketplace.data.api.NetworkResult
import com.sankalp.marketplace.data.repository.ProductRepo
import com.sankalp.marketplace.data.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashBoardVm(
    private val productRepo: ProductRepo,
    private val userRepo: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DashBoardState())
    val state = _state.asStateFlow()
    private val _effect = Channel<DashBoardEffect?>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        getCategories()
        getProducts()
    }

    fun onEvent(event: DashBoardEvent) {
        when (event) {
            is DashBoardEvent.OnNavItemClick -> {
                _state.update {
                    it.copy(
                        selectedNavItem = event.navItem
                    )
                }
            }

            is DashBoardEvent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query
                    )
                }
                filterProducts()
            }

            is DashBoardEvent.OnClearSearchQuery -> {
                _state.update {
                    it.copy(
                        searchQuery = ""
                    )
                }
                filterProducts()
            }

            is DashBoardEvent.OnCategoryClick -> {
                _state.update {
                    it.copy(
                        selectedCategory = event.category
                    )
                }
                filterProducts()
            }
            is DashBoardEvent.OnProductClick -> {
                viewModelScope.launch {
                    _effect.send(DashBoardEffect.NavigateToProductDetails(event.prodId))
                }
            }
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loadingCategories = true
                )
            }
            try {
                when (val result = productRepo.getCategories()) {
                    is NetworkResult.Success -> {
                        _state.update {
                            it.copy(
                                categories = result.data,
                            )
                        }
                    }

                    is NetworkResult.Error.GeneralError -> {
                        _effect.send(DashBoardEffect.ShowMessage(result.message))
                    }

                    is NetworkResult.Error.NetworkError -> {
                        _effect.send(DashBoardEffect.ShowMessage("Please check your internet connection!"))
                    }

                    is NetworkResult.Loading -> {}
                }
            } catch (e: Exception) {
                _effect.send(DashBoardEffect.ShowMessage(e.message ?: "Something went wrong!"))
            } finally {
                _state.update {
                    it.copy(
                        loadingCategories = false
                    )
                }
            }
        }
    }

    private fun getProducts() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loadingProducts = true
                )
            }
            try {
                when (val result = productRepo.getProductList()) {
                    is NetworkResult.Success -> {
                        _state.update {
                            it.copy(
                                productList = result.data,
                                filteredProducts = result.data
                            )
                        }
                    }

                    is NetworkResult.Error.GeneralError -> {
                        _effect.send(DashBoardEffect.ShowMessage(result.message))
                    }

                    is NetworkResult.Error.NetworkError -> {
                        _effect.send(DashBoardEffect.ShowMessage("Please check your internet connection!"))
                    }

                    is NetworkResult.Loading -> {}
                }
            } catch (e: Exception) {
                _effect.send(
                    DashBoardEffect.ShowMessage(
                        e.message ?: "Something went wrong, Please try again later!"
                    )
                )
            } finally {
                _state.update {
                    it.copy(
                        loadingProducts = false
                    )
                }
            }
        }
    }

    private fun filterProducts() {
        val query = _state.value.searchQuery.lowercase()
        val category = _state.value.selectedCategory

        val filtered = _state.value.productList.filter { product ->
            val matchesSearch = query.isBlank() ||
                    product.productName.lowercase().contains(query) ||
                    product.productCategory.lowercase().contains(query)
            val matchesCategory = category == null || category.categoryName == "All" ||
                    product.productCategory == category.categoryName

            matchesSearch && matchesCategory
        }

        _state.update { it.copy(filteredProducts = filtered) }
    }
}