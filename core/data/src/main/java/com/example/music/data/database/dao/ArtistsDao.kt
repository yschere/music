package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.ArtistWithExtraInfo
import com.example.music.data.database.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Artist] related operations.
 */
@Dao
abstract class ArtistsDao : BaseDao<Artist> {

    /**
     * Returns all the records within artists
     */
    @Query(
        """
        SELECT * FROM artists
        """
    )
    abstract fun getAllArtists(): Flow<List<Artist>>

    /**
     * Returns a flow of the artist record matching the specified id
     * @param id [Long]
     */
    @Query(
        """
        SELECT * FROM artists WHERE id = :id
        """
    )
    abstract fun getArtistById(id: Long): Flow<Artist>

    /**
     * Returns a flow of the artist record matching the specified name
     * @param name [String] the record's name to match on
     */
    @Query(
        """
        SELECT * FROM artists
        WHERE name = :name
        """
    )
    abstract fun getArtistByName(name: String): Flow<Artist>

    /**
     * Returns a flow of the artist record and its aggregated songs data,
     * song_count: the amount of songs by this artist,
     * album_count: the amount of albums by this artist.
     * If song_count is null, it will be replaced as 0.
     * If album_count is null, it will be replaced as 0.
     * @param id [Long] the artist_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT artists.*, COALESCE(song_count,0) AS song_count, COALESCE(album_count,0) AS album_count FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.artist_id
        ) AS songs ON songs.artist_id = artists.id
        LEFT JOIN (
            SELECT albums.album_artist_id, COUNT(*) AS album_count
            FROM albums
            GROUP BY albums.album_artist_id
        ) AS albums ON albums.album_artist_id = artists.id
        WHERE artists.id = :id
        """
    )
    abstract fun getArtistWithExtraInfo(id: Long): Flow<ArtistWithExtraInfo>

    /**
     * Returns a flow of the list of artists and their aggregated song data,
     * sorted by artist's name in ascending order.
     * If song_count is null, it will be replaced as 0.
     * If album_count is null, it will be replaced as 0.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT artists.*, COALESCE(song_count,0) AS song_count, COALESCE(album_count,0) AS album_count FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.artist_id
        ) AS songs ON songs.artist_id = artists.id
        LEFT JOIN (
            SELECT albums.album_artist_id, COUNT(*) AS album_count
            FROM albums
            GROUP BY albums.album_artist_id
        ) AS albums ON albums.album_artist_id = artists.id
        ORDER BY artists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsByNameAsc(limit: Int): Flow<List<ArtistWithExtraInfo>>

    /**
     * Returns a flow of the list of artists and their aggregated song data,
     * sorted by artist's name in descending order
     * If song_count is null, it will be replaced as 0.
     * If album_count is null, it will be replaced as 0.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT artists.*, COALESCE(song_count,0) AS song_count, COALESCE(album_count,0) AS album_count FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.artist_id
        ) AS songs ON songs.artist_id = artists.id
        LEFT JOIN (
            SELECT albums.album_artist_id, COUNT(*) AS album_count
            FROM albums
            GROUP BY albums.album_artist_id
        ) AS albums ON albums.album_artist_id = artists.id
        ORDER BY artists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsByNameDesc(limit: Int): Flow<List<ArtistWithExtraInfo>>

    /**
     * Returns a flow of the list of artists and their aggregated song data,
     * sorted by album_count in ascending order.
     * If song_count is null, it will be replaced as 0.
     * If album_count is null, it will be replaced as 0.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT artists.*, COALESCE(song_count,0) AS song_count, COALESCE(album_count,0) AS album_count FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.artist_id
        ) AS songs ON songs.artist_id = artists.id
        LEFT JOIN (
            SELECT albums.album_artist_id, COUNT(*) AS album_count
            FROM albums
            GROUP BY albums.album_artist_id
        ) AS albums ON albums.album_artist_id = artists.id
        ORDER BY album_count ASC, artists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsByAlbumCountAsc(
        limit: Int
    ): Flow<List<ArtistWithExtraInfo>>

    /**
     * Returns a flow of the list of artists and their aggregated song data,
     * sorted by album_count in descending order.
     * If song_count is null, it will be replaced as 0.
     * If album_count is null, it will be replaced as 0.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT artists.*, COALESCE(song_count,0) AS song_count, COALESCE(album_count,0) AS album_count FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.artist_id
        ) AS songs ON songs.artist_id = artists.id
        LEFT JOIN (
            SELECT albums.album_artist_id, COUNT(*) AS album_count
            FROM albums
            GROUP BY albums.album_artist_id
        ) AS albums ON albums.album_artist_id = artists.id
        ORDER BY album_count DESC, artists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsByAlbumCountDesc(
        limit: Int
    ): Flow<List<ArtistWithExtraInfo>>

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

    /**
     * Returns a flow of the list of artists and their aggregated song data,
     * sorted by song_count in ascending order.
     * If song_count is null, it will be replaced as 0.
     * If album_count is null, it will be replaced as 0.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT artists.*, COALESCE(song_count,0) AS song_count, COALESCE(album_count,0) AS album_count FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.artist_id
        ) AS songs ON songs.artist_id = artists.id
        LEFT JOIN (
            SELECT albums.album_artist_id, COUNT(*) AS album_count
            FROM albums
            GROUP BY albums.album_artist_id
        ) AS albums ON albums.album_artist_id = artists.id
        ORDER BY song_count ASC, artists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsBySongCountAsc(
        limit: Int
    ): Flow<List<ArtistWithExtraInfo>>

    /**
     * Returns a flow of the list of artists and their aggregated song data,
     * sorted by song_count in descending order.
     * If song_count is null, it will be replaced as 0.
     * If album_count is null, it will be replaced as 0.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT artists.*, COALESCE(song_count,0) AS song_count, COALESCE(album_count,0) AS album_count FROM artists
        LEFT JOIN (
            SELECT songs.artist_id, COUNT(*) AS song_count
            FROM songs
            GROUP BY songs.artist_id
        ) AS songs ON songs.artist_id = artists.id
        LEFT JOIN (
            SELECT albums.album_artist_id, COUNT(*) AS album_count
            FROM albums
            GROUP BY albums.album_artist_id
        ) AS albums ON albums.album_artist_id = artists.id
        ORDER BY song_count DESC, artists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortArtistsBySongCountDesc(
        limit: Int
    ): Flow<List<ArtistWithExtraInfo>>

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

    /*@Query(
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
    ): Flow<List<Artist>>*/

    /*@Query(
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
    ): Flow<List<Artist>>*/

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

    /**
     * Returns a flow of the list of artists and their aggregated song data that
     * have at least a partial match on their name attribute to the query string,
     * sorted by title in ascending order.
     * @param query [String] value of search query
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT artists.*, COALESCE(song_count,0) AS song_count, COALESCE(album_count,0) AS album_count FROM artists
        LEFT JOIN (
            SELECT artist_id, COUNT(*) as song_count
            FROM songs
            GROUP BY artist_id
        ) AS songs ON artists.id = songs.artist_id
        LEFT JOIN (
            SELECT albums.album_artist_id, COUNT(*) AS album_count
            FROM albums
            GROUP BY albums.album_artist_id
        ) AS albums ON albums.album_artist_id = artists.id
        WHERE artists.name LIKE '%' || :query || '%'
        ORDER BY artists.name ASC
        LIMIT :limit

        """
    )
    abstract fun searchArtistsByName(query: String, limit: Int): Flow<List<ArtistWithExtraInfo>>

    /**
     * Returns the integer value of the total amount of records in artists table
     * NOTE: Must be called within a coroutine, since it doesn't return a flow
     */
    @Query("SELECT COUNT(*) FROM artists")
    abstract suspend fun count(): Int
}
