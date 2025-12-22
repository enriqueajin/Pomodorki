package com.enriqueajin.pomidorki.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.enriqueajin.pomidorki.presentation.tasks.TasksScreenRoot
import com.enriqueajin.pomidorki.utils.Constants
import com.enriqueajin.pomidorki.utils.Constants.screensWithBottomBar
import com.enriqueajin.pomidorki.utils.Constants.screensWithTopBar

@Composable
fun MainGraph() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var selectedItem by remember { mutableIntStateOf(0) }
    val currentRoute = navBackStackEntry?.destination?.route

    selectedItem = when (currentRoute) {
        Route.Timer.route -> 0
        Route.Tasks.route -> 1
        Route.Stats.route -> 2
        else -> 0
    }

    val isBottomBarVisible = remember(navBackStackEntry) {
        navBackStackEntry?.destination?.route in screensWithBottomBar
    }

    Scaffold(
        topBar = {
            if(currentRoute in screensWithTopBar) {
                EmptyTopBar()
            }
        },
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
            startDestination = Route.Timer.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Route.Timer.route) {
                TimerScreenRoot {
                    navigateToDetail(navController) {
                        Route.Settings.route
                    }
                }
            }
            composable(route = Route.Tasks.route) {
                TasksScreenRoot()
            }
            composable(route = Route.Stats.route) {
                StatsScreen()
            }
            composable(route = Route.Settings.route) {
                PomodoroSettingsScreenRoot()
            }
        }
    }
}

private fun navigateToDetail(navController: NavController, routeBuilder: () -> String) {
    navController.navigate(routeBuilder()) {
        launchSingleTop = true
        restoreState = true
    }
}

private fun navigateToTab(navController: NavController, route: String) {
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