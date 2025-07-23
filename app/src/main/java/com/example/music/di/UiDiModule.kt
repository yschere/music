package com.example.music.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.music.data.Dispatcher
import com.example.music.data.MusicDispatchers
import com.example.music.service.SongController
import com.example.music.service.SongControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UiDiModule {
    @Singleton
    @Provides
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @OptIn(UnstableApi::class)
    @Provides
    //@Singleton
    fun providePlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes,
    ): Player = ExoPlayer.Builder(context, DefaultMediaSourceFactory(context))
        .setAudioAttributes(audioAttributes, true)
        .setWakeMode(C.WAKE_MODE_LOCAL)
        .setHandleAudioBecomingNoisy(true)
        .build()

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideSongController(
        @ApplicationContext context: Context,
        @Dispatcher(MusicDispatchers.Main) mainDispatcher: CoroutineDispatcher
    ): SongController = SongControllerImpl(context, mainDispatcher)
}