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

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import java.util.Objects

//TODO: wil be song to playlist table / entity / class
//TODO: create many-to-many relationship between playlists and songs
/**
 * Class SongPlayerData contains song object, song's
 * artistName (from table artists) and song's albumTitle
 * (from table albums). Use for SongPlayer screen.
 *
 * Should serve same purpose as Jet caster's
 * EpisodeToPodcast.toPlayerEpisode within PlayerEpisode model
 */

class SongPlayerData {
    @Embedded
    lateinit var song: Song

    @Relation(
        parentColumn = "artist_id", //playlists.playlist_id
        entityColumn = "id", //playlists.song_id
        associateBy = Junction(Artist::class)
    )
    lateinit var artist: Artist
    //@ColumnInfo(name = "artist_name") var artistName = artist.name
    var artistName = artist.name

    @Relation(
        parentColumn = "album_id", //playlists.playlist_id
        entityColumn = "id", //playlists.song_id
        associateBy = Junction(Album::class)
    )
    lateinit var album: Album
    //@ColumnInfo(name = "album_title") var albumTitle = album.title
    var albumTitle = album.title

    operator fun component1() = song
    operator fun component2() = artistName
    operator fun component3() = albumTitle

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is SongPlayerData -> song == other.song
                && artistName == other.artistName
                && albumTitle == other.albumTitle
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(song, artistName, albumTitle)
}