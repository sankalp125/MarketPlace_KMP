package com.sankalp.marketplace.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sankalp.marketplace.data.models.CategoryResponse
import com.sankalp.marketplace.data.models.ProductListResponse
import com.sankalp.marketplace.ui.component.DialogHost
import com.sankalp.marketplace.ui.component.DropdownField
import com.sankalp.marketplace.ui.component.ImagePickerDialog
import com.sankalp.marketplace.ui.component.ImagePickerSheet
import com.sankalp.marketplace.ui.component.ProductGridCard
import com.sankalp.marketplace.ui.component.ProductListCard
import com.sankalp.marketplace.ui.component.UniversalBottomSheet
import com.sankalp.marketplace.ui.component.shimmerable
import com.sankalp.marketplace.ui.theme.AppTheme
import com.sankalp.marketplace.utils.WindowSize
import com.sankalp.marketplace.utils.formatMillisToDate
import com.sankalp.marketplace.utils.getCurrentMillis
import com.sankalp.marketplace.utils.getWindowSize
import com.sankalp.marketplace.utils.rememberBottomSheetManager
import com.sankalp.marketplace.utils.rememberDialogManager
import com.sankalp.marketplace.utils.rememberImagePicker
import compose.icons.FeatherIcons
import compose.icons.feathericons.Camera
import compose.icons.feathericons.Image
import compose.icons.feathericons.LogOut
import compose.icons.feathericons.Plus
import compose.icons.feathericons.Search
import compose.icons.feathericons.User
import compose.icons.feathericons.X
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import com.sankalp.marketplace.utils.SessionManager

@Composable
fun DashBoardRoot(
    onNavigateToProductDetails: (prodId: String, isMyProduct: Boolean) -> Unit,
    onNavigateToEditProduct: (prodId: String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val sessionScope = SessionManager.getOrCreateSessionScope()
    val viewModel = koinViewModel<DashBoardVm>(scope = sessionScope)
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    val snackBarHostState = remember { SnackbarHostState() }
    val bottomSheetManager = rememberBottomSheetManager()
    val dialogManager = rememberDialogManager()
    val imagePicker = rememberImagePicker()
    val keyboardController = LocalSoftwareKeyboardController.current
    val windowSize = getWindowSize()
    val isDesktop = windowSize != WindowSize.Compact
    
    val currentIsDesktop by rememberUpdatedState(isDesktop)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DashBoardEffect.NavigateToProductDetails -> onNavigateToProductDetails(effect.prodId, effect.isMyProduct)
                is DashBoardEffect.NavigateToEditProduct -> onNavigateToEditProduct(effect.prodId)
                is DashBoardEffect.NavigateToLogin -> onNavigateToLogin()
                is DashBoardEffect.ShowMessage -> snackBarHostState.showSnackbar(effect.message)
                is DashBoardEffect.ShowBottomSheet -> {
                    if (currentIsDesktop){
                        dialogManager.show {
                            ImagePickerDialog(
                                onSelect = { source ->
                                    dialogManager.hide()
                                    onEvent(DashBoardEvent.OpenImagePicker(source, effect.type))
                                }
                            )
                        }
                    }else {
                        bottomSheetManager.show {
                            ImagePickerSheet(
                                onSelect = { source ->
                                    bottomSheetManager.hide()
                                    onEvent(DashBoardEvent.OpenImagePicker(source, effect.type))
                                }
                            )
                        }
                    }
                }

                is DashBoardEffect.OpenImagePicker -> {
                    imagePicker.pickImage(effect.source) { path ->
                        path?.let { path ->
                            when(effect.type){
                                BottomSheetType.PRODUCT_MAIN -> {
                                    onEvent(DashBoardEvent.OnMainImagePicked(path))
                                }
                                BottomSheetType.PRODUCT_OTHER -> {
                                    onEvent(DashBoardEvent.OnProductImagePicked(path))
                                }
                                BottomSheetType.PROFILE_PIC -> {
                                    onEvent(DashBoardEvent.OnProfilePicPicked(path))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AdaptiveNavigation(
            selectedItem = state.selectedNavItem,
            onItemSelected = {
                onEvent(DashBoardEvent.OnNavItemClick(it))
            }
        ) {
            when (state.selectedNavItem) {
                NavItem.HOME -> HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    onEvent = onEvent,
                    keyboardController = keyboardController
                )

                NavItem.ADD_PRODUCT -> AddProductScreen(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    onEvent = onEvent,
                    keyboardController = keyboardController
                )
                NavItem.PROFILE -> ProfileScreen(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    onEvent = onEvent,
                    keyboardController = keyboardController
                )
            }
        }
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
        )
        UniversalBottomSheet(
            manager = bottomSheetManager,
            onDismiss = { bottomSheetManager.hide() }
        )
        DialogHost(
            manager = dialogManager,
            onDismiss = { dialogManager.hide() }
        )
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: DashBoardState = DashBoardState(),
    onEvent: (DashBoardEvent) -> Unit = {},
    keyboardController: SoftwareKeyboardController? = null
) {
    val window = getWindowSize()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeTopBar(
            searchQuery = state.searchQuery,
            onEvent = onEvent
        )
        CategoryFilterRow(
            isLoading = state.loadingCategories,
            categories = state.categories,
            selectedCategory = state.selectedCategory,
            onCategorySelect = { onEvent(DashBoardEvent.OnCategoryClick(it)) }
        )
        ProductContent(
            isLoading = state.loadingProducts,
            products = state.filteredProducts,
            windowSize = window,
            onEvent = onEvent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    modifier: Modifier = Modifier,
    state: DashBoardState = DashBoardState(),
    onEvent: (DashBoardEvent) -> Unit = {},
    keyboardController: SoftwareKeyboardController? = null
) {
    val windowSize = getWindowSize()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= getCurrentMillis()
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onEvent(DashBoardEvent.OnProductTillDateChange(formatMillisToDate(it)))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .then(
                    if (windowSize != WindowSize.Compact) Modifier.widthIn(max = 600.dp)
                    else Modifier.fillMaxWidth()
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Image Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (windowSize != WindowSize.Compact) 300.dp else 200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onEvent(DashBoardEvent.OnMainImagePickRequest(BottomSheetType.PRODUCT_MAIN)) },
                contentAlignment = Alignment.Center
            ) {
            if (state.productMainImage.isNotBlank()) {
                AsyncImage(
                    model = state.productMainImage,
                    contentDescription = "Product Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = FeatherIcons.Image,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Select Main Image",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.productName,
            onValueChange = { onEvent(DashBoardEvent.OnProductNameChange(it)) },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
            value = state.productDesc,
            onValueChange = { onEvent(DashBoardEvent.OnProductDescChange(it)) },
            label = { Text("Product Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.productPrice,
                onValueChange = { onEvent(DashBoardEvent.OnProductPriceChange(it)) },
                label = { Text("Price") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(10.dp)
            )

            DropdownField(
                label = "Currency",
                items = state.currencyList,
                selectedItem = state.selectedCurrency,
                itemLabel = { it },
                onSelect = { onEvent(DashBoardEvent.OnProductCurrencyChange(it)) },
                loading = false,
                modifier = Modifier.weight(0.6f)
            )
        }

        DropdownField(
            label = "Category",
            items = state.categories.filter { it.categoryName != "All" },
            selectedItem = state.productCategory,
            itemLabel = { it.categoryName },
            onSelect = { onEvent(DashBoardEvent.OnProductCategoryChange(it)) },
            loading = state.loadingCategories
        )

        DropdownField(
            label = "Country",
            items = state.countryList,
            selectedItem = state.productCountry,
            itemLabel = { it.countryName },
            onSelect = { onEvent(DashBoardEvent.OnProductCountryChange(it)) },
            loading = false
        )

        DropdownField(
            label = "State",
            items = state.stateList,
            selectedItem = state.productState,
            itemLabel = { it.stateName },
            onSelect = { onEvent(DashBoardEvent.OnProductStateChange(it)) },
            loading = false,
            enabled = state.productCountry != null
        )

        DropdownField(
            label = "City",
            items = state.cityList,
            selectedItem = state.productCity,
            itemLabel = { it.cityName },
            onSelect = { onEvent(DashBoardEvent.OnProductCityChange(it)) },
            loading = false,
            enabled = state.productState != null
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        ) {
            OutlinedTextField(
                value = state.productTillDate,
                onValueChange = {},
                label = { Text("Till Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }

        Text(
            "Other Images",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            itemsIndexed(state.otherImages) { index, image ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = image,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { onEvent(DashBoardEvent.OnProductImageDeleteRequest(index)) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = FeatherIcons.X,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onEvent(DashBoardEvent.OnProductImagePickRequest(BottomSheetType.PRODUCT_OTHER)) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = FeatherIcons.Plus,
                        contentDescription = "Add Image",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Button(
            onClick = { onEvent(DashBoardEvent.OnAddProductClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !state.addingProduct
        ) {
            if (state.addingProduct) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Add Product",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    state: DashBoardState = DashBoardState(),
    onEvent: (DashBoardEvent) -> Unit = {},
    keyboardController: SoftwareKeyboardController? = null
) {
    val windowSize = getWindowSize()
    val isDesktop = windowSize != WindowSize.Compact

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section 1: User Profile
        item {
            Column(
                modifier = Modifier
                    .then(
                        if (isDesktop) Modifier.widthIn(max = 600.dp)
                        else Modifier.fillMaxWidth()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { onEvent(DashBoardEvent.OnProfilePicPickRequest(BottomSheetType.PROFILE_PIC)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.profileDetails?.photoUrl?.isNotBlank() == true || state.selectedProfilePic.isNotBlank()) {
                        AsyncImage(
                            model = if (state.selectedProfilePic.isNotBlank()) state.selectedProfilePic else state.profileDetails?.photoUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = FeatherIcons.User,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Edit Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Camera,
                            contentDescription = "Edit Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                if (state.updatingProfilePic) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }

                Text(
                    text = state.profileDetails?.name ?: "User Name",
                    style = MaterialTheme.typography.headlineSmall
                )

                // Profile Fields
                OutlinedTextField(
                    value = state.profileDetails?.name ?: "",
                    onValueChange = { onEvent(DashBoardEvent.OnProfileNameChange(it)) },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = state.profileDetails?.mobile ?: "",
                    onValueChange = { onEvent(DashBoardEvent.OnProfilePhoneChange(it)) },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = state.profileDetails?.country ?: "",
                        onValueChange = { onEvent(DashBoardEvent.OnProfileCountryChange(it)) },
                        label = { Text("Country") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = state.profileDetails?.state ?: "",
                        onValueChange = { onEvent(DashBoardEvent.OnProfileStateChange(it)) },
                        label = { Text("State") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                OutlinedTextField(
                    value = state.profileDetails?.city ?: "",
                    onValueChange = { onEvent(DashBoardEvent.OnProfileCityChange(it)) },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Button(
                    onClick = { onEvent(DashBoardEvent.OnProfileUpdateClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !state.updatingProfile
                ) {
                    if (state.updatingProfile) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Update Profile")
                    }
                }

                Button(
                    onClick = { onEvent(DashBoardEvent.OnLogoutClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(imageVector = FeatherIcons.LogOut, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout")
                }
            }
        }

        // Section 2: My Products
        item {
            Text(
                text = "My Products",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (state.loadingMyProducts) {
            items(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(vertical = 8.dp)
                        .shimmerable(enabled = true, shape = RoundedCornerShape(12.dp))
                )
            }
        } else if (state.myProducts.isEmpty()) {
            item {
                Text("You haven't uploaded any products yet.")
            }
        } else {
            items(state.myProducts) { product ->
                val adaptedProduct = ProductListResponse(
                    productId = product.productId,
                    productName = product.productName,
                    productDesc = "",
                    productCategory = "",
                    productCity = "",
                    productPrice = product.productPrice.toString(),
                    pictureUrl = product.productImage
                )

                ProductListCard(
                    product = adaptedProduct,
                    onClick = { onEvent(DashBoardEvent.OnMyProductClick(product.productId)) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

// top search bar
@Composable
private fun HomeTopBar(
    searchQuery: String,
    onEvent: (DashBoardEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "MarketPlace",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { onEvent(DashBoardEvent.OnSearchQueryChange(it)) },
            placeholder = { Text("Search products...") },
            leadingIcon = {
                Icon(
                    imageVector = FeatherIcons.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onEvent(DashBoardEvent.OnClearSearchQuery) }) {
                        Icon(
                            imageVector = FeatherIcons.X,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// categories Row
@Composable
private fun CategoryFilterRow(
    isLoading: Boolean,
    categories: List<CategoryResponse>,
    selectedCategory: CategoryResponse?,
    onCategorySelect: (CategoryResponse) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val windowSize = getWindowSize()

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(bottom = 8.dp)
            .then(
                // Desktop mouse wheel handling
                if (windowSize == WindowSize.Expanded) {
                    Modifier.pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                if (event.type == PointerEventType.Scroll) {
                                    val scrollDelta = event.changes
                                        .firstOrNull()
                                        ?.scrollDelta
                                        ?.y ?: 0f
                                    scope.launch {
                                        listState.scrollBy(scrollDelta * 50f)
                                    }
                                }
                            }
                        }
                    }
                } else Modifier
            )
    ) {
        if (isLoading) {
            items(5) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp)
                        .shimmerable(
                            enabled = true,
                            shape = RoundedCornerShape(50.dp)
                        )
                )
            }
        } else {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelect(category) },
                    label = { Text(category.categoryName) }
                )
            }
        }
    }
}

@Composable
private fun ProductContent(
    isLoading: Boolean,
    products: List<ProductListResponse>,
    windowSize: WindowSize,
    onEvent: (DashBoardEvent) -> Unit
) {
    when (windowSize) {

        // Phone + Tablet — Grid
        WindowSize.Compact,
        WindowSize.Medium -> {
            val columns = if (windowSize == WindowSize.Compact) 2 else 3

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (isLoading) {
                    items(6) { ShimmerGridCard() }  // ← Placeholder
                } else {
                    items(
                        items = products,
                        key = { it.productId }
                    ) { product ->
                        ProductGridCard(
                            product = product,
                            onClick = { onEvent(DashBoardEvent.OnProductClick(product.productId)) }
                        )
                    }
                }
            }
        }

        // Desktop — List
        WindowSize.Expanded -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (isLoading) {
                    items(5) { ShimmerListCard() }  // ← Placeholder
                } else {
                    items(
                        items = products,
                        key = { it.productId }
                    ) { product ->
                        ProductListCard(
                            product = product,
                            onClick = { onEvent(DashBoardEvent.OnProductClick(product.productId)) }
                        )
                    }
                }
            }
        }
    }
}

// ─── Shimmer Grid Card ─────────────────────────────────────

@Composable
private fun ShimmerGridCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
    ) {
        // Image placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shimmerable(enabled = true)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Name placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(14.dp)
                .shimmerable(enabled = true)
        )
        Spacer(modifier = Modifier.height(6.dp))
        // Price placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(18.dp)
                .shimmerable(enabled = true)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

// ─── Shimmer List Card ─────────────────────────────────────

@Composable
private fun ShimmerListCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .shimmerable(
                    enabled = true,
                    shape = MaterialTheme.shapes.medium
                )
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Category placeholder
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(14.dp)
                    .shimmerable(enabled = true)
            )
            // Name placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(18.dp)
                    .shimmerable(enabled = true)
            )
            // Price placeholder
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(22.dp)
                    .shimmerable(enabled = true)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview1() {
    AppTheme {
        HomeScreen(
            modifier = Modifier.fillMaxSize()
                .safeContentPadding()
                .padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview2() {
    AppTheme {
        AddProductScreen(
            modifier = Modifier.fillMaxSize()
                .safeContentPadding()
                .padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview3() {
    AppTheme {
        ProfileScreen(
            modifier = Modifier.fillMaxSize()
                .safeContentPadding()
                .padding(horizontal = 16.dp)
        )
    }
}