package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.ArtistWithExtraInfo
import com.example.music.data.database.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Album] related operations.
 */
@Dao
abstract class AlbumsDao : BaseDao<Album> {

    /**
     * Returns all the records within albums
     */
    @Query(
        """
        SELECT * FROM albums
        """
    )
    abstract fun getAllAlbums(): List<Album>

    /**
     * Returns a flow of the album record matching the specified id
     * @param id [Long]
     */
    @Query(
        """
        SELECT * FROM albums WHERE id = :id
        """
    )
    abstract fun getAlbumById(id: Long): Flow<Album> //equivalent of PodcastsDao.podcastWithUri

    /**
     * Returns a flow of the album record matching the specified title
     * @param title [String] the record's title to match on
     */
    @Query(
        """
        SELECT * FROM albums
        WHERE title = :title
        """
    )
    abstract fun getAlbumByTitle(title: String): Flow<Album>

    /**
     * Returns a flow of the list of filtered albums records matching the specified album_artist_id
     * @param albumArtistId [Long] the album_artist_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT albums.* FROM albums
        WHERE albums.album_artist_id = :albumArtistId
        LIMIT :limit
        """
    )
    abstract fun getAlbumsByAlbumArtistId(albumArtistId: Long, limit: Int): Flow<List<Album>>

    /**
     * Returns a flow of the album record and its aggregated songs data,
     * song_count: the amount of songs within album.
     * date_last_played: the MAX date_last_played value within the album's songs.
     * If song_count is null, it will be replaced as 0.
     * @param albumId [Long] the album_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT albums.*, COALESCE(song_count, 0) AS song_count, date_last_played, COALESCE(artists.name,"") AS album_artist_name FROM albums 
        INNER JOIN (
            SELECT album_id, COUNT(*) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        WHERE albums.id = :albumId
        """
    )
    abstract fun getAlbumWithExtraInfo(albumId: Long): Flow<AlbumWithExtraInfo> //equivalent of PodcastsDao.podcastWithExtraInfo

    /**
     * Returns a flow of the list of filtered albums records matching the specified album_artist_id
     * @param albumArtistId [Long] the album_artist_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT albums.*, COALESCE(song_count, 0) AS song_count, date_last_played, COALESCE(artists.name,"") as album_artist_name FROM albums 
        INNER JOIN (
            SELECT album_id, COUNT(*) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        WHERE albums.album_artist_id = :albumArtistId
        LIMIT :limit
        """
    )
    abstract fun getAlbumsWithExtraInfoByAlbumArtistId(albumArtistId: Long, limit: Int): Flow<List<AlbumWithExtraInfo>>

    /**
     * Returns a flow of the list of album records and their aggregated song data,
     * sorted by their album artist's name in ascending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing an album record when no songs with album's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT albums.*, date_last_played, COALESCE(song_count, 0) AS song_count, COALESCE(artists.name,"") AS album_artist_name FROM albums
        LEFT JOIN (
            SELECT album_id, MAX(date_last_played) AS date_last_played, COUNT(*) AS song_count
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        ORDER BY artists.name ASC, albums.title ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByAlbumArtistAsc(limit: Int): Flow<List<AlbumWithExtraInfo>>

    /**
     * Returns a flow of the list of album records and their aggregated song data,
     * sorted by their album artist's name in descending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing an album record when no songs with album's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT albums.*, date_last_played, COALESCE(song_count, 0) AS song_count, COALESCE(artists.name,"") AS album_artist_name FROM albums
        LEFT JOIN (
            SELECT album_id, MAX(date_last_played) AS date_last_played, COUNT(*) AS song_count
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        ORDER BY artists.name DESC, albums.title DESC 
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByAlbumArtistDesc(limit: Int): Flow<List<AlbumWithExtraInfo>>

    /**
     * Returns a flow of the list of album records and their aggregated song data,
     * sorted by their song's date_last_played in ascending order.
     * If songs.date_last_played is null, it will be set as the current local datetime
     * and sorted to the end of results list.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing an album record when no songs with album's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT albums.*, COALESCE(date_last_played, datetime(current_timestamp, 'localtime')) as date_last_played, COALESCE(song_count, 0) AS song_count, COALESCE(artists.name,"") AS album_artist_name FROM albums
        INNER JOIN (
            SELECT album_id, MAX(date_last_played) AS date_last_played, COUNT(*) AS song_count
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        ORDER BY datetime(date_last_played) ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByDateLastPlayedAsc(
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    /**
     * Returns a flow of the list of album records and their aggregated song data,
     * sorted by their song's date_last_played in descending order.
     * If songs.date_last_played is null, it will be set as '1900-01-01 00:00:00.000'
     * and sorted to the end of results list.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing an album record when no songs with album's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT albums.*, COALESCE(date_last_played,'1900-01-01 00:00:00.000') AS date_last_played, COALESCE(song_count, 0) AS song_count, COALESCE(artists.name,"") AS album_artist_name FROM albums
        INNER JOIN (
            SELECT album_id, MAX(date_last_played) AS date_last_played, COUNT(*) AS song_count
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        ORDER BY datetime(date_last_played) DESC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByDateLastPlayedDesc( //equivalent of PodcastsDao.podcastsSortedByLastEpisode
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    /**
     * Returns a flow of the list of album records and their aggregated songs data,
     * sorted by song_count in ascending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing an album record when no songs with album's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT albums.*, date_last_played, COALESCE(song_count, 0) AS song_count, COALESCE(artists.name,"") AS album_artist_name FROM albums
        INNER JOIN (
            SELECT album_id, MAX(date_last_played) AS date_last_played, COUNT(*) AS song_count
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        ORDER BY song_count ASC, albums.title ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsBySongCountAsc(
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    /**
     * Returns a flow of the list of album records and their aggregated songs data,
     * sorted by song_count in descending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing an album record when no songs with album's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT albums.*, date_last_played, COALESCE(song_count, 0) AS song_count, COALESCE(artists.name,"") AS album_artist_name FROM albums
        INNER JOIN (
            SELECT album_id, MAX(date_last_played) AS date_last_played, COUNT(*) AS song_count
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        ORDER BY song_count DESC, albums.title DESC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsBySongCountDesc(
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>>

    /**
     * Returns a flow of the list of album records and their aggregated songs data,
     * sorted by album's title attribute in ascending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing an album record when no songs with album's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT albums.*, date_last_played, COALESCE(song_count, 0) AS song_count, COALESCE(artists.name,"") AS album_artist_name FROM albums
        INNER JOIN (
            SELECT album_id, MAX(date_last_played) AS date_last_played, COUNT(*) AS song_count
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        ORDER BY albums.title ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByTitleAsc(limit: Int): Flow<List<AlbumWithExtraInfo>>

    /**
     * Returns a flow of the list of album records and their aggregated songs data,
     * sorted by album's title attribute in descending order.
     * NOTE: Setting of null song_count to 0 is used as a protective measure until support,
     * for removing an album record when no songs with album's id are present, is added.
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT albums.*, date_last_played, COALESCE(song_count, 0) AS song_count, COALESCE(artists.name,"") AS album_artist_name FROM albums
        INNER JOIN (
            SELECT album_id, MAX(date_last_played) AS date_last_played, COUNT(*) AS song_count
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        ORDER BY title DESC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsByTitleDesc(limit: Int): Flow<List<AlbumWithExtraInfo>>

    /*@Transaction
    @Query(
        """
        SELECT albums.* FROM albums
        INNER JOIN (
            SELECT songs.id, songs.album_id, songs.genre_id
            FROM songs
            WHERE genre_id = :genreId
            GROUP BY songs.album_id
        ) as songs ON albums.id = songs.album_id
        ORDER BY albums.title ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsInGenreByTitleAsc( //equivalent of PodcastsDao.podcastsInCategorySortedByLastEpisode
        genreId: Long,
        limit: Int
    ): Flow<List<Album>>*/

    /*@Transaction
    @Query(
        """
        SELECT albums.* FROM albums
        INNER JOIN (
            SELECT songs.id, songs.album_id, songs.genre_id
            FROM songs
            WHERE genre_id = :genreId
            GROUP BY songs.album_id
        ) as songs ON albums.id = songs.album_id
        ORDER BY albums.title DESC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsInGenreByTitleDesc( //equivalent of PodcastsDao.podcastsInCategorySortedByLastEpisode
        genreId: Long,
        limit: Int
    ): Flow<List<Album>>*/

    //return albums joined to the genresSortedByAlbumCount
    /* //sortAlbumsInGenreByDateLastPlayedAsc not needed atm
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
        ORDER BY datetime(date_last_played) ASC
        LIMIT :limit
        """
    )
    abstract fun sortAlbumsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>> */

    //return albums joined to the genresSortedByAlbumCount
    /* //sortAlbumsInGenreByDateLastPlayedDesc not needed atm
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
    abstract fun sortAlbumsInGenreByDateLastPlayedDesc( //equivalent of PodcastsDao.podcastsInCategorySortedByLastEpisode
        genreId: Long,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>> */

    /**
     * Returns a flow of the list of albums that have at least a partial match on their
     * title attribute to the query string, sorted by title in ascending order.
     * @param query [String] value of search query
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT albums.*, date_last_played, COALESCE(song_count, 0) AS song_count, COALESCE(artists.name,"") AS album_artist_name FROM albums
        INNER JOIN (
            SELECT album_id, MAX(date_last_played) AS date_last_played, COUNT(*) AS song_count
            FROM songs
            GROUP BY album_id
        ) AS songs ON albums.id = songs.album_id
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        WHERE albums.title LIKE '%' || :query || '%'
        ORDER BY albums.title ASC
        LIMIT :limit
        """
    )
    abstract fun searchAlbumsByTitle(query: String, limit: Int): Flow<List<AlbumWithExtraInfo>> //equivalent of PodcastsDao.searchPodcastByTitle

    @Transaction
    @Query(
        """
        SELECT COALESCE(artists.id,-1) AS id, COALESCE(artists.name,"") as name
        FROM albums 
        LEFT JOIN artists ON albums.album_artist_id = artists.id
        WHERE albums.id = :albumId
        """
    )
    abstract fun getAlbumArtistByAlbumId(albumId: Long): Flow<Artist>

    /**
     * Returns the integer value of the total amount of records in albums table.
     * NOTE: Must be called within a coroutine, since it doesn't return a flow.
     */
    @Query("SELECT COUNT(*) FROM albums")
    abstract suspend fun count(): Int

}
