package com.enriqueajin.pomidorki.presentation

data class UserSettingsState(
    val pomodoroDuration: Int? = null,
    val shortBreakDuration: Int? = null,
    val longBreakDuration: Int? = null
)