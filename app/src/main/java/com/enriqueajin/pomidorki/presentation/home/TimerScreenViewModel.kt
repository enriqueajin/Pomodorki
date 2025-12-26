package com.enriqueajin.pomidorki.presentation.home

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.data.countdown.ServiceHelper
import com.enriqueajin.pomidorki.data.services.CountdownState
import com.enriqueajin.pomidorki.domain.repository.TimerServiceRepository
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SELECTED_TIMER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel @Inject constructor(
    private val timerServiceRepository: TimerServiceRepository,
    private val userSettingsRepository: UserSettingsRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(TimerScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        timerServiceRepository.bindTimerService()
        observeServiceData()
        checkInitialSelectedTimer()
    }

    private fun checkInitialSelectedTimer() {
        println("The current state is ${_uiState.value.currentState}")
        if(_uiState.value.currentState == CountdownState.Started ||
            _uiState.value.currentState == CountdownState.Paused) {
            viewModelScope.launch {
                val preferences = userSettingsRepository.userSettingsFlow.first()
                val key = intPreferencesKey(SELECTED_TIMER)
                val storedSelectedTimer = preferences[key] ?: 0
                _uiState.value = _uiState.value.copy(selectedTimer = storedSelectedTimer)
            }
        }
    }

    private fun observeServiceData() {
        viewModelScope.launch {
            timerServiceRepository.getServiceData().collect { data ->
                _uiState.value = _uiState.value.copy(
                    currentState = data.currentState,
                    timeLeft = data.timeLeft,
                    initialMillis = data.initialMillis
                )
            }
        }
    }

    fun onEvent(event: TimerScreenEvent) {
        when(event) {
            is TimerScreenEvent.TriggerPomodoro -> triggerPomodoro(event.context, event.action)
            is TimerScreenEvent.UpdateSelectedTimer -> updateSelectedTimer(event.selected)
        }
    }

    private fun updateSelectedTimer(selected: Int) {
        updateDataStore(selected)
        _uiState.value = _uiState.value.copy(selectedTimer = selected)
        timerServiceRepository.setSelectedTimer(selected)
    }

    private fun updateDataStore(selectedTimer: Int) {
        viewModelScope.launch {
            userSettingsRepository.saveInt(
                key = SELECTED_TIMER,
                value = selectedTimer
            )
        }
    }

    private fun triggerPomodoro(context: Context, action: String) {
        ServiceHelper.triggerForegroundService(context, action)
    }


    override fun onCleared() {
        super.onCleared()
        timerServiceRepository.unbindTimerService()
    }
}