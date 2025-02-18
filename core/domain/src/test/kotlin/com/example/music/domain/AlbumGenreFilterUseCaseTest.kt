package com.example.music.domain

import com.example.music.data.database.model.Album
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.SongToAlbum
import com.example.music.data.database.model.Song
import com.example.music.data.testing.repository.TestGenreRepo
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

    private val genresRepo = TestGenreRepo()
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
                    //genreId = 2,
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
                    //genreId = 3,
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
                    //genreId = 1,
                    artwork = "",
                )
            )
        }
    )
    private val testGenre = Genre(1, "Soundtrack")

    val useCase = AlbumGenreFilterUseCase(
        genreRepo = genresRepo
    )

    @Test
    fun whenGenreNull_emptyFlow() = runTest {
        val resultFlow = useCase(null)

        genresRepo.setSongsFromAlbum(testGenre.id, testSongToAlbum) //songsAndAlbumsInGenre(genreId: Long
        genresRepo.setAlbumsInGenre(testGenre.id, testAlbums)

        val result = resultFlow.first()
        assertTrue(result.topAlbums.isEmpty())
        assertTrue(result.songs.isEmpty())
    }

    @Test
    fun whenGenreNotNull_validFlow() = runTest {
        val resultFlow = useCase(testGenre.asExternalModel())

//        genresRepo.setSongsFromAlbum(testGenre.id, testSongToAlbum)
        genresRepo.setAlbumsInGenre(testGenre.id, testAlbums)

        val result = resultFlow.first()
        assertEquals(
            testAlbums.map { it.asExternalModel() },
            result.topAlbums
        )
//        assertEquals(
//            testSongToAlbum.map {  },
//            result.songs
//        )
    }

    @Test
    fun whenGenreInfoNotNull_verifyLimitFlow() = runTest {
        val resultFlow = useCase(testGenre.asExternalModel())

        genresRepo.setSongsFromAlbum(
            testGenre.id,
            List(8) { testSongToAlbum }.flatten()
        )
        genresRepo.setAlbumsInGenre(
            testGenre.id,
            List(4) { testAlbums }.flatten()
        )

        val result = resultFlow.first()
        assertEquals(20, result.songs.size)
        assertEquals(10, result.topAlbums.size)
    }
}

val testAlbums = listOf(
    Album(id = 45, title = "Now in Android"),
    Album(id = 13, title = "Android Developers Backstage"),
    Album(id = 9, title = "Tech crunch"),
)
