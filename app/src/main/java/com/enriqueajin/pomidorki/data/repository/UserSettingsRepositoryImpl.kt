package com.enriqueajin.pomidorki.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.enriqueajin.pomidorki.domain.model.TimerSessionSnapshot
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SELECTED_TIMER
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SESSION_DEADLINE_ELAPSED
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SESSION_INITIAL_MILLIS
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SESSION_STATE
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SESSION_TIME_LEFT
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SessionState
import com.enriqueajin.pomidorki.utils.getDefaultPreference
import com.enriqueajin.pomidorki.utils.safeInvoke
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : UserSettingsRepository {
        override val userSettingsFlow: Flow<Preferences> = dataStore.data

        override suspend fun getSetting(key: String): String =
            safeInvoke {
                val preferences: Preferences = dataStore.data.first()
                val preferencesKey: Preferences.Key<String> = stringPreferencesKey(key)
                preferences[preferencesKey]
            } ?: getDefaultPreference(key)

        private inline fun <reified T> T.toPreferencesKey(key: String) =
            when (T::class) {
                String::class -> stringPreferencesKey(key)
                Int::class -> intPreferencesKey(key)
                Boolean::class -> booleanPreferencesKey(key)
                Float::class -> floatPreferencesKey(key)
                Long::class -> longPreferencesKey(key)
                else -> throw IllegalArgumentException("Type not supported by DataStore")
            }

        override suspend fun Preferences.getSetting(key: String): String =
            safeInvoke {
                val preferencesKey: Preferences.Key<String> = stringPreferencesKey(key)
                this[preferencesKey]
            } ?: getDefaultPreference(key)

        override suspend fun saveString(
            key: String,
            value: String,
        ) {
            safeInvoke {
                val preferencesKey = stringPreferencesKey(key)
                dataStore.edit { preferences ->
                    preferences[preferencesKey] = value
                }
            }
        }

        override suspend fun saveInt(
            key: String,
            value: Int,
        ) {
            safeInvoke {
                val preferencesKey = intPreferencesKey(key)
                dataStore.edit { preferences ->
                    preferences[preferencesKey] = value
                }
            }
        }

        override suspend fun saveLong(
            key: String,
            value: Long,
        ) {
            safeInvoke {
                val preferencesKey = longPreferencesKey(key)
                dataStore.edit { preferences ->
                    preferences[preferencesKey] = value
                }
            }
        }

        override suspend fun saveTimerSession(session: TimerSessionSnapshot) {
            safeInvoke {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey(SESSION_STATE)] = session.state
                    preferences[longPreferencesKey(SESSION_DEADLINE_ELAPSED)] = session.deadlineElapsed
                    preferences[longPreferencesKey(SESSION_TIME_LEFT)] = session.timeLeft
                    preferences[longPreferencesKey(SESSION_INITIAL_MILLIS)] = session.initialMillis
                    preferences[intPreferencesKey(SELECTED_TIMER)] = session.selectedTimer
                }
            }
        }

        override suspend fun clearTimerSession() {
            safeInvoke {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey(SESSION_STATE)] = SessionState.IDLE
                    preferences[longPreferencesKey(SESSION_DEADLINE_ELAPSED)] = 0L
                    preferences[longPreferencesKey(SESSION_TIME_LEFT)] = 0L
                    preferences[longPreferencesKey(SESSION_INITIAL_MILLIS)] = 0L
                }
            }
        }

        override suspend fun getTimerSession(): TimerSessionSnapshot {
            val preferences = dataStore.data.first()
            return TimerSessionSnapshot(
                state = preferences[stringPreferencesKey(SESSION_STATE)] ?: SessionState.IDLE,
                deadlineElapsed = preferences[longPreferencesKey(SESSION_DEADLINE_ELAPSED)] ?: 0L,
                timeLeft = preferences[longPreferencesKey(SESSION_TIME_LEFT)] ?: 0L,
                initialMillis = preferences[longPreferencesKey(SESSION_INITIAL_MILLIS)] ?: 0L,
                selectedTimer = preferences[intPreferencesKey(SELECTED_TIMER)] ?: 0,
            )
        }

        inline fun <reified T> getPreferencesKey(key: String): Preferences.Key<T> =
            when (T::class) {
                String::class -> stringPreferencesKey(key)
                Int::class -> intPreferencesKey(key)
                Boolean::class -> booleanPreferencesKey(key)
                Float::class -> floatPreferencesKey(key)
                Long::class -> longPreferencesKey(key)
                else -> throw IllegalArgumentException("Type not supported by DataStore")
            } as Preferences.Key<T>
    }
