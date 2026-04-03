package com.sankalp.marketplace

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


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
            // design splash here
        }
        composable(Screen.OnBoard.route){
            // design onBoard here
        }
        composable(Screen.Login.route){
            // design login here
        }
        composable(Screen.Register.route){
            // design register here
        }
    }
}