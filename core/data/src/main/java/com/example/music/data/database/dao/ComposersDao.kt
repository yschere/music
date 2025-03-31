package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.example.music.data.database.model.Composer
import com.example.music.data.database.model.ComposerWithExtraInfo
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Composer] related operations.
 */
@Dao
abstract class ComposersDao : BaseDao<Composer> {

    /**
     * Returns all the records within composers
     */
    @Query(
        """
        SELECT * FROM composers
        """
    )
    abstract fun getAllComposers(): Flow<List<Composer>>

    /**
     * Returns a flow of the composer record matching the specified id
     * @param id [Long]
     */
    @Query(
        """
        SELECT * FROM composers 
        WHERE id = :id
        """
    )
    abstract fun getComposerById(id: Long): Flow<Composer>

    /**
     * Returns a flow of the composer record matching the specified name
     * @param name [String] the record's name to match on
     */
    @Query(
        """
        SELECT * FROM composers 
        WHERE name = :name
        """
    )
    abstract fun getComposerByName(name: String): Flow<Composer>

    /**
     * Returns a flow of the composer record and its aggregated songs data,
     * song_count: the amount of songs within composer
     * @param id [Long] the composer_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT composers.*, song_count FROM composers
        INNER JOIN (
            SELECT songs.composer_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.composer_id
        ) AS songs ON songs.composer_id = composers.id
        WHERE composers.id = :id
        """
    )
    abstract fun getComposerWithExtraInfo(id: Long): Flow<ComposerWithExtraInfo>

    /**
     * Returns a flow of the composer record and its aggregated songs data,
     * sorted by composer's name attribute in ascending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing a composer record when no songs with composer's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT composers.*, COALESCE(song_count, 0) AS song_count FROM composers
        INNER JOIN (
            SELECT songs.composer_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.composer_id
        ) AS songs ON songs.composer_id = composers.id
        ORDER BY name ASC
        LIMIT :limit
        """
    )
    abstract fun sortComposersByNameAsc(limit: Int): Flow<List<ComposerWithExtraInfo>>

    /**
     * Returns a flow of the composer record and its aggregated songs data,
     * sorted by composer's name attribute in descending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing a composer record when no songs with composer's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT composers.*, COALESCE(song_count, 0) AS song_count FROM composers
        INNER JOIN (
            SELECT songs.composer_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.composer_id
        ) AS songs ON songs.composer_id = composers.id
        ORDER BY name DESC
        LIMIT :limit
        """
    )
    abstract fun sortComposersByNameDesc(limit: Int): Flow<List<ComposerWithExtraInfo>>

    /**
     * Returns a flow of the composer record and its aggregated songs data,
     * sorted by song_count in ascending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing a composer record when no songs with composer's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT composers.*, COALESCE(song_count, 0) AS song_count FROM composers
        INNER JOIN (
            SELECT songs.composer_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.composer_id
        ) AS songs ON songs.composer_id = composers.id
        ORDER BY song_count ASC, composers.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortComposersBySongCountAsc(
        limit: Int
    ): Flow<List<ComposerWithExtraInfo>>

    /**
     * Returns a flow of the list of composers and their aggregated song data,
     * sorted by song_count in descending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing a composer record when no songs with composer's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT composers.*, COALESCE(song_count, 0) AS song_count FROM composers
        INNER JOIN (
            SELECT songs.composer_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.composer_id
        ) AS songs ON songs.composer_id = composers.id
        ORDER BY song_count DESC, composers.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortComposersBySongCountDesc(
        limit: Int
    ): Flow<List<ComposerWithExtraInfo>>

    /**
     * Returns a flow of the list of composers that have at least a partial match on their
     * title attribute to the query string, sorted by title in ascending order.
     * @param query [String] value of search query
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT composers.*, COALESCE(song_count, 0) AS song_count FROM composers
        INNER JOIN (
            SELECT songs.composer_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.composer_id
        ) AS songs ON songs.composer_id = composers.id
        WHERE composers.name LIKE '%' || :query || '%'
        ORDER BY composers.name ASC
        LIMIT :limit

        """
    )
    abstract fun searchComposersByName(query: String, limit: Int): Flow<List<ComposerWithExtraInfo>>

    /**
     * Returns the integer value of the total amount of records in composers table.
     * NOTE: Must be called within a coroutine, since it doesn't return a flow
     */
    @Query("SELECT COUNT(*) FROM composers")
    abstract suspend fun count(): Int
}