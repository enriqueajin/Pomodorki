package com.enriqueajin.pomidorki.presentation

sealed interface UserSettingsEvent {
    data class UpdatePomodoroDuration(
        val minutes: Long,
    ) : UserSettingsEvent

    data class UpdateShortBreakDuration(
        val minutes: Long,
    ) : UserSettingsEvent

    data class UpdateLongBreakDuration(
        val minutes: Long,
    ) : UserSettingsEvent
}
