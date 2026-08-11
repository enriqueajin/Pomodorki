package com.enriqueajin.pomidorki.presentation.home

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.data.services.CountdownService
import com.enriqueajin.pomidorki.data.services.CountdownState.Paused
import com.enriqueajin.pomidorki.data.services.CountdownState.Started
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.presentation.base.BaseViewModel
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.Effect
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.Event
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.InternalEvent
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.State
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_CLOSE
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_RESET
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SELECTED_TIMER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel
    @Inject
    constructor(
        private val userSettingsRepository: UserSettingsRepository,
        private val timerUiMapper: TimerUiMapper,
    ) : BaseViewModel<State, Event, InternalEvent, Effect>(State()) {
        private var serviceDataJob: Job? = null

        init {
            checkInitialSelectedTimer()
        }

        override fun onEvent(event: Event) {
            val previousSelectedTimer = uiState.value.selectedTimer
            super.onEvent(event)
            val nextSelectedTimer = uiState.value.selectedTimer
            if (nextSelectedTimer != previousSelectedTimer) {
                updateDataStore(nextSelectedTimer)
            }
        }

        fun onServiceConnected(service: CountdownService) {
            serviceDataJob?.cancel()
            serviceDataJob =
                viewModelScope.launch {
                    service.serviceData.collect { data ->
                        dispatch(InternalEvent.ServiceDataUpdated(data))
                    }
                }
        }

        fun onServiceDisconnected() {
            serviceDataJob?.cancel()
            serviceDataJob = null
        }

        override fun handleUiEvent(event: Event) {
            when (event) {
                is Event.OnTabClicked -> handleTabClick(event.index)
                is Event.OnAlertConfirmClick -> handleAlertConfirm(event.newTabIndex)
                Event.OnAlertCancelClick -> setState { copy(isDialogOpen = false) }
                Event.OnRestartIconClick -> {
                    setState { copy(isDialogOpen = true) }
                    emitEffect(Effect.TimerToBeConfirmed(uiState.value.selectedTimer))
                }
            }
        }

        override fun handleInternalEvent(event: InternalEvent) {
            when (event) {
                is InternalEvent.ServiceDataUpdated -> {
                    val data = event.data
                    setState {
                        copy(
                            currentState = data.currentState,
                            timeLeft = data.timeLeft,
                            initialMillis = data.initialMillis,
                            selectedTimer = data.selectedTimer,
                            deadlineElapsed = data.deadlineElapsed,
                            timerText = timerUiMapper.formatTimerText(data.timeLeft),
                        )
                    }
                }
                is InternalEvent.RestoreSelectedTimer ->
                    setState { copy(selectedTimer = event.index) }
            }
        }

        private fun handleTabClick(index: Int) {
            val state = uiState.value
            if (state.selectedTimer == index) return
            if (state.hasTimerStarted()) {
                setState { copy(isDialogOpen = true) }
                emitEffect(Effect.TimerToBeConfirmed(index))
            } else {
                setState { copy(selectedTimer = index) }
                emitEffect(Effect.TriggerIntent(index))
            }
        }

        private fun handleAlertConfirm(newTabIndex: Int) {
            val action =
                if (uiState.value.hasTimerStarted()) ACTION_SERVICE_CLOSE else ACTION_SERVICE_RESET
            setState { copy(selectedTimer = newTabIndex, isDialogOpen = false) }
            emitEffect(
                Effect.TriggerForegroundService(
                    action = action,
                    timerType = newTabIndex,
                ),
            )
        }

        private fun checkInitialSelectedTimer() {
            if (uiState.value.hasTimerStarted()) {
                viewModelScope.launch {
                    val preferences = userSettingsRepository.userSettingsFlow.first()
                    val key = intPreferencesKey(SELECTED_TIMER)
                    val storedSelectedTimer = preferences[key] ?: 0
                    dispatch(InternalEvent.RestoreSelectedTimer(storedSelectedTimer))
                }
            }
        }

        private fun updateDataStore(selectedTimer: Int) {
            viewModelScope.launch {
                userSettingsRepository.saveInt(
                    key = SELECTED_TIMER,
                    value = selectedTimer,
                )
            }
        }

        private fun State.hasTimerStarted(): Boolean = currentState == Started || currentState == Paused
    }
