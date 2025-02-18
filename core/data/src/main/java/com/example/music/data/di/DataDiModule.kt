package com.example.music.data.di

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import coil3.ImageLoader
import com.example.music.data.Dispatcher
import com.example.music.data.MusicDispatchers
import com.example.music.data.database.MusicDatabase
import com.example.music.data.database.dao.AlbumsDao
import com.example.music.data.database.dao.ArtistsDao
import com.example.music.data.database.dao.ComposersDao
import com.example.music.data.database.dao.GenresDao
import com.example.music.data.database.dao.PlaylistsDao
import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.dao.TransactionRunner
import com.example.music.data.repository.AlbumRepo
import com.example.music.data.repository.ArtistRepo
import com.example.music.data.repository.GenreRepo
import com.example.music.data.repository.AlbumRepoImpl
import com.example.music.data.repository.AppPreferences
import com.example.music.data.repository.AppPreferencesRepo
import com.example.music.data.repository.ArtistRepoImpl
import com.example.music.data.repository.ComposerRepo
import com.example.music.data.repository.ComposerRepoImpl
import com.example.music.data.repository.GenreRepoImpl
import com.example.music.data.repository.PlaylistRepoImpl
import com.example.music.data.repository.SongRepoImpl
import com.example.music.data.repository.PlaylistRepo
import com.example.music.data.repository.SongRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import java.io.File
import javax.inject.Singleton

private const val APP_PREFERENCES = "app_preferences"

@Module
@InstallIn(SingletonComponent::class)
object DataDiModule {

//    @Provides
//    @Singleton
//    fun provideOkHttpClient(
//        @ApplicationContext context: Context
//    ): OkHttpClient = OkHttpClient.Builder()
//        .cache(Cache(File(context.cacheDir, "http_cache"), (20 * 1024 * 1024).toLong()))
//        .apply {
//            if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
//        }
//        .build() //TODO do i still need this if I do not want my app to rely on network calls?

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MusicDatabase =
        Room.databaseBuilder(context, MusicDatabase::class.java, "music.db")
            // This is not recommended for normal apps, but the goal of this sample isn't to
            // showcase all of Room.
            // TODO why is this not recommended for normal apps?

            .fallbackToDestructiveMigration()
            .createFromAsset("preview_data.db")
            //.fallbackToDestructiveMigration()
            .build()

    /* //coil image loader
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader = ImageLoader.Builder(context)
        // Disable `Cache-Control` header support as some 'podcast' images disable disk caching.
        //.respectCacheHeaders(false)
        .build()*/

    /* //original version of preferences data store
    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(
                object : DataMigration<Preferences> {
                    override suspend fun cleanUp() { TODO("Not yet implemented") }
                    override suspend fun migrate(currentData: Preferences): Preferences { TODO("Not yet implemented") }
                    override suspend fun shouldMigrate(currentData: Preferences): Boolean { TODO("Not yet implemented") }
                },
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(CURRENT_PREFERENCES) }
        )
    }*/

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
//        corruptionHandler = ReplaceFileCorruptionHandler(
//            produceNewData = { emptyPreferences() }
//        ),
//        migrations = listOf(
//            object : DataMigration<Preferences> {
//                override suspend fun cleanUp() { TODO("clean up Not yet implemented") }
//                override suspend fun migrate(currentData: Preferences): Preferences { TODO("migrate Not yet implemented") }
//                override suspend fun shouldMigrate(currentData: Preferences): Boolean { TODO("should migrate Not yet implemented") }
//            },
//        ),
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { appContext.preferencesDataStoreFile(APP_PREFERENCES) }
    )

    @Provides
    @Singleton
    fun provideAppPreferencesRepo(
        dataStore: DataStore<Preferences>,
        @ApplicationContext appContext: Context
    ) = AppPreferencesRepo(
        dataStore, appContext
    )

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
    fun provideComposersDao(
        database: MusicDatabase
    ): ComposersDao = database.composersDao()

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

    //composerRepo??
    //would use song-playlist-dao as localStore param

    @Provides
    @Singleton
    fun provideAlbumRepo(
        albumDao: AlbumsDao,
        songDao: SongsDao,
    ): AlbumRepo = AlbumRepoImpl(
        albumDao = albumDao,
        songDao = songDao
    )

    @Provides
    @Singleton
    fun provideArtistRepo(
        albumDao: AlbumsDao,
        artistDao: ArtistsDao,
        songDao: SongsDao,
    ): ArtistRepo = ArtistRepoImpl(
        artistDao = artistDao,
        albumDao = albumDao,
        songDao = songDao
    )

    @Provides
    @Singleton
    fun provideComposerRepo(
        composerDao: ComposersDao,
        songDao: SongsDao
    ): ComposerRepo = ComposerRepoImpl(
        composerDao = composerDao,
        songDao = songDao
    )

    @Provides
    @Singleton
    fun provideGenreRepo(
        genreDao: GenresDao,
        albumDao: AlbumsDao,
        artistDao: ArtistsDao,
        songDao: SongsDao,
    ): GenreRepo = GenreRepoImpl(
        genreDao = genreDao,
        albumDao = albumDao,
        artistDao = artistDao,
        songDao = songDao
    )

    @Provides
    @Singleton
    fun providePlaylistRepo(
        playlistDao: PlaylistsDao
    ): PlaylistRepo = PlaylistRepoImpl(
        playlistDao = playlistDao
    )

    @Provides
    @Singleton
    fun provideSongRepo(
        songDao: SongsDao
    ): SongRepo = SongRepoImpl(
        songDao = songDao
    )



}
