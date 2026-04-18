package com.enriqueajin.pomidorki.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.utils.PreferencesKeys.LONG_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.POMODORO_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SHORT_BREAK_DURATION
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
                    val pomodoroDuration = userSettingsRepository.getSetting(POMODORO_DURATION)
                    val shortBreakDuration = userSettingsRepository.getSetting(SHORT_BREAK_DURATION)
                    val longBreakDuration = userSettingsRepository.getSetting(LONG_BREAK_DURATION)
                    UserSettingsState(
                        pomodoroDuration = pomodoroDuration.toLong(),
                        shortBreakDuration = shortBreakDuration.toLong(),
                        longBreakDuration = longBreakDuration.toLong(),
                    )
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = UserSettingsState(),
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
