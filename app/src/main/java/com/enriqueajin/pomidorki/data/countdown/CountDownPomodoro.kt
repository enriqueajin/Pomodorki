package com.enriqueajin.pomidorki.data.countdown

import android.content.Context
import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_IDLE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class CountDownPomodoro(
    private val totalMinutes: Long,
    private val context: Context
) {

    private var countDownTimer: CountDownTimer? = null
    private val initialMillis = TimeUnit.MINUTES.toMillis(totalMinutes)

    private val _timeLeft: MutableStateFlow<Long> = MutableStateFlow(initialMillis)
    val timeLeft = _timeLeft.asStateFlow()

    val countDownInterval = 1_000L

    private var isActive by mutableStateOf(false)

    fun start() {
        isActive = true
        countDownTimer = object : CountDownTimer(timeLeft.value, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = millisUntilFinished
            }

            override fun onFinish() {
                ServiceHelper.triggerForegroundService(context, ACTION_SERVICE_IDLE)
                _timeLeft.value = initialMillis
                isActive = false
            }
        }.start()
    }

    fun pause() {
        isActive = false
        countDownTimer?.cancel()
    }

    fun reset() {
        isActive = false
        countDownTimer?.cancel()
        _timeLeft.value = initialMillis
    }
}