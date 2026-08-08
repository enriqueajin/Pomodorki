package com.enriqueajin.pomidorki.presentation.navigation

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.enriqueajin.pomidorki.presentation.home.TimerScreenRoot
import com.enriqueajin.pomidorki.presentation.home.TimerScreenViewModel
import com.enriqueajin.pomidorki.presentation.pomodorosettings.PomodoroSettingsScreenRoot
import com.enriqueajin.pomidorki.presentation.stats.StatsScreen
import com.enriqueajin.pomidorki.presentation.tasks.TasksScreenRoot
import com.enriqueajin.pomidorki.presentation.ui.theme.longBreakBackground
import com.enriqueajin.pomidorki.presentation.ui.theme.pinkPrimary
import com.enriqueajin.pomidorki.presentation.ui.theme.shortBreakBackground
import com.enriqueajin.pomidorki.presentation.ui.theme.shortBreakPickerIndicator
import com.enriqueajin.pomidorki.utils.Constants
import com.enriqueajin.pomidorki.utils.Constants.screensWithBottomBar

private const val NAVIGATION_TRANSITION_DURATION_MILLIS = 300

@Composable
fun MainGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val activity = LocalContext.current as ComponentActivity
    val timerScreenViewModel: TimerScreenViewModel = hiltViewModel(viewModelStoreOwner = activity)
    val timerUiState by timerScreenViewModel.uiState.collectAsStateWithLifecycle()

    val selectedItem =
        when (currentRoute) {
            Route.Timer.route -> 0
            Route.Tasks.route -> 1
            Route.Stats.route -> 2
            else -> 0
        }

    val isBottomBarVisible =
        remember(navBackStackEntry) {
            navBackStackEntry?.destination?.route in screensWithBottomBar
        }

    val targetStatusBarColor =
        when (currentRoute) {
            Route.Timer.route ->
                when (timerUiState.selectedTimer) {
                    0 -> pinkPrimary
                    1 -> shortBreakBackground
                    2 -> longBreakBackground
                    else -> pinkPrimary
                }
            Route.Settings.route -> shortBreakPickerIndicator
            else -> MaterialTheme.colorScheme.background
        }
    val statusBarColor by
        animateColorAsState(
            targetValue = targetStatusBarColor,
            animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
            label = "statusBarColor",
        )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                statusBarColor.luminance() > 0.5f
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            StatusBarSpacer(color = statusBarColor)
        },
        bottomBar = {
            AnimatedVisibility(isBottomBarVisible) {
                BottomNavigation(
                    items = Constants.getNavigationItems(),
                    selectedItem = selectedItem,
                    onItemClick = { route ->
                        navigateToTab(navController, route)
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Timer.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS))
            },
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

private fun navigateToDetail(
    navController: NavController,
    routeBuilder: () -> String,
) {
    navController.navigate(routeBuilder()) {
        launchSingleTop = true
        restoreState = true
    }
}

private fun navigateToTab(
    navController: NavController,
    route: String,
) {
    if (navController.currentDestination?.route == route) {
        return
    }
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
