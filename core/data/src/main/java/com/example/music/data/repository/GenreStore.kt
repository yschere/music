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
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import kotlinx.coroutines.flow.Flow

interface GenreStore {

    /**
     * return a flow containing a list of genres which is sorted by the
     * number of albums in each genre
     */
    fun genresSortedByAlbumCount(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    /**
     * return a flow containing a list of albums in the genre with the
     * given [genreId] sorted by their last played date.
     */
    fun albumsInGenreSortedByLastPlayedSong(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun artistsInGenreSortedBySongCount(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun songsInGenre(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

//    fun songsInGenresByLastPlayed(
//        genreIds: List<Long>,
//        limit: Int = Integer.MAX_VALUE
//    ): Flow<List<Song>>

//    fun songsInGenresBySongCount(
//        genreIds: List<Long>,
//        limit: Int = Integer.MAX_VALUE
//    ): Flow<List<Song>>

    fun songsAndAlbumsInGenre(//equivalent of categories episodesFromPodcastsInCategory
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<SongToAlbum>>

    suspend fun addGenre(genre: Genre): Long

    fun getGenreByName(name: String): Flow<Genre?>

    fun getGenreById(id: Long): Flow<Genre?>

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

    override fun genresSortedByAlbumCount(limit: Int): Flow<List<Genre>> {
        return genreDao.genresSortedByAlbumCount(limit)
    }

    override fun albumsInGenreSortedByLastPlayedSong(genreId: Long, limit: Int): Flow<List<AlbumWithExtraInfo>> {
        return albumDao.albumsInGenreSortedByLastPlayedSong(genreId, limit)
    }

    override fun artistsInGenreSortedBySongCount(genreId: Long, limit: Int): Flow<List<Artist>> {
        return artistDao.artistsInGenreSortedBySongCount(genreId, limit)
    }

    override fun songsInGenre(genreId: Long, limit: Int): Flow<List<Song>> {
        return songDao.getSongsInGenreSortedByLastPlayed(genreId, limit)
    }

//    override fun songsInGenresByLastPlayed(genreIds: List<Long>, limit: Int): Flow<List<Song>> {
//        return songDao.getSongsInGenresSortedByLastPlayed(genreIds, limit)
//    }

//    override fun songsInGenresBySongCount(genreIds: List<Long>, limit: Int): Flow<List<Song>> {
//        return songDao.getSongsInGenresSortedByCount(genreIds, limit)
//    }

    //equivalent of categories episodesFromPodcastsInCategory
    override fun songsAndAlbumsInGenre(genreId: Long, limit: Int): Flow<List<SongToAlbum>> {
        return songDao.getSongsAndAlbumsInGenreSortedByLastPlayed(genreId, limit)
    }

    override suspend fun addGenre(genre: Genre): Long = genreDao.insert(genre)

    override fun getGenreByName(name: String): Flow<Genre?> {
        return genreDao.getGenreByName(name)
    }

    override fun getGenreById(id: Long): Flow<Genre?> {
        return genreDao.getGenreById(id)
    }

    override suspend fun isEmpty(): Boolean = genreDao.count() == 0

}
