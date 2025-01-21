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
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

/**
 * Entity for table playlists / data class Playlist.
 * Used to contain base playlist information.
 * Columns 'date_last_played' and 'artwork' not included yet.
 */

@Entity(tableName = "playlists",
    indices = [
        Index("id", unique = true)
    ]
)

data class Playlist(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String? = null,
    @ColumnInfo(name = "creation_date") var creationDate: OffsetDateTime,
    @ColumnInfo(name = "date_last_accessed") var dateLastAccessed: OffsetDateTime,
    //@ColumnInfo(name = "date_last_played") var dateLastPlayed: OffsetDateTime
    //@ColumnInfo(name = "artwork") val artwork: Bitmap?
)