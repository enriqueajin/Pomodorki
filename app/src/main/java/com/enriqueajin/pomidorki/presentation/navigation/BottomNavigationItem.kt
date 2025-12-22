package com.enriqueajin.pomidorki.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val text: String,
    val route: String,
    val selectedItem: ImageVector,
    val unselectedItem: ImageVector,
)