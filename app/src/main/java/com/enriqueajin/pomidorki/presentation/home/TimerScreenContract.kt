package com.enriqueajin.pomidorki.presentation.home

import com.enriqueajin.pomidorki.data.services.CountdownState

sealed interface TimerScreenContract {
    data class State(
        val currentState: CountdownState = CountdownState.Idle,
        val timeLeft: Long = 0L,
        val initialMillis: Long = 0L,
        val selectedTimer: Int = 0,
        val timerProgress: Float = 0f,
        val timerText: String = "00:00",
        val isDialogOpen: Boolean = false,
    )

    sealed class Event {
        data class OnTabClicked(
            val index: Int,
        ) : Event()

        data class OnAlertConfirmClick(
            val newTabIndex: Int,
        ) : Event()

        data object OnAlertCancelClick : Event()

        data object OnRestartIconClick : Event()
    }

    sealed class Effect {
        data class TriggerIntent(
            val tabIndex: Int,
        ) : Effect()

        data class TimerToBeConfirmed(
            val index: Int,
        ) : Effect()

        data class TriggerForegroundService(
            val action: String,
        ) : Effect()
    }
}
