package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.GenreWithExtraInfo
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Genre] related operations.
 */
@Dao
abstract class GenresDao : BaseDao<Genre> {

    /**
     * Returns all the records within genres
     */
    @Query(
        """
        SELECT * FROM genres
        """
    )
    abstract fun getAllGenres(): Flow<List<Genre>>

    /**
     * Returns a flow of the genre record matching the specified id
     * @param id [Long]
     */
    @Query(
        """
        SELECT * FROM genres
        WHERE id = :id
        """
    )
    abstract fun getGenreById(id: Long): Flow<Genre>

    /**
     * Returns a flow of the genre record and its aggregated songs data,
     * song_count: the amount of songs within genre.
     * @param id [Long] the genre_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT genres.*, song_count FROM genres
        INNER JOIN (
            SELECT songs.genre_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.genre_id
        ) AS songs ON songs.genre_id = genres.id
        WHERE genres.id = :id
        """
    )
    abstract fun getGenreWithExtraInfo(id: Long): Flow<GenreWithExtraInfo>

    /**
     * Returns a flow of the list of genre records and their aggregated songs data,
     * sorted by genre's name attribute in ascending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing a genre record when no songs with genre's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT genres.*, COALESCE(song_count,0) AS song_count FROM genres
        INNER JOIN (
            SELECT id AS song_id, genre_id, COUNT(id) AS song_count FROM songs
            GROUP BY genre_id
        ) AS songs ON songs.genre_id = genres.id
        ORDER BY name ASC
        LIMIT :limit
        """
    )
    abstract fun sortGenresByNameAsc(limit: Int): Flow<List<GenreWithExtraInfo>>

    /**
     * Returns a flow of the list of genre records and their aggregated songs data,
     * sorted by genre's name attribute in descending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing a genre record when no songs with genre's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT genres.*, COALESCE(song_count,0) AS song_count FROM genres
        INNER JOIN (
            SELECT id AS song_id, genre_id, COUNT(id) AS song_count FROM songs
            GROUP BY genre_id
        ) AS songs ON songs.genre_id = genres.id
        ORDER BY name DESC
        LIMIT :limit
        """
    )
    abstract fun sortGenresByNameDesc(limit: Int): Flow<List<GenreWithExtraInfo>>

    //genres joined to albums on genre_id
    //group by genre_id, order by album count
    /*@Query(
        """
        SELECT genres.* FROM genres
        INNER JOIN (
            SELECT id as song_id, album_id, genre_id, COALESCE(COUNT(album_id),0) as album_count FROM songs
            GROUP BY genre_id
        ) as songs ON songs.genre_id = genres.id
        ORDER BY album_count ASC, genres.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortGenresByAlbumCountAsc(
        limit: Int
    ): Flow<List<Genre>>*/
    /*@Query(
        """
        SELECT genres.* FROM genres
        INNER JOIN (
            SELECT id as song_id, album_id, genre_id, COALESCE(COUNT(album_id),0) as album_count FROM songs
            GROUP BY genre_id
        ) as songs ON songs.genre_id = genres.id
        ORDER BY album_count DESC, genres.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortGenresByAlbumCountDesc(
        limit: Int
    ): Flow<List<Genre>>*/

    /**
     * Returns a flow of the list of genre records and their aggregated songs data,
     * sorted by song_count in ascending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing a genre record when no songs with genre's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT genres.*, COALESCE(song_count,0) AS song_count FROM genres
        INNER JOIN (
            SELECT id AS song_id, genre_id, COUNT(id) AS song_count FROM songs
            GROUP BY genre_id
        ) AS songs ON songs.genre_id = genres.id
        ORDER BY song_count ASC, genres.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortGenresBySongCountAsc(
        limit: Int
    ): Flow<List<GenreWithExtraInfo>>

    /**
     * Returns a flow of the list of genre records and their aggregated songs data,
     * sorted by song_count in descending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing a genre record when no songs with genre's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT genres.*, COALESCE(song_count,0) AS song_count FROM genres
        INNER JOIN (
            SELECT id AS song_id, genre_id, COUNT(id) AS song_count FROM songs
            GROUP BY genre_id
        ) AS songs ON songs.genre_id = genres.id
        ORDER BY song_count DESC, genres.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortGenresBySongCountDesc(
        limit: Int
    ): Flow<List<GenreWithExtraInfo>>

    /**
     * Returns the integer value of the total amount of records in genres table.
     * NOTE: Must be called within a coroutine, since it doesn't return a flow.
     */
    @Query("SELECT COUNT(*) FROM genres")
    abstract suspend fun count(): Int
}