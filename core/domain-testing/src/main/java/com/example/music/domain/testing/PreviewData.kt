package com.example.music.domain.testing

import android.net.Uri
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset

/* --- Preview Data Notes ---
//artist_id 113 = LM.C
//artist_id 22 = Paramore
//artist_id 9381 = ACIDMAN
//artist_id 6 = Yoko Shimomura
//artist_id 15 = Hiroyuki Nakayama 「中山 博之」
//artist_id 0 = "BUMP OF CHICKEN"
//artist_id 77 = "Tatsuya Kitani 「キタニタツヤ」"
//album_id 1145 = "88 / ...With Vampire - Single"
//album_id 71 = "Wonderful Wonderholic"
//album_id 281 = "Slow Rain"
//album_id 123 = "Riot!"
//album_id 124 = "Brand New Eyes"
//album_id 125 = "After Laughter"
//album_id 307 = "Kingdom Hearts Piano Collections - Field & Battle"
//album_id 216 = "Sleep Walking Orchestra - Single"
//album_id 964 = "Scar - Single"
//album_id 8 = "Kingdom Hearts Original Soundtrack Complete (Disc 1 ~ Kingdom Hearts)"
//composer_id 291 = "Yoko Shimomura"
//composer_id 82 = "Tatsuya Kitani 「キタニタツヤ」"
//composer_id 119 = "Motoo Fujiwara 「藤原 基央」" //bump of chicken
//composer_id 410 = "Hayley Williams/Josh Farro"
//composer_id 11 = "LM.C"
//composer_id 2950 = Sachiko Miyano 「宮野 幸子」
//composer_id  = "" //Slow Rain album, After Laughter album, ghost heart has null
//song_id 1023 = 88 // -> 1000288299
//song_id 103 = Ghost heart // -> 1000288293
//song_id 528 = Slow Rain // -> 1000274878
//song_id 529 = Isotope (instrumental) // -> 1000274879
//song_id 530 = Walking Dada // -> 1000274880
//song_id 12 = Misery Business // -> 1000289340
//song_id 17 = Ignorance // -> 1000289320
//song_id 21 = Hard Times // -> 1000685636
//song_id 6535 = Musique // -> 1001034416
//song_id 67 = Sleep Walking Orchestra // -> 1000931249
//song_id 59 = Scar // -> 1000940506
//song_id 171 = Dearly Beloved // -> 1001034275
*/

val PreviewGenres = listOf(
    GenreInfo(id = 0, name = "Alternative", songCount = 3),
    GenreInfo(id = 1, name = "Soundtrack", songCount = 2),
    GenreInfo(id = 2, name = "Pop", songCount = 2),
    GenreInfo(id = 3, name = "JPop", songCount = 5)
)

val PreviewArtists = listOf(
    ArtistInfo(
        id = 113,
        name = "LM.C",
        songCount = 2,
        albumCount = 2,
    ),
    ArtistInfo(
        id = 22,
        name = "Paramore",
        songCount = 3,
        albumCount = 3,
    ),
    ArtistInfo(
        id = 9381,
        name = "ACIDMAN",
        songCount = 3,
        albumCount = 1,
    ),
    ArtistInfo(
        id = 6,
        name = "Yoko Shimomura",
        songCount = 1,
        albumCount = 2,
    ),
    ArtistInfo(
        id = 1,
        name = "BUMP OF CHICKEN",
        songCount = 1,
        albumCount = 1,
    ),
    ArtistInfo(
        id = 77,
        name = "Tatsuya Kitani 「キタニタツヤ」",
        songCount = 1,
        albumCount = 1,
    ),
    ArtistInfo(
        id = 15,
        name = "Hiroyuki Nakayama 「中山 博之」",
        songCount = 1,
        albumCount = 0,
    )
)

val PreviewSongs = listOf(
    SongInfo(
        id = 1023,
        title = "88",
        artistId = 113,
        artistName = "LM.C",
        albumId = 1145,
        albumTitle = "88 / ...With Vampire - Single",
        genreId = 2,
        genreName = "Pop",
        composerId = 11,
        composerName = "LM.C",
        //albumTrackNumber = 1,
        trackNumber = 1,
        discNumber = 1,
        //duration = Duration.parse("PT4M"),//5S"),
        duration = Duration.ofSeconds(232),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        size = 1431,
        year = 2004,
        cdTrackNum = 1,
        srcTrackNum = 1
    ),
    SongInfo(
        id = 103,
        title = "Ghost heart",
        artistId = 113,
        artistName = "LM.C",
        albumId = 71,
        albumTitle = "Wonderful Wonderholic",
        genreId = 2,
        genreName = "Pop",
        composerId = null,
        composerName = null,
        //albumTrackNumber = 4,
        trackNumber = 4,
        discNumber = 1,
        duration = Duration.ofSeconds(217),
        //duration = Duration.parse("PT3M"),//13S"),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = OffsetDateTime.of(
            2023, 12, 9, 4,
            24, 0, 0, ZoneOffset.of("-0800")
        ),
        size = 1654,
        year = 2009,
        cdTrackNum = 4,
        srcTrackNum = 1
    ),
    SongInfo(
        id = 528,
        title = "Slow Rain",
        artistId = 9381,
        artistName = "ACIDMAN",
        albumId = 281,
        albumTitle = "Slow Rain",
        genreId = 3,
        genreName = "JPop",
        composerId = null,
        composerName = null,
        //albumTrackNumber = 1,
        trackNumber = 1,
        discNumber = 1,
        duration = Duration.ofSeconds(271),
        //duration = Duration.parse("PT3M"),//42S"),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = null,
        year = 2007,
        size = 6542,
        cdTrackNum = 1,
        srcTrackNum = 1,
    ),
    SongInfo(
        id = 529,
        title = "Isotope (instrumental)",
        artistId = 9381,
        artistName = "ACIDMAN",
        albumId = 281,
        albumTitle = "Slow Rain",
        genreId = 3,
        genreName = "JPop",
        composerId = null,
        composerName = null,
        //albumTrackNumber = 2,
        trackNumber = 2,
        discNumber = 1,
        duration = Duration.ofSeconds(184),
        //duration = Duration.parse("PT3M"),//19S"),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = null,
        size = 1231,
        year = 2007,
        cdTrackNum = 2,
        srcTrackNum = 1,
    ),
    SongInfo(
        id = 530,
        title = "Walking Dada",
        artistId = 9381,
        artistName = "ACIDMAN",
        albumId = 281,
        albumTitle = "Slow Rain",
        genreId = 3,
        genreName = "JPop",
        composerId = null,
        composerName = null,
        //albumTrackNumber = 3,
        trackNumber = 3,
        discNumber = 1,
        duration = Duration.ofSeconds(143),
        //duration = Duration.parse("PT4M"),//55S"),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = null,
        size = 1121,
        year = 2007,
        cdTrackNum = 3,
        srcTrackNum = 1,
    ),
    SongInfo(
        id = 12,
        title = "Misery Business",
        artistId = 22,
        artistName = "Paramore",
        albumId = 123,
        albumTitle = "Riot!",
        genreId = 0,
        genreName = "Alternative",
        composerId = 410,
        composerName = "Hayley Williams/Josh Farro",
        //albumTrackNumber = 8,
        trackNumber = 8,
        discNumber = 1,
        duration = Duration.ofSeconds(212),
        //duration = Duration.parse("PT4M"),//10S"),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = null,
        size = 931,
        year = 2007,
        cdTrackNum = 8,
        srcTrackNum = 1,
    ),
    SongInfo(
        id = 17,
        title = "Ignorance",
        artistId = 22,
        artistName = "Paramore",
        albumId = 124,
        albumTitle = "Brand New Eyes",
        genreId = 0,
        composerId = 410,
        composerName = "Hayley Williams/Josh Farro",
        //albumTrackNumber = 2,
        trackNumber = 2,
        discNumber = 1,
        duration = Duration.ofSeconds(218),
        //duration = Duration.parse("PT4M"),//26S"),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = null,
        size = 352,
        year = 2009,
        cdTrackNum = 2,
        srcTrackNum = 1,
    ),
    SongInfo(
        id = 21,
        title = "Hard Times",
        artistId = 22,
        artistName = "Paramore",
        albumId = 125,
        albumTitle = "After Laughter",
        genreId = 0,
        composerId = 410,
        composerName = "Hayley Williams/Josh Farro",
        //albumTrackNumber = 11,
        trackNumber = 11,
        discNumber = 1,
        duration = Duration.ofSeconds(183),
        //duration = Duration.parse("PT2M"),//51S"),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = null,
        size = 29889,
        year = 2012,
        cdTrackNum = 11,
        srcTrackNum = 1,
    ),
    SongInfo(
        id = 6535,
        title = "Musique pour la Tristesse de Xion",
        artistId = 15,
        artistName = "Hiroyuki Nakayama 「中山 博之」",
        albumId = 307,
        albumTitle = "Kingdom Hearts Piano Collections - Field & Battle",
        genreId = 1,
        composerId = 2950,
        composerName = "Sachiko Miyano 「宮野 幸子」",
        //albumTrackNumber = 9,
        trackNumber = 9,
        discNumber = 1,
        duration = Duration.ofSeconds(336),
        //duration = Duration.parse("PT6M"),//22S"),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = OffsetDateTime.of(
            2025, 1, 3, 16,
            24, 45, 0, ZoneOffset.of("-0800")
        ),
        size = 292,
        year = 2009,
        cdTrackNum = 9,
        srcTrackNum = 1,
    ),
    SongInfo(
        id = 67,
        title = "Sleep Walking Orchestra",
        artistId = 1,
        artistName = "BUMP OF CHICKEN",
        albumId = 216,
        albumTitle = "Sleep Walking Orchestra - Single",
        genreId = 3,
        composerId = 119,
        composerName = "Motoo Fujiwara 「藤原 基央」",
        //albumTrackNumber = 1,
        trackNumber = 1,
        discNumber = 1,
        duration = Duration.ofSeconds(236),
        //duration = Duration.parse("PT3M"),//42S"),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = OffsetDateTime.of(
            2025, 2, 1, 21,
            18, 30, 283, ZoneOffset.of("-0800")
        ),
        size = 1991,
        year = 2024,
        cdTrackNum = 1,
        srcTrackNum = 1,
    ),
    SongInfo(
        id = 59,
        title = "Scar",
        artistId = 77,
        artistName = "Tatsuya Kitani 「キタニタツヤ」",
        albumId = 964,
        albumTitle = "Scar - Single",
        genreId = 3,
        composerId = 82,
        composerName = "Tatsuya Kitani 「キタニタツヤ」",
        //albumTrackNumber = 1,
        trackNumber = 1,
        discNumber = 1,
        duration = Duration.ofSeconds(259),
        //duration = Duration.parse("PT3M"),//42S"),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = OffsetDateTime.of(
            2025, 1, 27, 11,
            27, 45, 0, ZoneOffset.of("-0800")
        ),
        size = 5762,
        year = 2024,
        cdTrackNum = 1,
        srcTrackNum = 1,
    ),
    SongInfo(
        id = 171,
        title = "Dearly Beloved",
        artistId = 6,
        artistName = "Yoko Shimomura",
        albumId = 8,
        albumTitle = "Kingdom Hearts Original Soundtrack Complete (Disc 1 ~ Kingdom Hearts)",
        genreId = 1,
        composerId = 291,
        composerName = "Yoko Shimomura",
        //albumTrackNumber = 1,
        trackNumber = 1,
        discNumber = 1,
        duration = Duration.ofSeconds(131),
        dateAdded = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateModified = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        dateLastPlayed = OffsetDateTime.of(
            2023, 8, 10, 21,
            12, 41, 652, ZoneOffset.of("-0800")
        ),
        size = 1431,
        year = 2002,
        cdTrackNum = 1,
        srcTrackNum = 1,
    ),
)

val PreviewAlbums = listOf(
    AlbumInfo(
        id = 1145,
        title = "88 / ...With Vampire - Single",
        albumArtistId = 113,
        albumArtistName = "LM.C",
        year = 2007,
        artworkUri = Uri.parse(""),
        dateLastPlayed = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        ),
        trackTotal = 1,
        discTotal = 1,
        songCount = 1,
    ),
    AlbumInfo(
        id = 71,
        title = "Wonderful Wonderholic",
        albumArtistId = 113,
        albumArtistName = "LM.C",
        year = 2009,
        dateLastPlayed = OffsetDateTime.of(
            2023, 12, 9, 4,
            24, 0, 0, ZoneOffset.of("-0800")
        ),
        trackTotal = 5,
        discTotal = 1,
        artworkUri = Uri.parse(""),
        songCount = 1,
    ),
    AlbumInfo(
        id = 281,
        title = "Slow Rain",
        albumArtistId = 9381,
        albumArtistName = "ACIDMAN",
        year = 2008,
        dateLastPlayed = null,
        trackTotal = 3,
        discTotal = 1,
        artworkUri = Uri.parse(""),
        songCount = 3,
    ),
    AlbumInfo(
        id = 123,
        title = "Riot!",
        albumArtistId = 22,
        albumArtistName = "Paramore",
        year = 2007,
        dateLastPlayed = null,
        trackTotal = 11,
        discTotal = 1,
        artworkUri = Uri.parse(""),
        songCount = 1,
    ),
    AlbumInfo(
        id = 124,
        title = "Brand New Eyes",
        albumArtistId = 22,
        albumArtistName = "Paramore",
        year = 2009,
        dateLastPlayed = null,
        trackTotal = 10,
        discTotal = 1,
        artworkUri = Uri.parse(""),
        songCount = 1,
    ),
    AlbumInfo(
        id = 125,
        title = "After Laughter",
        albumArtistId = 22,
        albumArtistName = "Paramore",
        year = 2012,
        dateLastPlayed = null,
        trackTotal = 14,
        discTotal = 1,
        artworkUri = Uri.parse(""),
        songCount = 1,
    ),
    AlbumInfo(
        id = 307,
        title = "Kingdom Hearts Piano Collections - Field & Battle",
        albumArtistId = 6,
        albumArtistName = "Yoko Shimomura",
        year = 2009,
        dateLastPlayed = OffsetDateTime.of(
            2025, 1, 3, 16,
            24, 45, 0, ZoneOffset.of("-0800")
        ),
        trackTotal = 9,
        discTotal = 1,
        artworkUri = Uri.parse(""),
        songCount = 1,
    ),
    AlbumInfo(
        id = 216,
        title = "Sleep Walking Orchestra - Single",
        albumArtistId = 1,
        albumArtistName = "BUMP OF CHICKEN",
        dateLastPlayed = OffsetDateTime.of(
            2025, 2, 1, 21,
            18, 30, 283, ZoneOffset.of("-0800")
        ),
        year = 2024,
        trackTotal = 1,
        discTotal = 1,
        artworkUri = Uri.parse(""),
        songCount = 1,
    ),
    AlbumInfo(
        id = 964,
        title = "Scar - Single",
        albumArtistId = 77,
        albumArtistName = "Tatsuya Kitani 「キタニタツヤ」",
        year = 2024,
        dateLastPlayed = OffsetDateTime.of(
            2025, 1, 27, 11,
            27, 45, 0, ZoneOffset.of("-0800")
        ),
        trackTotal = 1,
        discTotal = 1,
        artworkUri = Uri.parse(""),
        songCount = 1,
    ),
    AlbumInfo(
        id = 8,
        title = "Kingdom Hearts Original Soundtrack Complete (Disc 1 ~ Kingdom Hearts)",
        albumArtistId = 6,
        albumArtistName = "Yoko Shimomura",
        year = 2002,
        dateLastPlayed = OffsetDateTime.of(
            2023, 8, 10, 21,
            12, 41, 652, ZoneOffset.of("-0800")
        ),
        trackTotal = 1,
        discTotal = 2,
        artworkUri = Uri.parse(""),
        songCount = 1,
    ),
)

val PreviewComposers = listOf(
    ComposerInfo(
        id = 291,
        name = "Yoko Shimomura",
        songCount = 1,
    ),
    ComposerInfo(
        id = 82,
        name = "Tatsuya Kitani 「キタニタツヤ」",
        songCount = 1,
    ),
    ComposerInfo(
        id = 119,
        name = "Motoo Fujiwara 「藤原 基央」",
        songCount = 1,
    ),
    ComposerInfo(
        id = 410,
        name = "Hayley Williams/Josh Farro",
        songCount = 3,
    ),
    ComposerInfo(
        id = 11,
        name = "LM.C",
        songCount = 1,
    ),
    ComposerInfo(
        id = 2950,
        name = "Sachiko Miyano 「宮野 幸子」",
        songCount = 1,
    )
)

val PreviewPlaylists = listOf(
    PlaylistInfo(
        id = 0,
        name = "hello",
        description = "HIHIHIHIHI",
        dateCreated = OffsetDateTime.now(),
        songCount = 6,
        //tracks = (17, 530, 529, 528, 1023, 103)
    ),
    PlaylistInfo(
        id = 1,
        name = "ack",
        description = "",
        dateCreated = OffsetDateTime.of(
            2021, 10, 23, 1,
            4, 5, 0, ZoneOffset.of("-0800")
        ),
        songCount = 4,
        //tracks = intArrayOf(1023, 12, 17, 21)
    ),
    PlaylistInfo(
        id = 2,
        name = "give the goods",
        description = "MAKE ME FEEL SOMETHING",
        dateCreated = OffsetDateTime.of(
            2018, 5, 3, 5,
            49, 0, 0, ZoneOffset.of("-0800")
        ),
        songCount = 2,
        //tracks = intArrayOf(6535, 6535)
    ),
)

data class SongPlaylistCombo(
    val id: Long,
    val playlistId: Long,
    val songId: Long,
    val playlistTrackNumber: Int
)

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
fun getPlaylistData(playlistId: Long): PlaylistInfo = PreviewPlaylists.single { s -> s.id == playlistId }
fun getPlaylistSongs(playlistId: Long): List<SongInfo> = PreviewSongPlaylistCombo.filter { entry ->
        entry.playlistId == playlistId
    }.map { song ->
        getSongData(song.songId)
    } //returns songs in playlist

fun getSongsInAlbum(albumId: Long): List<SongInfo> = PreviewSongs.filter { entry ->
    entry.albumId == albumId
}

fun getAlbumsByArtist(albumArtistId: Long): List<AlbumInfo> = PreviewAlbums.filter{ entry ->
    entry.albumArtistId == albumArtistId
}

fun getSongsByArtist(artistId: Long): List<SongInfo> = PreviewSongs.filter { entry ->
    entry.artistId == artistId
}

fun getComposerData(composerId: Long): ComposerInfo = PreviewComposers.single { s -> s.id == composerId }

fun getSongsByComposer(composerId: Long): List<SongInfo> = PreviewSongs.filter { entry ->
    (entry.composerId != null) && (entry.composerId == composerId)
}

fun getSongsInGenre(genreId: Long): List<SongInfo> = PreviewSongs.filter{ entry ->
    entry.genreId == genreId
}
