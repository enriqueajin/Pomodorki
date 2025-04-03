package com.enriqueajin.pomidorki.domain.repository

interface UserSettingsRepository {
    suspend fun getSetting(key: String): String?
    suspend fun saveSetting(key: String, value: String)
}