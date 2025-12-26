package com.enriqueajin.pomidorki.domain.repository

import com.enriqueajin.pomidorki.data.model.PomodoroServiceData
import kotlinx.coroutines.flow.StateFlow

interface TimerServiceRepository {

    fun bindTimerService()
    fun unbindTimerService()
    fun setSelectedTimer(selected: Int)
    fun getServiceData(): StateFlow<PomodoroServiceData>
}