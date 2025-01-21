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

package com.example.music.data.database.model

//TODO: See if below is the correct iteration of song-playlist
//Intended purpose: to have the playlists song data storage here
//contains playlistId, songId, playlistTrackNumber

/**
 * Entity containing table song_playlist_entries / data class SongPlaylistEntry.
 * Used to contain many-to-many relationship between songs and playlists.
 */
//class SongToPlaylist {
//    @Embedded
//    lateinit var song: Song
//
//    @Relation(parentColumn = "playlist_id", entityColumn = "id")
//    lateinit var playlistEntries: List<SongPlaylistEntry>
//
//    @Relation(parentColumn = "playlist_id", entityColumn = "playlist_id")
//    lateinit var playlist: List<Playlist>
//
//    @get:Ignore
//    val playlistEntry: SongPlaylistEntry
//        get() = playlistEntries[0]
//
//    /*** Allow consumers to destructure this class ***/
//    operator fun component1() = song
//    operator fun component2() = playlistEntry
//    operator fun component3() = playlist
//
//    override fun equals(other: Any?): Boolean = when {
//        other === this -> true
//        //need to
//        other is SongToPlaylist -> song == other.song && playlistEntries == other.playlistEntries
//        else -> false
//    }
//
//    override fun hashCode(): Int = Objects.hash(song, playlistEntries)
//}

//data class PlaylistsToSong(
//    @Embedded val song: Song,
//    @Relation(
//        parentColumn = "song_id",
//        entityColumn = "playlist_id",
//        associateBy = Junction(SongPlaylistEntry::class)
//    )
//    val playlists: List<Playlist>
//)