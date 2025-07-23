package com.example.music.domain.di

import android.content.Context
import com.example.music.data.Dispatcher
import com.example.music.data.MusicDispatchers
import com.example.music.domain.util.MediaRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainDiModule {
    @Provides
    @Singleton
    fun provideMediaRepo(
        @ApplicationContext appContext: Context,
    ) = MediaRepo(appContext)
}
