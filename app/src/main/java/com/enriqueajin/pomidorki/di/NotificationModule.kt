package com.enriqueajin.pomidorki.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.data.countdown.ServiceHelper
import com.enriqueajin.pomidorki.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.enriqueajin.pomidorki.utils.Constants.PAUSE_BUTTON_TITLE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
class NotificationModule {

    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(@ApplicationContext context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Remaining time")
            .setContentText("25:00")
            .setSmallIcon(R.drawable.filled_timer)
            .setOngoing(true)
            .addAction(0, PAUSE_BUTTON_TITLE, ServiceHelper.pausePendingIntent(context))
            .setContentIntent(ServiceHelper.clickPendingIntent(context))
    }

    @ServiceScoped
    @Provides
    fun providesNotificationManager(@ApplicationContext context: Context)
        = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}