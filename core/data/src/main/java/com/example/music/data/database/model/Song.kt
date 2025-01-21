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
import java.time.Duration
import java.time.OffsetDateTime

/**
 * Entity for table songs / data class Song.
 * Used to contain base song information.
 * Columns album_artist_id and artwork not included.
 * Album_artist_id can either be from album or saved as song element,
 * not sure which is better.
 */

@Entity(
    tableName = "songs",
    indices = [
        Index("id", unique = true),
        Index("artist_id"),
        Index("album_id"),
        Index("genre_id"),
        Index("composer_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Artist::class,
            parentColumns = ["id"],
            childColumns = ["artist_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Album::class,
            parentColumns = ["id"],
            childColumns = ["album_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Genre::class,
            parentColumns = ["id"],
            childColumns = ["genre_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Composer::class,
            parentColumns = ["id"],
            childColumns = ["composer_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Song(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "artist_id") var artistId: Long? = null,
    //@ColumnInfo(name = "album_artist_id") val albumArtistId: Int? = null,
    @ColumnInfo(name = "album_id") var albumId: Long? = null,
    @ColumnInfo(name = "genre_id") var genreId: Long? = null,
    @ColumnInfo(name = "year") var year: Int? = null,
    @ColumnInfo(name = "album_track_number") var albumTrackNumber: Int? = null,
    @ColumnInfo(name = "lyrics") var lyrics: String? = null,
    //@ColumnInfo(name = "album_artwork), //there are songs with individual artwork
    @ColumnInfo(name = "composer_id") var composerId: Long? = null,
    @ColumnInfo(name = "date_added") var dateAdded: OffsetDateTime = OffsetDateTime.now(),
    @ColumnInfo(name = "date_modified") var dateModified: OffsetDateTime = OffsetDateTime.now(),
    @ColumnInfo(name = "date_last_played") var dateLastPlayed: OffsetDateTime? = null,
    @ColumnInfo(name = "duration") var duration: Duration? = null
)