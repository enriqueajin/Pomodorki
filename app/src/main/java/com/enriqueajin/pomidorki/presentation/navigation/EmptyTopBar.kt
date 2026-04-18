package com.enriqueajin.pomidorki.presentation.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.enriqueajin.pomidorki.presentation.ui.theme.pinkPrimary

/*
Empty top bar with a background color to apply the insets to the system's top bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyTopBar(modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = Modifier.height(0.dp),
        title = { Text(text = "") },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = pinkPrimary,
            ),
    )
}
