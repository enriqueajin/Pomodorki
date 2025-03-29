package com.enriqueajin.pomidorki.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.utils.PreferencesKeys.LONG_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.POMODORO_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SHORT_BREAK_DURATION
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserSettingsRepository {

    override fun getDataStoreData(): Flow<Preferences> = dataStore.data

    override suspend fun updatePomodoroDuration(minutes: Int) {
        safeInvoke {
            dataStore.edit { preferences ->
                preferences[POMODORO_DURATION] = minutes
            }
        }
    }

    override suspend fun updateShortBreakDuration(minutes: Int) {
        safeInvoke {
            dataStore.edit { preferences ->
                preferences[SHORT_BREAK_DURATION] = minutes
            }
        }
    }

    override suspend fun updateLongBreakDuration(minutes: Int) {
        safeInvoke {
            dataStore.edit { preferences ->
                preferences[LONG_BREAK_DURATION] = minutes
            }
        }
    }

    private inline fun <T> safeInvoke(execute: () -> T?): T? {
        return try {
            execute()
        } catch (e: IOException) {
            Log.e("PreferencesRepository", "IOException: ${e.localizedMessage}")
            null
        } catch (e: Exception) {
            Log.e("PreferencesRepository", "Error: ${e.localizedMessage}")
            null
        }
    }
}