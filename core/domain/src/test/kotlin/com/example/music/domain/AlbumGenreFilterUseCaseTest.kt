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

package com.example.music.domain

import com.example.music.data.database.model.Album
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.SongToAlbum
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.testing.repository.TestGenreStore
import com.example.music.model.asAlbumToSongInfo
import com.example.music.model.asExternalModel
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.ZoneOffset

class AlbumGenreFilterUseCaseTest {

    private val genresStore = TestGenreStore()
    private val testSongToAlbum = listOf(
        SongToAlbum().apply {
            song = Song(
                id = 1023,
                title = "88",
                artistId = 113,
                albumId = 1145,
                genreId = 2,
                year = 2007,
                albumTrackNumber = 1,
                dateAdded = OffsetDateTime.now(),
                dateModified = OffsetDateTime.now(),
                dateLastPlayed = OffsetDateTime.now(),
                duration = Duration.ofSeconds(453),
            )
            _albums = listOf(
                Album(
                    id = 1145,
                    title = "88 / ...With Vampire - Single",
                    albumArtistId = 113,
                    genreId = 2,
                    artwork = "",
                )
            )
        },
        SongToAlbum().apply {
            song = Song(
                id = 528, //id
                title = "Slow Rain", //title
                artistId = 9381, //artist_id
                albumId = 281, //album_id
                genreId = 3, //genre_id
                albumTrackNumber = 1, //track_number
                duration = Duration.parse("PT3M"),//42S"),
                dateLastPlayed = null
            )
            _albums = listOf(
                Album(
                    id = 281,
                    title = "Slow Rain",
                    albumArtistId = 9381,
                    genreId = 3,
                    artwork = "",
                )
            )
        },
        SongToAlbum().apply {
            song = Song(
                id = 6535, //id
                title = "Musique pour la Tristesse de Xion", //title
                artistId = 6, //artist_id
                albumId = 307, //album_id
                genreId = 1, //genre_id
                albumTrackNumber = 9, //track_number
                duration = Duration.parse("PT6M"),//22S"),
                dateLastPlayed = OffsetDateTime.of(
                    2025, 1, 3, 16,
                    24, 45, 0, ZoneOffset.of("-0800")
                )
            )
            _albums = listOf(
                Album(
                    id = 307,
                    title = "Kingdom Hearts Piano Collections - Field & Battle",
                    albumArtistId = 6,
                    genreId = 1,
                    artwork = "",
                )
            )
        }
    )
    private val testGenre = Genre(1, "Soundtrack")

    val useCase = AlbumGenreFilterUseCase(
        genreStore = genresStore
    )

    @Test
    fun whenGenreNull_emptyFlow() = runTest {
        val resultFlow = useCase(null)

        genresStore.setSongsFromAlbum(testGenre.id, testSongToAlbum) //songsAndAlbumsInGenre(genreId: Long
        genresStore.setAlbumsInGenre(testGenre.id, testAlbums)

        val result = resultFlow.first()
        assertTrue(result.topAlbums.isEmpty())
        assertTrue(result.songs.isEmpty())
    }

    @Test
    fun whenGenreNotNull_validFlow() = runTest {
        val resultFlow = useCase(testGenre.asExternalModel())

        genresStore.setSongsFromAlbum(testGenre.id, testSongToAlbum)
        genresStore.setAlbumsInGenre(testGenre.id, testAlbums)

        val result = resultFlow.first()
        assertEquals(
            testAlbums.map { it.asExternalModel() },
            result.topAlbums
        )
        assertEquals(
            testSongToAlbum.map { it.asAlbumToSongInfo() },
            result.songs
        )
    }

    @Test
    fun whenGenreInfoNotNull_verifyLimitFlow() = runTest {
        val resultFlow = useCase(testGenre.asExternalModel())

        genresStore.setSongsFromAlbum(
            testGenre.id,
            List(8) { testSongToAlbum }.flatten()
        )
        genresStore.setAlbumsInGenre(
            testGenre.id,
            List(4) { testAlbums }.flatten()
        )

        val result = resultFlow.first()
        assertEquals(20, result.songs.size)
        assertEquals(10, result.topAlbums.size)
    }
}

val testAlbums = listOf(
    AlbumWithExtraInfo().apply {
        album = Album(id = 45, title = "Now in Android")
    },
    AlbumWithExtraInfo().apply {
        album = Album(id = 13, title = "Android Developers Backstage")
    },
    AlbumWithExtraInfo().apply {
        album = Album(id = 9, title = "Techcrunch")
    },
)
