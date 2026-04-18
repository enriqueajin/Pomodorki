package com.enriqueajin.pomidorki.data.model

import com.enriqueajin.pomidorki.data.services.CountdownState

data class PomodoroServiceData(
    val currentState: CountdownState = CountdownState.Idle,
    val timeLeft: Long = 0L,
    val initialMillis: Long = 0L,
    val selectedTimer: Int = 0,
)