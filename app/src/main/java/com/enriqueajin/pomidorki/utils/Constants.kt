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
}