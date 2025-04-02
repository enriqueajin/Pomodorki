package com.enriqueajin.pomidorki.data.countdown

import android.content.Context
import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_IDLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CountDownPomodoro(
    private val pomodoroDuration: Long,
    private val shortBreakDuration: Long,
    private val longBreakDuration: Long,
    private val context: Context,
    private val selectedTimer: StateFlow<Int>,
    private val onTimerTick: (Long) -> Unit,
    private val onInitialMillisChange: (Long) -> Unit,
) {

    private var countDownTimer: CountDownTimer? = null
    private val countDownInterval = 1_000L

    private var isActive by mutableStateOf(false)
    var initialMillis: Long = 0
        private set

    private val _timeLeft: MutableStateFlow<Long> = MutableStateFlow(getInitialMillis(selectedTimer.value))
    val timeLeft = _timeLeft.asStateFlow()

    init {
        selectedTimer
            .onEach { timerIndex ->
                _timeLeft.value = getInitialMillis(timerIndex)
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun start() {
        isActive = true
        countDownTimer = object : CountDownTimer(timeLeft.value, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = millisUntilFinished
                onTimerTick(millisUntilFinished)
            }

            override fun onFinish() {
                _timeLeft.value = 0L
                onTimerTick(0L)
                ServiceHelper.triggerForegroundService(context, ACTION_SERVICE_IDLE)

                // Delay to make sure that timeLeft = 0 first, and then reset to initialMillis
                CoroutineScope(Dispatchers.Main).launch {
                    delay(100L)
                    _timeLeft.value = initialMillis
                    onTimerTick(initialMillis)
                    isActive = false
                }
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
        onTimerTick(initialMillis)
    }

    private fun getInitialMillis(timerIndex: Int): Long {
        val returningInitialMillis = when (timerIndex) {
            0 -> {
                initialMillis = TimeUnit.MINUTES.toMillis(pomodoroDuration)
                initialMillis
            }
            1 -> {
                initialMillis = TimeUnit.MINUTES.toMillis(shortBreakDuration)
                initialMillis
            }
            2 -> {
                initialMillis = TimeUnit.MINUTES.toMillis(longBreakDuration)
                initialMillis
            }
            else -> {
                initialMillis = TimeUnit.MINUTES.toMillis(pomodoroDuration)
                initialMillis
            }
        }
        onInitialMillisChange(returningInitialMillis)
        return returningInitialMillis
    }
}