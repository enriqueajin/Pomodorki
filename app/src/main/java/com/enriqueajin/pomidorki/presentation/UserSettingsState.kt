package com.enriqueajin.pomidorki.presentation

data class UserSettingsState(
    val pomodoroDuration: Long = 25,
    val shortBreakDuration: Long = 5,
    val longBreakDuration: Long = 15,
)
