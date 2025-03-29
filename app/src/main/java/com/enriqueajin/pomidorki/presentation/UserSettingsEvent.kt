package com.enriqueajin.pomidorki.presentation

sealed interface UserSettingsEvent {
    data class UpdatePomodoroDuration(val minutes: Int) : UserSettingsEvent
    data class UpdateShortBreakDuration(val minutes: Int) : UserSettingsEvent
    data class UpdateLongBreakDuration(val minutes: Int) : UserSettingsEvent
}