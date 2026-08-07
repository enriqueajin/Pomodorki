package com.enriqueajin.pomidorki.data.countdown

import android.content.Context
import android.os.SystemClock
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.presentation.UserSettingsState
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_IDLE
import com.enriqueajin.pomidorki.utils.PreferencesKeys.LONG_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.POMODORO_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SHORT_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.getSetting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class CountDownPomodoro
    @Inject
    constructor(
        private val context: Context,
        private val userSettingsRepository: UserSettingsRepository,
        @Named("countdown") private val dispatcher: CoroutineDispatcher,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + dispatcher)

        private var tickJob: Job? = null
        private var isRunning = false

        private val selectedTimer = MutableStateFlow(0)
        private val durationSettings = MutableStateFlow(UserSettingsState())
        private val _initialMillis: MutableStateFlow<Long> = MutableStateFlow(0L)
        val initialMillis = _initialMillis.asStateFlow()
        private val _timeLeft: MutableStateFlow<Long> = MutableStateFlow(updateInitialMillis(selectedTimer.value))
        val timeLeft = _timeLeft.asStateFlow()
        private val _deadlineElapsed: MutableStateFlow<Long> = MutableStateFlow(0L)
        val deadlineElapsed = _deadlineElapsed.asStateFlow()

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
                    updateInitialMillis(selectedTimer.value)
                    if (!isRunning) {
                        _timeLeft.value = _initialMillis.value
                    }
                }
            }
        }

        fun start() {
            tickJob?.cancel()
            isRunning = true
            val initialRemaining = _timeLeft.value
            val deadline = SystemClock.elapsedRealtime() + _timeLeft.value
            _deadlineElapsed.value = deadline
            tickJob =
                scope.launch {
                    val displayJob =
                        launch {
                            var displayedSeconds = (initialRemaining + 999L) / 1_000L
                            var delayMillis = initialRemaining % 1_000L
                            if (delayMillis == 0L) delayMillis = 1_000L

                            while (isActive && displayedSeconds > 0L) {
                                delay(delayMillis)
                                if (!isActive) break
                                displayedSeconds--
                                _timeLeft.value = displayedSeconds * 1_000L
                                delayMillis = 1_000L
                            }
                        }

                    try {
                        delay(initialRemaining)
                    } finally {
                        displayJob.cancel()
                    }

                    if (isActive) {
                        _deadlineElapsed.value = 0L
                        _timeLeft.value = 0L
                        ServiceHelper.triggerForegroundService(context, ACTION_SERVICE_IDLE)
                        // Emit 0 first, then restore initial duration for next start
                        delay(100L)
                        _timeLeft.value = _initialMillis.value
                        isRunning = false
                    }
                }
        }

        fun pause() {
            isRunning = false
            tickJob?.cancel()
            tickJob = null
            val deadline = _deadlineElapsed.value
            if (deadline > 0L) {
                _timeLeft.value = (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
            }
            _deadlineElapsed.value = 0L
        }

        fun reset() {
            isRunning = false
            tickJob?.cancel()
            tickJob = null
            _deadlineElapsed.value = 0L
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
            _deadlineElapsed.value = 0L
            _timeLeft.value = updateInitialMillis(selectedTimer)
        }
    }
