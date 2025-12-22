package com.enriqueajin.pomidorki.presentation.home

import com.enriqueajin.pomidorki.data.services.CountdownState

data class TimerScreenState(
    val currentState: CountdownState = CountdownState.Idle,
    val timeLeft: Long = 0L,
    val initialMillis: Long = 0L,
    val selectedTimer: Int = 0,
)
