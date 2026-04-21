package com.enriqueajin.pomidorki.data.countdown

import android.content.Context
import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.presentation.UserSettingsState
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_IDLE
import com.enriqueajin.pomidorki.utils.PreferencesKeys.LONG_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.POMODORO_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SHORT_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.getSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CountDownPomodoro
    @Inject
    constructor(
        private val context: Context,
        private val userSettingsRepository: UserSettingsRepository,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private var countDownTimer: CountDownTimer? = null

        private val selectedTimer = MutableStateFlow(0)
        private val durationSettings = MutableStateFlow(UserSettingsState())
        private val _initialMillis: MutableStateFlow<Long> = MutableStateFlow(0L)
        val initialMillis = _initialMillis.asStateFlow()
        private val _timeLeft: MutableStateFlow<Long> = MutableStateFlow(updateInitialMillis(selectedTimer.value))
        val timeLeft = _timeLeft.asStateFlow()

        private var isActive by mutableStateOf(false)
        private val countDownInterval = 1_000L

        init {
            observeDurationSettings()
        }

        private fun observeDurationSettings() {
            scope.launch {
                userSettingsRepository.userSettingsFlow.collect { pref ->
                    val pomodoro = pref.getSetting(POMODORO_DURATION).toLong()
                    val shortBreak = pref.getSetting(SHORT_BREAK_DURATION).toLong()
                    val longBreak = pref.getSetting(LONG_BREAK_DURATION).toLong()
                    durationSettings.value =
                        durationSettings.value.copy(
                            pomodoroDuration = pomodoro,
                            shortBreakDuration = shortBreak,
                            longBreakDuration = longBreak,
                        )
                    _timeLeft.value = updateInitialMillis(selectedTimer.value)
                }
            }
        }

        fun start() {
            isActive = true
            countDownTimer =
                object : CountDownTimer(timeLeft.value, countDownInterval) {
                    override fun onTick(millisUntilFinished: Long) {
                        _timeLeft.value = millisUntilFinished
                    }

                    override fun onFinish() {
                        _timeLeft.value = 0L
                        ServiceHelper.triggerForegroundService(context, ACTION_SERVICE_IDLE)

                        // Delay to make sure that timeLeft = 0 first, and then reset to initialMillis
                        scope.launch {
                            delay(100L)
                            _timeLeft.value = _initialMillis.value
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
            _timeLeft.value = _initialMillis.value
        }

        private fun updateInitialMillis(timerIndex: Int): Long {
            val millis =
                when (timerIndex) {
                    0 -> TimeUnit.MINUTES.toMillis(durationSettings.value.pomodoroDuration)
                    1 -> TimeUnit.MINUTES.toMillis(durationSettings.value.shortBreakDuration)
                    2 -> TimeUnit.MINUTES.toMillis(durationSettings.value.longBreakDuration)
                    else -> TimeUnit.MINUTES.toMillis(durationSettings.value.pomodoroDuration)
                }
            _initialMillis.value = millis
            return millis
        }

        fun updateSelectedTimer(selectedTimer: Int) {
            this.selectedTimer.value = selectedTimer
            _timeLeft.value = updateInitialMillis(selectedTimer)
        }
    }
