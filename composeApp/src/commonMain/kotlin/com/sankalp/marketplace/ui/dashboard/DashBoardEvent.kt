package com.sankalp.marketplace.ui.dashboard

sealed interface DashBoardEvent {
    data class OnNavItemClick(val navItem : NavItem) : DashBoardEvent
}