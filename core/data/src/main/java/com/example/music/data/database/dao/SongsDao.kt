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
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [Song] related operations.
 */

@Dao
abstract class SongsDao : BaseDao<Song> {

    //select song info on value param songs.id
    @Query(
        """
        SELECT * FROM songs
        WHERE id = :id
        """
    )
    abstract fun getSongById(id: Long): Flow<Song> //equivalent of episode

    @Query(
        """
        SELECT * FROM songs
        WHERE title = :title
        """
    )
    abstract fun getSongByTitle(title: String): Flow<Song> //NO EQUIVALENT

    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        INNER JOIN albums ON songs.album_id = albums.id
        WHERE songs.id = :songId
        """
    )
    abstract fun getSongAndAlbumBySongId(songId: Long): Flow<SongToAlbum> //equivalent of episodeAndPodcast

    //select most recent songs based on limit
    @Query(
        """
        SELECT * FROM songs
        ORDER BY datetime(date_added) DESC
        LIMIT :limit
        """
    )
    abstract fun mostRecentSongs(limit: Int): Flow<List<Song>> //NO EQUIVALENT

    //return episodes count
    @Query("SELECT COUNT(*) FROM songs")
    abstract suspend fun count(): Int


    //query to retrieve songs for a specified artist using param artist Id in ascending order
    //order by column passed as param sortCol
    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        INNER JOIN artists on artists.id = songs.artist_id
        WHERE artists.id = :artistId
        ORDER BY artists.name ASC
        LIMIT :limit
        """
    )
    abstract fun getSongsByArtistId(artistId: Long, limit: Int): Flow<List<Song>>

    //retrieve list of songs and albums within list of albumIds
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE artist_id IN (:artistIds)
        ORDER BY datetime(date_last_played)
        LIMIT :limit
        """
    )
    abstract fun getSongsByArtistIds(artistIds: List<Long>, limit: Int): Flow<List<Song>>


    //retrieve list of songs by their albumId
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE album_id = :albumId
        ORDER BY datetime(date_last_played)
        LIMIT :limit
        """
    )
    abstract fun getSongsByAlbumId(albumId: Long, limit: Int): Flow<List<Song>> //equivalent for episodesForPodcastUri

    //return list of songs and album using specified album id
    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        INNER JOIN (
            SELECT id as albumId FROM albums
        ) ON songs.album_id = albumId
        WHERE songs.album_id = :albumId
        ORDER BY songs.title ASC
        """
    )
    abstract fun getSongsAndAlbumByAlbumId(albumId: Long): Flow<List<SongToAlbum>>

    //retrieve list of songs and albums within list of albumIds
    @Transaction
    @Query(
        """
        SELECT * FROM songs
        WHERE album_id IN (:albumIds)
        ORDER BY datetime(date_last_played)
        LIMIT :limit
        """
    )
    abstract fun getSongsAndAlbumsByAlbumIds(albumIds: List<Long>, limit: Int): Flow<List<SongToAlbum>> //equivalent for episodesForPodcasts

    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id = :genreId
        ORDER BY date_last_played DESC
        LIMIT :limit
        """
    )
    abstract fun getSongsInGenreSortedByLastPlayed(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM songs
        WHERE genre_id IN (:genreIds)
        GROUP BY genre_id
        ORDER BY date_last_played DESC
        LIMIT :limit
        """
    )
    abstract fun getSongsInGenresSortedByLastPlayed(genreIds: List<Long>, limit: Int): Flow<List<Song>>


    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT *, COUNT(*) as genre_count FROM songs
        WHERE genre_id IN (:genreIds)
        GROUP BY genre_id
        ORDER BY genre_count ASC
        LIMIT :limit
        """
    )
    abstract fun getSongsInGenresSortedByCount(genreIds: List<Long>, limit: Int): Flow<List<Song>>


    @Transaction
    @Query(
        """
        SELECT songs.* FROM songs
        INNER JOIN (
            SELECT albums.* FROM albums
            WHERE albums.genre_id = :genreId
        ) as albums on albums.id = songs.album_id
        WHERE songs.genre_id = :genreId
        ORDER BY songs.date_last_played DESC
        LIMIT :limit
        """
    )
    abstract fun getSongsAndAlbumsInGenreSortedByLastPlayed( //equivalent of categoriesDao.episodesFromPodcastsInCategory
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<SongToAlbum>>

}

//retrieves songs from specified playlist
//starts with songs joins to song_playlist_entries to retrieve songs in playlist
//    @Transaction
//    @Query(
//        """
//        SELECT songs.* FROM songs
//        INNER JOIN song_playlist_entries ON song_playlist_entries.song_id = songs.id
//        WHERE song_playlist_entries.playlist_id = :playlistId
//        ORDER BY song_playlist_entries.playlist_track_number
//        """
//    )
//    abstract fun songsInPlaylist(playlistId: Long): Flow<List<Song>>


