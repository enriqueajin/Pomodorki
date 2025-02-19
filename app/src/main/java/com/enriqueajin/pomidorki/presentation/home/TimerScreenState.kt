package com.enriqueajin.pomidorki.presentation.home

import com.enriqueajin.pomidorki.data.services.CountdownState

data class TimerScreenState(
    val currentState: CountdownState? = null,
    val timeLeft: Long? = null,
)
