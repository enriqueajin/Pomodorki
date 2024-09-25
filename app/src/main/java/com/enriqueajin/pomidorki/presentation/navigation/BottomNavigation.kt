package com.enriqueajin.pomidorki.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomNavigation(
    items: List<BottomNavigationItem>,
    selectedItem: Int,
    onItemClick: (Route) -> Unit
) {
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedItem,
                onClick = { onItemClick(item.route) },
                icon = {
                    val icon = if (index == selectedItem) item.selectedItem else item.unselectedItem
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                },
                label = { Text(text = item.text) }
            )
        }
    }
}