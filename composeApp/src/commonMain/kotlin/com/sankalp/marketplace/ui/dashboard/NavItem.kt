package com.sankalp.marketplace.ui.dashboard

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons
import compose.icons.feathericons.Home
import compose.icons.feathericons.PlusCircle
import compose.icons.feathericons.User

enum class NavItem(
    val label: String,
    val icon : ImageVector
) {
    HOME(
        label = "Home",
        icon  = FeatherIcons.Home
    ),
    ADD_PRODUCT(
        label = "Add Product",
        icon  = FeatherIcons.PlusCircle
    ),
    PROFILE(
        label = "Profile",
        icon  = FeatherIcons.User
    )
}