package com.enriqueajin.pomidorki.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TimerScreen() {
    Box(contentAlignment = Alignment.Center) {
        Text(text = "Timer Screen")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TimerScreenPreview() {
    TimerScreen()
}