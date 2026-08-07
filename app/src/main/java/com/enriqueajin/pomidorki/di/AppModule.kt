package com.enriqueajin.pomidorki.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideContext(
        @ApplicationContext appContext: Context,
    ) = appContext

    @Provides
    @Named("countdown")
    fun provideCountdownDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
