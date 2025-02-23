package com.enriqueajin.pomidorki.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.enriqueajin.pomidorki.presentation.ui.theme.pinkPrimary
import com.enriqueajin.pomidorki.presentation.ui.theme.pinkSecondary

@Composable
fun PomodoroCountdown(
    modifier: Modifier,
    initialValue: Int,
    timeLeftMillis: Long,
    arcColor: Color,
    timeElapsedArcColor: Color,
    minValue: Int = 0,
    maxValue: Int = 100,
    circleRadius: Float,
    backgroundColor: Color,
    onPositionChange: (Int) -> Unit,
) {
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    val maxTime by remember { mutableLongStateOf(26 * 60 * 1000) }
    var percentage by remember { mutableFloatStateOf(0f) }

    val cuPercentage = animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 0
        ), label = ""
    )

    LaunchedEffect(timeLeftMillis) {
         percentage = calculatePercentage(timeLeftMillis, maxTime)
    }

    Box(
        modifier = modifier,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            val circleThickness = width / 10f
            circleCenter = Offset(x = width / 2f, y = height / 2f)

            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor,
                    )
                ),
                radius = circleRadius,
                center = circleCenter,
            )
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor,
                    )
                ),
                radius = circleRadius + 70f,
                center = circleCenter,
            )
            drawCircle(
                style = Stroke(
                    width = circleThickness
                ),
                color = timeElapsedArcColor,
                radius = circleRadius,
                center = circleCenter,
            )
            drawArc(
                color = arcColor,
                startAngle = -90f,
                sweepAngle = (360f / maxValue) * cuPercentage.value,
                style = Stroke(
                    width = circleThickness,
                    cap = StrokeCap.Round
                ),
                useCenter = false,
                size = Size(
                    width = circleRadius * 2f,
                    height = circleRadius * 2f
                ),
                topLeft = Offset(
                    x = (width - circleRadius * 2f) / 2f,
                    y = (width - circleRadius * 2f) / 2f,
                )
            )
        }
    }
}

fun calculatePercentage(currentTime: Long, maxTime: Long): Float {
    return ((currentTime.toFloat() / maxTime.toFloat()) * 100)
}

@Preview(showBackground = true)
@Composable
fun PomodoroCountdownPreview() {
    PomodoroCountdown(
        modifier = Modifier
            .size(300.dp)
            .background(pinkPrimary),
        initialValue = 67,
        timeLeftMillis = 0L,
        arcColor = pinkSecondary,
        timeElapsedArcColor = Color.LightGray,
        circleRadius = 340f,
        backgroundColor = MaterialTheme.colorScheme.background,
        onPositionChange = {}
    )
}