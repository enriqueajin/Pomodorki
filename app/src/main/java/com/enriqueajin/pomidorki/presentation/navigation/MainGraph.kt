package com.enriqueajin.pomidorki.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.enriqueajin.pomidorki.presentation.home.TimerScreenRoot
import com.enriqueajin.pomidorki.presentation.pomodoro_settings.PomodoroSettingsScreenRoot
import com.enriqueajin.pomidorki.presentation.stats.StatsScreen
import com.enriqueajin.pomidorki.presentation.tasks.TasksScreen
import com.enriqueajin.pomidorki.utils.Constants

@Composable
fun MainGraph() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var selectedItem by remember { mutableStateOf(0) }

    selectedItem = when (navBackStackEntry?.destination?.route) {
        Route.Timer::class.qualifiedName -> 0
        Route.Tasks::class.qualifiedName -> 1
        Route.Stats::class.qualifiedName -> 2
        else -> 0
    }

    val isBottomBarVisible = remember(navBackStackEntry) {
        navBackStackEntry?.destination?.route == Route.Timer::class.qualifiedName ||
        navBackStackEntry?.destination?.route == Route.Tasks::class.qualifiedName ||
        navBackStackEntry?.destination?.route == Route.Stats::class.qualifiedName
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(isBottomBarVisible) {
                BottomNavigation(
                    items = Constants.getNavigationItems(),
                    selectedItem = selectedItem,
                    onItemClick = { route ->
                        navigateToTab(navController, route)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Timer,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Route.Timer> {
                TimerScreenRoot {
                    navigateToDetail(navController) {
                        Route.Settings
                    }
                }
            }
            composable<Route.Tasks> {
                TasksScreen()
            }
            composable<Route.Stats> {
                StatsScreen()
            }
            composable<Route.Settings> {
                PomodoroSettingsScreenRoot()
            }
        }
    }
}

private fun navigateToDetail(navController: NavController, routeBuilder: () -> Route) {
    navController.navigate(routeBuilder()) {
        launchSingleTop = true
        restoreState = true
    }
}

private fun navigateToTab(navController: NavController, route: Route) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { screenRoute ->
            popUpTo(screenRoute) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}