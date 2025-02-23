package com.enriqueajin.pomidorki.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.presentation.navigation.BottomNavigationItem
import com.enriqueajin.pomidorki.presentation.navigation.Route

object Constants {

    @Composable
    fun getNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                text = "Timer",
                route = Route.Timer,
                selectedItem = ImageVector.vectorResource(R.drawable.filled_timer),
                unselectedItem = ImageVector.vectorResource(R.drawable.outlined_timer),
            ),
            BottomNavigationItem(
                text = "Tasks",
                route = Route.Tasks,
                selectedItem = ImageVector.vectorResource(R.drawable.filled_task),
                unselectedItem = ImageVector.vectorResource(R.drawable.outlined_task),
            ),
            BottomNavigationItem(
                text = "Stats",
                route = Route.Stats,
                selectedItem = ImageVector.vectorResource(R.drawable.filled_stats),
                unselectedItem = ImageVector.vectorResource(R.drawable.outlined_stats),
            ),
        )
    }

    val pomodoroTabItems = listOf("Pomodoro", "Short Break", "Long Break")

    // Countdown Timer
    const val ACTION_SERVICE_START = "ACTION_SERVICE_START"
    const val ACTION_SERVICE_PAUSE = "ACTION_SERVICE_PAUSE"
    const val ACTION_SERVICE_RESET = "ACTION_SERVICE_RESET"
    const val ACTION_SERVICE_CLOSE = "ACTION_SERVICE_CLOSE"
    const val ACTION_SERVICE_IDLE = "ACTION_SERVICE_IDLE"

    const val COUNTDOWN_STATE = "COUNTDOWN_STATE"
    const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    const val NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME"
    const val NOTIFICATION_ID = 10

    // PendingIntent request codes
    const val CLICK_REQUEST_CODE = 100
    const val PAUSE_REQUEST_CODE = 101
    const val RESUME_REQUEST_CODE = 102
    const val CANCEL_REQUEST_CODE = 103
    const val START_REQUEST_CODE = 107
    const val RESET_REQUEST_CODE = 106
    const val IDLE_REQUEST_CODE = 110

    // Notification action buttons' title
    const val START_BUTTON_TITLE = "START"
    const val PAUSE_BUTTON_TITLE = "PAUSE"
    const val RESET_BUTTON_TITLE = "RESET"
    const val CLOSE_BUTTON_TITLE = "CLOSE"
    const val RESUME_BUTTON_TITLE = "RESUME"

}
