package com.enriqueajin.pomidorki.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.enriqueajin.pomidorki.data.countdown.ServiceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TimerScreenViewModel @Inject constructor(
//    @ApplicationContext var countdownService: CountdownService
): ViewModel() {

//    private val _remainingTime = MutableStateFlow(countdownService.remainingTime)
//    val remainingTime: StateFlow<String> = _remainingTime.asStateFlow()

    private val _state = MutableStateFlow(TimerScreenState())
    val state = _state.asStateFlow()

    fun onEvent(event: TimerScreenEvent) {
        when(event) {
            is TimerScreenEvent.TriggerPomodoro -> triggerPomodoro(event.context, event.action)
        }
    }

    private fun triggerPomodoro(context: Context, action: String) {
        ServiceHelper.triggerForegroundService(context, action)
    }
}