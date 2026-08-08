package com.enriqueajin.pomidorki.data.countdown

import android.content.Context
import android.os.SystemClock
import com.enriqueajin.pomidorki.domain.model.TimerSessionSnapshot
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.presentation.UserSettingsState
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_IDLE
import com.enriqueajin.pomidorki.utils.PreferencesKeys.LONG_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.POMODORO_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SHORT_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SessionState
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

enum class SessionRestoreResult {
    Idle,
    Started,
    Paused,
    CompletedWhileAway,
}

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
        private var sessionActive = false

        private val _selectedTimer = MutableStateFlow(0)
        val selectedTimer = _selectedTimer.asStateFlow()
        private val durationSettings = MutableStateFlow(UserSettingsState())
        private val _initialMillis: MutableStateFlow<Long> = MutableStateFlow(0L)
        val initialMillis = _initialMillis.asStateFlow()
        private val _timeLeft: MutableStateFlow<Long> = MutableStateFlow(updateInitialMillis(_selectedTimer.value))
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
                    if (!sessionActive) {
                        applyDurationSettingsToTimer()
                    }
                }
            }
        }

        suspend fun restoreSession(): SessionRestoreResult {
            val session = userSettingsRepository.getTimerSession()
            return when (session.state) {
                SessionState.STARTED -> restoreStartedSession(session)
                SessionState.PAUSED -> restorePausedSession(session)
                else -> SessionRestoreResult.Idle
            }
        }

        private suspend fun restoreStartedSession(session: TimerSessionSnapshot): SessionRestoreResult {
            val remaining = session.deadlineElapsed - SystemClock.elapsedRealtime()
            if (remaining <= 0L) {
                userSettingsRepository.clearTimerSession()
                sessionActive = false
                isRunning = false
                _selectedTimer.value = session.selectedTimer
                applyDurationSettingsToTimer()
                return SessionRestoreResult.CompletedWhileAway
            }
            _selectedTimer.value = session.selectedTimer
            _initialMillis.value = session.initialMillis
            _timeLeft.value = remaining
            _deadlineElapsed.value = session.deadlineElapsed
            sessionActive = true
            start()
            return SessionRestoreResult.Started
        }

        private fun restorePausedSession(session: TimerSessionSnapshot): SessionRestoreResult {
            _selectedTimer.value = session.selectedTimer
            _initialMillis.value = session.initialMillis
            _timeLeft.value = session.timeLeft.coerceAtLeast(0L)
            _deadlineElapsed.value = 0L
            isRunning = false
            sessionActive = true
            return SessionRestoreResult.Paused
        }

        fun start() {
            tickJob?.cancel()
            isRunning = true
            sessionActive = true
            val initialRemaining = _timeLeft.value
            val deadline = SystemClock.elapsedRealtime() + _timeLeft.value
            _deadlineElapsed.value = deadline
            persistStartedSession(deadline)
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
                        isRunning = false
                        sessionActive = false
                        userSettingsRepository.clearTimerSession()
                        applyDurationSettingsToTimer()
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
            persistPausedSession()
        }

        fun reset() {
            isRunning = false
            sessionActive = false
            tickJob?.cancel()
            tickJob = null
            _deadlineElapsed.value = 0L
            clearPersistedSessionAsync()
            applyDurationSettingsToTimer()
        }

        private fun applyDurationSettingsToTimer() {
            _timeLeft.value = updateInitialMillis(_selectedTimer.value)
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
            _selectedTimer.value = selectedTimer
            sessionActive = false
            isRunning = false
            tickJob?.cancel()
            tickJob = null
            _deadlineElapsed.value = 0L
            clearPersistedSessionAsync()
            applyDurationSettingsToTimer()
        }

        private fun persistStartedSession(deadline: Long) {
            scope.launch {
                userSettingsRepository.saveTimerSession(
                    TimerSessionSnapshot(
                        state = SessionState.STARTED,
                        deadlineElapsed = deadline,
                        timeLeft = 0L,
                        initialMillis = _initialMillis.value,
                        selectedTimer = _selectedTimer.value,
                    ),
                )
            }
        }

        private fun persistPausedSession() {
            scope.launch {
                userSettingsRepository.saveTimerSession(
                    TimerSessionSnapshot(
                        state = SessionState.PAUSED,
                        deadlineElapsed = 0L,
                        timeLeft = _timeLeft.value,
                        initialMillis = _initialMillis.value,
                        selectedTimer = _selectedTimer.value,
                    ),
                )
            }
        }

        private fun clearPersistedSessionAsync() {
            scope.launch {
                userSettingsRepository.clearTimerSession()
            }
        }
    }
