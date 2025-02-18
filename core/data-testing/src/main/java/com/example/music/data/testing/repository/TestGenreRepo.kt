package com.example.music.data.testing.repository
/*
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.GenreWithExtraInfo
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import com.example.music.data.repository.GenreRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * A [GenreRepo] used for testing.
 */
class TestGenreRepo : GenreRepo {

    private val genresFlow = MutableStateFlow<List<Genre>>(emptyList())
    private val genresExtraInfoFlow = MutableStateFlow<List<GenreWithExtraInfo>>(emptyList())
    private val songsFlow = MutableStateFlow<List<Song>>(emptyList())
    private val albumsInGenreFlow =
        MutableStateFlow<Map<Long, List<Album>>>(emptyMap())
    private val artistsInGenreFlow =
        MutableStateFlow<Map<Long, List<Artist>>>(emptyMap())
    private val songsInGenreFlow =
        MutableStateFlow<Map<Long, List<Song>>>(emptyMap())
    private val songsFromAlbums =
        MutableStateFlow<Map<Long, List<SongToAlbum>>>(emptyMap())

    override fun getAllGenres(): Flow<List<Genre>> {
        TODO("Not yet implemented")
    }

    override fun getGenreById(id: Long): Flow<Genre> = genresFlow.map { genres ->
        genres.first {it.id == id}
    }

    override fun getGenreWithExtraInfo(id: Long): Flow<GenreWithExtraInfo> {
        TODO("Not yet implemented")
    }

    override fun sortGenresByNameAsc(limit: Int): Flow<List<GenreWithExtraInfo>> =
        genresExtraInfoFlow

    override fun sortGenresByNameDesc(limit: Int): Flow<List<GenreWithExtraInfo>> =
        genresExtraInfoFlow

//    override fun sortGenresByAlbumCountAsc(limit: Int): Flow<List<Genre>> =
//        genresFlow

//    override fun sortGenresByAlbumCountDesc(limit: Int): Flow<List<Genre>> =
//        genresFlow

    override fun sortGenresBySongCountAsc(limit: Int): Flow<List<GenreWithExtraInfo>> =
        genresExtraInfoFlow

    override fun sortGenresBySongCountDesc(limit: Int): Flow<List<GenreWithExtraInfo>> =
        genresExtraInfoFlow

    override fun sortAlbumsInGenreByTitleAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<Album>> = albumsInGenreFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

//    override fun sortAlbumsInGenreByTitleDesc(
//        genreId: Long,
//        limit: Int
//    ): Flow<List<Album>> = albumsInGenreFlow.map {
//        it[genreId]?.take(limit) ?: emptyList()
//    }

    override fun sortArtistsInGenreByNameAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<Artist>> = artistsInGenreFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

//    override fun sortArtistsInGenreByNameDesc(
//        genreId: Long,
//        limit: Int
//    ): Flow<List<Artist>> = artistsInGenreFlow.map {
//        it[genreId]?.take(limit) ?: emptyList()
//    }

    /* override fun sortArtistsInGenreBySongCountAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<Artist>> = artistsInGenreFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    } */

    /* override fun sortArtistsInGenreBySongCountDesc(
        genreId: Long,
        limit: Int
    ): Flow<List<Artist>> = artistsInGenreFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    } */

    override fun sortSongsInGenreByTitleAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<Song>> = songsInGenreFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

    override fun sortSongsInGenreByTitleDesc(
        genreId: Long,
        limit: Int
    ): Flow<List<Song>> = songsInGenreFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

    override fun sortSongsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<Song>> = songsInGenreFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

    override fun sortSongsInGenreByDateLastPlayedDesc(
        genreId: Long,
        limit: Int
    ): Flow<List<Song>> = songsInGenreFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

    //equivalent of categories episodesFromPodcastsInCategory
//    override fun songsAndAlbumsInGenre(
//        genreId: Long,
//        limit: Int,
//    ): Flow<List<SongToAlbum>> =
//        songsFlow.map { songs ->
//            songs.filter {
//                it.genreId == genreId
//            }.map { s ->
//                SongToAlbum().apply {
//                    song = s
//                }
//            }
//        }

    override suspend fun addGenre(genre: Genre): Long = -1

    override suspend fun count(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun isEmpty(): Boolean =
        genresFlow.first().isEmpty()

    /**
     * Test-only API for setting the list of genres backed by this [TestGenreRepo].
     */
    fun setGenres(genres: List<Genre>) {
        genresFlow.value = genres
    }

    /**
     * Test-only API for setting the list of albums in a genre backed by this
     * [TestGenreRepo].
     */
    fun setAlbumsInGenre(genreId: Long, albumsInGenre: List<Album>) {
        albumsInGenreFlow.update {
            it + Pair(genreId, albumsInGenre)
        }
    }

    /**
     * Test-only API for setting the list of songs in an album backed by this
     * [TestGenreRepo].
     */
    fun setSongsFromAlbum(genreId: Long, albumsInGenre: List<SongToAlbum>) {
        songsFromAlbums.update {
            it + Pair(genreId, albumsInGenre)
        }
    }

}
*/