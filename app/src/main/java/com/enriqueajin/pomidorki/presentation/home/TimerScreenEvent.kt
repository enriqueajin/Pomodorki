package com.enriqueajin.pomidorki.presentation.home

import android.content.Context

sealed interface TimerScreenEvent {

    data class TriggerPomodoro(val context: Context, val action: String) : TimerScreenEvent
    data class UpdateSelectedTimer(val selected: Int): TimerScreenEvent
}