package com.enriqueajin.pomidorki.domain.repository

import androidx.datastore.preferences.core.Preferences
import com.enriqueajin.pomidorki.domain.model.TimerSessionSnapshot
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    val userSettingsFlow: Flow<Preferences>

    suspend fun getSetting(key: String): String

    suspend fun Preferences.getSetting(key: String): String

    suspend fun saveString(
        key: String,
        value: String,
    )

    suspend fun saveInt(
        key: String,
        value: Int,
    )

    suspend fun saveLong(
        key: String,
        value: Long,
    )

    suspend fun saveTimerSession(session: TimerSessionSnapshot)

    suspend fun clearTimerSession()

    suspend fun getTimerSession(): TimerSessionSnapshot
}
