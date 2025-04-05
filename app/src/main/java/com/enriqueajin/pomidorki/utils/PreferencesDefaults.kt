package com.enriqueajin.pomidorki.utils

enum class PreferencesDefaults(val value: String) {
    POMODORO_DURATION("25"),
    SHORT_BREAK_DURATION("5"),
    LONG_BREAK_DURATION("15")
}

fun getDefaultPreference(key: String): String {
    return when(key) {
        PreferencesKeys.POMODORO_DURATION -> PreferencesDefaults.POMODORO_DURATION.value
        PreferencesKeys.SHORT_BREAK_DURATION -> PreferencesDefaults.SHORT_BREAK_DURATION.value
        PreferencesKeys.LONG_BREAK_DURATION -> PreferencesDefaults.LONG_BREAK_DURATION.value
        else -> PreferencesDefaults.POMODORO_DURATION.value
    }
}