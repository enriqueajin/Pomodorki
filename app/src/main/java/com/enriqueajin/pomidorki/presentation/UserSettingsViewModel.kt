package com.enriqueajin.pomidorki.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.utils.PreferencesKeys.LONG_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.POMODORO_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SHORT_BREAK_DURATION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    suspend fun getSetting(key: String) {
        userSettingsRepository.getSetting(key)
    }

    fun onUserSettingsEvent(event: UserSettingsEvent) {
        when(event) {
            is UserSettingsEvent.UpdatePomodoroDuration -> {
                viewModelScope.launch {
                    userSettingsRepository.saveSetting(
                        key = POMODORO_DURATION,
                        value = event.minutes.toString()
                    )
                }
            }
            is UserSettingsEvent.UpdateShortBreakDuration -> {
                viewModelScope.launch {
                    userSettingsRepository.saveSetting(
                        key = SHORT_BREAK_DURATION,
                        value = event.minutes.toString()
                    )
                }
            }
            is UserSettingsEvent.UpdateLongBreakDuration -> {
                viewModelScope.launch {
                    userSettingsRepository.saveSetting(
                        key = LONG_BREAK_DURATION,
                        value = event.minutes.toString()
                    )
                }
            }
        }
    }
}