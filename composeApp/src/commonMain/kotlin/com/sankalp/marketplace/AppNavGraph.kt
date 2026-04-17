package com.sankalp.marketplace

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sankalp.marketplace.ui.login.LoginRoot
import com.sankalp.marketplace.ui.on_board.OnBoardRoot
import com.sankalp.marketplace.ui.register.RegisterRoot
import com.sankalp.marketplace.ui.splash.SplashRoot


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
                onNavigateToDashBoard = {},
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
                onNavigateToDashBoard = {},
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route){
                        popUpTo(Screen.Login.route) { inclusive = false }
                    }
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
    }
}