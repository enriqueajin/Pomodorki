package com.enriqueajin.pomidorki.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.utils.PreferencesKeys.preferencesDefaults
import com.enriqueajin.pomidorki.utils.safeInvoke
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserSettingsRepository {

    override suspend fun getSetting(key: String): String? {
        return safeInvoke {
            val preferences: Preferences = dataStore.data.first()
            val preferencesKey: Preferences.Key<String> = stringPreferencesKey(key)
            val setting = preferences[preferencesKey]
            setting ?: preferencesDefaults[key]
        }
    }

    override suspend fun saveSetting(key: String, value: String) {
        safeInvoke {
            val preferencesKey = stringPreferencesKey(key)
            dataStore.edit { preferences ->
                preferences[preferencesKey] = value
            }
        }
    }
}