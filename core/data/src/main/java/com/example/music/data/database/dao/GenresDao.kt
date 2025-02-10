package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.music.data.database.model.Genre
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Genre] related operations.
 */
//take podcast categories like the combo of genres to albums, category = genre, podcast = album
@Dao
abstract class GenresDao : BaseDao<Genre> {

    @Query(
        """
        SELECT * FROM genres
        """
    )
    abstract fun getAllGenres(): Flow<List<Genre>>

    @Query("SELECT * FROM genres WHERE id = :id")
    abstract fun getGenreById(id: Long): Flow<Genre>

    //return genre names in alphabetical order
    @Query(
        """
        SELECT * FROM genres
        ORDER BY name ASC
        LIMIT :limit
        """
    )
    abstract fun sortGenresByNameAsc(limit: Int): Flow<List<Genre>>

    @Query(
        """
        SELECT * FROM genres
        ORDER BY name DESC
        LIMIT :limit
        """
    )
    abstract fun sortGenresByNameDesc(limit: Int): Flow<List<Genre>>

    //genres joined to albums on genre_id
    //group by genre_id, order by album count
    @Query(
        """
        SELECT genres.* FROM genres
        INNER JOIN (
            SELECT id as song_id, album_id, genre_id, COUNT(album_id) as album_count FROM songs
            GROUP BY genre_id
        ) as songs ON songs.genre_id = genres.id
        ORDER BY album_count ASC
        LIMIT :limit
        """
    )
    abstract fun sortGenresByAlbumCountAsc(
        limit: Int
    ): Flow<List<Genre>>

    @Query(
        """
        SELECT genres.* FROM genres
        INNER JOIN (
            SELECT id as song_id, album_id, genre_id, COUNT(album_id) as album_count FROM songs
            GROUP BY genre_id
        ) as songs ON songs.genre_id = genres.id
        ORDER BY album_count DESC
        LIMIT :limit
        """
    )
    abstract fun sortGenresByAlbumCountDesc(
        limit: Int
    ): Flow<List<Genre>>

    @Query(
        """
        SELECT genres.* FROM genres
        INNER JOIN (
            SELECT id as song_id, genre_id, COUNT(id) as song_count FROM songs
            GROUP BY genre_id
        ) as songs ON songs.genre_id = genres.id
        ORDER BY song_count ASC
        LIMIT :limit
        """
    )
    abstract fun sortGenresBySongCountAsc(
        limit: Int
    ): Flow<List<Genre>>

    @Query(
        """
        SELECT genres.* FROM genres
        INNER JOIN (
            SELECT id as song_id, genre_id, COUNT(id) as song_count FROM songs
            GROUP BY genre_id
        ) as songs ON songs.genre_id = genres.id
        ORDER BY song_count DESC
        LIMIT :limit
        """
    )
    abstract fun sortGenresBySongCountDesc(
        limit: Int
    ): Flow<List<Genre>>

    //return genres count
    @Query("SELECT COUNT(*) FROM genres")
    abstract suspend fun count(): Int
}