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

import com.example.music.data.database.dao.AlbumsDao
import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.database.model.SongToAlbum
import kotlinx.coroutines.flow.Flow

interface AlbumStore {

    fun getAlbumById(id: Long): Flow<Album>

    fun albumWithExtraInfo(albumId: Long): Flow<AlbumWithExtraInfo>

    fun albumsSortedByLastPlayedSong(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun albumsSortedBySongCount(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun getAlbumsByAlbumArtistId(
        albumArtistId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Album>>

    fun albumsInGenreSortedByLastPlayedSong(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun searchAlbumByTitle(
        query: String,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun searchAlbumByTitleAndGenre(
        query: String,
        genreIdList: List<Long>,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun getAllAlbums(): List<Album>

    fun songsInAlbum(albumId: Long): Flow<List<SongToAlbum>>

    //fun allAlbumNames(): List<String>

    //fun lastPlayedAlbums(): List<AlbumExtraInfo>

    suspend fun addAlbum(album: Album)

    suspend fun isEmpty(): Boolean
}

/**
 * A data repository for [Album] instances.
 */
class LocalAlbumStore(
    private val albumDao: AlbumsDao,
    private val songDao: SongsDao,
) : AlbumStore {

    //equivalent of podcastStore.podcastWithUri
    override fun getAlbumById(id: Long): Flow<Album> {
        return albumDao.getAlbumById(id)
        //equivalent of podcastsDao.podcastWithUri
    }

    //equivalent of podcastStore.podcastWithExtraInfo
    override fun albumWithExtraInfo(albumId: Long): Flow<AlbumWithExtraInfo> =
        albumDao.albumWithExtraInfo(albumId)

    override fun albumsSortedBySongCount(limit: Int): Flow<List<AlbumWithExtraInfo>> {
        return albumDao.albumsSortedBySongCount(limit)
    }

    //equivalent of podcastStore.podcastsSortedByLastEpisode
    override fun albumsSortedByLastPlayedSong(limit: Int): Flow<List<AlbumWithExtraInfo>> {
        return albumDao.albumsSortedByLastPlayedSong(limit)
        //equivalent of podcastsDao.podcastsSortedByLastEpisode
    } //use as replacement for mostRecentAlbums

    override fun getAlbumsByAlbumArtistId(
        albumArtistId: Long,
        limit: Int
    ): Flow<List<Album>> = getAlbumsByAlbumArtistId(albumArtistId, limit)

    override fun albumsInGenreSortedByLastPlayedSong(
        genreId: Long,
        limit: Int,
    ): Flow<List<AlbumWithExtraInfo>> {
        return albumDao.albumsInGenreSortedByLastPlayedSong(genreId, limit)
    }

    //equivalent of podcastStore.searchPodcastByTitle
    override fun searchAlbumByTitle(
        query: String,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>> {
        return albumDao.searchAlbumByTitle(query, limit)
        //equivalent of podcastsDao.searchPodcastByTitle
    }

    //equivalent of podcastStore.searchPodcastByTitleAndAlbum
    override fun searchAlbumByTitleAndGenre(
        query: String,
        genreIdList: List<Long>,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>> {
        return albumDao.searchAlbumByTitleAndGenre(query, genreIdList, limit)
        //equivalent of podcastStore.searchPodcastByTitleAndCategory
    }

    override fun getAllAlbums(): List<Album> {
        return albumDao.getAllAlbums()
    }

    override fun songsInAlbum(albumId: Long): Flow<List<SongToAlbum>> {
        return songDao.getSongsAndAlbumByAlbumId(albumId)
    }

    /**
     * Add a new [Album] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addAlbum(album: Album) {
        albumDao.insert(album)
    }

    override suspend fun isEmpty(): Boolean = albumDao.count() == 0
}
