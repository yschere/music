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
import com.example.music.data.database.model.Composer
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Composer] related operations.
 */
@Dao
abstract class ComposersDao : BaseDao<Composer> {

    //return composer names in alphabetical order
    @Query(
        """
        SELECT * FROM composers
        ORDER BY name ASC
        """
    )
    abstract fun composers(): Flow<Composer>

    //composers joined to songs on composer_id
    //group by composer_id, order by song count
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT composers.* FROM composers
        INNER JOIN (
            SELECT composer_id, COUNT(id) AS song_count FROM songs
            GROUP BY composer_id
        ) as songs ON songs.composer_id = composers.id
        ORDER BY song_count DESC
        LIMIT :limit
        """
    )
    abstract fun composersSortedBySongCount(
        limit: Int
    ): Flow<List<Composer>>

    //return composers count
    @Query("SELECT COUNT(*) FROM composers")
    abstract suspend fun count(): Int

    @Query("SELECT * FROM composers WHERE name = :name")
    abstract fun getComposerByName(name: String): Flow<Composer?>

    @Query("SELECT * FROM composers WHERE id = :id")
    abstract fun getComposerById(id: Long): Flow<Composer?>

}