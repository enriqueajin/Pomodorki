package com.enriqueajin.pomidorki.presentation.home

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriqueajin.pomidorki.data.model.PomodoroServiceData
import com.enriqueajin.pomidorki.data.services.CountdownService
import com.enriqueajin.pomidorki.data.services.CountdownState.Paused
import com.enriqueajin.pomidorki.data.services.CountdownState.Started
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.Effect
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.Event
import com.enriqueajin.pomidorki.presentation.home.TimerScreenContract.State
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_CLOSE
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_RESET
import com.enriqueajin.pomidorki.utils.PreferencesKeys.SELECTED_TIMER
import com.enriqueajin.pomidorki.utils.updateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel
    @Inject
    constructor(
        private val userSettingsRepository: UserSettingsRepository,
        private val timerUiMapper: TimerUiMapper,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(State())
        val uiState = _uiState.asStateFlow()

        private val _uiEffects = Channel<Effect>()
        val uiEffects = _uiEffects.receiveAsFlow()

        private var serviceDataJob: Job? = null

        init {
            checkInitialSelectedTimer()
        }

        fun onServiceConnected(service: CountdownService) {
            serviceDataJob?.cancel()
            serviceDataJob =
                viewModelScope.launch {
                    service.serviceData.collect(::updateStateServiceData)
                }
        }

        fun onServiceDisconnected() {
            serviceDataJob?.cancel()
            serviceDataJob = null
        }

        private fun updateStateServiceData(data: PomodoroServiceData) {
            _uiState.update {
                it.copy(
                    currentState = data.currentState,
                    timeLeft = data.timeLeft,
                    initialMillis = data.initialMillis,
                    selectedTimer = data.selectedTimer,
                    timerProgress =
                        timerUiMapper.formatProgress(
                            timeLeftMillis = data.timeLeft,
                            initialMillis = data.initialMillis,
                        ),
                    timerText = timerUiMapper.formatTimerText(data.timeLeft),
                )
            }
        }

        private fun checkInitialSelectedTimer() {
            if (_uiState.value.hasTimerStarted()) {
                viewModelScope.launch {
                    val preferences = userSettingsRepository.userSettingsFlow.first()
                    val key = intPreferencesKey(SELECTED_TIMER)
                    val storedSelectedTimer = preferences[key] ?: 0
                    _uiState.update { it.copy(selectedTimer = storedSelectedTimer) }
                }
            }
        }

        fun onEvent(event: Event) =
            when (event) {
                is Event.OnAlertConfirmClick -> handleOnAlertConfirmClick(event.newTabIndex)
                is Event.OnTabClicked -> handleTabClick(event.index)
                Event.OnAlertCancelClick -> _uiState.updateState { it.copy(isDialogOpen = false) }
                Event.OnRestartIconClick -> openDialog(_uiState.value.selectedTimer)
            }

        private fun handleOnAlertConfirmClick(newTabIndex: Int) {
            _uiState.updateState { it.copy(selectedTimer = newTabIndex, isDialogOpen = false) }
            val action = if (_uiState.value.hasTimerStarted()) ACTION_SERVICE_CLOSE else ACTION_SERVICE_RESET
            _uiEffects.trySend(Effect.TriggerForegroundService(action))
        }

        private fun handleTabClick(index: Int) {
            val state = _uiState.value
            if (state.selectedTimer == index) return
            if (state.hasTimerStarted()) {
                openDialog(index)
            } else {
                _uiState.updateState { it.copy(selectedTimer = index) }
                updateDataStore(index)
                _uiEffects.trySend(Effect.TriggerIntent(index))
            }
        }

        private fun openDialog(tabToConfirmIndex: Int) {
            _uiState.updateState { it.copy(isDialogOpen = true) }
            _uiEffects.trySend(Effect.TimerToBeConfirmed(tabToConfirmIndex))
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
