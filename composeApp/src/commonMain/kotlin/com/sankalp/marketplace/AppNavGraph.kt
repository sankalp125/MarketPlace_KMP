package com.sankalp.marketplace

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sankalp.marketplace.ui.dashboard.DashBoardRoot
import com.sankalp.marketplace.ui.details.ProductDetailsScreen
import com.sankalp.marketplace.ui.edit_product.EditProductScreen
import com.sankalp.marketplace.ui.login.LoginRoot
import com.sankalp.marketplace.ui.on_board.OnBoardRoot
import com.sankalp.marketplace.ui.register.RegisterRoot
import com.sankalp.marketplace.ui.splash.SplashRoot
import androidx.savedstate.read


@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination : String = Screen.Splash.route
){
    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        composable(Screen.Splash.route){
            // splash screen
            SplashRoot(
                onNavigateToDashBoard = {navController.navigate(Screen.Home.route){
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }},
                onNavigateToOnBoard = {
                    navController.navigate(Screen.OnBoard.route){
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.OnBoard.route){
            OnBoardRoot(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route){
                        popUpTo(Screen.OnBoard.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route){
            LoginRoot(
                onNavigateToDashBoard = {
                    navController.navigate(Screen.Home.route){
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route){
            RegisterRoot(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Home.route){
            DashBoardRoot(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route){
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToProductDetails = { prodId, isMyProduct ->
                    navController.navigate(Screen.ProductDetail.createRoute(prodId, isMyProduct))
                },
                onNavigateToEditProduct = { prodId ->
                    navController.navigate(Screen.EditProduct.createRoute(prodId))
                }
            )
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("prodId") { type = NavType.StringType },
                navArgument("isMyProduct") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val prodId = backStackEntry.arguments?.read { getString("prodId") } ?: ""
            val isMyProduct = backStackEntry.arguments?.read { getString("isMyProduct") }?.toBoolean() ?: false
            ProductDetailsScreen(
                productId = prodId,
                isMyProduct = isMyProduct,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.EditProduct.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(navArgument("prodId") { type = NavType.StringType })
        ) { backStackEntry ->
            val prodId = backStackEntry.arguments?.read { getString("prodId") } ?: ""
            EditProductScreen(
                productId = prodId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}