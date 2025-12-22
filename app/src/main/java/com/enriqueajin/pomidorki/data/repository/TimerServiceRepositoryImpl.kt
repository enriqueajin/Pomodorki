package com.enriqueajin.pomidorki.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.enriqueajin.pomidorki.data.model.PomodoroServiceData
import com.enriqueajin.pomidorki.data.services.CountdownService
import com.enriqueajin.pomidorki.domain.repository.TimerServiceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimerServiceRepositoryImpl @Inject constructor(
    private val context: Context
): TimerServiceRepository {

    private var countDownService: CountdownService? = null
    private var isBound by mutableStateOf(false)

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _selectedTimer = MutableStateFlow(0)
    val selectedTimer = _selectedTimer.asStateFlow()

    private val _serviceData = MutableStateFlow(PomodoroServiceData())
    override val serviceData = _serviceData.asStateFlow()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CountdownService.CountdownBinder
            countDownService = binder.getService()

            countDownService?.let { countdownService ->
                countdownService.initCountdown()
                coroutineScope.launch {
                    countdownService.serviceData.collect { data ->
                        _serviceData.value = data
                    }

                }
                coroutineScope.launch {
                    _selectedTimer.collect { selectedTimer ->
                        countdownService.updateSelectedTimer(selectedTimer)
                    }
                }
            }
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun bindTimerService() {
        Intent(context, CountdownService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun unbindTimerService() {
        if(isBound) {
            context.unbindService(connection)
            isBound = false
        }
    }

    override fun setSelectedTimer(selected: Int) {
        _selectedTimer.value = selected
    }
}