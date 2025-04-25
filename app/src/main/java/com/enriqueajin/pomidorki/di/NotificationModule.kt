package com.enriqueajin.pomidorki.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.utils.Constants.NOTIFICATION_TICK_CHANNEL_ID
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
        return NotificationCompat.Builder(context, NOTIFICATION_TICK_CHANNEL_ID)
                .setContentTitle("Remaining time")
                .setContentText("25:00")
                .setSmallIcon(R.drawable.filled_timer)

    }

    @ServiceScoped
    @Provides
    fun providesNotificationManager(@ApplicationContext context: Context)
        = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}