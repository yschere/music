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
//import com.example.music.data.database.model.PlaylistWithSongCount
import kotlinx.coroutines.flow.Flow

interface PlaylistStore {

    fun getPlaylistById(id: Long): Playlist?

    fun observePlaylist(name: String): Flow<Playlist>

    fun mostRecentPlaylists(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Playlist>>

    fun sortPlaylistsBySongCount(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    fun sortPlaylistsByLastPlayed(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    //fun songsInPlaylist(playlistId: Long): Flow<List<Song>>

    //fun allPlaylistNames(): List<String>

    //fun lastPlayedPlaylists(): List<PlaylistExtraInfo>

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun isEmpty(): Boolean
}

/**
 * A data repository for [Playlist] instances.
 */
class LocalPlaylistStore(
    private val playlistDao: PlaylistsDao
) : PlaylistStore {

    override fun getPlaylistById(id: Long): Playlist? {
        return playlistDao.getPlaylistById(id)
    }

    override fun mostRecentPlaylists(limit: Int): Flow<List<Playlist>> {
        return playlistDao.mostRecentPlaylists(limit)
    }

    override fun observePlaylist(name: String): Flow<Playlist> {
        return playlistDao.observePlaylist(name)
    }

    override fun sortPlaylistsBySongCount(limit: Int): Flow<List<PlaylistWithExtraInfo>> {
        return playlistDao.sortPlaylistsBySongCount(limit)
    }

    override fun sortPlaylistsByLastPlayed(limit: Int): Flow<List<PlaylistWithExtraInfo>> {
        return playlistDao.sortPlaylistsByLastPlayed(limit)
    }

//    override fun songsInPlaylist(playlistId: Long): Flow<List<Song>> {
//        return playlistDao.songsInPlaylist(playlistId)
//    }

//    override fun allPlaylistNames(): List<String> {
//        return playlistDao.allPlaylistNames()
//    }

//    override fun lastPlayedPlaylists(): List<PlaylistExtraInfo> {
//        return playlistDao.lastPlayedPlaylists()
//    }

    /**
     * Add a new [Playlist] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addPlaylist(playlist: Playlist) {
        playlistDao.insert(playlist)
    }

    override suspend fun isEmpty(): Boolean = playlistDao.count() == 0
}
