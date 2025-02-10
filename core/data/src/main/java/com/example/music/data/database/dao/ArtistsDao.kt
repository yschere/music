package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.example.music.data.database.model.Artist
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Artist] related operations.
 */
@Dao
abstract class ArtistsDao : BaseDao<Artist> {

    @Query(
        """
        SELECT * FROM artists
        """
    )
    abstract fun getAllArtists(): Flow<List<Artist>>

    //select artist info based on artists.id
    @Query(
        """
        SELECT * FROM artists WHERE id = :id
        """
    )
    abstract fun getArtistById(id: Long): Flow<Artist>

    @Query(
        """
        SELECT * FROM artists
        ORDER BY name ASC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsByNameAsc(limit: Int): Flow<List<Artist>>

    @Query(
        """
        SELECT * FROM artists
        ORDER BY name DESC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsByNameDesc(limit: Int): Flow<List<Artist>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT artists.* FROM artists
        INNER JOIN (
            SELECT album_artist_id, COUNT(id) AS album_count FROM albums
            GROUP BY album_artist_id
        ) as albums ON albums.album_artist_id = artists.id
        ORDER BY album_count ASC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsByAlbumCountAsc(
        limit: Int
    ): Flow<List<Artist>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT artists.* FROM artists
        INNER JOIN (
            SELECT album_artist_id, COUNT(id) AS album_count FROM albums
            GROUP BY album_artist_id
        ) as albums ON albums.album_artist_id = artists.id
        ORDER BY album_count DESC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsByAlbumCountDesc(
        limit: Int
    ): Flow<List<Artist>>

    /* //sortArtistsByDateLastPlayedAsc not needed atm
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT artists.* FROM artists
        INNER JOIN (
            SELECT artist_id, COUNT(id) AS song_count FROM songs
            GROUP BY artist_id
        ) AS songs ON songs.artist_id = artists.id
        ORDER BY song_count ASC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsByDateLastPlayedAsc(
        limit: Int
    ): Flow<List<Artist>> */

    /* //sortArtistsByDateLastPlayedDesc not needed atm
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT artists.* FROM artists
        INNER JOIN (
            SELECT artist_id, COUNT(id) AS song_count FROM songs
            GROUP BY artist_id
        ) AS songs ON songs.artist_id = artists.id
        ORDER BY song_count DESC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsByDateLastPlayedDesc(
        limit: Int
    ): Flow<List<Artist>> */

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT artists.* FROM artists
        INNER JOIN (
            SELECT artist_id, COUNT(id) AS song_count FROM songs
            GROUP BY artist_id
        ) as songs ON songs.artist_id = artists.id
        ORDER BY song_count ASC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsBySongCountAsc(
        limit: Int
    ): Flow<List<Artist>>

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
    abstract fun sortArtistsBySongCountDesc(
        limit: Int
    ): Flow<List<Artist>>

    /* //sortArtistsInGenreByDateLastPlayedAsc not needed atm
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT artists.* FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, songs.genre_id, songs.date_last_played
            FROM songs
            GROUP BY songs.artist_id
        ) as songs on artists.id = songs.artist_id
        WHERE songs.genre_id = :genreId
        GROUP BY songs.artist_id
        ORDER BY songs.date_last_played ASC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<Artist>> */

    /* //sortArtistsInGenreByDateLastPlayedDesc not needed atm
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT artists.* FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, songs.genre_id, songs.date_last_played
            FROM songs
            GROUP BY songs.artist_id
        ) as songs on artists.id = songs.artist_id
        WHERE songs.genre_id = :genreId
        GROUP BY songs.artist_id
        ORDER BY songs.date_last_played DESC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsInGenreByDateLastPlayedDesc(
        genreId: Long,
        limit: Int
    ): Flow<List<Artist>> */

    @Query(
        """
        SELECT artists.* FROM artists
        INNER JOIN songs ON songs.artist_id = artists.id
        WHERE songs.genre_id = :genreId
        ORDER BY artists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsInGenreByNameAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<Artist>>

    @Query(
        """
        SELECT artists.* FROM artists
        INNER JOIN (
            SELECT songs.genre_id, songs.artist_id FROM songs
            WHERE songs.genre_id = :genreId
        ) AS songs ON songs.artist_id = artists.id
        ORDER BY artists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsInGenreByNameDesc(
        genreId: Long,
        limit: Int
    ): Flow<List<Artist>>

    /* //TODO: not sure if sortArtistsInGenreBySongCountAsc is needed
    @Query(
        """
        SELECT artists.* FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, songs.genre_id, COUNT(id) as count
            FROM songs
            GROUP BY songs.artist_id
        ) as songs on artists.id = songs.artist_id
        WHERE songs.genre_id = :genreId
        GROUP BY songs.artist_id
        ORDER BY songs.count ASC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsInGenreBySongCountAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<Artist>> */

    /* //TODO: not sure if sortArtistsInGenreBySongCountDesc is needed
    @Query(
        """
        SELECT artists.* FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, songs.genre_id, COUNT(id) as count
            FROM songs
            GROUP BY songs.artist_id
        ) as songs on artists.id = songs.artist_id
        WHERE songs.genre_id = :genreId
        GROUP BY songs.artist_id
        ORDER BY songs.count DESC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsInGenreBySongCountDesc(
        genreId: Long,
        limit: Int
    ): Flow<List<Artist>> */

    /* //return artists info and genres info
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
//    abstract fun artistsAndGenres(id: Long): Flow<ArtistWithGenre> */

    //search artist names by param query
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

    //return count of artists
    @Query("SELECT COUNT(*) FROM artists")
    abstract suspend fun count(): Int
}
