package com.sankalp.marketplace.ui.edit_product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.sankalp.marketplace.data.models.ProductDetailsResponse
import com.sankalp.marketplace.ui.component.DialogHost
import com.sankalp.marketplace.ui.component.ImagePickerDialog
import com.sankalp.marketplace.ui.component.ImagePickerSheet
import com.sankalp.marketplace.ui.component.UniversalBottomSheet
import com.sankalp.marketplace.ui.theme.AppTheme
import com.sankalp.marketplace.utils.WindowSize
import com.sankalp.marketplace.utils.formatMillisToDate
import com.sankalp.marketplace.utils.getWindowSize
import com.sankalp.marketplace.utils.rememberBottomSheetManager
import com.sankalp.marketplace.utils.rememberDialogManager
import com.sankalp.marketplace.utils.rememberImagePicker
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Plus
import compose.icons.feathericons.X
import org.koin.compose.viewmodel.koinViewModel
import com.sankalp.marketplace.utils.SessionManager

@Composable
fun EditProductScreen(
    productId: String,
    onNavigateBack: () -> Unit
) {
    val sessionScope = SessionManager.getOrCreateSessionScope()
    val viewModel = koinViewModel<EditProductVm>(scope = sessionScope)
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    val snackbarHostState = remember { SnackbarHostState() }
    val windowSize = getWindowSize()
    val isDesktop = windowSize != WindowSize.Compact
    
    val imagePicker = rememberImagePicker()

    LaunchedEffect(productId) {
        viewModel.init(productId)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                EditProductEffect.NavigateBack -> onNavigateBack()
                is EditProductEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                is EditProductEffect.OpenImagePicker -> {
                    imagePicker.pickImage(effect.source) { path ->
                        path?.let { viewModel.onImagesPicked(listOf(it)) }
                    }
                }
            }
        }
    }

    EditProductContent(
        state = state,
        onEvent = onEvent,
        isDesktop = isDesktop,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductContent(
    state: EditProductState,
    onEvent: (EditProductEvent) -> Unit,
    isDesktop: Boolean,
    snackbarHostState: SnackbarHostState
) {
    val bottomSheetManager = rememberBottomSheetManager()
    val dialogManager = rememberDialogManager()

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onEvent(EditProductEvent.OnTillDateChange(formatMillisToDate(it)))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Product") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(EditProductEvent.OnBackClick) }) {
                        Icon(FeatherIcons.ArrowLeft, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier.then(if (isDesktop) Modifier.widthIn(max = 600.dp) else Modifier.fillMaxWidth()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = state.price,
                            onValueChange = { onEvent(EditProductEvent.OnPriceChange(it)) },
                            label = { Text("Price") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = state.tillDate,
                            onValueChange = {},
                            label = { Text("Till Date") },
                            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                            readOnly = true,
                            enabled = false,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Product Active Status")
                            Switch(
                                checked = state.status,
                                onCheckedChange = { onEvent(EditProductEvent.OnStatusChange(it)) }
                            )
                        }

                        Button(
                            onClick = { onEvent(EditProductEvent.OnUpdateClick) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !state.isUpdating
                        ) {
                            if (state.isUpdating) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            else Text("Update Basic Info")
                        }

                        HorizontalDivider()

                        Text("Manage Pictures", style = MaterialTheme.typography.titleMedium)

                        // Add Pictures Button
                        Button(
                            onClick = {
                                if (isDesktop) {
                                    dialogManager.show {
                                        ImagePickerDialog(onSelect = {
                                            dialogManager.hide()
                                            onEvent(EditProductEvent.OnAddPicturesClick(it))
                                        })
                                    }
                                } else {
                                    bottomSheetManager.show {
                                        ImagePickerSheet(onSelect = {
                                            bottomSheetManager.hide()
                                            onEvent(EditProductEvent.OnAddPicturesClick(it))
                                        })
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.addingPictures
                        ) {
                            if (state.addingPictures) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            else {
                                Icon(FeatherIcons.Plus, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add More Pictures")
                            }
                        }

                        // Existing Pictures List
                        state.product?.otherPictures?.let { pictures ->
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(pictures) { url ->
                                    Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp))) {
                                        AsyncImage(
                                            model = url,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        if (state.deletingPicture == url) {
                                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).size(20.dp))
                                        } else {
                                            IconButton(
                                                onClick = { onEvent(EditProductEvent.OnDeletePictureClick(url)) },
                                                modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(0.5f), RoundedCornerShape(4.dp)).size(24.dp)
                                            ) {
                                                Icon(FeatherIcons.X, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            UniversalBottomSheet(manager = bottomSheetManager, onDismiss = { bottomSheetManager.hide() })
            DialogHost(manager = dialogManager, onDismiss = { dialogManager.hide() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProductScreenPreview() {
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
    val state = EditProductState(
        productId = "1",
        product = sampleProduct,
        price = "4500.0",
        tillDate = "2023-10-27",
        status = true
    )
    AppTheme {
        EditProductContent(
            state = state,
            onEvent = {},
            isDesktop = false,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
