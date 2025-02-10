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
import com.example.music.data.database.dao.GenresDao
import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import kotlinx.coroutines.flow.Flow

interface GenreStore {

    fun getAllGenres(): Flow<List<Genre>>

    fun getGenreById(id: Long): Flow<Genre>

    /**
     * Returns a flow containing a list of genres which are sorted by the
     * number of albums in each genre
     */
    fun sortGenresByNameAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    fun sortGenresByNameDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    /**
     * Returns a flow containing a list of genres which are sorted by the
     * number of albums in each genre
     */
    fun sortGenresByAlbumCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    fun sortGenresByAlbumCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    /**
     * Returns a flow containing a list of genres which are sorted by the
     * number of songs in each genre
     */
    fun sortGenresBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    fun sortGenresBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    /**
     * Returns a flow containing a list of albums in the genre with the
     * given [genreId] sorted by their last played date.
     */
    fun sortAlbumsInGenreByTitleAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Album>>

    fun sortAlbumsInGenreByTitleDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Album>>

    fun sortArtistsInGenreByNameAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun sortArtistsInGenreByNameDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    /* fun sortArtistsInGenreBySongCountAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    /* fun sortArtistsInGenreBySongCountDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    fun sortSongsInGenreByTitleAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun sortSongsInGenreByTitleDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun sortSongsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun sortSongsInGenreByDateLastPlayedDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    /* fun songsAndAlbumsInGenre(//equivalent of categories episodesFromPodcastsInCategory
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<SongToAlbum>> */

    suspend fun addGenre(genre: Genre): Long

    suspend fun count(): Int

    suspend fun isEmpty(): Boolean
}

/**
 * A data repository for [Genre] instances.
 */
class LocalGenreStore(
    private val genreDao: GenresDao,
    private val artistDao: ArtistsDao,
    private val albumDao: AlbumsDao,
    private val songDao: SongsDao,
) : GenreStore {

    override fun getAllGenres(): Flow<List<Genre>> =
        genreDao.getAllGenres()

    override fun getGenreById(id: Long): Flow<Genre> =
        genreDao.getGenreById(id)

    override fun sortGenresByNameAsc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresByNameAsc(limit)

    override fun sortGenresByNameDesc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresByNameDesc(limit)

    override fun sortGenresByAlbumCountAsc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresByAlbumCountAsc(limit)

    override fun sortGenresByAlbumCountDesc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresByAlbumCountDesc(limit)

    override fun sortGenresBySongCountAsc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresBySongCountAsc(limit)

    override fun sortGenresBySongCountDesc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresBySongCountDesc(limit)

    override fun sortAlbumsInGenreByTitleAsc(genreId: Long, limit: Int): Flow<List<Album>> =
        albumDao.sortAlbumsInGenreByTitleAsc(genreId, limit)

    override fun sortAlbumsInGenreByTitleDesc(genreId: Long, limit: Int): Flow<List<Album>> =
        albumDao.sortAlbumsInGenreByTitleDesc(genreId, limit)

    override fun sortArtistsInGenreByNameAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameAsc(genreId, limit)

    override fun sortArtistsInGenreByNameDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameDesc(genreId, limit)

    /* override fun sortArtistsInGenreBySongCountAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountAsc(genreId, limit) */

    /* override fun sortArtistsInGenreBySongCountDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountDesc(genreId, limit) */

    override fun sortSongsInGenreByTitleAsc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByTitleAsc(genreId, limit)

    override fun sortSongsInGenreByTitleDesc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByTitleDesc(genreId, limit)

    override fun sortSongsInGenreByDateLastPlayedAsc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByDateLastPlayedAsc(genreId, limit)

    override fun sortSongsInGenreByDateLastPlayedDesc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByDateLastPlayedDesc(genreId, limit)

    //equivalent of categories episodesFromPodcastsInCategory
    /* override fun songsAndAlbumsInGenre(genreId: Long, limit: Int): Flow<List<SongToAlbum>> =
        songDao.getSongsAndAlbumsInGenreSortedByLastPlayed(genreId, limit) */

    override suspend fun addGenre(genre: Genre): Long = genreDao.insert(genre)

    override suspend fun count(): Int = genreDao.count()

    override suspend fun isEmpty(): Boolean = genreDao.count() == 0

}
