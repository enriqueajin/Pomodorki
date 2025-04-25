package com.enriqueajin.pomidorki.data.model

import com.enriqueajin.pomidorki.data.services.CountdownState

data class PomodoroServiceData(
    val currentState: CountdownState? = null,
    val timeLeft: Long? = null,
    val initialMillis: Long? = null
)