package com.enriqueajin.pomidorki.domain.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    val userSettingsFlow: Flow<Preferences>
    suspend fun getSetting(key: String): String
    suspend fun getSetting(preferences: Preferences, key: String): String
    suspend fun saveSetting(key: String, value: String)
}