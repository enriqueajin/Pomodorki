package com.enriqueajin.pomidorki.domain.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    fun getDataStoreData(): Flow<Preferences>
    suspend fun updatePomodoroDuration(minutes: Int)
    suspend fun updateShortBreakDuration(minutes: Int)
    suspend fun updateLongBreakDuration(minutes: Int)
}