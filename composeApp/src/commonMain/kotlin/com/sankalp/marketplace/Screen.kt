package com.sankalp.marketplace

sealed class Screen(val route : String){
    data object Splash : Screen("splash")
    data object OnBoard : Screen("on_board")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object ProductDetail : Screen("product_detail/{prodId}/{isMyProduct}") {
        fun createRoute(prodId: String, isMyProduct: Boolean) = "product_detail/$prodId/$isMyProduct"
    }
    data object EditProduct : Screen("edit_product/{prodId}") {
        fun createRoute(prodId: String) = "edit_product/$prodId"
    }
}
