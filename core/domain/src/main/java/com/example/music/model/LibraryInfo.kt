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

package com.example.music.model

data class LibraryInfo(
    val songs: List<AlbumToSongInfo> = emptyList()
) : List<AlbumToSongInfo> by songs

/*
data class LibraryInfo(
    val songs: List<SongInfo> = emptyList()
) : List<SongInfo> by songs

data class LibraryInfo(
    val songs: List<PlaylistToSongInfo> = emptyList()
) : List<PlaylistToSongInfo> by songs
 */

//was PlaylistToSongInfo, initially intended to store songs and
//playlists data together. currently set to SongInfo so that at
//least the song data will be shown to Home Screen