/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.music.player.model

import com.example.music.data.database.model.SongToAlbum
import com.example.music.model.AlbumInfo
//import com.example.music.model.ArtistInfo
import com.example.music.model.SongInfo
import java.time.Duration

/**
 * Song data with necessary information to be used within a player.
 * TODO: rework SongToAlbum or create new model that contains artistInfo
 */

data class PlayerSong(
    var id: Long? = 0,
    var title: String? = "",
    var artistName: String? = "",
    var albumTitle: String? = "",
    var duration: Duration? = null,
    val artwork: String? = "",
/*
    val dateAdded - song.dateAdded
    val dateModified - song.dateModified
    val dateLastPlayed - song.dateLastPlayed
    val lyric - song.lyric
     */
) {
    constructor(songInfo: SongInfo, /*artistInfo: ArtistInfo,*/ albumInfo: AlbumInfo) : this(
        id = songInfo.id,
        title = songInfo.title,
        //artistName = artistInfo.name,
        albumTitle = albumInfo.title,
        duration = songInfo.duration,
        artwork = albumInfo.artwork,
    )
}
fun SongToAlbum.toPlayerSong(): PlayerSong =
    PlayerSong(
        id = song.id,
        title = song.title,
        //artistName = artist.name,
        albumTitle = album.title,
        duration = song.duration,
        artwork = album.artwork,
    )
