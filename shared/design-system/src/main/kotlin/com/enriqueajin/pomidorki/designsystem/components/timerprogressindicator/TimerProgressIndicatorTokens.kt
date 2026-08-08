package com.enriqueajin.pomidorki.designsystem.components.timerprogressindicator

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal object TimerProgressIndicatorTokens {
    val IndicatorSize = 300.dp
    val StrokeWidth = 20.dp

    @Composable
    fun indicatorColor(): Color = MaterialTheme.colorScheme.primary

    @Composable
    fun containerColor(): Color = MaterialTheme.colorScheme.background

    @Composable
    fun trackColor(): Color = MaterialTheme.colorScheme.surfaceVariant

    @Composable
    fun textColor(): Color = MaterialTheme.colorScheme.onSurface
}
