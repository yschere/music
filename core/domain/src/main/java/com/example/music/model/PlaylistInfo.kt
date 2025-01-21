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

import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import java.time.OffsetDateTime

/**
 * External data layer representation of an episode.
 */
//TODO: across all the domain info pages, see if the properties need
//to be changed to var instead of val
data class PlaylistInfo(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val creationDate: OffsetDateTime? = null,
    val dateLastPlayed: OffsetDateTime? = null,
    val count: Int? = 0
)

fun Playlist.asExternalModel(): PlaylistInfo =
    PlaylistInfo(
        id = this.id,
        name = this.name,
        description = this.description ?: "",
        creationDate = this.creationDate
    )

fun PlaylistWithExtraInfo.asExternalModel(): PlaylistInfo =
    this.playlist.asExternalModel().copy(
        dateLastPlayed = this.dateLastPlayed,
        count = this.count
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
