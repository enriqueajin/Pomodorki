package com.enriqueajin.pomidorki.domain.repository

import com.enriqueajin.pomidorki.data.services.CountdownState
import kotlinx.coroutines.flow.StateFlow

interface TimerServiceRepository {

    fun bindTimerService()
    fun unbindTimerService()
    fun getCurrentStatus(): StateFlow<CountdownState?>
    fun getTimeLeft(): StateFlow<Long?>
}