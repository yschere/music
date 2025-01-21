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
import com.example.music.data.database.dao.ArtistsDao
import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.ArtistWithGenre
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import kotlinx.coroutines.flow.Flow

interface ArtistStore {

    fun getArtistById(id: Long): Flow<Artist>

    fun getAlbumsByArtistId(
        artistId: Long,
        limit: Int = Integer.MAX_VALUE,
    ): Flow<List<Album>>

    fun getSongsByArtistId(
        artistId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun artistsSortedByLastPlayedSong(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun artistsSortedBySongCount(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    //why this one not in PodcastStore?
    fun artistsInGenreSortedByLastPlayedSong(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun artistsInGenreSortedBySongCount(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    //fun artistsAndGenres(id: Long): Flow<Artist>

    fun searchArtistByName(
        query: String,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    suspend fun addArtist(artist: Artist)

    suspend fun isEmpty(): Boolean
}

/**
 * A data repository for [Album] instances.
 */
class LocalArtistStore(
    private val artistDao: ArtistsDao,
    private val albumDao: AlbumsDao,
    private val songDao: SongsDao,
) : ArtistStore {

    override fun getArtistById(id: Long): Flow<Artist> {
        return artistDao.getArtistById(id)
    }

    override fun getAlbumsByArtistId(
        artistId: Long,
        limit: Int,
    ): Flow<List<Album>> {
        return albumDao.getAlbumsByAlbumArtistId(artistId, limit)
    }

    override fun getSongsByArtistId(
        artistId: Long,
        limit: Int,
    ): Flow<List<Song>> = songDao.getSongsByArtistId(artistId, limit)

    override fun artistsSortedByLastPlayedSong(limit: Int): Flow<List<Artist>> {
        return artistDao.artistsSortedByLastPlayedSong(limit)
    } //use as replacement for mostRecentArtists

    override fun artistsSortedBySongCount(limit: Int): Flow<List<Artist>> {
        return artistDao.artistsSortedBySongCount(limit)
    }

    override fun artistsInGenreSortedByLastPlayedSong(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.artistsInGenreSortedByLastPlayedSong(genreId, limit)

    override fun artistsInGenreSortedBySongCount(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.artistsInGenreSortedBySongCount(genreId, limit)

    override fun searchArtistByName(
        query: String,
        limit: Int
    ): Flow<List<Artist>> {
        return artistDao.searchArtistByName(query, limit)
    }

//    override fun artistsAndGenres(id: Long): Flow<ArtistWithGenre> {
//        return artistDao.artistsAndGenres(id)
//    } //don't know the necessity of this query

    /**
     * Add a new [Artist] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addArtist(artist: Artist) {
        artistDao.insert(artist)
    }

    override suspend fun isEmpty(): Boolean = albumDao.count() == 0
}
