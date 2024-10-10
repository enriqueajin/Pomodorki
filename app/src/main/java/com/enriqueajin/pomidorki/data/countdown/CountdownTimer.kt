package com.enriqueajin.pomidorki.data.countdown

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class CountdownTimer(private val totalTimeMillis: Long) {

    var formattedTime by mutableStateOf(formatTime(totalTimeMillis))

    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var isActive = false

    private var remainingTimeMillis = totalTimeMillis
    private var lastTimestamp = 0L

    fun start() {
        if (isActive) return

        coroutineScope.launch {
            lastTimestamp = System.currentTimeMillis()
            this@CountdownTimer.isActive = true
            while (this@CountdownTimer.isActive && remainingTimeMillis > 0) {
                delay(10L)
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - lastTimestamp
                lastTimestamp = currentTime

                remainingTimeMillis -= elapsedTime
                if (remainingTimeMillis <= 0L) {
                    remainingTimeMillis = 0L
                    isActive = false // Stop the timer when it reaches zero
                }

                formattedTime = formatTime(remainingTimeMillis)
            }
        }
    }

    fun pause() {
        isActive = false
    }

    fun reset() {
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.Main)
        remainingTimeMillis = totalTimeMillis
        lastTimestamp = 0L
        formattedTime = formatTime(totalTimeMillis)
        isActive = false
    }

    private fun formatTime(timeMillis: Long): String {
        val localDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timeMillis),
            ZoneId.systemDefault()
        )
        val formatter = DateTimeFormatter.ofPattern(
            "mm:ss",
            Locale.getDefault()
        )
        return localDateTime.format(formatter)
    }
}