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
import com.example.music.data.database.model.Genre
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Genre] related operations.
 */
//take com.example.music.ui.album categories like the combo of genres to albums, category = genre, com.example.music.ui.album = album
@Dao
abstract class GenresDao : BaseDao<Genre> {

    //return genre names in alphabetical order
    @Query(
        """
        SELECT * FROM genres
        ORDER BY name ASC
        """
    )
    abstract fun genres(): Flow<Genre>

    //genres joined to albums on genre_id
    //group by genre_id, order by album count
    @Query(
        """
        SELECT genres.* FROM genres
        INNER JOIN (
            SELECT genre_id, COUNT(*) AS album_count FROM albums
            GROUP BY genre_id
        ) ON genre_id = genres.id
        ORDER BY album_count DESC
        LIMIT :limit
        """
    )
    abstract fun genresSortedByAlbumCount(
        limit: Int
    ): Flow<List<Genre>>

    //return playlists count
    @Query("SELECT COUNT(*) FROM playlists")
    abstract suspend fun count(): Int

    @Query("SELECT * FROM genres WHERE name = :name")
    abstract fun getGenreByName(name: String): Flow<Genre?>

    @Query("SELECT * FROM genres WHERE id = :id")
    abstract fun getGenreById(id: Long): Flow<Genre?>

}