package com.enriqueajin.pomidorki.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.utils.PreferencesKeys.LONG_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.POMODORO_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SHORT_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.getSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel
    @Inject
    constructor(
        private val userSettingsRepository: UserSettingsRepository,
    ) : ViewModel() {
        val userSettingsState =
            userSettingsRepository.userSettingsFlow
                .map { preferences ->
                    UserSettingsState(
                        pomodoroDuration = preferences.getSetting(POMODORO_DURATION).toLong(),
                        shortBreakDuration = preferences.getSetting(SHORT_BREAK_DURATION).toLong(),
                        longBreakDuration = preferences.getSetting(LONG_BREAK_DURATION).toLong(),
                    )
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null,
                )

        fun onUserSettingsEvent(event: UserSettingsEvent) {
            when (event) {
                is UserSettingsEvent.UpdatePomodoroDuration -> {
                    viewModelScope.launch {
                        userSettingsRepository.saveString(
                            key = POMODORO_DURATION,
                            value = event.minutes.toString(),
                        )
                    }
                }
                is UserSettingsEvent.UpdateShortBreakDuration -> {
                    viewModelScope.launch {
                        userSettingsRepository.saveString(
                            key = SHORT_BREAK_DURATION,
                            value = event.minutes.toString(),
                        )
                    }
                }
                is UserSettingsEvent.UpdateLongBreakDuration -> {
                    viewModelScope.launch {
                        userSettingsRepository.saveString(
                            key = LONG_BREAK_DURATION,
                            value = event.minutes.toString(),
                        )
                    }
                }
            }
        }
    }
