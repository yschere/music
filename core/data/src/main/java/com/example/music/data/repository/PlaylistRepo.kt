/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.music.data.repository

import com.example.music.data.database.dao.PlaylistsDao
import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.database.model.Song
//import com.example.music.data.database.model.PlaylistWithSongCount
import kotlinx.coroutines.flow.Flow

interface PlaylistRepo {

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(id: Long): Flow<Playlist>

    fun getPlaylistsByIds(ids: List<Long>): Flow<List<Playlist>>

    fun observePlaylist(name: String): Flow<Playlist>

    fun getPlaylistExtraInfo(playlistId: Long): Flow<PlaylistWithExtraInfo>

    fun sortPlaylistsByNameAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Playlist>>

    fun sortPlaylistsByNameDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Playlist>>

    fun sortPlaylistsByDateCreatedAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Playlist>>

    fun sortPlaylistsByDateCreatedDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Playlist>>

    fun sortPlaylistsByDateLastAccessedAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Playlist>>

    fun sortPlaylistsByDateLastAccessedDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Playlist>>

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

    override fun observePlaylist(name: String): Flow<Playlist> =
        playlistDao.observePlaylist(name)

    override fun getPlaylistExtraInfo(playlistId: Long): Flow<PlaylistWithExtraInfo> =
        playlistDao.getPlaylistExtraInfo(playlistId)

    override fun sortPlaylistsByNameAsc(limit: Int): Flow<List<Playlist>> =
        playlistDao.sortPlaylistsByNameAsc(limit)

    override fun sortPlaylistsByNameDesc(limit: Int): Flow<List<Playlist>> =
        playlistDao.sortPlaylistsByNameDesc(limit)

    override fun sortPlaylistsByDateCreatedAsc(limit: Int): Flow<List<Playlist>> =
        playlistDao.sortPlaylistsByDateCreatedAsc(limit)

    override fun sortPlaylistsByDateCreatedDesc(limit: Int): Flow<List<Playlist>> =
        playlistDao.sortPlaylistsByDateCreatedDesc(limit)

    override fun sortPlaylistsByDateLastAccessedAsc(limit: Int): Flow<List<Playlist>> =
        playlistDao.sortPlaylistsByDateLastAccessedAsc(limit)

    override fun sortPlaylistsByDateLastAccessedDesc(limit: Int): Flow<List<Playlist>> =
        playlistDao.sortPlaylistsByDateLastAccessedDesc(limit)

    override fun sortPlaylistsByDateLastPlayedAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsByDateLastPlayedAsc()

    override fun sortPlaylistsByDateLastPlayedDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsByDateLastPlayedDesc()

    override fun sortPlaylistsBySongCountAsc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsBySongCountAsc()

    override fun sortPlaylistsBySongCountDesc(limit: Int): Flow<List<PlaylistWithExtraInfo>> =
        playlistDao.sortPlaylistsBySongCountDesc()

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
