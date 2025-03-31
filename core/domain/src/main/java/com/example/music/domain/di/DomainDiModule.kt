package com.example.music.di

import android.content.Context
import com.example.music.data.Dispatcher
import com.example.music.data.MusicDispatchers
import com.example.music.player.SongPlayerImpl
import com.example.music.player.SongPlayer
//import com.example.music.store.MediaRetriever
//import com.example.music.store.MediaRetrieverImpl
//import com.example.music.store.Resolver
//import com.example.music.store.MediaProvider
//import com.example.music.store.MediaProviderImpl
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
    fun provideSongPlayer(
        @Dispatcher(MusicDispatchers.Main) mainDispatcher: CoroutineDispatcher
    ): SongPlayer = SongPlayerImpl(mainDispatcher)

    /*@Provides
    @Singleton
    fun provideResolver(
        @ApplicationContext appContext: Context,
        //@Dispatcher(MusicDispatchers.Main) mainDispatcher: CoroutineDispatcher
    ): Resolver = Resolver(appContext)
    //): ContentResolver = Resolver(appContext, mainDispatcher)*/

}
