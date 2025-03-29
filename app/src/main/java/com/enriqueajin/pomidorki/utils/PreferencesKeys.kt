package com.enriqueajin.pomidorki.utils

import androidx.datastore.preferences.core.intPreferencesKey

object PreferencesKeys {
    val POMODORO_DURATION = intPreferencesKey("pomodoro_duration")
    val SHORT_BREAK_DURATION = intPreferencesKey("short_break_duration")
    val LONG_BREAK_DURATION = intPreferencesKey("long_break_duration")
}