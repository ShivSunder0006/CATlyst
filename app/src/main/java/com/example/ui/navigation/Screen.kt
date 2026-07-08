package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Stats : Screen("stats")
}
