package com.sankalp.marketplace.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import com.sankalp.marketplace.data.models.ProductDetailsResponse
import com.sankalp.marketplace.ui.theme.AppTheme
import com.sankalp.marketplace.utils.WindowSize
import com.sankalp.marketplace.utils.getWindowSize
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Edit
import compose.icons.feathericons.Trash2
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import com.sankalp.marketplace.utils.SessionManager
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    isMyProduct: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val sessionScope = SessionManager.getOrCreateSessionScope()
    val viewModel = koinViewModel<ProductDetailsVm>(scope = sessionScope)
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    val snackbarHostState = remember { SnackbarHostState() }
    val windowSize = getWindowSize()
    val isDesktop = windowSize != WindowSize.Compact

    LaunchedEffect(productId) {
        viewModel.init(productId, isMyProduct)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProductDetailsEffect.NavigateBack -> onNavigateBack()
                is ProductDetailsEffect.NavigateToEditProduct -> onNavigateToEdit(effect.productId)
                is ProductDetailsEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                ProductDetailsEffect.ProductDeleted -> onNavigateBack()
            }
        }
    }

    ProductDetailsContent(
        state = state,
        onEvent = onEvent,
        isDesktop = isDesktop,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsContent(
    state: ProductDetailsState,
    onEvent: (ProductDetailsEvent) -> Unit,
    isDesktop: Boolean,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(ProductDetailsEvent.OnBackClick) }) {
                        Icon(FeatherIcons.ArrowLeft, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isMyProduct) {
                        IconButton(onClick = { onEvent(ProductDetailsEvent.OnEditClick) }) {
                            Icon(FeatherIcons.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { 
                            onEvent(ProductDetailsEvent.OnDeleteConfirm(state.productId)) 
                        }) {
                            Icon(FeatherIcons.Trash2, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            state.product?.let { product ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val allPictures = remember(product) {
                        listOfNotNull(product.pictureUrl) + product.otherPictures
                    }

                    if (allPictures.isNotEmpty()) {
                        val pagerState = rememberPagerState(pageCount = { allPictures.size })
                        val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        
                        // Auto-scroll logic
                        LaunchedEffect(isDragged, isPressed) {
                            if (!isDragged && !isPressed) {
                                while (true) {
                                    delay(3000.milliseconds)
                                    val nextPage = (pagerState.currentPage + 1) % allPictures.size
                                    pagerState.animateScrollToPage(nextPage)
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (isDesktop) 400.dp else 250.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                AsyncImage(
                                    model = allPictures[page],
                                    contentDescription = product.name,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null
                                        ) {},
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Page Indicator
                            if (allPictures.size > 1) {
                                Row(
                                    Modifier
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    repeat(pagerState.pageCount) { iteration ->
                                        val color = if (pagerState.currentPage == iteration) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.outlineVariant
                                        Box(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .size(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .then(if (isDesktop) Modifier.widthIn(max = 800.dp) else Modifier.fillMaxWidth()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(product.name, style = MaterialTheme.typography.headlineMedium)
                        Text(product.category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text("₹${(product.price).toLong()}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.secondary)
                        
                        HorizontalDivider()
                        
                        Text("Description", style = MaterialTheme.typography.titleMedium)
                        Text(product.description, style = MaterialTheme.typography.bodyMedium)
                        
                        HorizontalDivider()

                        Text("Location", style = MaterialTheme.typography.titleMedium)
                        Text("${product.forCity}, ${product.forState}, ${product.forCountry}", style = MaterialTheme.typography.bodyMedium)

                        HorizontalDivider()
                        
                        Text("Publisher Info", style = MaterialTheme.typography.titleMedium)
                        Text("Name: ${product.publisher}", style = MaterialTheme.typography.bodyMedium)
                        Text("Email: ${product.publisherEmail}", style = MaterialTheme.typography.bodyMedium)
                        Text("Phone: ${product.publisherMobileNumber}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailsPreview() {
    val sampleProduct = ProductDetailsResponse(
        name = "Vintage Camera",
        category = "Electronics",
        description = "A well-maintained vintage camera from the 1970s. Perfect for collectors and photography enthusiasts.",
        price = 4500.0,
        date = "2023-10-27",
        status = true,
        forCountry = "India",
        forState = "Karnataka",
        forCity = "Bengaluru",
        pictureUrl = "https://example.com/camera.jpg",
        publisher = "John Doe",
        publisherEmail = "john.doe@example.com",
        publisherMobileNumber = "+91 9876543210",
        otherPictures = listOf(
            "https://example.com/camera1.jpg",
            "https://example.com/camera2.jpg"
        )
    )
    val state = ProductDetailsState(
        productId = "1",
        isMyProduct = true,
        product = sampleProduct
    )
    AppTheme {
        ProductDetailsContent(
            state = state,
            onEvent = {},
            isDesktop = false,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
