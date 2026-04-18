package com.enriqueajin.pomidorki.presentation.home

import com.enriqueajin.pomidorki.data.services.CountdownState

sealed interface TimerScreenContract {

    data class State(
        val currentState: CountdownState = CountdownState.Idle,
        val timeLeft: Long = 0L,
        val initialMillis: Long = 0L,
        val selectedTimer: Int = 0,
    )

    sealed class Effect {
        data class UpdateSelectedTimer(val selected: Int): Effect()
    }
}

