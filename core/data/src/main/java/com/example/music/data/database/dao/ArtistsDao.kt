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
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.ArtistWithGenre
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Artist] related operations.
 */
@Dao
abstract class ArtistsDao : BaseDao<Artist> {

    //return artist names in alphabetical order
    @Query(
        """
        SELECT * FROM artists
        ORDER BY name ASC
        """
    )
    abstract fun artists(): Flow<Artist>

    //select artist info based on artists.id
    @Query(
        """
        SELECT * FROM artists WHERE id = :id
        """
    )
    abstract fun getArtistById(id: Long): Flow<Artist>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT artists.* FROM artists
        INNER JOIN (
            SELECT artist_id, COUNT(id) AS song_count FROM songs
            GROUP BY artist_id
        ) as songs ON songs.artist_id = artists.id
        ORDER BY song_count DESC
        LIMIT :limit
        """
    )
    abstract fun artistsSortedByLastPlayedSong(
        limit: Int
    ): Flow<List<Artist>>

    //composers joined to songs on composer_id
    //group by composer_id, order by song count
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT artists.* FROM artists
        INNER JOIN (
            SELECT artist_id, COUNT(id) AS song_count FROM songs
            GROUP BY artist_id
        ) as songs ON songs.artist_id = artists.id
        ORDER BY song_count DESC
        LIMIT :limit
        """
    )
    abstract fun artistsSortedBySongCount(
        limit: Int
    ): Flow<List<Artist>>

    //return count of artists
    @Query("SELECT COUNT(*) FROM artists")
    abstract suspend fun count(): Int

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT artists.* FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, COUNT(id) as count
            FROM songs
            GROUP BY songs.artist_id
        ) as songs on artists.id = songs.artist_id
        WHERE artists.genre_id = :genreId
        GROUP BY songs.artist_id
        ORDER BY songs.count DESC
        LIMIT :limit
        """
    )
    abstract fun artistsInGenreSortedByLastPlayedSong(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    @Query(
        """
        SELECT artists.* FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, COUNT(id) as count
            FROM songs
            GROUP BY songs.artist_id
        ) as songs on artists.id = songs.artist_id
        WHERE artists.genre_id = :genreId
        GROUP BY songs.artist_id
        ORDER BY songs.count DESC
        LIMIT :limit
        """
    )
    abstract fun artistsInGenreSortedBySongCount(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    //return artists info and genres info
    //what intent is this call trying to serve?
    //why would i need artist info and the genre name
    //is this meant to be a list of artists and a list of genres? or a list of artists under one genre and that genre's name? why?
//    @Transaction
//    @Query(
//        """
//        SELECT artists.* FROM artists
//        INNER JOIN genres ON artists.genre_id = genres.id
//        WHERE artists.id = :id
//        """
//    )
//    abstract fun artistsAndGenres(id: Long): Flow<ArtistWithGenre>

    //search album titles by param query
    @Transaction
    @Query(
        """
        SELECT artists.*
        FROM artists
        INNER JOIN (
            SELECT artist_id, count(*) as song_count
            FROM songs
            GROUP BY artist_id
        ) as songs ON artists.id = songs.artist_id
        WHERE artists.name LIKE '%' || :query || '%'
        ORDER BY artists.name DESC
        LIMIT :limit

        """
    )
    abstract fun searchArtistByName(query: String, limit: Int): Flow<List<Artist>>

}
