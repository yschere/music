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

import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import java.time.OffsetDateTime

/**
 * External data layer representation of an episode.
 */
//TODO: across all the domain info pages, see if the properties need
//to be changed to var instead of val
data class AlbumInfo(
    var id: Long = 0,
    var title: String = "",
    var albumArtistId: Long? = 0,
    var genreId: Long? = 0,
    var artwork: String? = "",
    var songCount: Int? = 0,
    var dateLastPlayed: OffsetDateTime? = null
)

fun Album.asExternalModel(): AlbumInfo =
    AlbumInfo(
        id = this.id,
        title = this.title,
        albumArtistId = this.albumArtistId?: 0,
        genreId = this.genreId ?: 0,
        artwork = this.artwork ?: "",
    )

fun AlbumWithExtraInfo.asExternalModel(): AlbumInfo =
    this.album.asExternalModel().copy(
        songCount = songCount ?: 0,
        dateLastPlayed = dateLastPlayed,
    )

/**
 * External data layer representation of a com.example.music.ui.album.

data class PodcastInfo(
val uri: String = "",
val title: String = "",
val author: String = "",
val imageUrl: String = "",
val description: String = "",
val isSubscribed: Boolean? = null,
val lastEpisodeDate: OffsetDateTime? = null,
)

fun Podcast.asExternalModel(): PodcastInfo =
PodcastInfo(
uri = this.uri,
title = this.title,
author = this.author ?: "",
imageUrl = this.imageUrl ?: "",
description = this.description ?: "",
)

fun PodcastWithExtraInfo.asExternalModel(): PodcastInfo =
this.com.example.music.ui.album.asExternalModel().copy(
isSubscribed = isFollowed,
lastEpisodeDate = lastEpisodeDate,
)
 */
