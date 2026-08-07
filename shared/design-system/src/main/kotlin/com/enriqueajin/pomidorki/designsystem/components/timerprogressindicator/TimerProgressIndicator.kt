package com.enriqueajin.pomidorki.designsystem.components.timerprogressindicator

import android.os.SystemClock
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
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
import kotlinx.coroutines.isActive

@Composable
fun TimerProgressIndicator(
    totalMillis: Long,
    remainingMillis: Long,
    endsAtElapsedRealtime: Long,
    timerText: String,
    modifier: Modifier = Modifier,
    indicatorColor: Color = TimerProgressIndicatorDefaults.indicatorColor(),
    containerColor: Color = TimerProgressIndicatorDefaults.containerColor(),
    trackColor: Color = TimerProgressIndicatorDefaults.trackColor(),
    textColor: Color = TimerProgressIndicatorDefaults.textColor(),
    strokeWidth: Dp = TimerProgressIndicatorDefaults.StrokeWidth,
    textStyle: TextStyle = TimerProgressIndicatorDefaults.textStyle(),
) {
    val progress =
        rememberTimerProgress(
            totalMillis = totalMillis,
            remainingMillis = remainingMillis,
            endsAtElapsedRealtime = endsAtElapsedRealtime,
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
            progress = { progress },
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

@Composable
private fun rememberTimerProgress(
    totalMillis: Long,
    remainingMillis: Long,
    endsAtElapsedRealtime: Long,
): Float {
    val isLive = endsAtElapsedRealtime > 0L && totalMillis > 0L
    var progress by remember {
        mutableFloatStateOf(progressRatio(remainingMillis, totalMillis))
    }

    LaunchedEffect(remainingMillis, totalMillis, isLive) {
        if (!isLive) {
            progress = progressRatio(remainingMillis, totalMillis)
        }
    }

    LaunchedEffect(endsAtElapsedRealtime, totalMillis, isLive) {
        if (!isLive) return@LaunchedEffect
        while (isActive) {
            withFrameMillis {
                val left = (endsAtElapsedRealtime - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
                progress = progressRatio(left, totalMillis)
            }
        }
    }

    return progress
}

private fun progressRatio(
    remainingMillis: Long,
    totalMillis: Long,
): Float {
    if (totalMillis <= 0L) return 0f
    return (remainingMillis.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
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
                        totalMillis = 25 * 60_000L,
                        remainingMillis = (25 * 60_000L * 0.65f).toLong(),
                        endsAtElapsedRealtime = 0L,
                        timerText = "12:34",
                    )
                }
            }
        }
    }
}
