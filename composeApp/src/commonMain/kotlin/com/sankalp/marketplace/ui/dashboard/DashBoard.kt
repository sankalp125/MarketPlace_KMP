package com.sankalp.marketplace.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sankalp.marketplace.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashBoardRoot(
    onNavigateToProductDetails : (prodId : String) -> Unit,
    onNavigateToEditProduct : (prodId : String) -> Unit,
    onaNavigateToLogin : () -> Unit
){
    val viewModel = koinViewModel<DashBoardVm>()
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit){
        viewModel.effect.collect { effect ->

        }
    }

    AdaptiveNavigation(
        selectedItem   = state.selectedNavItem,
        onItemSelected = {
            onEvent(DashBoardEvent.OnNavItemClick(it))
        }
    ) {
        // Content — selected item ke hisaab se screen dikhao
        when (state.selectedNavItem) {
            NavItem.HOME        -> HomeScreen()
            NavItem.ADD_PRODUCT -> AddProductScreen()
            NavItem.PROFILE     -> ProfileScreen()
        }
    }
}
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
}

@Composable
fun AddProductScreen(
    modifier: Modifier = Modifier
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun Preview1(){
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
fun Preview2(){
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
fun Preview3(){
    AppTheme {
        ProfileScreen(
            modifier = Modifier.fillMaxSize()
                .safeContentPadding()
                .padding(horizontal = 16.dp)
        )
    }
}