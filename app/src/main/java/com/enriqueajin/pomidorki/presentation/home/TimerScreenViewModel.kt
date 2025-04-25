package com.enriqueajin.pomidorki.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.data.countdown.ServiceHelper
import com.enriqueajin.pomidorki.domain.repository.TimerServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel @Inject constructor(
    private val timerServiceRepository: TimerServiceRepository
): ViewModel() {

    init {
        timerServiceRepository.bindTimerService()
    }

    val uiState: StateFlow<TimerScreenState> = timerServiceRepository.serviceData.map {
        TimerScreenState(
            currentState = it.currentState,
            timeLeft = it.timeLeft,
            initialMillis = it.initialMillis
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimerScreenState()
    )

    fun onEvent(event: TimerScreenEvent) {
        when(event) {
            is TimerScreenEvent.TriggerPomodoro -> triggerPomodoro(event.context, event.action)
            is TimerScreenEvent.UpdateSelectedTimer -> timerServiceRepository.setSelectedTimer(event.selected)
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