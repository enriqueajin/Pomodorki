package com.enriqueajin.pomidorki.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.data.countdown.ServiceHelper
import com.enriqueajin.pomidorki.domain.repository.TimerServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel @Inject constructor(
    private val timerServiceRepository: TimerServiceRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(TimerScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        timerServiceRepository.bindTimerService()
        observeServiceData()
    }

    private fun observeServiceData() {
        viewModelScope.launch {
            timerServiceRepository.serviceData.collect { data ->
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
        _uiState.value = _uiState.value.copy(selectedTimer = selected)
        timerServiceRepository.setSelectedTimer(selected)
    }

    private fun triggerPomodoro(context: Context, action: String) {
        ServiceHelper.triggerForegroundService(context, action)
    }

    override fun onCleared() {
        super.onCleared()
        timerServiceRepository.unbindTimerService()
    }
}