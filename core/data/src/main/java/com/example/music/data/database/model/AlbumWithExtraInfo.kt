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
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

//album with extra info ??
//could combine album and last accessed date
/**
 * Class object PlaylistExtraInfo contains album object
 * and the count of songs within the album.
 * Used to be last accessed date, but count is easier to test for now.
 */

class AlbumWithExtraInfo {
    @Embedded
    lateinit var album: Album

    @ColumnInfo(name = "song_count")
    var songCount: Int? = 0

    @ColumnInfo(name = "date_last_played") //TODO: changed from last_played to count for now
    var dateLastPlayed: OffsetDateTime? = null

    /**
     * Allow consumers to destruct this class
     */
    operator fun component1() = album
    operator fun component2() = songCount
    operator fun component3() = dateLastPlayed

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is AlbumWithExtraInfo -> {
            album == other.album &&
                    songCount == other.songCount &&
                    dateLastPlayed == other.dateLastPlayed
        }
        else -> false
    }
    override fun hashCode(): Int = Objects.hash(album, songCount, dateLastPlayed)

}