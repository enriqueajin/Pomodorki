package com.enriqueajin.pomidorki.domain.model

import com.enriqueajin.pomidorki.utils.PreferencesKeys.SessionState

data class TimerSessionSnapshot(
    val state: String = SessionState.IDLE,
    val deadlineElapsed: Long = 0L,
    val timeLeft: Long = 0L,
    val initialMillis: Long = 0L,
    val selectedTimer: Int = 0,
)
