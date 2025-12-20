package com.phoenix.booklet.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.phoenix.booklet.screen.home.HomeRoute
import com.phoenix.booklet.screen.settings.SettingsRoute
import kotlinx.serialization.Serializable

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = NavDestinations.Home) {
        composable<NavDestinations.Home> {
            HomeRoute(
                navigateToSettings = { navController.navigate(NavDestinations.Settings) }
            )
        }
        composable<NavDestinations.Settings> {
            SettingsRoute(
                navigateBack = { navController.popBackStack() },
                navigateFreshHome = {
                    navController.navigate(NavDestinations.Home) {
                        popUpTo(NavDestinations.Home) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

sealed interface NavDestinations {
    @Serializable
    data object Home: NavDestinations
    @Serializable
    data object Settings: NavDestinations
}