package com.enriqueajin.pomidorki.designsystem.components.timerprogressindicator

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enriqueajin.pomidorki.designsystem.theme.PomidorkiTheme

@Composable
fun TimerProgressIndicator(
    progress: Float,
    timerText: String,
    modifier: Modifier = Modifier,
    indicatorColor: Color = TimerProgressIndicatorDefaults.indicatorColor(),
    containerColor: Color = TimerProgressIndicatorDefaults.containerColor(),
    trackColor: Color = TimerProgressIndicatorDefaults.trackColor(),
    textColor: Color = TimerProgressIndicatorDefaults.textColor(),
    strokeWidth: Dp = TimerProgressIndicatorDefaults.StrokeWidth,
    textStyle: TextStyle = TimerProgressIndicatorDefaults.textStyle(),
    progressAnimationSpec: AnimationSpec<Float> = TimerProgressIndicatorDefaults.ProgressAnimationSpec,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = progressAnimationSpec,
    )

    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(999.dp))
                .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        // Duplicated because strokeCap was also applied to the tracked stretch, which is unwanted
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = { 1f },
            color = trackColor,
            trackColor = Color.Transparent,
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Butt,
        )
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = { animatedProgress },
            color = indicatorColor,
            trackColor = Color.Transparent,
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round,
        )
        Text(
            text = timerText,
            fontSize = 75.sp,
            color = textColor,
            style = textStyle,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerProgressIndicatorPreview() {
    Column {
        arrayOf(false, true).forEach {
            PomidorkiTheme(it) {
                Surface {
                    TimerProgressIndicator(
                        modifier =
                            Modifier
                                .padding(24.dp)
                                .size(TimerProgressIndicatorDefaults.Size),
                        progress = 0.65f,
                        timerText = "12:34",
                    )
                }
            }
        }
    }
}
