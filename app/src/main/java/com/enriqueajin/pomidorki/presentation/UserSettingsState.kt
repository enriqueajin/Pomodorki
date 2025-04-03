package com.enriqueajin.pomidorki.presentation

data class UserSettingsState(
    val pomodoroDuration: Long? = null,
    val shortBreakDuration: Long? = null,
    val longBreakDuration: Long? = null
)