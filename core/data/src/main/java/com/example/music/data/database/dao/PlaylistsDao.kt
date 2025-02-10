package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.database.model.Song
//import com.example.music.data.database.model.PlaylistWithSongCount
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Playlist] related operations.
 */

@Dao
abstract class PlaylistsDao : BaseDao<Playlist> {

    @Query(
        """
        SELECT * FROM playlists
        """
    )
    abstract fun getAllPlaylists(): Flow<List<Playlist>>

    //select song info on value param songs.id
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

    @Query(
        """
        SELECT * FROM playlists
        WHERE name = :name
        """
    )
    abstract fun observePlaylist(name: String): Flow<Playlist>

    @Query(
        """
        SELECT playlists.*, MAX(songs.date_last_played) AS date_last_played, COUNT(songs.id) AS song_count FROM playlists
        INNER JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        INNER JOIN (
            SELECT songs.id, songs.date_last_played FROM songs
        ) AS songs ON songs.id = song_playlist_entries.song_id
        WHERE playlists.id = :playlistId
        """
    )
    abstract fun getPlaylistExtraInfo(playlistId: Long): Flow<PlaylistWithExtraInfo>

    @Query(
        """
        SELECT * FROM playlists
        ORDER BY name ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByNameAsc(limit: Int): Flow<List<Playlist>>

    @Query(
        """
        SELECT * FROM playlists
        ORDER BY name DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByNameDesc(limit: Int): Flow<List<Playlist>>

    @Query(
        """
        SELECT * FROM playlists
        ORDER BY date_created ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateCreatedAsc(limit: Int): Flow<List<Playlist>>

    @Query(
        """
        SELECT * FROM playlists
        ORDER BY date_created DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateCreatedDesc(limit: Int): Flow<List<Playlist>>

    @Query(
        """
        SELECT * FROM playlists
        ORDER BY datetime(date_last_accessed) ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateLastAccessedAsc(limit: Int): Flow<List<Playlist>>

    //select most recent playlists based on limit
    @Query(
        """
        SELECT * FROM playlists
        ORDER BY datetime(date_last_accessed) DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateLastAccessedDesc(limit: Int): Flow<List<Playlist>> //mostRecentPlaylists(limit: Int): Flow<List<Playlist>>
    // want this to be used for ... add to playlist i think? ie if adding a song to playlist
    // and added a song previously, want that previous playlist to show up first

    // retrieve playlist data and their last played datetime, ordered by last played
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT playlists.*, date_last_played, song_count FROM playlists
        INNER JOIN (
            SELECT songs.*, song_playlist_entries.playlist_id, MAX(songs.date_last_played) as date_last_played, COUNT(songs.id) as song_count
            FROM songs
            INNER JOIN song_playlist_entries on songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) songs ON playlists.id = songs.playlist_id
        ORDER BY date_last_played ASC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateLastPlayedAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    // retrieve playlist data and their last played datetime, ordered by last played
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT playlists.*, date_last_played, song_count FROM playlists
        INNER JOIN (
            SELECT songs.*, song_playlist_entries.playlist_id, MAX(songs.date_last_played) as date_last_played, COUNT(songs.id) as song_count
            FROM songs
            INNER JOIN song_playlist_entries on songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) songs ON playlists.id = songs.playlist_id
        ORDER BY date_last_played DESC
        LIMIT :limit
        """
    )
    abstract fun sortPlaylistsByDateLastPlayedDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT playlists.*, date_last_played, song_count FROM playlists
        INNER JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) as song_count, MAX(songs.date_last_played) as date_last_played
            FROM songs
            INNER JOIN song_playlist_entries on songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) as song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY song_count ASC
        LIMIT :limit
        """ //need to figure out how to include song info if want playlist extra info as is, like the last accessed date
    )
    abstract fun sortPlaylistsBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT playlists.*, date_last_played, song_count FROM playlists
        INNER JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) as song_count, MAX(songs.date_last_played) as date_last_played
            FROM songs
            INNER JOIN song_playlist_entries on songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) as song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY song_count DESC
        LIMIT :limit
        """ //need to figure out how to include song info if want playlist extra info as is, like the last accessed date
    )
    abstract fun sortPlaylistsBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    //retrieves songs on specified playlist Id
    @Transaction
    @Query(
        """
        SELECT songs.* FROM playlists 
        INNER JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        INNER JOIN songs ON songs.id = song_playlist_entries.song_id
        WHERE playlist_id = :playlistId
        ORDER BY song_playlist_entries.playlist_track_number ASC
        """
    )
    abstract fun sortSongsInPlaylistByTrackNumberAsc(playlistId: Long): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT songs.* FROM playlists 
        INNER JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        INNER JOIN songs ON songs.id = song_playlist_entries.song_id
        WHERE playlist_id = :playlistId
        ORDER BY song_playlist_entries.playlist_track_number DESC
        """
    )
    abstract fun sortSongsInPlaylistByTrackNumberDesc(playlistId: Long): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT songs.* FROM playlists 
        INNER JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        INNER JOIN songs ON songs.id = song_playlist_entries.song_id
        WHERE playlist_id = :playlistId
        ORDER BY songs.title ASC
        """
    )
    abstract fun sortSongsInPlaylistByTitleAsc(playlistId: Long): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT songs.* FROM playlists 
        INNER JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        INNER JOIN songs ON songs.id = song_playlist_entries.song_id
        WHERE playlist_id = :playlistId
        ORDER BY songs.title DESC
        """
    )
    abstract fun sortSongsInPlaylistByTitleDesc(playlistId: Long): Flow<List<Song>>

    //retrieves mapping of playlist to its songs on specified playlist Id
    //SELECT playlists.id, playlists.name, playlists.description, playlists.date_created, playlists.date_last_accessed, songs.id, songs.title, songs.artist_id, songs.album_id, songs.date_added, songs.date_modified, songs.date_last_played FROM playlists
    @Transaction
    @Query(
        """
        SELECT playlists.*, songs.* FROM playlists
        INNER JOIN song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        INNER JOIN songs ON songs.id = song_playlist_entries.song_id
        WHERE playlist_id = :playlistId
        ORDER BY song_playlist_entries.playlist_track_number ASC
        """
    )
    abstract fun sortSongsAndPlaylistByTrackNumberAsc(playlistId: Long): Map<Playlist, List<Song>>

    //need update function
    //maybe need delete function / remove hide function

    //return playlists count
    @Query("SELECT COUNT(*) FROM playlists")
    abstract suspend fun count(): Int
}
