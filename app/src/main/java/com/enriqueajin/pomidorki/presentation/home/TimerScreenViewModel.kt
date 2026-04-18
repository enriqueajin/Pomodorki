package com.enriqueajin.pomidorki.presentation.home

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.data.countdown.ServiceHelper
import com.enriqueajin.pomidorki.data.model.PomodoroServiceData
import com.enriqueajin.pomidorki.data.services.CountdownService
import com.enriqueajin.pomidorki.data.services.CountdownState
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SELECTED_TIMER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.State
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.Effect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private val _uiEffects = Channel<Effect>()
    val uiEffects = _uiEffects.receiveAsFlow()

    init {
        checkInitialSelectedTimer()
    }

    fun onServiceConnected(service: CountdownService) {
        println("onServiceConnected was called!")
        service.observeTimeLeft()
        viewModelScope.launch {
            service.serviceData.collect(::updateStateServiceData)
        }
    }

    private fun updateStateServiceData(data: PomodoroServiceData) {
        _uiState.update {
            it.copy(
                currentState = data.currentState,
                timeLeft = data.timeLeft,
                initialMillis = data.initialMillis,
                selectedTimer = data.selectedTimer,
            )
        }
    }

    private fun checkInitialSelectedTimer() {
        if(_uiState.value.hasTimerStarted()) {
            viewModelScope.launch {
                val preferences = userSettingsRepository.userSettingsFlow.first()
                val key = intPreferencesKey(SELECTED_TIMER)
                val storedSelectedTimer = preferences[key] ?: 0
                _uiState.update { it.copy(selectedTimer = storedSelectedTimer) }
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
        _uiEffects.trySend(Effect.UpdateSelectedTimer(selected))
        updateDataStore(selected)
        _uiState.update { it.copy(selectedTimer = selected) }
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

    private fun State.hasTimerStarted(): Boolean = currentState == CountdownState.Started || currentState == CountdownState.Paused
}