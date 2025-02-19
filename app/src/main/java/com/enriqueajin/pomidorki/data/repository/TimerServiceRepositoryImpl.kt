package com.enriqueajin.pomidorki.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.enriqueajin.pomidorki.data.services.CountdownService
import com.enriqueajin.pomidorki.data.services.CountdownState
import com.enriqueajin.pomidorki.domain.repository.TimerServiceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimerServiceRepositoryImpl @Inject constructor(
    private val context: Context
): TimerServiceRepository {

    private var countDownService: CountdownService? = null
    private var isBound by mutableStateOf(false)

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _currentState = MutableStateFlow<CountdownState?>(null)
    private val _timeLeft = MutableStateFlow<Long?>(null)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CountdownService.CountdownBinder
            countDownService = binder.getService()

            countDownService?.let {
                coroutineScope.launch {
                    it.currentState.collect { state ->
                        _currentState.value = state
                    }
                }
                coroutineScope.launch {
                    it.getTimeLeft().collect { timeLeft ->
                        _timeLeft.value = timeLeft
                    }
                }
            }
            Log.i("TAG", "onServiceConnected: ${countDownService?.currentState}")
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

    override fun getCurrentStatus(): StateFlow<CountdownState?> = _currentState.asStateFlow()
    override fun getTimeLeft(): StateFlow<Long?> =_timeLeft.asStateFlow()
}