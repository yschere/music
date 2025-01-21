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

package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Album] related operations.
 */
@Dao
abstract class AlbumsDao : BaseDao<Album> {

    //return album info based on param album Id
    @Query(
        """
        SELECT * FROM albums WHERE id = :id
        """
    )
    abstract fun getAlbumById(id: Long): Flow<Album>
    //equivalent of PodcastsDao.podcastWithUri
    //was albums()

    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums 
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(songs.date_last_played) as date_last_played
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        WHERE albums.id = :albumId
        ORDER BY datetime(date_last_played) DESC
        """
    )
    abstract fun albumWithExtraInfo(albumId: Long): Flow<AlbumWithExtraInfo> //equivalent of PodcastsDao.podcastWithExtraInfo

    //abstract fun albumsSortedBySongCount(limit: Int): Flow<List<AlbumWithExtraInfo>> {
        //return albumDao.albumsSortedBySongCount(limit)
    //}

    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        ORDER BY datetime(song_count) DESC
        LIMIT :limit
        """
    )
    abstract fun albumsSortedBySongCount(
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        ORDER BY datetime(date_last_played) DESC
        LIMIT :limit
        """
    )
    abstract fun albumsSortedByLastPlayedSong( //equivalent of PodcastsDao.podcastsSortedByLastEpisode
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    //return albums by album artist id
    @Transaction
    @Query(
        """
        SELECT albums.* FROM albums
        WHERE albums.album_artist_id = :albumArtistId
        LIMIT :limit
        """
    )
    abstract fun getAlbumsByAlbumArtistId(albumArtistId: Long, limit: Int): Flow<List<Album>>

    //return albums joined to the genresSortedByAlbumCount
    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT songs.album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            WHERE genre_id = :genreId
            GROUP BY songs.album_id
        ) as songs ON albums.id = songs.album_id
        ORDER BY datetime(date_last_played) DESC
        LIMIT :limit
        """
    )
    abstract fun albumsInGenreSortedByLastPlayedSong( //equivalent of PodcastsDao.podcastsInCategorySortedByLastEpisode
        genreId: Long,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    //search album titles by param query
    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            GROUP BY album_id
        ) as songs ON albums.id = songs.album_id
        WHERE albums.title LIKE '%' || :query || '%'
        ORDER BY datetime(date_last_played) DESC
        LIMIT :limit

        """
    )
    abstract fun searchAlbumByTitle(query: String, limit: Int): Flow<List<AlbumWithExtraInfo>> //equivalent of PodcastsDao.searchPodcastByTitle

    @Transaction
    @Query(
        """
        SELECT albums.*, song_count, date_last_played
        FROM albums
        INNER JOIN (
            SELECT album_id, count(*) as song_count, max(date_last_played) as date_last_played
            FROM songs
            WHERE genre_id IN (:genreIdList)
            GROUP BY album_id
        ) as songs ON albums.id = songs.album_id
        WHERE albums.title LIKE '%' || :query || '%'
        ORDER BY datetime(date_last_played) DESC
        LIMIT :limit
        """
    )
    abstract fun searchAlbumByTitleAndGenre(  //equivalent of PodcastsDao.searchPodcastByTitleAndCategory
        query: String,
        genreIdList: List<Long>,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    //return count of albums
    @Query("SELECT COUNT(*) FROM albums")
    abstract suspend fun count(): Int

    //return ids and titles of all albums
    @Query(
        """
        SELECT * FROM albums
        """
    )
    abstract fun getAllAlbums(): List<Album>
    //TODO: SUGGESTION: place in ViewModel or Repository to update UI with albums

}
