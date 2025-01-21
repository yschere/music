/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.music.data.database

import androidx.room.Database
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
    version = 1,
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
}
