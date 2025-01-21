/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.music.model

import com.example.music.data.database.model.Song
import java.time.Duration
import java.time.OffsetDateTime

/**
 * External data layer representation of a song.
 */
//TODO: across all the domain info pages, see if the properties need
//to be changed to var instead of val
data class SongInfo(
    val id: Long = 0,
    val title: String = "",
    val artistId: Long? = 0,
    val albumId: Long? = 0,
    val genreId: Long? = 0,
    val albumTrackNumber: Int? = 0,
    val duration: Duration? = null,
    val dateLastPlayed: OffsetDateTime? = null,
)

fun Song.asExternalModel(): SongInfo =
    SongInfo(
        id = id,
        title = title,
        artistId = artistId,
        albumId = albumId,
        genreId = genreId,
        albumTrackNumber = albumTrackNumber,
        duration = duration,
        dateLastPlayed = dateLastPlayed,
    )

