package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.database.model.Song
import kotlinx.coroutines.flow.Flow

/** Changelog:
 * 4/5/2025 - I adjusted the song_playlist_entries Table in preview_data.db to reference ids from
 * MediaStore. So want to adjust song_playlist_entries queries to just get the ids. Will use
 * ContentResolver to retrieve the referenced Audios elsewhere.
 */

/**
 * Room DAO for [Playlist] related operations.
 */

@Dao
abstract class PlaylistsDao : BaseDao<Playlist> {

    /**
     * Returns all the records within playlists
     */
    @Query(
        """
        SELECT * FROM playlists
        """
    )
    abstract fun getAllPlaylists(): Flow<List<Playlist>>

    /**
     * Returns a flow of the playlist record matching the specified id
     * @param id [Long]
     */
    @Query(
        """
        SELECT * FROM playlists
        WHERE id = :id
        """
    )
    abstract fun getPlaylistById(id: Long): Flow<Playlist>

    @Query(
        """
        SELECT * FROM playlists
        WHERE id in (:ids)
        """
    )
    abstract fun getPlaylistsByIds(ids: List<Long>): Flow<List<Playlist>>

    /**
     * Returns a flow of the playlist record matching the specified name
     * @param name [String] the record's name to match on
     */
    @Query(
        """
        SELECT * FROM playlists
        WHERE name = :name
        """
    )
    abstract fun getPlaylistByName(name: String): Flow<Playlist>

    /**
     * Returns a flow of the playlist record and its aggregated songs data,
     * song_count: the amount of songs within playlist,
     * date_last_played: the MAX date_last_played value within the playlist's songs.
     * NOTE: Setting of null song_count to 0 necessary because playlists can have 0 songs.
     * @param playlistId [Long] the playlist_id to match on
     */
    /*@Query(
        """
        SELECT playlists.*, date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            INNER JOIN song_playlist_entries ON songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        WHERE playlists.id = :playlistId
        """
    )
    abstract fun getPlaylistWithExtraInfo(playlistId: Long): Flow<PlaylistWithExtraInfo>*/

    @Query(
        """
        SELECT playlists.*, date_last_accessed AS date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT playlist_id, COUNT(song_playlist_entries.id) as song_count FROM song_playlist_entries
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        WHERE playlists.id = :playlistId
        """
    )
    abstract fun getPlaylistWithExtraInfo(playlistId: Long): Flow<PlaylistWithExtraInfo>


    /**
     * Returns a flow of the list of playlist records and their aggregated songs data,
     * sorted by playlist's name attribute in ascending order.
     * NOTE: Setting of null song_count to 0 necessary because playlists can have 0 songs.
     * @param limit [Int] an optional limit on the records returned
     */
    /*@Query(
        """
        SELECT playlists.*, date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            INNER JOIN song_playlist_entries ON songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY playlists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByNameAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>>*/

    @Query(
        """
        SELECT playlists.*, date_last_accessed AS date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count
            FROM song_playlist_entries
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY playlists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByNameAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>>

    /**
     * Returns a flow of the list of playlist records and their aggregated songs data,
     * sorted by playlist's name attribute in descending order.
     * NOTE: Setting of null song_count to 0 necessary because playlists can have 0 songs.
     * @param limit [Int] an optional limit on the records returned
     */
    /*@Query(
        """
        SELECT playlists.*, date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            INNER JOIN song_playlist_entries ON songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY playlists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByNameDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>>*/

    @Query(
        """
        SELECT playlists.*, date_last_accessed AS date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count
            FROM song_playlist_entries
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY playlists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByNameDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>>

    /**
     * Returns a flow of the list of playlist records and their aggregated songs data,
     * sorted by playlist's date_created attribute in ascending order.
     * NOTE: Setting of null song_count to 0 necessary because playlists can have 0 songs.
     * @param limit [Int] an optional limit on the records returned
     */
    /*@Query(
        """
        SELECT playlists.*, date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            INNER JOIN song_playlist_entries ON songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY playlists.date_created ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateCreatedAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>>*/

    @Query(
        """
        SELECT playlists.*, date_last_accessed AS date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count
            FROM song_playlist_entries
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY playlists.date_created ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateCreatedAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>>

    /**
     * Returns a flow of the list of playlist records and their aggregated songs data,
     * sorted by playlist's date_created attribute in descending order.
     * NOTE: Setting of null song_count to 0 necessary because playlists can have 0 songs.
     * @param limit [Int] an optional limit on the records returned
     */
    /*@Query(
        """
        SELECT playlists.*, date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            INNER JOIN song_playlist_entries ON songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY playlists.date_created DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateCreatedDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>>*/

    @Query(
        """
        SELECT playlists.*, date_last_accessed AS date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count
            FROM song_playlist_entries
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY playlists.date_created DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateCreatedDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>>

    /**
     * Returns a flow of the list of playlist records and their aggregated songs data,
     * sorted by playlist's date_last_accessed attribute in ascending order.
     * NOTE: Setting of null song_count to 0 necessary because playlists can have 0 songs.
     * @param limit [Int] an optional limit on the records returned
     */
    /*@Query(
        """
        SELECT playlists.*, date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            INNER JOIN song_playlist_entries ON songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY datetime(date_last_accessed) ASC, playlists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateLastAccessedAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>>*/

    @Query(
        """
        SELECT playlists.*, date_last_accessed AS date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count
            FROM song_playlist_entries
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY datetime(date_last_accessed) ASC, playlists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateLastAccessedAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>>

    /**
     * Returns a flow of the list of playlist records and their aggregated songs data,
     * sorted by playlist's date_last_accessed attribute in descending order.
     * NOTE: Setting of null song_count to 0 necessary because playlists can have 0 songs.
     * @param limit [Int] an optional limit on the records returned
     */
    /*@Query(
        """
        SELECT playlists.*, date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            INNER JOIN song_playlist_entries ON songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY datetime(date_last_accessed) DESC, playlists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateLastAccessedDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>>*/ //mostRecentPlaylists(limit: Int): Flow<List<Playlist>>
    // want this to be used for ... add to playlist i think? ie if adding a song to playlist and added a song previously, want that previous playlist to show up first

    @Query(
        """
        SELECT playlists.*, date_last_accessed AS date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count
            FROM song_playlist_entries
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY datetime(date_last_accessed) ASC, playlists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateLastAccessedDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>>

    /**
     * Returns a flow of the list of playlist records and their aggregated songs data,
     * sorted by playlist's aggregated date_last_played attribute in ascending order.
     * NOTE: Setting of null song_count to 0 necessary because playlists can have 0 songs.
     * If date_last_played is null, it will be set as the current local datetime,
     * and be sorted to the end of results list.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT playlists.*, COALESCE(date_last_played, datetime(current_timestamp, 'localtime')) AS date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            INNER JOIN song_playlist_entries ON songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY date_last_played ASC, playlists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateLastPlayedAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    /**
     * Returns a flow of the list of playlist records and their aggregated songs data,
     * sorted by playlist's aggregated date_last_played attribute in descending order.
     * NOTE: Setting of null song_count to 0 necessary because playlists can have 0 songs.
     * If date_last_played is null, it will be set as '1900-01-01 00:00:00.000'
     * and be sorted to the end of results list.
     * @param limit [Int] an optional limit on the records returned
     */
    @Transaction
    @Query(
        """
        SELECT playlists.*, COALESCE(date_last_played, '1900-01-01 00:00:00.000') AS date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            INNER JOIN song_playlist_entries ON songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY date_last_played DESC, playlists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateLastPlayedDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    /**
     * Returns a flow of the list of playlist records and their aggregated songs data,
     * sorted by playlist's aggregated song_count attribute in ascending order.
     * NOTE: Setting of null song_count to 0 necessary because playlists can have 0 songs.
     * @param limit [Int] an optional limit on the records returned
     */
    /*@Transaction
    @Query(
        """
        SELECT playlists.*, date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            INNER JOIN song_playlist_entries ON songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY song_count ASC, playlists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>*/

    @Transaction
    @Query(
        """
        SELECT playlists.*, date_last_accessed AS date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count
            FROM song_playlist_entries
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY song_count ASC, playlists.name ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    /**
     * Returns a flow of the list of playlist records and their aggregated songs data,
     * sorted by playlist's aggregated song_count attribute in descending order.
     * NOTE: Setting of null song_count to 0 necessary because playlists can have 0 songs.
     * @param limit [Int] an optional limit on the records returned
     */
    /*@Transaction
    @Query(
        """
        SELECT playlists.*, date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count, MAX(songs.date_last_played) AS date_last_played
            FROM songs
            INNER JOIN song_playlist_entries ON songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY song_count DESC, playlists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>*/

    @Transaction
    @Query(
        """
        SELECT playlists.*, date_last_accessed AS date_last_played, COALESCE(song_count, 0) AS song_count FROM playlists
        LEFT JOIN (
            SELECT song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) AS song_count
            FROM song_playlist_entries
            GROUP BY song_playlist_entries.playlist_id
        ) AS song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY song_count DESC, playlists.name DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    /**
     * Returns a flow of the list of songs filtered by playlist_id,
     * sorted by their playlist_track_number in ascending order.
     * songs.album_track_number is set to the playlist_track_number
     * to make use of the existing attribute.
     * @param playlistId [Long] the playlist_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT songs.id, songs.title, songs.artist_id, songs.album_id, songs.genre_id, songs.year, song_playlist_entries.playlist_track_number+1 AS album_track_number, songs.lyrics, songs.composer_id, songs.date_added, songs.date_modified, songs.date_last_played, songs.duration FROM playlists 
        LEFT JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        LEFT JOIN songs ON songs.id = song_playlist_entries.song_id
        WHERE playlist_id = :playlistId
        ORDER BY song_playlist_entries.playlist_track_number ASC
        """
    )
    abstract fun sortSongsInPlaylistByTrackNumberAsc(playlistId: Long): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs filtered by playlist_id,
     * sorted by their playlist_track_number in descending order.
     * songs.album_track_number is set to the playlist_track_number
     * to make use of the existing attribute.
     * @param playlistId [Long] the playlist_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT songs.id, songs.title, songs.artist_id, songs.album_id, songs.genre_id, songs.year, song_playlist_entries.playlist_track_number+1 AS album_track_number, songs.lyrics, songs.composer_id, songs.date_added, songs.date_modified, songs.date_last_played, songs.duration FROM playlists 
        LEFT JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        LEFT JOIN songs ON songs.id = song_playlist_entries.song_id
        WHERE playlist_id = :playlistId
        ORDER BY song_playlist_entries.playlist_track_number DESC
        """
    )
    abstract fun sortSongsInPlaylistByTrackNumberDesc(playlistId: Long): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs filtered by playlist_id,
     * sorted by song title in ascending order.
     * songs.album_track_number is set to the playlist_track_number
     * to make use of the existing attribute.
     * @param playlistId [Long] the playlist_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT songs.id, songs.title, songs.artist_id, songs.album_id, songs.genre_id, songs.year, song_playlist_entries.playlist_track_number+1 AS album_track_number, songs.lyrics, songs.composer_id, songs.date_added, songs.date_modified, songs.date_last_played, songs.duration FROM playlists 
        LEFT JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        LEFT JOIN songs ON songs.id = song_playlist_entries.song_id
        WHERE playlist_id = :playlistId
        ORDER BY songs.title ASC
        """
    )
    abstract fun sortSongsInPlaylistByTitleAsc(playlistId: Long): Flow<List<Song>>

    /**
     * Returns a flow of the list of songs filtered by playlist_id,
     * sorted by song title in descending order.
     * songs.album_track_number is set to the playlist_track_number
     * to make use of the existing attribute.
     * @param playlistId [Long] the playlist_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT songs.id, songs.title, songs.artist_id, songs.album_id, songs.genre_id, songs.year, song_playlist_entries.playlist_track_number+1 AS album_track_number, songs.lyrics, songs.composer_id, songs.date_added, songs.date_modified, songs.date_last_played, songs.duration FROM playlists 
        LEFT JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        LEFT JOIN songs ON songs.id = song_playlist_entries.song_id
        WHERE playlist_id = :playlistId
        ORDER BY songs.title DESC
        """
    )
    abstract fun sortSongsInPlaylistByTitleDesc(playlistId: Long): Flow<List<Song>>

    //retrieves mapping of playlist to its songs on specified playlist Id
    //SELECT playlists.id, playlists.name, playlists.description, playlists.date_created, playlists.date_last_accessed, songs.id, songs.title, songs.artist_id, songs.album_id, songs.date_added, songs.date_modified, songs.date_last_played FROM playlists
    /**
     * Returns a flow of the mapping of a playlist to its list of songs,
     * sorted by song's playlist track number in ascending order.
     * @param playlistId [Long] the playlist_id to match on
     */
    @Transaction
    @Query(
        """
        SELECT playlists.*, songs.id, songs.title, songs.artist_id, songs.album_id, songs.genre_id, songs.year, song_playlist_entries.playlist_track_number+1 AS album_track_number, songs.lyrics, songs.composer_id, songs.date_added, songs.date_modified, songs.date_last_played, songs.duration FROM playlists
        INNER JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        INNER JOIN songs ON songs.id = song_playlist_entries.song_id
        WHERE playlist_id = :playlistId
        ORDER BY song_playlist_entries.playlist_track_number ASC
        """
    )
    abstract fun sortSongsAndPlaylistByTrackNumberAsc(playlistId: Long): Map<Playlist, List<Song>>
    //NOTE: playlist_track_number+1 is to cover the fact that the numbers are autogenerated with 0 as the starting number

    @Transaction
    @Query(
        """
        SELECT song_playlist_entries.song_id FROM playlists
        LEFT JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        WHERE playlists.id = :playlistId
        ORDER BY song_playlist_entries.playlist_track_number ASC
        LIMIT :limit
        """
    )
    abstract fun getSongsByPlaylistId(playlistId: Long, limit: Int): Flow<List<Long>>

    //need update function, for updating song_playlist_entries, for removing songs from playlist
    //maybe need delete function / remove hide function

    /**
     * Returns the integer value of the total amount of records in playlists table
     * NOTE: Must be called within a coroutine, since it doesn't return a flow
     */
    @Query("SELECT COUNT(*) FROM playlists")
    abstract suspend fun count(): Int
}
