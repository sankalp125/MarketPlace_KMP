package com.sankalp.marketplace

sealed class Screen(val route : String){
    object Splash : Screen("splash")
    object OnBoard : Screen("on_board")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ProductDetail : Screen("product_detail")
    object EditProduct : Screen("edit_product")
}