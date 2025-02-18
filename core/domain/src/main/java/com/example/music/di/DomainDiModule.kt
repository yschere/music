package com.example.music.di

import com.example.music.data.Dispatcher
import com.example.music.data.MusicDispatchers
import com.example.music.player.SongPlayerImpl
import com.example.music.player.SongPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainDiModule {
    @Provides
    @Singleton
    fun provideSongPlayer(
        @Dispatcher(MusicDispatchers.Main) mainDispatcher: CoroutineDispatcher
    ): SongPlayer = SongPlayerImpl(mainDispatcher)
}
