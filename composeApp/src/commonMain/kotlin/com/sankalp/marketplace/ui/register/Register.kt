package com.sankalp.marketplace.ui.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.sankalp.marketplace.ui.component.DialogHost
import com.sankalp.marketplace.ui.component.DropdownField
import com.sankalp.marketplace.ui.component.ImagePickerDialog
import com.sankalp.marketplace.ui.component.ImagePickerSheet
import com.sankalp.marketplace.ui.component.UniversalBottomSheet
import com.sankalp.marketplace.ui.theme.AppTheme
import com.sankalp.marketplace.utils.WindowSize
import com.sankalp.marketplace.utils.getWindowSize
import com.sankalp.marketplace.utils.rememberBottomSheetManager
import com.sankalp.marketplace.utils.rememberDialogManager
import com.sankalp.marketplace.utils.rememberImagePicker
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowRight
import compose.icons.feathericons.Camera
import compose.icons.feathericons.User
import marketplaceapp.composeapp.generated.resources.Res
import marketplaceapp.composeapp.generated.resources.ic_visibility
import marketplaceapp.composeapp.generated.resources.ic_visibility_off
import marketplaceapp.composeapp.generated.resources.jmp_logo
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterRoot(
    onNavigateToLogin: () -> Unit
){
    val viewModel = koinViewModel<RegisterVm>()
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomSheetManager = rememberBottomSheetManager()
    val dialogManager = rememberDialogManager()
    val imagePicker = rememberImagePicker()
    val windowSize = getWindowSize()

    // Check weather it is desktop or not
    val isDesktop : Boolean = when(windowSize){
        WindowSize.Compact -> false
        WindowSize.Medium -> false
        WindowSize.Expanded -> true
    }

    LaunchedEffect(Unit){
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterEffect.ShowMessage -> {
                    snackBarHostState.showSnackbar(effect.message)
                }
                is RegisterEffect.NavigateToLogin -> onNavigateToLogin()
                is RegisterEffect.OpenBottomSheet -> {
                    if (isDesktop){
                        dialogManager.show {
                            ImagePickerDialog(
                                onSelect = { source ->
                                    dialogManager.hide()
                                    onEvent(RegisterEvent.OpenImagePicker(source))
                                }
                            )
                        }
                    }else {
                        bottomSheetManager.show {
                            ImagePickerSheet(
                                onSelect = { source ->
                                    bottomSheetManager.hide()
                                    onEvent(RegisterEvent.OpenImagePicker(source))
                                }
                            )
                        }
                    }
                }
                is RegisterEffect.OpenImagePicker -> {
                    imagePicker.pickImage(effect.source) { path ->
                        path?.let { path ->
                            onEvent(RegisterEvent.OnImageSelected(path))
                        }
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        RegisterScreen(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onEvent = onEvent,
            windowSize,
            snackBarHostState = snackBarHostState,
            keyboardController = keyboardController
        )
        UniversalBottomSheet(
            manager = bottomSheetManager,
            onDismiss = {
                bottomSheetManager.hide()
            }
        )
        DialogHost(
            dialogManager,
            onDismiss = {
                dialogManager.hide()
            }
        )
    }
}

@Composable
private fun RegisterScreen(
    modifier: Modifier = Modifier,
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit,
    windowSize: WindowSize,
    snackBarHostState: SnackbarHostState,
    keyboardController: SoftwareKeyboardController?
){
    // Card width responsive
    val cardWidth: Dp = when (windowSize) {
        WindowSize.Compact -> 360.dp   // Mobile
        WindowSize.Medium -> 460.dp   // Tablet
        WindowSize.Expanded -> 520.dp   // Desktop
    }

    // Logo size responsive
    val logoSize: Dp = when (windowSize) {
        WindowSize.Compact -> 100.dp
        WindowSize.Medium -> 120.dp
        WindowSize.Expanded -> 150.dp
    }
    Box(modifier = modifier) {

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .safeContentPadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── Logo ──────────────────────────────────────
            Image(
                painter = painterResource(Res.drawable.jmp_logo),
                contentDescription = "App Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(logoSize)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ─── Title ─────────────────────────────────────
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Let's get you started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))
            // ─── Card ──────────────────────────────────────
            Box(
                modifier = Modifier.width(cardWidth)
                    .clip(RoundedCornerShape(28.dp))
            ) {
                // Background blur layer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                )
                            )
                        )
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 👤 Profile Image
                        Box(contentAlignment = Alignment.BottomEnd) {

                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.selectedImage != null){
                                    Image(
                                        painter = rememberAsyncImagePainter(state.selectedImage),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }else{
                                    Icon(
                                        imageVector = FeatherIcons.User,
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onEvent(RegisterEvent.OnPickImageClick) },
                                modifier = Modifier
                                    .size(55.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = FeatherIcons.Camera,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { onEvent(RegisterEvent.OnUserNameChange(it)) },
                            label = { Text("Name") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.email,
                            onValueChange = { onEvent(RegisterEvent.OnEmailChange(it)) },
                            label = { Text("Email") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { onEvent(RegisterEvent.OnPasswordChange(it)) },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = if (state.isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { onEvent(RegisterEvent.OnPasswordVisibilityToggle) }
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            if (state.isPasswordVisible) Res.drawable.ic_visibility
                                            else Res.drawable.ic_visibility_off
                                        ),
                                        contentDescription = "Password Toggle"
                                    )
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.confirmPassword,
                            onValueChange = { onEvent(RegisterEvent.OnConfirmPasswordChange(it)) },
                            label = { Text("Confirm Password") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.mobileNumber,
                            onValueChange = { onEvent(RegisterEvent.OnMobileNumberChange(it)) },
                            label = { Text("Mobile Number") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownField(
                            label = "Country",
                            items = state.countries,
                            selectedItem = state.selectedCountry,
                            itemLabel = { it.countryName },
                            onSelect = {
                                onEvent(RegisterEvent.OnCountryChange(it))
                            },
                            loading = state.loadingCountries,
                        )
                        DropdownField(
                            label = "State",
                            items = state.states,
                            selectedItem = state.selectedState,
                            itemLabel = { it.stateName },
                            onSelect = {
                                onEvent(RegisterEvent.OnStateChange(it))
                            },
                            loading = state.loadingStates,
                        )
                        DropdownField(
                            label = "City",
                            items = state.cities,
                            selectedItem = state.selectedCity,
                            itemLabel = { it.cityName },
                            onSelect = {
                                onEvent(RegisterEvent.OnCityChange(it))
                            },
                            loading = state.loadingCities,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                onEvent(RegisterEvent.OnRegisterClick)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = !state.isRegistering,
                            shape = MaterialTheme.shapes.medium
                        ){
                            Row(modifier = Modifier.fillMaxWidth()){
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Register",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (state.isRegistering) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                    )
                                }else{
                                    Icon(
                                        imageVector = FeatherIcons.ArrowRight,
                                        contentDescription = "next icon",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        TextButton(
                            onClick = { onEvent(RegisterEvent.OnBackClick) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Already have an account? ",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Login",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
        // Snackbar
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    AppTheme {
        RegisterScreen(
            modifier = Modifier.fillMaxSize(),
            state = RegisterState(),
            onEvent = {},
            windowSize = WindowSize.Compact,
            snackBarHostState = remember { SnackbarHostState() },
            null
        )
    }
}
