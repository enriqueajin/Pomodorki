package com.enriqueajin.pomidorki.di

import android.content.Context
import com.enriqueajin.pomidorki.data.repository.TimerServiceRepositoryImpl
import com.enriqueajin.pomidorki.domain.repository.TimerServiceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideContext(@ApplicationContext appContext: Context) = appContext

    @Provides
    @Singleton
    fun provideTimerServiceRepository(
        @ApplicationContext appContext: Context
    ): TimerServiceRepository = TimerServiceRepositoryImpl(appContext)
}