package com.enriqueajin.pomidorki.data.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.presentation.MainActivity

class StopwatchService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // Intent to launch the MainActivity
        val activityIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val flag = PendingIntent.FLAG_IMMUTABLE
        val activityPendIntent = PendingIntent.getActivity(this, 0, activityIntent, flag)

        when (intent?.action) {
            Action.START.toString() -> start(intent, activityPendIntent)
            Action.STOP.toString() -> stopSelf()
            Action.PAUSE.toString() -> onPause(intent, activityPendIntent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(intent: Intent, activityIntent: PendingIntent) {
        val flag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_IMMUTABLE
            else
                0

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            flag
        )

        val notification = NotificationCompat.Builder(this, "stopwatch_channel")
            .setSmallIcon(R.drawable.filled_timer)
            .setContentTitle("Stopwatch")
            .setContentText("Elapsed time: 25:00")
            .setContentIntent(activityIntent)
            .addAction(0, "PAUSE", pendingIntent)
            .build()
        startForeground(1, notification)
    }

    private fun onPause(intent: Intent, activityIntent: PendingIntent) {
        val flag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_IMMUTABLE
            else
                0

        val resumePendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            flag
        )
        val resetPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            intent,
            flag
        )
        val cancelPendingIntent = PendingIntent.getBroadcast(
            this,
            2,
            intent,
            flag
        )

        val notification = NotificationCompat.Builder(this, "stopwatch_channel")
            .setSmallIcon(R.drawable.filled_timer)
            .setContentTitle("Stopwatch")
            .setContentText("Elapsed time: 25:00")
            .setContentIntent(activityIntent)
            .addAction(0, "RESUME", resumePendingIntent)
            .addAction(0, "RESET", resetPendingIntent)
            .addAction(0, "CANCEL", cancelPendingIntent)
            .build()
        startForeground(2, notification)
    }
}