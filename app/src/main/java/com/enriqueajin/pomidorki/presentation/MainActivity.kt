package com.enriqueajin.pomidorki.presentation

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.enriqueajin.pomidorki.data.services.CountdownService
import com.enriqueajin.pomidorki.presentation.home.TimerScreenViewModel
import com.enriqueajin.pomidorki.presentation.navigation.MainGraph
import com.enriqueajin.pomidorki.presentation.ui.theme.PomidorkiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: TimerScreenViewModel by viewModels()
    private var isBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomidorkiTheme {
                MainGraph()
            }
        }
    }

    private val connection =
        object : ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                service: IBinder?,
            ) {
                val binder = service as CountdownService.CountdownBinder
                val countdownService = binder.getService()
                viewModel.onServiceConnected(countdownService)
                isBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                viewModel.onServiceDisconnected()
                isBound = false
            }
        }

    override fun onStart() {
        super.onStart()
        Intent(this, CountdownService::class.java).also {
            bindService(it, connection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}
