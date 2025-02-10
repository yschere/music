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
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.Song
import kotlinx.coroutines.flow.Flow

interface ArtistStore {

    fun getAllArtists(): Flow<List<Artist>>

    fun getArtistById(id: Long): Flow<Artist>

    fun sortArtistsByNameAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun sortArtistsByNameDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun sortArtistsByAlbumCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun sortArtistsByAlbumCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    /* //sortArtistsByDateLastPlayedAsc not needed atm
    fun sortArtistsByDateLastPlayedAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    /* //sortArtistsByDateLastPlayedDesc not needed atm
    fun sortArtistsByDateLastPlayedDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    fun sortArtistsBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun sortArtistsBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    //why this one not in PodcastStore?
    /* //sortArtistsInGenreByDateLastPlayedAsc not needed atm
    fun sortArtistsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    /* //sortArtistsInGenreByDateLastPlayedDesc not needed atm
    fun sortArtistsInGenreByDateLastPlayedDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    fun sortArtistsInGenreByNameAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun sortArtistsInGenreByNameDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    /* //TODO: not sure if sortArtistsInGenreBySongCountAsc is needed
    fun sortArtistsInGenreBySongCountAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    /* //TODO: not sure if sortArtistsInGenreBySongCountDesc is needed
    fun sortArtistsInGenreBySongCountDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    //fun artistsAndGenres(id: Long): Flow<Artist>

    fun searchArtistByName(
        query: String,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun getAlbumsByArtistId(
        artistId: Long,
        limit: Int = Integer.MAX_VALUE,
    ): Flow<List<Album>>

    fun getSongsByArtistId(
        artistId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    suspend fun addArtist(artist: Artist)

    suspend fun count(): Int

    suspend fun isEmpty(): Boolean
}

/**
* A data repository for [Artist] instances.
*/
class LocalArtistStore(
private val artistDao: ArtistsDao,
private val albumDao: AlbumsDao,
private val songDao: SongsDao,
) : ArtistStore {

    override fun getAllArtists(): Flow<List<Artist>> =
        artistDao.getAllArtists()

    override fun getArtistById(id: Long): Flow<Artist> =
        artistDao.getArtistById(id)

    override fun sortArtistsByNameAsc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsByNameAsc(limit)

    override fun sortArtistsByNameDesc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsByNameDesc(limit)

    override fun sortArtistsByAlbumCountAsc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsByAlbumCountAsc(limit)

    override fun sortArtistsByAlbumCountDesc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsByAlbumCountDesc(limit)

    /* //sortArtistsByDateLastPlayedAsc not needed atm
    override fun sortArtistsByDateLastPlayedAsc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsByDateLastPlayedAsc(limit) //use as replacement for mostRecentArtists */

    /* //sortArtistsByDateLastPlayedDesc not needed atm
    override fun sortArtistsByDateLastPlayedDesc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsByDateLastPlayedDesc(limit) //use as replacement for mostRecentArtists */

    override fun sortArtistsBySongCountAsc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsBySongCountAsc(limit)

    override fun sortArtistsBySongCountDesc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsBySongCountDesc(limit)

    /* //sortArtistsInGenreByDateLastPlayedAsc not needed atm
    override fun sortArtistsInGenreByDateLastPlayedAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByDateLastPlayedAsc(genreId, limit) */

    /* //sortArtistsInGenreByDateLastPlayedDesc not needed atm
    override fun sortArtistsInGenreByDateLastPlayedDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByDateLastPlayedDesc(genreId, limit) */

    override fun sortArtistsInGenreByNameAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameAsc(genreId, limit)

    override fun sortArtistsInGenreByNameDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameDesc(genreId, limit)

    /* //TODO: not sure if sortArtistsInGenreBySongCountAsc is needed
    override fun sortArtistsInGenreBySongCountAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountAsc(genreId, limit) */

    /* //TODO: not sure if sortArtistsInGenreBySongCountDesc is needed
    override fun sortArtistsInGenreBySongCountDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountDesc(genreId, limit) */

    /* override fun artistsAndGenres(id: Long): Flow<ArtistWithGenre> =
        return artistDao.artistsAndGenres(id) */

    override fun searchArtistByName(
        query: String,
        limit: Int
    ): Flow<List<Artist>> {
        return artistDao.searchArtistByName(query, limit)
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

    /**
     * Add a new [Artist] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addArtist(artist: Artist) {
        artistDao.insert(artist)
    }

    override suspend fun count(): Int = artistDao.count()

    override suspend fun isEmpty(): Boolean = artistDao.count() == 0
}
