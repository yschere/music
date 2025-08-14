package com.example.music.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
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
import javax.inject.Singleton

private const val APP_PREFERENCES = "app_preferences.pb"

@Module
@InstallIn(SingletonComponent::class)
object DataDiModule {

    @Provides
    @Dispatcher(MusicDispatchers.IO)
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(MusicDispatchers.Main)
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

//    @Provides
//    @Singleton
//    fun provideOkHttpClient(
//        @ApplicationContext context: Context
//    ): OkHttpClient = OkHttpClient.Builder()
//        .cache(Cache(File(context.cacheDir, "http_cache"), (20 * 1024 * 1024).toLong()))
//        .apply {
//            if (BuildConfig.DEBUG) eventListenerFactory(LoggingEventListener.Factory())
//        }
//        .build() // Question: do i still need this if I do not want my app to rely on network calls?

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MusicDatabase =
        Room.databaseBuilder(context, MusicDatabase::class.java, "music.db")
            // This is not recommended for normal apps, but the goal of this sample isn't to
            // showcase all of Room.
            // Question: why is this not recommended for normal apps?

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

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        /*corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        ),
        migrations = listOf(
            object : DataMigration<Preferences> {
                override suspend fun cleanUp() { TODO("clean up Not yet implemented") }
                override suspend fun migrate(currentData: Preferences): Preferences { TODO("migrate Not yet implemented") }
                override suspend fun shouldMigrate(currentData: Preferences): Boolean { TODO("should migrate Not yet implemented") }
            },
        ),*/
        corruptionHandler = null,
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { appContext.preferencesDataStoreFile(APP_PREFERENCES) }
    )

    @Provides
    @Singleton
    fun provideAppPreferencesRepo(
        dataStore: DataStore<Preferences>
    ) = AppPreferencesRepo(
        dataStore = dataStore
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
        songDao: SongsDao,
    ): GenreRepo = GenreRepoImpl(
        genreDao = genreDao,
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
