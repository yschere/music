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

import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import com.example.music.data.repository.SongStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * A [SongStore] used for testing.
 */
class TestSongStore : SongStore {

    private val songsFlow = MutableStateFlow<List<Song>>(emptyList())
    private val songsListFlow =
        MutableStateFlow<Map<Long, List<Song>>>(emptyMap())
    //need private val for all SongStore properties
    //need override functions for all the SongStore methods

    //methods this should have
    //getSongById
    //getSongByName?
    //getSongsInAlbum
    //getSongsByArtist
    //getSongsByGenre
    //getSongsInPlaylist
    //addSong? addSongs?
    //isEmpty (checks the songsFlow)

    override fun getSongById(id: Long): Flow<Song> =
        songsFlow.map { songs ->
            songs.first { it.id == id }
        }

    override fun getSongByTitle(title: String): Flow<Song> =
        songsFlow.map { songs ->
            songs.first { it.title == title }
        }

    override fun songAndAlbumBySongId(songId: Long): Flow<SongToAlbum> =
        songsFlow.map { songs ->
            val s = songs.first {
                it.id == songId
            }
            SongToAlbum().apply {
                song = s
                _albums = emptyList()
            }
        }

    override fun mostRecentSongs(
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun getSongsByArtistId(
        artistId: Long,
        limit: Int,
    ): Flow<List<Song>> = songsFlow /*songsListFlow.map {
        it[artistId]?.take(limit) ?: emptyList()
    }*/

    override fun getSongsByAlbumId(
        albumId: Long,
        limit: Int,
    ): Flow<List<Song>> = songsFlow /*songsListFlow.map {
        it[albumId]?.take(limit) ?: emptyList()
    }*/

    override fun songsInAlbum(
        albumId: Long,
        limit: Int,
    ): Flow<List<SongToAlbum>> = songsFlow.map { songs ->
        songs.filter {
            it.albumId == albumId
        }.map { s ->
            SongToAlbum().apply {
                song = s
            }
        }
    }
    /*override fun songsInAlbum(albumId: Long, limit: Int): Flow<List<SongToAlbum>> = songsAlbumListFlow.map {
        it[albumId]?.take(limit) ?: emptyList()
    }*/

    override fun songsInAlbums( //equivalent of episodeStore.episodesInPodcasts
        albumIds: List<Long>,
        limit: Int,
    ): Flow<List<SongToAlbum>>  =
        songsFlow.map { songs ->
            songs.filter {
                albumIds.contains(it.albumId)
            }.map { s ->
                SongToAlbum().apply {
                    song = s
                }
            }
        }

    override fun songsInGenreSortedByLastPlayed(
        genreId: Long,
        limit: Int
    ): Flow<List<Song>> = songsListFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

//    override fun songsInGenresSortedByLastPlayed(
//        genreIds: List<Long>,
//        limit: Int
//    ): Flow<List<Song>> =
//        songsFlow.map { songs ->
//            songs.filter {
//                genreIds.contains(it.genreId)
//            }.map { s ->
//
//                }
//        }
//    }

//    override fun songsInGenresSortedByCount(
//        genreIds: List<Long>,
//        limit: Int,
//    ): Flow<List<Song>> { }

    override fun getSongsAndAlbumsInGenreSortedByLastPlayed(
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

    //this version relies on addSong returning with Any type
    //current version relies on addSong returning as Long
    //because of BaseDAO object that uses insert() with Long return
    override suspend fun addSong(song: Song): Long = -1
    //TODO: fix this so it will correctly addSong with result coming back as long
    override suspend fun addSongs(songs: Collection<Song>) =
        songsFlow.update {
            it + songs
        }

    override suspend fun isEmpty(): Boolean =
        songsFlow.first().isEmpty()
}
