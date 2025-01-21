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

//playlist with extra info ??
//could combine playlist and last accessed date
/**
 * Class object PlaylistExtraInfo contains playlist object
 * and the count of songs within the playlist.
 * Used to be last accessed date, but count is easier to test for now.
 */

class PlaylistWithExtraInfo {
    @Embedded
    lateinit var playlist: Playlist

    @ColumnInfo(name = "date_last_played")
    var dateLastPlayed: OffsetDateTime? = null

    @ColumnInfo(name = "count")
    var count: Int? = 0

    /**
     * Allow consumers to destruct this class
     */
    operator fun component1() = playlist
    operator fun component2() = dateLastPlayed
    operator fun component3() = count

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is PlaylistWithExtraInfo -> {
            playlist == other.playlist &&
                    dateLastPlayed == other.dateLastPlayed &&
                    count == other.count
        }
        else -> false
    }
    override fun hashCode(): Int = Objects.hash(playlist, dateLastPlayed, count)

}