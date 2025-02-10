package com.example.music.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.music.data.database.dao.AlbumsDao
import com.example.music.data.database.dao.ArtistsDao
import com.example.music.data.database.dao.GenresDao
import com.example.music.data.database.dao.PlaylistsDao
import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.dao.TransactionRunnerDao
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.Composer
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongPlaylistEntry

/**
 * The [RoomDatabase] we use in this app.
 */
@Database(
    entities = [
        Album::class,
        Artist::class,
        Composer::class,
        Genre::class,
        Playlist::class,
        Song::class,
        SongPlaylistEntry::class
    ],
    version = 3,
    exportSchema = false
)

@TypeConverters(DateTimeTypeConverters::class)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun albumsDao(): AlbumsDao
    abstract fun artistsDao(): ArtistsDao
    abstract fun genresDao(): GenresDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun songsDao(): SongsDao
    abstract fun transactionRunnerDao(): TransactionRunnerDao

//    companion object {
//        @Volatile
//        private var INSTANCE: MusicDatabase? = null
//
//        fun getDatabase(
//            context: Context
//        ): MusicDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context,
//                    MusicDatabase::class.java,
//                    "music_database"
//                )
//                    .createFromAsset("database/preview_data.db")
//                    .build()
//                INSTANCE = instance
//
//                instance
//            }
//
//        }
//    }
}

//
//@TypeConverters(DateTimeTypeConverters::class)
//abstract class MusicDatabase : RoomDatabase {
//    abstract fun songsDao(): SongsDao
//    private lateinit var dbInstance: MusicDatabase
//
//    fun getDatabase(Context context): MusicDatabase {
//        if (dbInstance == null) {
//            synchronized (MusicDatabase.class) {
//                if (dbInstance == null) {
//                    dbInstance = Room.databaseBuilder(context.getApplicationContext(),
//                        MusicDatabase.class, DbConfig.ROOM_DB_NAME)
//                    .build()
//                }
//            }
//        }
//    return dbInstance
//}
