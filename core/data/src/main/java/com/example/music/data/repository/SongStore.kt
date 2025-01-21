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

package com.example.music.data.repository

import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import kotlinx.coroutines.flow.Flow

interface SongStore {

    /**
    Return flow containing song given [id]
     */
    fun getSongById(id: Long): Flow<Song>

    fun getSongByTitle(title: String): Flow<Song>

    /**
     * Returns a flow containing the song and corresponding album given a [songId].
     */
    //equivalent of episodeAndPodcastWithUri
    fun songAndAlbumBySongId(songId: Long): Flow<SongToAlbum>

    /**
    Return limited list flow of most recent songs
     */
    fun mostRecentSongs(limit: Int = Integer.MAX_VALUE): Flow<List<Song>>

    /**
    Return list of songs given artist id in ascending order based on sortColumn
     */
    //replaced id with artistId, replaced sortCol with limit for testing TestSongStore
    fun getSongsByArtistId(artistId: Long, limit: Int = Integer.MAX_VALUE): Flow<List<Song>>

    fun getSongsByAlbumId(albumId: Long, limit: Int = Integer.MAX_VALUE): Flow<List<Song>>

    fun songsInAlbum( //equivalent of episodeStore.episodesInPodcast
        albumId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<SongToAlbum>>

    fun songsInAlbums( //equivalent of episodeStore.episodesInPodcasts
        albumIds: List<Long>,
        limit: Int = Integer.MAX_VALUE,
    ): Flow<List<SongToAlbum>>

    fun songsInGenreSortedByLastPlayed(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

//    fun songsInGenresSortedByLastPlayed(
//        genreIds: List<Long>,
//        limit: Int = Integer.MAX_VALUE
//    ): Flow<List<Song>>

//    fun songsInGenresSortedByCount(
//        genreIds: List<Long>,
//        limit: Int = Integer.MAX_VALUE
//    ): Flow<List<Song>>

    fun getSongsAndAlbumsInGenreSortedByLastPlayed(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE,
    ): Flow<List<SongToAlbum>>

    /**
     * Add a new [Song] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addSong(song: Song): Long
    suspend fun addSongs(songs: Collection<Song>)

    suspend fun isEmpty(): Boolean
}

/**
 * A data repository for [Song] instances.
 */
class LocalSongStore(
    private val songDao: SongsDao
) : SongStore {

    //equivalent of episodeStore.episodeWithUri
    override fun getSongById(id: Long): Flow<Song> {
        return songDao.getSongById(id)
        //equivalent of episodesDao.episode
    }

    override fun getSongByTitle(title: String): Flow<Song> {
        return songDao.getSongByTitle(title)
    }

    //equivalent of episodeStore.episodeAndPodcastWithUri
    override fun songAndAlbumBySongId(songId: Long): Flow<SongToAlbum> =
        songDao.getSongAndAlbumBySongId(songId)
    //equivalent of episodesDao.episodeAndPodcast

    override fun mostRecentSongs(limit: Int): Flow<List<Song>> {
        return songDao.mostRecentSongs(limit)
    }

    //replaced id with artistId, replaced sortCol with limit for testing TestSongStore
    override fun getSongsByArtistId(artistId: Long, limit: Int): Flow<List<Song>> {
        return songDao.getSongsByArtistId(artistId, limit)
    }

    //replaced id with artistId, replaced sortCol with limit for testing TestSongStore
    override fun getSongsByAlbumId(albumId: Long, limit: Int): Flow<List<Song>> {
        return songDao.getSongsByAlbumId(albumId, limit)
    }

    //equivalent of episodeStore.episodesInPodcast
    override fun songsInAlbum(
        albumId: Long,
        limit: Int
    ): Flow<List<SongToAlbum>> {
        return songDao.getSongsAndAlbumByAlbumId(albumId)
        //equivalent of episodesDao.episodesForPodcastUri
    }

    //equivalent of episodeStore.episodesInPodcasts
    override fun songsInAlbums(
        albumIds: List<Long>,
        limit: Int,
    ): Flow<List<SongToAlbum>> =
        songDao.getSongsAndAlbumsByAlbumIds(albumIds, limit)
    //equivalent of episodesDao.episodesForPodcasts

    //replaced id with genreId, replaced sortCol with limit for testing TestSongStore
    override fun songsInGenreSortedByLastPlayed(genreId: Long, limit: Int): Flow<List<Song>> {
        return songDao.getSongsInGenreSortedByLastPlayed(genreId, limit)
    }

//    override fun songsInGenresSortedByLastPlayed(genreIds: List<Long>, limit: Int): Flow<List<Song>> {
//        return songDao.getSongsInGenresSortedByLastPlayed(genreIds, limit)
//    }

//    override fun songsInGenresSortedByCount(genreIds: List<Long>, limit: Int): Flow<List<Song>> {
//        return songDao.getSongsInGenresSortedByCount(genreIds, limit)
//    }

    //equivalent of categoriesDao.episodesFromPodcastsInCategory
    override fun getSongsAndAlbumsInGenreSortedByLastPlayed(
        genreId: Long,
        limit: Int,
    ): Flow<List<SongToAlbum>> = songDao.getSongsAndAlbumsInGenreSortedByLastPlayed(genreId, limit)
    /**
     * Add a new [Song] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addSong(song: Song): Long = songDao.insert(song)

    override suspend fun addSongs(songs: Collection<Song>) =
        songDao.insertAll(songs)

    override suspend fun isEmpty(): Boolean = songDao.count() == 0
}

//    override fun songsInPlaylist(playlistId: Long): Flow<List<Song>> {
//        return songDao.songsInPlaylist(playlistId)
//    }
