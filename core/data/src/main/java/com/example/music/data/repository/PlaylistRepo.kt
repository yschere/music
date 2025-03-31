package com.example.music.data.repository

import com.example.music.data.database.dao.PlaylistsDao
import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.database.model.Song
//import com.example.music.data.database.model.PlaylistWithSongCount
import kotlinx.coroutines.flow.Flow

/**
 * Interface for [PlaylistsDao] abstract functions
 */
interface PlaylistRepo {

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(id: Long): Flow<Playlist>

    fun getPlaylistsByIds(ids: List<Long>): Flow<List<Playlist>>

    fun getPlaylistByName(name: String): Flow<Playlist>

    fun getPlaylistWithExtraInfo(playlistId: Long): Flow<PlaylistWithExtraInfo>

    fun sortPlaylistsByNameAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>
    fun sortPlaylistsByNameDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    fun sortPlaylistsByDateCreatedAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>
    fun sortPlaylistsByDateCreatedDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    fun sortPlaylistsByDateLastAccessedAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>
    fun sortPlaylistsByDateLastAccessedDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    fun sortPlaylistsByDateLastPlayedAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>
    fun sortPlaylistsByDateLastPlayedDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    fun sortPlaylistsBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>
    fun sortPlaylistsBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    fun sortSongsInPlaylistByTrackNumberAsc(playlistId: Long): Flow<List<Song>>
    fun sortSongsInPlaylistByTrackNumberDesc(playlistId: Long): Flow<List<Song>>

    fun sortSongsInPlaylistByTitleAsc(playlistId: Long): Flow<List<Song>>
    fun sortSongsInPlaylistByTitleDesc(playlistId: Long): Flow<List<Song>>

    fun sortSongsAndPlaylistByTrackNumberAsc(playlistId: Long): Map<Playlist, List<Song>>

    suspend fun addPlaylist(playlist: Playlist): Long

    suspend fun addPlaylists(playlists: Collection<Playlist>)

    suspend fun count(): Int

    suspend fun isEmpty(): Boolean
}

/**
 * A data repository for [Playlist] instances.
 */
class PlaylistRepoImpl(
    private val playlistDao: PlaylistsDao
) : PlaylistRepo {

    override fun getAllPlaylists(): Flow<List<Playlist>> =
        playlistDao.getAllPlaylists()

    override fun getPlaylistById(id: Long): Flow<Playlist> =
        playlistDao.getPlaylistById(id)

    override fun getPlaylistsByIds(ids: List<Long>): Flow<List<Playlist>> =
        playlistDao.getPlaylistsByIds(ids)

    override fun getPlaylistByName(name: String): Flow<Playlist> =
        playlistDao.getPlaylistByName(name)

    override fun getPlaylistWithExtraInfo(playlistId: Long): Flow<PlaylistWithExtraInfo> =
        playlistDao.getPlaylistWithExtraInfo(playlistId)

    override fun sortPlaylistsByNameAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsByNameAsc(limit)

    override fun sortPlaylistsByNameDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsByNameDesc(limit)

    override fun sortPlaylistsByDateCreatedAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsByDateCreatedAsc(limit)

    override fun sortPlaylistsByDateCreatedDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsByDateCreatedDesc(limit)

    override fun sortPlaylistsByDateLastAccessedAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsByDateLastAccessedAsc(limit)

    override fun sortPlaylistsByDateLastAccessedDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsByDateLastAccessedDesc(limit)

    override fun sortPlaylistsByDateLastPlayedAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsByDateLastPlayedAsc(limit)

    override fun sortPlaylistsByDateLastPlayedDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsByDateLastPlayedDesc(limit)

    override fun sortPlaylistsBySongCountAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsBySongCountAsc(limit)

    override fun sortPlaylistsBySongCountDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsBySongCountDesc(limit)

    override fun sortSongsInPlaylistByTrackNumberAsc(playlistId: Long): Flow<List<Song>> =
        playlistDao.sortSongsInPlaylistByTrackNumberAsc(playlistId)

    override fun sortSongsInPlaylistByTrackNumberDesc(playlistId: Long): Flow<List<Song>> =
        playlistDao.sortSongsInPlaylistByTrackNumberDesc(playlistId)

    override fun sortSongsInPlaylistByTitleAsc(playlistId: Long): Flow<List<Song>> =
        playlistDao.sortSongsInPlaylistByTitleAsc(playlistId)

    override fun sortSongsInPlaylistByTitleDesc(playlistId: Long): Flow<List<Song>> =
        playlistDao.sortSongsInPlaylistByTitleDesc(playlistId)

    override fun sortSongsAndPlaylistByTrackNumberAsc(playlistId: Long): Map<Playlist, List<Song>> =
        playlistDao.sortSongsAndPlaylistByTrackNumberAsc(playlistId)

    /* override fun getSongsAndPlaylistSortedByTrackNumberAsc(playlistId: Long): Map<Playlist, List<Song>> =
        playlistDao.getSongsAndPlaylistSortedByTrackNumberAsc(playlistId) */

    /**
     * Add a new [Playlist] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addPlaylist(playlist: Playlist): Long =
        playlistDao.insert(playlist)

    override suspend fun addPlaylists(playlists: Collection<Playlist>) =
        playlistDao.insertAll(playlists)

    override suspend fun count(): Int = playlistDao.count()

    override suspend fun isEmpty(): Boolean = playlistDao.count() == 0
}
