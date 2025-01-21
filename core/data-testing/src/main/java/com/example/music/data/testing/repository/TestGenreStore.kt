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

package com.example.music.data.testing.repository

import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import com.example.music.data.repository.GenreStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * A [GenreStore] used for testing.
 */
class TestGenreStore : GenreStore {

    private val genresFlow = MutableStateFlow<List<Genre>>(emptyList())
    private val albumsInGenreFlow =
        MutableStateFlow<Map<Long, List<AlbumWithExtraInfo>>>(emptyMap())
    private val artistsInGenreFlow =
        MutableStateFlow<Map<Long, List<Artist>>>(emptyMap())
    private val songsInGenreFlow =
        MutableStateFlow<Map<Long, List<Song>>>(emptyMap())
    private val songsFromAlbums =
        MutableStateFlow<Map<Long, List<SongToAlbum>>>(emptyMap())
    private val songsFlow = MutableStateFlow<List<Song>>(emptyList())


    override fun genresSortedByAlbumCount(limit: Int): Flow<List<Genre>> =
        genresFlow

    override fun albumsInGenreSortedByLastPlayedSong(
        genreId: Long,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>> = albumsInGenreFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

    override fun artistsInGenreSortedBySongCount(
        genreId: Long,
        limit: Int
    ): Flow<List<Artist>> = artistsInGenreFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

    override fun songsInGenre(
        genreId: Long,
        limit: Int
    ): Flow<List<Song>> = songsInGenreFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

//    fun songsInGenresByLastPlayed(
//        genreIds: List<Long>,
//        limit: Int = Integer.MAX_VALUE
//    ): Flow<List<Song>>

//    fun songsInGenresBySongCount(
//        genreIds: List<Long>,
//        limit: Int = Integer.MAX_VALUE
//    ): Flow<List<Song>>

    //equivalent of categories episodesFromPodcastsInCategory
    override fun songsAndAlbumsInGenre(
        genreId: Long,
        limit: Int,
    ): Flow<List<SongToAlbum>> =
        songsFlow.map { songs ->
            songs.filter {
                it.genreId == genreId
            }.map { s ->
                SongToAlbum().apply {
                    song = s
                }
            }
        }

    override suspend fun addGenre(genre: Genre): Long = -1

    override fun getGenreByName(name: String): Flow<Genre?> = genresFlow.map { genres ->
        genres.first {it.name == name}
    }

    override fun getGenreById(id: Long): Flow<Genre?> = genresFlow.map { genres ->
        genres.first {it.id == id}
    }

    override suspend fun isEmpty(): Boolean =
        genresFlow.first().isEmpty()


    /**
     * Test-only API for setting the list of genres backed by this [TestGenreStore].
     */
    fun setGenres(genres: List<Genre>) {
        genresFlow.value = genres
    }


    /**
     * Test-only API for setting the list of podcasts in a category backed by this
     * [TestGenreStore].
     */
    fun setAlbumsInGenre(genreId: Long, albumsInGenre: List<AlbumWithExtraInfo>) {
        albumsInGenreFlow.update {
            it + Pair(genreId, albumsInGenre)
        }
    }


    /**
     * Test-only API for setting the list of podcasts in a category backed by this
     * [TestGenreStore].
     */
    fun setSongsFromAlbum(genreId: Long, albumsInGenre: List<SongToAlbum>) {
        songsFromAlbums.update {
            it + Pair(genreId, albumsInGenre)
        }
    }

}
