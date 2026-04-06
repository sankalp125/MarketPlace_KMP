package com.sankalp.marketplace

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sankalp.marketplace.ui.on_board.OnBoardRoot
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
                onNavigateToLogin = {}
            )
        }
        composable(Screen.Login.route){
            // design login here
        }
        composable(Screen.Register.route){
            // design register here
        }
    }
}