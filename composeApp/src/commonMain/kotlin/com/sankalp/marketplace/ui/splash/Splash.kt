package com.sankalp.marketplace.ui.splash

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import marketplaceapp.composeapp.generated.resources.Res
import marketplaceapp.composeapp.generated.resources.jmp_logo
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashRoot(
    onNavigateToDashBoard : () -> Unit,
    onNavigateToOnBoard : () -> Unit
) {
    val viewModel = koinViewModel<SplashVm>()
    LaunchedEffect(Unit){
        viewModel.effect.collect { effect ->
            when(effect){
                is SplashEffect.NavigateToDashboard -> onNavigateToDashBoard()
                is SplashEffect.NavigateToOnBoard -> onNavigateToOnBoard()
            }
        }
    }
    SplashScreen(
        modifier = Modifier.fillMaxSize()
    )
}
@Composable
private fun SplashScreen(
    modifier: Modifier = Modifier
){
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 5000,
            easing = LinearOutSlowInEasing
        )
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Image(
            painter = painterResource(Res.drawable.jmp_logo),
            contentDescription = "App logo",
            modifier = Modifier.size(200.dp)
                .alpha(alpha)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    MaterialTheme{
        SplashScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}