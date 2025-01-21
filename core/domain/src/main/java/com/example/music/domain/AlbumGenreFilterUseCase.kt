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

package com.example.music.domain

import com.example.music.data.repository.GenreStore
import com.example.music.model.AlbumGenreFilterResult
import com.example.music.model.GenreInfo
import com.example.music.model.asAlbumToSongInfo
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 *  A use case which returns top podcasts and matching episodes in a given Genre.
 */
/**
    TODO: Rework this for returning songs, artists, albums, genres(?) on a given category
 */
class AlbumGenreFilterUseCase @Inject constructor(
    private val genreStore: GenreStore
) {
    operator fun invoke(genre: GenreInfo?): Flow<AlbumGenreFilterResult> {
        if (genre == null) {
            return flowOf(AlbumGenreFilterResult())
        }

        val recentAlbumsFlow = genreStore.albumsInGenreSortedByLastPlayedSong(
            genre.id,
            limit = 10
        )

        val songsFlow = genreStore.songsAndAlbumsInGenre(
            genre.id,
            limit = 20
        )

        // Combine our flows and collect them into the view state StateFlow
        return combine(recentAlbumsFlow, songsFlow) { topAlbums, songs ->
            AlbumGenreFilterResult(
                topAlbums = topAlbums.map { it.asExternalModel() },
                songs = songs.map { it.asAlbumToSongInfo() }
            )
        }
    }
}
