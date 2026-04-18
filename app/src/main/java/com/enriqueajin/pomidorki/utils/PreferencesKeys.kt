package com.enriqueajin.pomidorki.utils

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.IOException

object PreferencesKeys {
    val POMODORO_DURATION = "pomodoro_duration"
    val SHORT_BREAK_DURATION = "short_break_duration"
    val LONG_BREAK_DURATION = "long_break_duration"
    val SELECTED_TIMER = "selected_timer"

    val preferencesDefaults =
        mapOf(
            POMODORO_DURATION to "25",
            SHORT_BREAK_DURATION to "5",
            LONG_BREAK_DURATION to "15",
        )

    fun String.toKey(): Preferences.Key<String> = stringPreferencesKey(this)
}

inline fun <T> safeInvoke(execute: () -> T?): T? =
    try {
        execute()
    } catch (e: IOException) {
        Log.e("PreferencesRepository", "IOException: ${e.localizedMessage}")
        null
    } catch (e: Exception) {
        Log.e("PreferencesRepository", "Error: ${e.localizedMessage}")
        null
    }
