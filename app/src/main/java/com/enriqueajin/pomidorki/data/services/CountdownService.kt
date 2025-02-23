package com.enriqueajin.pomidorki.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import androidx.core.app.NotificationCompat
import com.enriqueajin.pomidorki.data.countdown.CountDownPomodoro
import com.enriqueajin.pomidorki.data.countdown.ServiceHelper
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_CLOSE
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_IDLE
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_PAUSE
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_RESET
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_START
import com.enriqueajin.pomidorki.utils.Constants.CLOSE_BUTTON_TITLE
import com.enriqueajin.pomidorki.utils.Constants.COUNTDOWN_STATE
import com.enriqueajin.pomidorki.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.enriqueajin.pomidorki.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.enriqueajin.pomidorki.utils.Constants.NOTIFICATION_ID
import com.enriqueajin.pomidorki.utils.Constants.PAUSE_BUTTON_TITLE
import com.enriqueajin.pomidorki.utils.Constants.RESET_BUTTON_TITLE
import com.enriqueajin.pomidorki.utils.Constants.RESUME_BUTTON_TITLE
import com.enriqueajin.pomidorki.utils.Constants.START_BUTTON_TITLE
import com.enriqueajin.pomidorki.utils.TimeFormatter.formatTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CountdownService: Service() {

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    private var countdownTimer = CountDownPomodoro(totalMinutes = 26L, context = this)

    private val binder = CountdownBinder()

    private val _currentState: MutableStateFlow<CountdownState> = MutableStateFlow(CountdownState.Idle)
    val currentState = _currentState.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        val action =
            intent?.action ?: // when triggered by the UI
            intent?.getStringExtra(COUNTDOWN_STATE) // When trigger by the notification

        println("Intent: ${intent?.action}")
        println("Intent: ${intent?.getStringExtra(COUNTDOWN_STATE)}")

        when(action) {
            CountdownState.Started.name, ACTION_SERVICE_START -> {
                startForegroundService()
                setStartedActions()
                startTimer()
                coroutineScope.launch {
                    countdownTimer.timeLeft.collect { timeLeft ->
                        updateNotification(timeLeft)
                    }
                }
            }
            CountdownState.Paused.name, ACTION_SERVICE_PAUSE -> {
                setPausedActions()
                pauseTimer()
            }
            CountdownState.Reset.name, ACTION_SERVICE_RESET -> {
                setResetActions()
                resetTimer()
            }
            CountdownState.Closed.name, ACTION_SERVICE_CLOSE -> {
                resetTimer()
                closeTimer()
            }
            CountdownState.Idle.name, ACTION_SERVICE_IDLE -> {
                _currentState.value = CountdownState.Idle
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startTimer() {
        _currentState.value = CountdownState.Started
        countdownTimer.start()
    }

    private fun pauseTimer() {
        _currentState.value = CountdownState.Paused
        countdownTimer.pause()
    }

    private fun resetTimer() {
        _currentState.value = CountdownState.Reset
        countdownTimer.reset()
    }

    private fun closeTimer() {
        _currentState.value = CountdownState.Idle
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun setStartedActions() {
        val actionList = listOf(
            NotificationAction(
                PAUSE_BUTTON_TITLE,
                ServiceHelper.pausePendingIntent(this)
            ),
        )
        addNotificationActions(
            actionList = actionList,
            notificationBuilder = notificationBuilder,
            notificationManager = notificationManager
        )
    }

    private fun setPausedActions() {
        val actionList = listOf(
            NotificationAction(
                RESUME_BUTTON_TITLE,
                ServiceHelper.resumePendingIntent(this)
            ),
            NotificationAction(
                RESET_BUTTON_TITLE,
                ServiceHelper.resetPendingIntent(this)
            ),
            NotificationAction(
                CLOSE_BUTTON_TITLE,
                ServiceHelper.cancelPendingIntent(this)
            ),
        )
        addNotificationActions(
            actionList = actionList,
            notificationBuilder = notificationBuilder,
            notificationManager = notificationManager
        )
    }

    private fun setResetActions() {
        val actionList = listOf(
            NotificationAction(
                START_BUTTON_TITLE,
                ServiceHelper.startPendingIntent(this)
            ),
            NotificationAction(
                CLOSE_BUTTON_TITLE,
                ServiceHelper.cancelPendingIntent(this)
            ),
        )
        addNotificationActions(
            actionList = actionList,
            notificationBuilder = notificationBuilder,
            notificationManager = notificationManager
        )
    }

    private fun updateNotification(timeLeft: Long) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                timeLeft.formatTime()
            ).build()
        )
    }

    fun getTimeLeft(): StateFlow<Long> = countdownTimer.timeLeft

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    inner class CountdownBinder: Binder() {
        fun getService(): CountdownService = this@CountdownService
    }
}

data class NotificationAction(
    val title: String,
    val intent: PendingIntent,
)

private fun addNotificationActions(
    actionList: List<NotificationAction>,
    notificationBuilder: NotificationCompat.Builder,
    notificationManager: NotificationManager
) {
    notificationBuilder.mActions.clear()
    actionList.forEachIndexed { index, action ->
        notificationBuilder.mActions.add(
            index,
            NotificationCompat.Action(
                0,
                action.title,
                action.intent
            )
        )
    }
    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
}

enum class CountdownState {
    Idle,
    Started,
    Paused,
    Reset,
    Closed
}