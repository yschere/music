package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Song] related operations.
 */
@Dao
abstract class SongsDao : BaseDao<Song> {

    /**
     * Returns a flow of all the records within songs
     */
    @Query(
        """
        SELECT * FROM songs
        """
    )
    abstract fun getAllSongs(): Flow<List<Song>>

    /**
     * Returns a flow of the song record matching the specified id
     * @param id [Long] the record's id to match on
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE id = :id
        """
    )
    abstract fun getSongById(id: Long): Flow<Song>

    /**
     * Returns a flow of the song record matching the specified title
     * @param title [String] the record's title to match on
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE title = :title
        """
    )
    abstract fun getSongByTitle(title: String): Flow<Song>

    /**
     * Returns a flow of the combined song and album object [SongToAlbum] matching the specified song id
     * @param songId [Long] the record's id to match on
     * FUTURE THOUGHT: update when SongToAlbum fixed
     */
    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        INNER JOIN albums ON songs.album_id = albums.id
        WHERE songs.id = :songId
        """
    )
    abstract fun getSongAndAlbumBySongId(songId: Long): Flow<SongToAlbum> //equivalent of episodeAndPodcast

    /**
     * Returns a flow of the list of songs sorted by their title attribute in ascending order
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        ORDER BY title ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByTitleAsc(limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs sorted by their title attribute in descending order
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        ORDER BY title DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByTitleDesc(limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs sorted by their artist's name attribute in ascending order.
     * If songs.artist_id is null, it will be sorted to end of results list.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        LEFT JOIN artists ON songs.artist_id = artists.id
        ORDER BY CASE 
            WHEN artists.name IS NULL THEN '2'
            ELSE '1' || name
        END ASC, songs.title ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByArtistAsc(limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs sorted by their artist's name attribute in descending order
     * If songs.artist_id is null, it will be sorted to end of results list.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        LEFT JOIN artists ON songs.artist_id = artists.id
        ORDER BY CASE 
            WHEN artists.name IS NULL THEN '0'
            ELSE '1' || name
        END DESC, songs.title DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByArtistDesc(limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs sorted by their album's title attribute in ascending order
     * If songs.album_id is null, it will be sorted to the end of results list
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        LEFT JOIN albums ON songs.album_id = albums.id
        ORDER BY CASE 
            WHEN albums.title IS NULL THEN '2'
            ELSE '1' || albums.title
        END ASC, songs.title ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByAlbumAsc(limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs sorted by their album's title attribute in descending order
     * If songs.album_id is null, it will be sorted to the end of results list
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        LEFT JOIN albums ON songs.album_id = albums.id
        ORDER BY CASE 
            WHEN albums.title IS NULL THEN '0'
            ELSE '1' || albums.title
        END DESC, songs.title DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByAlbumDesc(limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs sorted by their date_added attribute in ascending order
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        ORDER BY date_added ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByDateAddedAsc(limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs sorted by their date_added attribute in descending order
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        ORDER BY date_added DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByDateAddedDesc(limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs sorted by their date_last_played attribute in ascending order
     * If songs.date_last_played is null, it will be sorted to the end of results list
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        ORDER BY COALESCE(date_last_played, datetime(current_timestamp, 'localtime')) ASC, title ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByDateLastPlayedAsc(limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs sorted by their date_last_played attribute in descending order
     * If songs.date_last_played is null, it will be sorted to the end of results list
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        ORDER BY COALESCE(date_last_played, '1900-01-01 00:00:00.000') DESC, title DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsByDateLastPlayedDesc(limit: Int): Flow<List<Song>>

    //variant of getSongsByArtistId that returns mapping of artist to list of songs
    /*@Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        INNER JOIN (SELECT * FROM artists) as artists on artists.id = songs.artist_id
        WHERE artists.id = :artistId
        ORDER BY artists.name ASC
        LIMIT :limit
        """
    )
    abstract fun getSongsByArtistIdV2(artistId: Long, limit: Int): Map<Artist, List<Song>>*/

    /**
     * Returns a flow of the list of filtered song records matching the specified artist_id
     * @param artistId [Long] the artist_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE artist_id = :artistId
        LIMIT :limit
        """
    )
    abstract fun getSongsByArtistId(artistId: Long, limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching any artist_id within artistIds
     * @param artistIds [List] of type [Long] - the list of artist_ids to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE artist_id IN (:artistIds)
        LIMIT :limit
        """
    )
    abstract fun getSongsByArtistIds(artistIds: List<Long>, limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified artist_id,
     * sorted by their title attribute in ascending order
     * @param artistId [Long] the artist_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE artist_id = :artistId
        ORDER BY title ASC
        """
    )
    abstract fun sortSongsInArtistBySongTitleAsc(artistId: Long): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified artist_id,
     * sorted by their title attribute in descending order
     * @param artistId [Long] the artist_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE artist_id = :artistId
        ORDER BY title DESC
        """
    )
    abstract fun sortSongsInArtistBySongTitleDesc(artistId: Long): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified artist_id,
     * sorted by their album's title attribute in ascending order
     * If songs.album_id is null, it will be sorted to the end of results list
     * If songs.album_track_number is null, it will be sorted to the end of results list
     * @param artistId [Long] the artist_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        LEFT JOIN ( 
            SELECT albums.id, albums.title FROM albums 
        ) AS albums ON songs.album_id = albums.id
        WHERE songs.artist_id = :artistId
        ORDER BY CASE 
            WHEN albums.title IS NULL THEN '2'
            ELSE '1' || albums.title
        END ASC, CASE 
            WHEN songs.album_track_number IS NULL THEN '2'
            ELSE '1' || songs.album_track_number 
        END ASC, songs.title ASC
        """
    )
    abstract fun sortSongsInArtistByAlbumTitleAsc(artistId: Long): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified artist_id,
     * sorted by their album's title attribute in descending order
     * If songs.album_id is null, it will be sorted to the end of results list
     * If songs.album_track_number is null, it will be sorted to the end of results list
     * @param artistId [Long] the artist_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        LEFT JOIN ( 
            SELECT albums.id, albums.title FROM albums 
        ) AS albums ON songs.album_id = albums.id
        WHERE songs.artist_id = :artistId
        ORDER BY CASE 
            WHEN albums.title IS NULL THEN '0'
            ELSE '1' || albums.title
        END DESC, CASE 
            WHEN songs.album_track_number IS NULL THEN '0'
            ELSE '1' || songs.album_track_number 
        END DESC,  songs.title DESC
        """
    )
    abstract fun sortSongsInArtistByAlbumTitleDesc(artistId: Long): Flow<List<Song>>

    //variant of getSongsByArtistId that returns mapping of artist to list of songs
    /*@Transaction
    @Query(
        """
        SELECT songs.*, albums.title, albums.album_artist_id, albums.track_total, albums.disc_number, albums.disc_total, albums.artwork FROM songs
        JOIN albums on albums.id = songs.album_id
        WHERE albums.id = :albumId
        LIMIT :limit
        """
    )
    abstract fun getSongsByAlbumIdV2(albumId: Long, limit: Int): Map<Album, List<Song>>*/

    /**
     * Returns a flow of the list of filtered song records matching the specified album_id
     * @param albumId [Long] the album_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE album_id = :albumId
        LIMIT :limit
        """
    )
    abstract fun getSongsByAlbumId(albumId: Long, limit: Int): Flow<List<Song>> //equivalent for episodesForPodcastUri

    /**
     * Returns a flow of the combined list of songs to one album object [SongToAlbum] matching the specified album id,
     * sorted by the song's title attribute in ascending order
     * @param albumId [Long]
     * FUTURE THOUGHT: update when SongToAlbum fixed
     */
    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        INNER JOIN (
            SELECT id as albumId, title, track_total, disc_number, disc_total FROM albums
        ) ON songs.album_id = albumId
        WHERE songs.album_id = :albumId
        ORDER BY songs.title ASC
        """
    )
    abstract fun getSongsAndAlbumByAlbumId(albumId: Long): Flow<List<SongToAlbum>>

    /**
     * Returns a flow of the combined list of songs to one album object [SongToAlbum] matching any albumId within albumIds,
     * @param albumIds [List] of type [Long]
     * FUTURE THOUGHT: update when SongToAlbum fixed
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE album_id IN (:albumIds)
        LIMIT :limit
        """
    )
    abstract fun getSongsAndAlbumsByAlbumIds(albumIds: List<Long>, limit: Int): Flow<List<SongToAlbum>> //equivalent for episodesForPodcasts

    /**
     * Returns a flow of the list of filtered song records matching the specified album_id,
     * sorted by their song title attribute in ascending order
     * @param albumId [Long] the album_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE album_id = :albumId
        ORDER BY title ASC
        """
    )
    abstract fun sortSongsInAlbumBySongTitleAsc(albumId: Long): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified album_id,
     * sorted by their song title attribute in descending order
     * @param albumId [Long] the album_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE album_id = :albumId
        ORDER BY title DESC
        """
    )
    abstract fun sortSongsInAlbumBySongTitleDesc(albumId: Long): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified album_id,
     * sorted by their album_track_number attribute in ascending order
     * If songs.album_track_number is null, it will be sorted to the end of the results list
     * @param albumId [Long] the album_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE songs.album_id = :albumId
        ORDER BY COALESCE(songs.album_track_number,'2') ASC, songs.title ASC
        """
    )
    abstract fun sortSongsInAlbumByTrackNumberAsc(albumId: Long): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified album_id,
     * sorted by their album_track_number attribute in descending order
     * If songs.album_track_number is null, it will be sorted to the end of the results list
     * @param albumId [Long] the album_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE songs.album_id = :albumId
        ORDER BY COALESCE(songs.album_track_number,'0') DESC, songs.title DESC
        """
    )
    abstract fun sortSongsInAlbumByTrackNumberDesc(albumId: Long): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified composer_id
     * @param composerId [Long] the composer_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE composer_id = :composerId
        LIMIT :limit
        """
    )
    abstract fun getSongsByComposerId(composerId: Long, limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified composer_id,
     * sorted by their song title attribute in ascending order
     * @param composerId [Long] the composer_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE composer_id = :composerId
        ORDER BY title ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInComposerByTitleAsc(
        composerId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified composer_id,
     * sorted by their song title attribute in descending order
     * @param composerId [Long] the composer_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE composer_id = :composerId
        ORDER BY title DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInComposerByTitleDesc(
        composerId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified composer_id,
     * sorted by their date_last_played attribute in ascending order
     * If songs.date_last_played is null, it will be set as the current local datetime and sorted to the end of results list
     * @param composerId [Long] the composer_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE composer_id = :composerId
        ORDER BY COALESCE(date_last_played, datetime(current_timestamp, 'localtime')) ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInComposerByDateLastPlayedAsc(
        composerId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified composer_id,
     * sorted by their date_last_played attribute in descending order
     * If songs.date_last_played is null, it will be sorted to the end of results list
     * @param composerId [Long] the composer_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE composer_id = :composerId
        ORDER BY COALESCE(date_last_played, '1900-01-01 00:00:00.000') DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInComposerByDateLastPlayedDesc(
        composerId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified genre_id
     * @param genreId [Long] the genre_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id = :genreId
        LIMIT :limit
        """
    )
    abstract fun getSongsByGenreId(genreId: Long, limit: Int): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified genre_id,
     * sorted by their song title attribute in ascending order
     * @param genreId [Long] the genre_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id = :genreId
        ORDER BY title ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInGenreByTitleAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified genre_id,
     * sorted by their song title attribute in descending order
     * @param genreId [Long] the genre_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id = :genreId
        ORDER BY title DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInGenreByTitleDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified genre_id,
     * sorted by their date_last_played attribute in ascending order
     * If songs.date_last_played is null, it will be set as the current local datetime and sorted to the end of results list
     * @param genreId [Long] the genre_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id = :genreId
        ORDER BY COALESCE(date_last_played, datetime(current_timestamp, 'localtime')) ASC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    /**
     * Returns a flow of the list of filtered song records matching the specified genre_id,
     * sorted by their date_last_played attribute in descending order
     * If songs.date_last_played is null, it will be sorted to the end of results list
     * @param genreId [Long] the genre_id to match on
     * @param limit [Int] an optional limit on the records returned
     */
    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id = :genreId
        ORDER BY COALESCE(date_last_played, '1900-01-01 00:00:00.000') DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsInGenreByDateLastPlayedDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    /* @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT songs.*, albums.title/*, albums.album_artist_id, albums.year, albums.track_total, albums.disc_number, albums.disc_total, albums.artwork*/ FROM songs
        JOIN albums on albums.id = songs.album_id
        WHERE songs.genre_id = :genreId
        ORDER BY songs.date_last_played DESC
        LIMIT :limit
        """
    )
    abstract fun sortSongsAndAlbumsInGenreByDateLastPlayed( //equivalent of categoriesDao.episodesFromPodcastsInCategory
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<SongToAlbum>> */
    // FUTURE THOUGHT: fix SongToAlbum so that it is actually either all columns of song and album,
    // or is both objects as a whole

    /**
     * Returns a flow of the list of songs that have at least a partial match on their
     * title attribute to the query string, sorted by title in ascending order
     * @param query [String] value of search query
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE songs.title LIKE '%' || :query || '%'
        ORDER BY songs.title ASC
        LIMIT :limit
        """
    )
    abstract fun searchSongsByTitle(query: String, limit: Int): Flow<List<Song>>

    /**
     * Returns the integer value of the total amount of records in songs table
     * NOTE: Must be called within a coroutine, since it doesn't return a flow
     */
    @Query("SELECT COUNT(*) FROM songs")
    abstract suspend fun count(): Int
}
