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

    @Query(
        """
        SELECT * FROM composers
        """
    )
    abstract fun getAllComposers(): Flow<List<Composer>>

    @Query("SELECT * FROM composers WHERE id = :id")
    abstract fun getComposerById(id: Long): Flow<Composer>

    @Query("SELECT * FROM composers WHERE name = :name")
    abstract fun getComposerByName(name: String): Flow<Composer?>

    @Query(
        """
        SELECT * FROM composers
        ORDER BY name ASC
        """
    )
    abstract fun sortComposersByNameAsc(): Flow<List<Composer>>

    @Query(
        """
        SELECT * FROM composers
        ORDER BY name DESC
        """
    )
    abstract fun sortComposersByNameDesc(): Flow<List<Composer>>

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
        ORDER BY song_count ASC
        LIMIT :limit
        """
    )
    abstract fun sortComposersBySongCountAsc(
        limit: Int
    ): Flow<List<Composer>>

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
    abstract fun sortComposersBySongCountDesc(
        limit: Int
    ): Flow<List<Composer>>

    //return composers count
    @Query("SELECT COUNT(*) FROM composers")
    abstract suspend fun count(): Int
}