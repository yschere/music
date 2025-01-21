/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.music.data.di

import android.content.Context
import androidx.room.Room
import coil3.ImageLoader
import com.example.music.data.BuildConfig
import com.example.music.data.Dispatcher
import com.example.music.data.MusicDispatchers
import com.example.music.data.database.MusicDatabase
import com.example.music.data.database.dao.AlbumsDao
import com.example.music.data.database.dao.ArtistsDao
import com.example.music.data.database.dao.GenresDao
import com.example.music.data.database.dao.PlaylistsDao
import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.dao.TransactionRunner
import com.example.music.data.repository.AlbumStore
import com.example.music.data.repository.ArtistStore
import com.example.music.data.repository.GenreStore
import com.example.music.data.repository.LocalAlbumStore
import com.example.music.data.repository.LocalArtistStore
import com.example.music.data.repository.LocalGenreStore
import com.example.music.data.repository.LocalPlaylistStore
import com.example.music.data.repository.LocalSongStore
import com.example.music.data.repository.PlaylistStore
import com.example.music.data.repository.SongStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataDiModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(File(context.cacheDir, "http_cache"), (20 * 1024 * 1024).toLong()))
        .apply {
            if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
        }
        .build() //TODO do i still need this if I do not want my app to rely on network calls?

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MusicDatabase =
        Room.databaseBuilder(context, MusicDatabase::class.java, "data.db")
            // This is not recommended for normal apps, but the goal of this sample isn't to
            // showcase all of Room.
            // TODO why is this not recommended for normal apps?
            .fallbackToDestructiveMigration()
            .build()


    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader = ImageLoader.Builder(context)
        // Disable `Cache-Control` header support as some com.example.music.ui.album images disable disk caching.
        //.respectCacheHeaders(false)
        .build()

    @Provides
    @Singleton
    fun provideAlbumsDao(
        database: MusicDatabase
    ): AlbumsDao = database.albumsDao()

    @Provides
    @Singleton
    fun provideArtistsDao(
        database: MusicDatabase
    ): ArtistsDao = database.artistsDao()

    @Provides
    @Singleton
    fun provideGenresDao(
        database: MusicDatabase
    ): GenresDao = database.genresDao()

    @Provides
    @Singleton
    fun providePlaylistsDao(
        database: MusicDatabase
    ): PlaylistsDao = database.playlistsDao()

    @Provides
    @Singleton
    fun provideSongsDao(
        database: MusicDatabase
    ): SongsDao = database.songsDao()

    @Provides
    @Singleton
    fun provideTransactionRunner(
        database: MusicDatabase
    ): TransactionRunner = database.transactionRunnerDao()

//    @Provides
//    @Singleton
//    fun provideSyndFeedInput() = SyndFeedInput()

    @Provides
    @Dispatcher(MusicDispatchers.IO)
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(MusicDispatchers.Main)
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    //composerStore??
    //would use song-playlist-dao as localStore param

    @Provides
    @Singleton
    fun provideAlbumStore(
        albumDao: AlbumsDao,
        songDao: SongsDao,
    ): AlbumStore = LocalAlbumStore(
        albumDao = albumDao,
        songDao = songDao
    )

    @Provides
    @Singleton
    fun provideArtistStore(
        albumDao: AlbumsDao,
        artistDao: ArtistsDao,
        songDao: SongsDao,
    ): ArtistStore = LocalArtistStore(
        albumDao = albumDao,
        artistDao = artistDao,
        songDao = songDao
    )

    @Provides
    @Singleton
    fun provideGenreStore(
        genreDao: GenresDao,
        albumDao: AlbumsDao,
        artistDao: ArtistsDao,
        songDao: SongsDao,
    ): GenreStore = LocalGenreStore(
        genreDao = genreDao,
        albumDao = albumDao,
        artistDao = artistDao,
        songDao = songDao
    )

    @Provides
    @Singleton
    fun providePlaylistStore(
        playlistDao: PlaylistsDao
    ): PlaylistStore = LocalPlaylistStore(
        playlistDao = playlistDao
    )

    @Provides
    @Singleton
    fun provideSongStore(
        songDao: SongsDao
    ): SongStore = LocalSongStore(
        songDao = songDao
    )

}
