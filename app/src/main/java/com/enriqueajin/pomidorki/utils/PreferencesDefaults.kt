package com.enriqueajin.pomidorki.utils

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

enum class PreferencesDefaults(
    val value: String,
) {
    POMODORO_DURATION("25"),
    SHORT_BREAK_DURATION("5"),
    LONG_BREAK_DURATION("15"),
}

fun getDefaultPreference(key: String): String =
    when (key) {
        PreferencesKeys.POMODORO_DURATION -> PreferencesDefaults.POMODORO_DURATION.value
        PreferencesKeys.SHORT_BREAK_DURATION -> PreferencesDefaults.SHORT_BREAK_DURATION.value
        PreferencesKeys.LONG_BREAK_DURATION -> PreferencesDefaults.LONG_BREAK_DURATION.value
        else -> PreferencesDefaults.POMODORO_DURATION.value
    }

fun Preferences.getSetting(key: String): String =
    safeInvoke {
        val preferencesKey: Preferences.Key<String> = stringPreferencesKey(key)
        this[preferencesKey]
    } ?: getDefaultPreference(key)
