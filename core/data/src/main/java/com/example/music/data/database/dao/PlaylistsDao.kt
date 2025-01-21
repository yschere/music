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

package com.example.music.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
//import com.example.music.data.database.model.PlaylistWithSongCount
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Playlist] related operations.
 */

@Dao
abstract class PlaylistsDao : BaseDao<Playlist> {

    //select song info on value param songs.id
    @Query(
        """
        SELECT * FROM playlists
        WHERE id = :id
        """
    )
    abstract fun getPlaylistById(id: Long): Playlist?

    @Query(
        """
        SELECT * FROM playlists
        WHERE name = :name
        """
    )
    abstract fun observePlaylist(name: String): Flow<Playlist>

    //select most recent playlists based on limit
    @Query(
        """
        SELECT * FROM playlists
        ORDER BY datetime(date_last_accessed) DESC
        LIMIT :limit
        """
    )
    abstract fun mostRecentPlaylists(limit: Int): Flow<List<Playlist>>

    //return playlists count
    @Query("SELECT COUNT(*) FROM playlists")
    abstract suspend fun count(): Int

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM playlists
        LEFT JOIN (
            SELECT songs.id, song_playlist_entries.playlist_id, COUNT(song_playlist_entries.song_id) as count, MAX(songs.date_last_played) as date_last_played
            FROM songs
            INNER JOIN song_playlist_entries on songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) as song_playlist_entries ON playlists.id = song_playlist_entries.playlist_id
        ORDER BY song_playlist_entries.count DESC
        LIMIT :limit
        """ //need to figure out how to include song info if want playlist extra info as is, like the last accessed date
    )
    abstract fun sortPlaylistsBySongCount(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    //retrieves songIds on specified playlist Id
//    @Transaction
//    @Query(
//        """
//        SELECT songs.* FROM songs
//        INNER JOIN song_playlist_entries ON song_playlist_entries.song_id = songs.id
//        INNER JOIN playlists ON playlists.id = song_playlist_entries.playlist_id
//        WHERE playlists.id = :playlistId
//        """
//    )
//    abstract fun songsInPlaylist(playlistId: Long): Flow<List<Song>>

    //return all playlists and select info needed for PlaylistViewModel
    //ordered by name ascending
//    @Query(
//        """
//        SELECT name FROM playlists
//        ORDER BY name ASC
//        """
//    )
//    abstract fun allPlaylistNames(): List<String>

    //TODO: create query to retrieve playlist data and their last played datetime, ordered by last played
    //last played would be the time of the last played song
    //so per playlist, order it by its last played song
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT playlists.*, date_last_played FROM playlists
        INNER JOIN (
            SELECT songs.*, song_playlist_entries.playlist_id, MAX(songs.date_last_played) as date_last_played
            FROM songs
            INNER JOIN song_playlist_entries on songs.id = song_playlist_entries.song_id
            GROUP BY song_playlist_entries.playlist_id
        ) songs ON playlists.id = songs.playlist_id
        ORDER BY date_last_played DESC
        LIMIT :limit
        """
    )
    //abstract fun sortPlaylistsByLastPlayed(limit: Int): List<SongPlaylistEntry>
    abstract fun sortPlaylistsByLastPlayed(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<PlaylistWithExtraInfo>>

    //need insert function
    //need update function
    //need insertAll function
    //maybe need delete function / remove hide function

    //selects episodes on com.example.music.ui.album uri, ordered on datetime with limit
    //could use this idea for songs in album, artist
    //order by and limit dependent on context
//    @Transaction
//    @Query(
//        """
//        SELECT * FROM episodes WHERE podcast_uri = :podcastUri
//        ORDER BY datetime(published) DESC
//        LIMIT :limit
//        """
//    )
//    abstract fun episodesForPodcastUri(
//        podcastUri: String,
//        limit: Int
//    ): Flow<List<EpisodeToPodcast>>

    //select episodes joined to com.example.music.ui.album category entries on provided category, using datetime order and limit
    //could repurpose for selected view based on genre? for album and artist
//    @Transaction
//    @Query(
//        """
//        SELECT episodes.* FROM episodes
//        INNER JOIN podcast_category_entries ON episodes.podcast_uri = podcast_category_entries.podcast_uri
//        WHERE category_id = :categoryId
//        ORDER BY datetime(published) DESC
//        LIMIT :limit
//        """
//    )
//    abstract fun episodesFromPodcastsInCategory(
//        categoryId: Long,
//        limit: Int
//    ): Flow<List<EpisodeToPodcast>>

    //select episodes based on com.example.music.ui.album, ordered by datetime and using limit
//    @Transaction
//    @Query(
//        """
//        SELECT * FROM episodes WHERE podcast_uri IN (:podcastUris)
//        ORDER BY datetime(published) DESC
//        LIMIT :limit
//        """
//    )
//    abstract fun episodesForPodcasts(
//        podcastUris: List<String>,
//        limit: Int
//    ): Flow<List<EpisodeToPodcast>>
}
