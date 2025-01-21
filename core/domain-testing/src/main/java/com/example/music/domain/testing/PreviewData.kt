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

package com.example.music.domain.testing

import com.example.music.model.AlbumInfo
import com.example.music.model.AlbumToSongInfo
import com.example.music.model.ArtistInfo
import com.example.music.model.GenreInfo
import com.example.music.model.PlaylistInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset

val PreviewGenres = listOf(
    GenreInfo(id = 0, name = "Alternative"),
    GenreInfo(id = 1, name = "Soundtrack"),
    GenreInfo(id = 2, name = "Pop"),
    GenreInfo(id = 3, name = "JPop")
)

//artist_id 113 = LM.C
//artist_id 22 = Paramore
//artist_id 9381 = ACIDMAN
//artist_id 6 = Yoko Shimomura
//album_id 1145 = "88 / ...With Vampire - Single"
//album_id 71 = "Wonderful Wonderholic"
//album_id 281 = "Slow Rain"
//album_id 123 = "Riot!"
//album_id 124 = "Brand New Eyes"
//album_id 125 = "After Laughter"
//album_id 307 = "Kingdom Hearts Piano Collections - Field & Battle"

val PreviewArtists = listOf(
    ArtistInfo(
        id = 113,
        name = "LM.C",
        genreId = 2 //Pop
    ),
    ArtistInfo(
        id = 22,
        name = "Paramore",
        genreId = 0 //Alternative
    ),
    ArtistInfo(
        id = 9381,
        name = "ACIDMAN",
        genreId = 3 //JPop
    ),
    ArtistInfo(
        id = 6,
        name = "Yoko Shimomura",
        genreId = 1 //Soundtrack
    )
)

val PreviewSongs = listOf(
    SongInfo(
        id = 1023, //id
        title = "88", //title
        artistId = 113, //artist_id
        albumId = 1145, //album_id
        genreId = 2, //genre_id
        albumTrackNumber = 1, //track_number
        //duration = Duration.parse("PT4M"),//5S"),
        duration = Duration.ofSeconds(232),
        dateLastPlayed = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        )
    ),
    SongInfo(
        id = 103, //id
        title = "Ghost heart", //title
        artistId = 113, //artist_id
        albumId = 71, //album_id
        genreId = 2, //genre_id
        albumTrackNumber = 4, //track_number
        duration = Duration.ofSeconds(217),
        //duration = Duration.parse("PT3M"),//13S"),
        dateLastPlayed = OffsetDateTime.of(
            2023, 12, 9, 4,
            24, 0, 0, ZoneOffset.of("-0800")
        )
    ),
    SongInfo(
        id = 528, //id
        title = "Slow Rain", //title
        artistId = 9381, //artist_id
        albumId = 281, //album_id
        genreId = 3, //genre_id
        albumTrackNumber = 1, //track_number
        duration = Duration.ofSeconds(271),
        //duration = Duration.parse("PT3M"),//42S"),
        dateLastPlayed = null
    ),
    SongInfo(
        id = 529, //id
        title = "Isotope (instrumental)", //title
        artistId = 9381, //artist_id
        albumId = 281, //album_id
        genreId = 3, //genre_id
        albumTrackNumber = 2, //track_number
        duration = Duration.ofSeconds(184),
        //duration = Duration.parse("PT3M"),//19S"),
        dateLastPlayed = null
    ),
    SongInfo(
        id = 530, //id
        title = "Walking Dada", //title
        artistId = 9381, //artist_id
        albumId = 281, //album_id
        genreId = 3, //genre_id
        albumTrackNumber = 3, //track_number
        duration = Duration.ofSeconds(143),
        //duration = Duration.parse("PT4M"),//55S"),
        dateLastPlayed = null
    ),
    SongInfo(
        id = 12, //id
        title = "Misery Business", //title
        artistId = 22, //artist_id
        albumId = 123, //album_id
        genreId = 0, //genreId
        albumTrackNumber = 8, //track_number
        duration = Duration.ofSeconds(212),
        //duration = Duration.parse("PT4M"),//10S"),
        dateLastPlayed = null
    ),
    SongInfo(
        id = 17, //id
        title = "Ignorance", //title
        artistId = 22, //artist_id
        albumId = 124, //album_id
        genreId = 0, //genre_id
        albumTrackNumber = 2, //track_number
        duration = Duration.ofSeconds(218),
        //duration = Duration.parse("PT4M"),//26S"),
        dateLastPlayed = null
    ),
    SongInfo(
        id = 21, //id
        title = "Hard Times", //title
        artistId = 22, //artist_id
        albumId = 125, //album_id
        genreId = 0, //genre_id
        albumTrackNumber = 11, //track_number
        duration = Duration.ofSeconds(183),
        //duration = Duration.parse("PT2M"),//51S"),
        dateLastPlayed = null
    ),
    SongInfo(
        id = 6535, //id
        title = "Musique pour la Tristesse de Xion", //title
        artistId = 6, //artist_id
        albumId = 307, //album_id
        genreId = 1, //genre_id
        albumTrackNumber = 9, //track_number
        duration = Duration.ofSeconds(336),
        //duration = Duration.parse("PT6M"),//22S"),
        dateLastPlayed = OffsetDateTime.of(
            2025, 1, 3, 16,
            24, 45, 0, ZoneOffset.of("-0800")
        )
    ),
)

val PreviewAlbums = listOf(
    AlbumInfo(
        id = 1145,
        title = "88 / ...With Vampire - Single",
        albumArtistId = 113,
        genreId = 2,
        artwork = "",
    ),
    AlbumInfo(
        id = 71,
        title = "Wonderful Wonderholic",
        albumArtistId = 113,
        genreId = 2,
        artwork = "",
    ),
    AlbumInfo(
        id = 281,
        title = "Slow Rain",
        albumArtistId = 9381,
        genreId = 3,
        artwork = "",
    ),
    AlbumInfo(
        id = 123,
        title = "Riot!",
        albumArtistId = 22,
        genreId = 0,
        artwork = "",
    ),
    AlbumInfo(
        id = 124,
        title = "Brand New Eyes",
        albumArtistId = 22,
        genreId = 0,
        artwork = "",
    ),
    AlbumInfo(
        id = 125,
        title = "After Laughter",
        albumArtistId = 22,
        genreId = 0,
        artwork = "",
    ),
    AlbumInfo(
        id = 307,
        title = "Kingdom Hearts Piano Collections - Field & Battle",
        albumArtistId = 6,
        genreId = 1,
        artwork = "",
    ),
)

val PreviewPlaylists = listOf(
    PlaylistInfo(
        id = 0,
        name = "hello",
        description = "HIHIHIHIHI",
        creationDate = OffsetDateTime.now(),
        //tracks = intArrayOf(528, 529, 530),
        //newTrackArray based on SongPlaylistCombo = intArrayOf(17, 530, 529, 528, 1023, 103)
    ),
    PlaylistInfo(
        id = 1,
        name = "ack",
        description = "",
        creationDate = OffsetDateTime.of(
            2021, 10, 23, 1,
            4, 5, 0, ZoneOffset.of("-0800")
        ),
        //tracks = intArrayOf(1023, 6535, 103, 17),
        //newTrackArray based on SongPlaylistCombo = intArrayOf(1023, 12, 17, 21)
    ),
    PlaylistInfo(
        id = 2,
        name = "give the goods",
        description = "MAKE ME FEEL SOMETHING",
        creationDate = OffsetDateTime.of(
            2018, 5, 3, 5,
            49, 0, 0, ZoneOffset.of("-0800")
        ),
        //tracks = intArrayOf(1023, 103, 17, 21, 6535),
        //newTrackArray based on SongPlaylistCombo = intArrayOf(6535, 6535)
    ),
)

val PreviewPlayerSongs = listOf(
    PlayerSong(
        PreviewSongs[0],
        PreviewAlbums[0]
    )
)

val PreviewAlbumSongs = listOf(
    AlbumToSongInfo(
        album = PreviewAlbums[0],
        song = PreviewSongs[0]
    ),
    AlbumToSongInfo(
        album = PreviewAlbums[1],
        song = PreviewSongs[1]
    ),
    AlbumToSongInfo(
        album = PreviewAlbums[2],
        song = PreviewSongs[2]
    ),
    AlbumToSongInfo(
        album = PreviewAlbums[2],
        song = PreviewSongs[3]
    ),
    AlbumToSongInfo(
        album = PreviewAlbums[2],
        song = PreviewSongs[4]
    ),
    AlbumToSongInfo(
        album = PreviewAlbums[3],
        song = PreviewSongs[5]
    ),
    AlbumToSongInfo(
        album = PreviewAlbums[4],
        song = PreviewSongs[6]
    ),
    AlbumToSongInfo(
        album = PreviewAlbums[5],
        song = PreviewSongs[7]
    ),
    AlbumToSongInfo(
        album = PreviewAlbums[6],
        song = PreviewSongs[8]
    ),
)
data class SongPlaylistCombo(val id: Int, val playlistId: Int, val songId: Int, val playlistTrackNumber: Int)

val PreviewSongPlaylistCombo = listOf(
    SongPlaylistCombo(
        0, 0, 17, 0
    ),
    SongPlaylistCombo(
        1, 0, 530, 1
    ),
    SongPlaylistCombo(
        2, 0, 529, 2
    ),
    SongPlaylistCombo(
        3, 0, 528, 3
    ),
    SongPlaylistCombo(
        4, 0, 1023, 4
    ),
    SongPlaylistCombo(
        5, 1, 1023, 0
    ),
    SongPlaylistCombo(
        6, 1, 12, 1
    ),
    SongPlaylistCombo(
        7, 1, 17, 2
    ),
    SongPlaylistCombo(
        8, 1, 21, 3
    ),
    SongPlaylistCombo(
        9, 2, 6535, 0
    ),
    SongPlaylistCombo(
        10, 2, 6535, 1
    ),
    SongPlaylistCombo(
        11, 0, 103, 5
    )
)

fun getAlbumData(albumId: Long): AlbumInfo = PreviewAlbums.single { s -> s.id == albumId }
fun getSongData(songId: Long): SongInfo = PreviewSongs.single { s -> s.id == songId }
fun getGenreData(genreId: Long): GenreInfo = PreviewGenres.single { s -> s.id == genreId }
fun getArtistData(artistId: Long): ArtistInfo = PreviewArtists.single { s -> s.id == artistId }
