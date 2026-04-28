package com.sankalp.marketplace.ui.dashboard

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sankalp.marketplace.data.models.CategoryResponse
import com.sankalp.marketplace.data.models.ProductListResponse
import com.sankalp.marketplace.ui.component.ProductGridCard
import com.sankalp.marketplace.ui.component.ProductListCard
import com.sankalp.marketplace.ui.component.shimmerable
import com.sankalp.marketplace.ui.theme.AppTheme
import com.sankalp.marketplace.utils.WindowSize
import com.sankalp.marketplace.utils.getWindowSize
import compose.icons.FeatherIcons
import compose.icons.feathericons.Search
import compose.icons.feathericons.X
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashBoardRoot(
    onNavigateToProductDetails: (prodId: String) -> Unit,
    onNavigateToEditProduct: (prodId: String) -> Unit,
    onaNavigateToLogin: () -> Unit
) {
    val viewModel = koinViewModel<DashBoardVm>()
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DashBoardEffect.NavigateToProductDetails -> onNavigateToProductDetails(effect.prodId)
                is DashBoardEffect.NavigateToEditProduct -> onNavigateToEditProduct(effect.prodId)
                is DashBoardEffect.NavigateToLogin -> onaNavigateToLogin()
                is DashBoardEffect.ShowMessage -> snackBarHostState.showSnackbar(effect.message)
                else -> {}
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

                NavItem.ADD_PRODUCT -> AddProductScreen()
                NavItem.PROFILE -> ProfileScreen()
            }
        }
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
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

@Composable
fun AddProductScreen(
    modifier: Modifier = Modifier,
    state: DashBoardState = DashBoardState(),
    onEvent: (DashBoardEvent) -> Unit = {},
    keyboardController: SoftwareKeyboardController? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    state: DashBoardState = DashBoardState(),
    onEvent: (DashBoardEvent) -> Unit = {},
    keyboardController: SoftwareKeyboardController? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

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
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        if (isLoading) {
            items(5) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp)
                        .shimmerable(
                            enabled = true,
                            shape = RoundedCornerShape(50.dp) // Chip shape
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
                .verticalScroll(rememberScrollState())
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