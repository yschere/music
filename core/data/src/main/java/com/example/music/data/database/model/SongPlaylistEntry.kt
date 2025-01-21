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

package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

//TODO: See if below is the correct iteration of song-playlist
//Associative entity between songs and playlists
//Intended purpose: to have the playlists song data storage here
//contains playlistId, songId, playlistTrackNumber

/**
 * Entity containing table song_playlist_entries / data class SongPlaylistEntry.
 * Used to contain many-to-many relationship between songs and playlists.
 */

@Entity(
    tableName = "song_playlist_entries",
    //primaryKeys = ["playlist_id", "song_id"],
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Song::class,
            parentColumns = ["id"],
            childColumns = ["song_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("playlist_id"),
        Index("song_id")
    ]
)

data class SongPlaylistEntry(
    //singular id primary key might not be necessary
    //could use combo playlistId, songId as primary
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "playlist_id") var playlistId: Long,
    @ColumnInfo(name = "song_id") var songId: Long,
    @ColumnInfo(name = "playlist_track_number") var playlistTrackNumber: Int
)
