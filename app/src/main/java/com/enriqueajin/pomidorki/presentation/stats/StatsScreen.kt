package com.enriqueajin.pomidorki.presentation.stats

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StatsScreen() {
    Box(contentAlignment = Alignment.Center) {
        Text(text = "Stats Screen")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StatsScreenPreview() {
    StatsScreen()
}