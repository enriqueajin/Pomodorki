package com.enriqueajin.pomidorki.data.countdown

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.enriqueajin.pomidorki.data.services.CountdownService
import com.enriqueajin.pomidorki.data.services.CountdownState
import com.enriqueajin.pomidorki.presentation.MainActivity
import com.enriqueajin.pomidorki.utils.Constants.CANCEL_REQUEST_CODE
import com.enriqueajin.pomidorki.utils.Constants.CLICK_REQUEST_CODE
import com.enriqueajin.pomidorki.utils.Constants.COUNTDOWN_STATE
import com.enriqueajin.pomidorki.utils.Constants.PAUSE_REQUEST_CODE
import com.enriqueajin.pomidorki.utils.Constants.RESET_REQUEST_CODE
import com.enriqueajin.pomidorki.utils.Constants.RESUME_REQUEST_CODE
import com.enriqueajin.pomidorki.utils.Constants.START_REQUEST_CODE
import com.enriqueajin.pomidorki.utils.Constants.TIMER_OVER
import com.enriqueajin.pomidorki.utils.Constants.TIME_OVER_REQUEST_CODE

object ServiceHelper {

    private val flag =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_IMMUTABLE
        else
            0

    fun clickPendingIntent(context: Context): PendingIntent {
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(COUNTDOWN_STATE, CountdownState.Started.name)
        }
        return PendingIntent.getActivity(
            context, CLICK_REQUEST_CODE, clickIntent, flag
        )
    }

    fun startPendingIntent(context: Context): PendingIntent {
        val startIntent = Intent(context, CountdownService::class.java).apply {
            putExtra(COUNTDOWN_STATE, CountdownState.Started.name)
        }
        return PendingIntent.getService(
            context, START_REQUEST_CODE, startIntent, flag
        )
    }

    fun pausePendingIntent(context: Context): PendingIntent {
        val stopIntent = Intent(context, CountdownService::class.java).apply {
            putExtra(COUNTDOWN_STATE, CountdownState.Paused.name)
        }
        return PendingIntent.getService(
            context, PAUSE_REQUEST_CODE, stopIntent, flag
        )
    }

    fun resumePendingIntent(context: Context): PendingIntent {
        val resumeIntent = Intent(context, CountdownService::class.java).apply {
            putExtra(COUNTDOWN_STATE, CountdownState.Started.name)
        }
        return PendingIntent.getService(
            context, RESUME_REQUEST_CODE, resumeIntent, flag
        )
    }

    fun cancelPendingIntent(context: Context): PendingIntent {
        val cancelIntent = Intent(context, CountdownService::class.java).apply {
            putExtra(COUNTDOWN_STATE, CountdownState.Closed.name)
        }
        return PendingIntent.getService(
            context, CANCEL_REQUEST_CODE, cancelIntent, flag
        )
    }

    fun resetPendingIntent(context: Context): PendingIntent {
        val resetIntent = Intent(context, CountdownService::class.java).apply {
            putExtra(COUNTDOWN_STATE, CountdownState.Reset.name)
        }
        return PendingIntent.getService(
            context, RESET_REQUEST_CODE, resetIntent, flag
        )
    }

    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, CountdownService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}