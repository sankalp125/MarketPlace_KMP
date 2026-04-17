package com.sankalp.marketplace.ui.login

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import com.sankalp.marketplace.ui.component.UniversalBottomSheet
import com.sankalp.marketplace.ui.theme.AppTheme
import com.sankalp.marketplace.utils.WindowSize
import com.sankalp.marketplace.utils.getWindowSize
import com.sankalp.marketplace.utils.rememberBottomSheetManager
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowRight
import marketplaceapp.composeapp.generated.resources.Res
import marketplaceapp.composeapp.generated.resources.ic_visibility
import marketplaceapp.composeapp.generated.resources.ic_visibility_off
import marketplaceapp.composeapp.generated.resources.jmp_logo
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginRoot(
    onNavigateToDashBoard: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val viewModel = koinViewModel<LoginVm>()
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomSheetManager = rememberBottomSheetManager()
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.ShowMessage -> {
                    snackBarHostState.showSnackbar(effect.message)
                }
                is LoginEffect.NavigateToHome -> onNavigateToDashBoard()
                is LoginEffect.NavigateToRegister -> onNavigateToRegister()
                is LoginEffect.ShowBottomSheet -> {
                    bottomSheetManager.show {
                        when (effect.type) {
                            BottomSheetType.EMAIL -> ForgotPasswordEmail(
                                onEvent = onEvent,
                                state = state
                            )

                            BottomSheetType.OTP -> ForgotPasswordOTP(
                                onEvent = onEvent,
                                state = state
                            )
                            BottomSheetType.PASSWORD -> ForgotPasswordPassword(
                                onEvent = onEvent,
                                state = state
                            )
                            BottomSheetType.NONE -> {
                                bottomSheetManager.hide()
                            }
                        }
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LoginScreen(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onEvent = onEvent,
            snackBarHostState = snackBarHostState,
            keyboardController = keyboardController
        )
        UniversalBottomSheet(
            manager = bottomSheetManager,
            onDismiss = {
                bottomSheetManager.hide()
                onEvent(LoginEvent.OnForgotPasswordFlowCancel)
            }
        )
    }
}

@Composable
private fun LoginScreen(
    modifier: Modifier = Modifier,
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    snackBarHostState: SnackbarHostState,
    keyboardController: SoftwareKeyboardController?
) {
    val windowSize = getWindowSize()

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

        // Snackbar
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .safeContentPadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Login to continue",
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        // Email
                        OutlinedTextField(
                            value = state.userName,
                            onValueChange = { onEvent(LoginEvent.OnUserNameChange(it)) },
                            label = { Text("Email") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Password
                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { onEvent(LoginEvent.OnPasswordChange(it)) },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = if (state.isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { onEvent(LoginEvent.OnPasswordVisibilityToggle) }
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
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    onEvent(LoginEvent.OnLoginClick)
                                }
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Forgot Password
                        TextButton(
                            onClick = {
                                onEvent(LoginEvent.OnForgotPasswordClick)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Forgot Password?",
                                    color = Color.Red
                                )
                            }
                        }

                        // Login Button
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                onEvent(LoginEvent.OnLoginClick)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = !state.loggingIn,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()){
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Login",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (state.loggingIn) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = FeatherIcons.ArrowRight,
                                        contentDescription = "next icon",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // Register
                        TextButton(
                            onClick = { onEvent(LoginEvent.OnRegisterClick) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Don't have and account? ",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Register here",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ForgotPasswordEmail(
    onEvent: (LoginEvent) -> Unit,
    state: LoginState
) {
    OutlinedTextField(
        value = state.forgotPasswordEmail,
        onValueChange = { onEvent(LoginEvent.OnForgotPasswordEmailChange(it)) },
        label = { Text("Email") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(10.dp))
    Button(
        onClick = { onEvent(LoginEvent.OnSubmitEmail) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Submit")
            Spacer(modifier = Modifier.weight(1f))
            if (state.sendingOtp) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = FeatherIcons.ArrowRight,
                    contentDescription = "next icon",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ForgotPasswordOTP(
    onEvent: (LoginEvent) -> Unit,
    state: LoginState
) {
    OutlinedTextField(
        value = state.forgotPasswordOTP,
        onValueChange = { onEvent(LoginEvent.OnForgotPasswordOTPChange(it)) },
        label = { Text("OTP") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(10.dp))
    Button(
        onClick = { onEvent(LoginEvent.OnSubmitOTP) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Submit")
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = FeatherIcons.ArrowRight,
            contentDescription = "next icon",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ForgotPasswordPassword(
    onEvent: (LoginEvent) -> Unit,
    state: LoginState
) {
    OutlinedTextField(
        value = state.forgotPasswordPassword,
        onValueChange = { onEvent(LoginEvent.OnForgotPasswordPasswordChange(it)) },
        label = { Text("New Password") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(10.dp))
    OutlinedTextField(
        value = state.forgotPasswordConfirmPassword,
        onValueChange = { onEvent(LoginEvent.OnForgotPasswordConfirmPasswordChange(it)) },
        label = { Text("New Password") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(10.dp))
    Button(
        onClick = { onEvent(LoginEvent.OnSubmitPassword) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Submit")
            Spacer(modifier = Modifier.weight(1f))
            if (state.changingPassword) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = FeatherIcons.ArrowRight,
                    contentDescription = "next icon",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        LoginScreen(
            modifier = Modifier.fillMaxSize(),
            state = LoginState(),
            onEvent = {},
            snackBarHostState = remember { SnackbarHostState() },
            null
        )
    }
}