package com.enriqueajin.pomidorki.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Binder
import android.os.Build
import androidx.core.app.NotificationCompat
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.data.countdown.CountDownPomodoro
import com.enriqueajin.pomidorki.data.countdown.ServiceHelper
import com.enriqueajin.pomidorki.data.model.PomodoroServiceData
import com.enriqueajin.pomidorki.domain.repository.UserSettingsRepository
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_CLOSE
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_IDLE
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_PAUSE
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_RESET
import com.enriqueajin.pomidorki.utils.Constants.ACTION_SERVICE_START
import com.enriqueajin.pomidorki.utils.Constants.CLOSE_BUTTON_TITLE
import com.enriqueajin.pomidorki.utils.Constants.COUNTDOWN_STATE
import com.enriqueajin.pomidorki.utils.Constants.NOTIFICATION_TICK_CHANNEL_ID
import com.enriqueajin.pomidorki.utils.Constants.NOTIFICATION_TICK_CHANNEL_NAME
import com.enriqueajin.pomidorki.utils.Constants.NOTIFICATION_TICK_ID
import com.enriqueajin.pomidorki.utils.Constants.NOTIFICATION_TIMER_RINGTONE_CHANNEL_ID
import com.enriqueajin.pomidorki.utils.Constants.NOTIFICATION_TIMER_RINGTONE_CHANNEL_NAME
import com.enriqueajin.pomidorki.utils.Constants.NOTIFICATION_TIMER_RINGTONE_ID
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CountdownService : Service() {

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var userSettingsRepository: UserSettingsRepository

    @Inject
    lateinit var countdownTimer: CountDownPomodoro

    private val binder = CountdownBinder()

    private val _serviceData = MutableStateFlow(PomodoroServiceData())
    val serviceData = _serviceData.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun initCountdown() {
        coroutineScope.launch {
            countdownTimer.timeLeft.collect { timeLeft ->
                if(timeLeft > 0L) {
                    updateServiceData(timeLeft = timeLeft)
                    updateNotification(timeLeft)
                } else {
                    notificationManager.cancel(NOTIFICATION_TICK_ID)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    notifyTimerOver()
                }
            }
        }
    }

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
                updateServiceData(currentState = CountdownState.Idle)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        createNotificationChannels()
        startForeground(
            NOTIFICATION_TICK_ID,
            notificationBuilder.build()
        )
    }

    private fun updateServiceData(
        currentState: CountdownState? = null,
        timeLeft: Long? = null,
        initialMillis: Long? = null,
    ) {
        _serviceData.value = _serviceData.value.copy(
            currentState = currentState ?: _serviceData.value.currentState,
            timeLeft = timeLeft ?: _serviceData.value.timeLeft,
            initialMillis = initialMillis ?: _serviceData.value.initialMillis
        )
    }

    private fun createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Notification channel used for timer onTick updates & action buttons
            val timerTickChannel = NotificationChannel(
                NOTIFICATION_TICK_CHANNEL_ID,
                NOTIFICATION_TICK_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

            // Notification channel used when the timer is over
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val timerEndsChannel = NotificationChannel(
                NOTIFICATION_TIMER_RINGTONE_CHANNEL_ID,
                NOTIFICATION_TIMER_RINGTONE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(soundUri, audioAttributes)
                enableVibration(true)
            }
            notificationManager.createNotificationChannels(
                listOf(timerTickChannel, timerEndsChannel)
            )
        }
    }

    private fun startTimer() {
        updateServiceData(currentState = CountdownState.Started)
        countdownTimer.start()
    }

    private fun pauseTimer() {
        updateServiceData(currentState = CountdownState.Paused)
        countdownTimer.pause()
    }

    private fun resetTimer() {
        updateServiceData(currentState = CountdownState.Reset)
        countdownTimer.reset()
    }

    private fun closeTimer() {
        notificationManager.cancel(NOTIFICATION_TICK_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun updateSelectedTimer(selectedTimer: Int) {
        countdownTimer.updateSelectedTimer(selectedTimer)
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
        val notification = notificationBuilder
            .setContentText(timeLeft.formatTime())
            .setContentIntent(ServiceHelper.clickPendingIntent(this))
            .build()

        notificationManager.notify(NOTIFICATION_TICK_ID, notification)
    }

    private fun notifyTimerOver() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_TIMER_RINGTONE_CHANNEL_ID)
            .setContentTitle("Pomodoro ended")
            .setContentText("Timer is over")
            .setContentIntent(ServiceHelper.clickPendingIntent(this))
            .setSmallIcon(R.drawable.filled_timer)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .clearActions()
            .build()

        notificationManager.notify(NOTIFICATION_TIMER_RINGTONE_ID, notification)
    }

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
    notificationManager.notify(NOTIFICATION_TICK_ID, notificationBuilder.build())
}

enum class CountdownState {
    Idle,
    Started,
    Paused,
    Reset,
    Closed
}