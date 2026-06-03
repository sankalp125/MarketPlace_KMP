package com.sankalp.marketplace.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sankalp.marketplace.data.api.NetworkResult
import com.sankalp.marketplace.data.models.MultipartFile
import com.sankalp.marketplace.data.models.MultipartRequest
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
    private val _effect = Channel<DashBoardEffect>(Channel.BUFFERED)
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
                if (_state.value.countryList.isEmpty()){
                    getCountries()
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
            is DashBoardEvent.OnMainImagePickRequest -> {
                viewModelScope.launch {
                    _effect.send(DashBoardEffect.ShowBottomSheet(event.type))
                }
            }
            is DashBoardEvent.OnProductImagePickRequest -> {
                viewModelScope.launch {
                    _effect.send(DashBoardEffect.ShowBottomSheet(event.type))
                }
            }
            is DashBoardEvent.OpenImagePicker -> {
                viewModelScope.launch {
                    _effect.send(DashBoardEffect.OpenImagePicker(event.source, event.type))
                }
            }
            is DashBoardEvent.OnMainImagePicked -> {
                _state.update {
                    it.copy(
                        productMainImage = event.imagePath
                    )
                }
            }
            is DashBoardEvent.OnProductImagePicked -> {
                _state.update {
                    it.copy(
                        otherImages = it.otherImages
                            .toMutableList()
                            .also { list -> list.add(event.imagePath) }
                    )
                }
            }
            is DashBoardEvent.OnProductImageDeleteRequest -> {
                _state.update {
                    it.copy(
                        otherImages = it.otherImages
                            .toMutableList()
                            .also { list -> list.removeAt(event.index) }
                    )
                }
            }
            is DashBoardEvent.OnProductNameChange -> {
                _state.update {
                    it.copy(
                        productName = event.name
                    )
                }
            }
            is DashBoardEvent.OnProductDescChange -> {
                _state.update {
                    it.copy(
                        productDesc = event.desc
                    )
                }
            }
            is DashBoardEvent.OnProductPriceChange -> {
                _state.update {
                    it.copy(
                        productPrice = event.price
                    )
                }
            }
            is DashBoardEvent.OnProductCategoryChange -> {
                _state.update {
                    it.copy(
                        productCategory = event.category
                    )
                }
            }
            is DashBoardEvent.OnProductCountryChange -> {
                _state.update {
                    it.copy(
                        productCountry = event.country
                    )
                }
                getStates()
            }
            is DashBoardEvent.OnProductStateChange -> {
                _state.update {
                    it.copy(
                        productState = event.state
                    )
                }
                getCities()
            }
            is DashBoardEvent.OnProductCityChange -> {
                _state.update {
                    it.copy(
                        productCity = event.city
                    )
                }
            }
            is DashBoardEvent.OnProductCurrencyChange -> {
                _state.update {
                    it.copy(
                        selectedCurrency = event.currency
                    )
                }
            }
            is DashBoardEvent.OnProductTillDateChange -> {
                _state.update {
                    it.copy(
                        productTillDate = event.date
                    )
                }
            }
            is DashBoardEvent.OnAddProductClick -> {
                addProduct()
            }
        }
    }

    private fun addProduct() {
        val currentState = _state.value

        if (currentState.productName.isBlank() ||
            currentState.productPrice.isBlank() ||
            currentState.productMainImage.isBlank() ||
            currentState.productCategory == null
        ) {
            viewModelScope.launch {
                _effect.send(DashBoardEffect.ShowMessage("Please fill in all required fields and select a category/image"))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(addingProduct = true) }

            val formFields = mutableMapOf(
                "productName" to currentState.productName,
                "description" to currentState.productDesc,
                "price" to currentState.productPrice,
                "category" to currentState.productCategory.categoryId,
                "productCurrency" to currentState.selectedCurrency,
                "country" to (currentState.productCountry?.countryId ?: ""),
                "state" to (currentState.productState?.stateId ?: ""),
                "city" to (currentState.productCity?.cityId ?: ""),
                "tillDate" to currentState.productTillDate
            )

            val files = mutableListOf<MultipartFile>()

            // Main Image
            files.add(
                MultipartFile(
                    key = "picture",
                    filePath = currentState.productMainImage,
                    fileName = "main_image.jpg"
                )
            )

            // Other Images
            currentState.otherImages.forEachIndexed { index, path ->
                files.add(
                    MultipartFile(
                        key = "other_pictures",
                        filePath = path,
                        fileName = "other_image_$index.jpg"
                    )
                )
            }

            val request = MultipartRequest(
                formFields = formFields,
                files = files
            )

            try {
                when (val result = productRepo.addProduct(request)) {
                    is NetworkResult.Success -> {
                        _effect.send(DashBoardEffect.ShowMessage(result.data.message))
                        _state.update {
                            it.copy(
                                productName = "",
                                productPrice = "",
                                productDesc = "",
                                productMainImage = "",
                                productTillDate = "",
                                otherImages = emptyList(),
                                selectedNavItem = NavItem.HOME
                            )
                        }
                        getProducts()
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
                        addingProduct = false
                    )
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
    private fun getCountries() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loadingCountries = true
                )
            }
            try {
                when(val result = productRepo.requestCountries()){
                    is NetworkResult.Success -> {
                        _state.update {
                            it.copy(
                                countryList = result.data
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
            }catch (e : Exception){
                _effect.send(DashBoardEffect.ShowMessage(e.message ?: "Something went wrong!"))
            }finally {
                _state.update {
                    it.copy(
                        loadingCountries = false
                    )
                }
            }
        }
    }
    private fun getStates() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loadingStates = true
                )
            }
            try {
                when(val result = productRepo.requestStates(_state.value.productCountry?.countryId ?: "")){
                    is NetworkResult.Success -> {
                        _state.update {
                            it.copy(
                                stateList = result.data
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
            }catch (e : Exception){
                _effect.send(DashBoardEffect.ShowMessage(e.message ?: "Something went wrong!"))
            }finally {
                _state.update {
                    it.copy(
                        loadingStates = false
                    )
                }
            }
        }
    }
    private fun getCities() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loadingCities = true
                )
            }
            try {
                when(val result = productRepo.requestCities(_state.value.productState?.stateId ?: "")){
                    is NetworkResult.Success -> {
                        _state.update {
                            it.copy(
                                cityList = result.data
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
            }catch (e : Exception){
                _effect.send(DashBoardEffect.ShowMessage(e.message ?: "Something went wrong!"))
            }finally {
                _state.update {
                    it.copy(
                        loadingCities = false
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