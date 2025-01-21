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
import java.util.Objects

//album with extra info ??
//could combine album and last accessed date
/**
 * Class object PlaylistExtraInfo contains album object
 * and the count of songs within the album.
 * Used to be last accessed date, but count is easier to test for now.
 */

class ArtistWithGenre {
    @Embedded
    lateinit var artist: Artist

    @ColumnInfo(name = "genre_name")
    var genreName: String? = ""

    /**
     * Allow consumers to destruct this class
     */
    operator fun component1() = artist
    operator fun component2() = genreName

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is ArtistWithGenre -> {
            artist == other.artist &&
                    genreName == other.genreName
        }
        else -> false
    }
    override fun hashCode(): Int = Objects.hash(artist, genreName)

}