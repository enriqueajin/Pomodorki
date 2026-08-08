package com.enriqueajin.pomidorki.fake

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.enriqueajin.pomidorki.domain.model.TimerSessionSnapshot
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.utils.PreferencesKeys
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SELECTED_TIMER
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SESSION_DEADLINE_ELAPSED
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SESSION_INITIAL_MILLIS
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SESSION_STATE
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SESSION_TIME_LEFT
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SessionState
import com.enriqueajin.pomidorki.utils.getDefaultPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeUserSettingsRepository(
    initialPreferences: Preferences = emptyPreferences(),
) : UserSettingsRepository {
    private val preferencesFlow = MutableStateFlow(initialPreferences)

    override val userSettingsFlow: Flow<Preferences> = preferencesFlow.asStateFlow()

    override suspend fun getSetting(key: String): String = preferencesFlow.value[stringPreferencesKey(key)] ?: getDefaultPreference(key)

    override suspend fun Preferences.getSetting(key: String): String = this[stringPreferencesKey(key)] ?: getDefaultPreference(key)

    override suspend fun saveString(
        key: String,
        value: String,
    ) {
        preferencesFlow.update { current ->
            current.toMutablePreferences().apply {
                this[stringPreferencesKey(key)] = value
            }
        }
    }

    override suspend fun saveInt(
        key: String,
        value: Int,
    ) {
        preferencesFlow.update { current ->
            current.toMutablePreferences().apply {
                this[intPreferencesKey(key)] = value
            }
        }
    }

    override suspend fun saveLong(
        key: String,
        value: Long,
    ) {
        preferencesFlow.update { current ->
            current.toMutablePreferences().apply {
                this[longPreferencesKey(key)] = value
            }
        }
    }

    override suspend fun saveTimerSession(session: TimerSessionSnapshot) {
        preferencesFlow.update { current ->
            current.toMutablePreferences().apply {
                this[stringPreferencesKey(SESSION_STATE)] = session.state
                this[longPreferencesKey(SESSION_DEADLINE_ELAPSED)] = session.deadlineElapsed
                this[longPreferencesKey(SESSION_TIME_LEFT)] = session.timeLeft
                this[longPreferencesKey(SESSION_INITIAL_MILLIS)] = session.initialMillis
                this[intPreferencesKey(SELECTED_TIMER)] = session.selectedTimer
            }
        }
    }

    override suspend fun clearTimerSession() {
        preferencesFlow.update { current ->
            current.toMutablePreferences().apply {
                this[stringPreferencesKey(SESSION_STATE)] = SessionState.IDLE
                this[longPreferencesKey(SESSION_DEADLINE_ELAPSED)] = 0L
                this[longPreferencesKey(SESSION_TIME_LEFT)] = 0L
                this[longPreferencesKey(SESSION_INITIAL_MILLIS)] = 0L
            }
        }
    }

    override suspend fun getTimerSession(): TimerSessionSnapshot {
        val preferences = preferencesFlow.value
        return TimerSessionSnapshot(
            state = preferences[stringPreferencesKey(SESSION_STATE)] ?: SessionState.IDLE,
            deadlineElapsed = preferences[longPreferencesKey(SESSION_DEADLINE_ELAPSED)] ?: 0L,
            timeLeft = preferences[longPreferencesKey(SESSION_TIME_LEFT)] ?: 0L,
            initialMillis = preferences[longPreferencesKey(SESSION_INITIAL_MILLIS)] ?: 0L,
            selectedTimer = preferences[intPreferencesKey(SELECTED_TIMER)] ?: 0,
        )
    }

    fun seedDefaults() {
        preferencesFlow.update { current ->
            current.toMutablePreferences().apply {
                PreferencesKeys.preferencesDefaults.forEach { (key, value) ->
                    this[stringPreferencesKey(key)] = value
                }
            }
        }
    }

    fun putString(
        key: String,
        value: String,
    ) {
        preferencesFlow.update { current ->
            current.toMutablePreferences().apply {
                this[stringPreferencesKey(key)] = value
            }
        }
    }

    fun putInt(
        key: String,
        value: Int,
    ) {
        preferencesFlow.update { current ->
            current.toMutablePreferences().apply {
                this[intPreferencesKey(key)] = value
            }
        }
    }

    fun putLong(
        key: String,
        value: Long,
    ) {
        preferencesFlow.update { current ->
            current.toMutablePreferences().apply {
                this[longPreferencesKey(key)] = value
            }
        }
    }

    fun clear() {
        preferencesFlow.value = emptyPreferences()
    }

    companion object {
        fun withDefaults(): FakeUserSettingsRepository = FakeUserSettingsRepository().apply { seedDefaults() }
    }
}
