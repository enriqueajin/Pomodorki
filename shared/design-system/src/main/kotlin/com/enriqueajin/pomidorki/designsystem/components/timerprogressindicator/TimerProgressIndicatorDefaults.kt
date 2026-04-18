package com.enriqueajin.pomidorki.designsystem.components.timerprogressindicator

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

object TimerProgressIndicatorDefaults {
    val Size: Dp = TimerProgressIndicatorTokens.IndicatorSize
    val StrokeWidth: Dp = TimerProgressIndicatorTokens.StrokeWidth
    val ProgressAnimationSpec: AnimationSpec<Float> =
        tween(durationMillis = TimerProgressIndicatorTokens.ANIMATION_DURATION_MILLIS)

    @Composable
    fun indicatorColor(): Color = TimerProgressIndicatorTokens.indicatorColor()

    @Composable
    fun containerColor(): Color = TimerProgressIndicatorTokens.containerColor()

    @Composable
    fun trackColor(): Color = TimerProgressIndicatorTokens.trackColor()

    @Composable
    fun textColor(): Color = TimerProgressIndicatorTokens.textColor()

    @Composable
    fun textStyle(): TextStyle = MaterialTheme.typography.displayLarge
}
