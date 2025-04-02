package com.enriqueajin.pomidorki.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.utils.PreferencesKeys.LONG_BREAK_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.POMODORO_DURATION
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SHORT_BREAK_DURATION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val dataStoreFlow = userSettingsRepository.getDataStoreData()

    val userSettingsState: StateFlow<UserSettingsState> = dataStoreFlow
        .map { preferences ->
            UserSettingsState(
                pomodoroDuration = preferences[POMODORO_DURATION] ?: 25,
                shortBreakDuration = preferences[SHORT_BREAK_DURATION] ?: 5,
                longBreakDuration = preferences[LONG_BREAK_DURATION] ?: 15
            )
         }
        .stateIn(
             viewModelScope,
             SharingStarted.WhileSubscribed(5000),
             UserSettingsState()
        )

    fun onUserSettingsEvent(event: UserSettingsEvent) {
        when(event) {
            is UserSettingsEvent.UpdatePomodoroDuration -> {
                viewModelScope.launch {
                    userSettingsRepository.updatePomodoroDuration(event.minutes)
                }
            }
            is UserSettingsEvent.UpdateShortBreakDuration -> {
                viewModelScope.launch {
                    userSettingsRepository.updateShortBreakDuration(event.minutes)
                }
            }
            is UserSettingsEvent.UpdateLongBreakDuration -> {
                viewModelScope.launch {
                    userSettingsRepository.updateLongBreakDuration(event.minutes)
                }
            }
        }
    }
}