package com.example.music.data.repository

import com.example.music.data.database.dao.AlbumsDao
import com.example.music.data.database.dao.ArtistsDao
import com.example.music.data.database.dao.GenresDao
import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

interface GenreRepo {

    fun getAllGenres(): Flow<List<Genre>>

    fun getGenreById(id: Long): Flow<Genre>

    /**
     * Returns a flow containing a list of genres which are sorted by the
     * number of albums in each genre
     */
    fun sortGenresByNameAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    fun sortGenresByNameDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    /**
     * Returns a flow containing a list of genres which are sorted by the
     * number of albums in each genre
     */
    fun sortGenresByAlbumCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    fun sortGenresByAlbumCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    /**
     * Returns a flow containing a list of genres which are sorted by the
     * number of songs in each genre
     */
    fun sortGenresBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    fun sortGenresBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    /**
     * Returns a flow containing a list of albums in the genre with the
     * given [genreId] sorted by their last played date.
     */
    fun sortAlbumsInGenreByTitleAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Album>>

    fun sortAlbumsInGenreByTitleDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Album>>

    fun sortArtistsInGenreByNameAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun sortArtistsInGenreByNameDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    /* fun sortArtistsInGenreBySongCountAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    /* fun sortArtistsInGenreBySongCountDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    fun sortSongsInGenreByTitleAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun sortSongsInGenreByTitleDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun sortSongsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun sortSongsInGenreByDateLastPlayedDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    /* fun songsAndAlbumsInGenre(//equivalent of categories episodesFromPodcastsInCategory
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<SongToAlbum>> */

    fun songsAndAlbumsInGenre(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<SongToAlbum>>

    suspend fun addGenre(genre: Genre): Long

    suspend fun count(): Int

    suspend fun isEmpty(): Boolean
}

/**
 * A data repository for [Genre] instances.
 */
class GenreRepoImpl(
    private val genreDao: GenresDao,
    private val artistDao: ArtistsDao,
    private val albumDao: AlbumsDao,
    private val songDao: SongsDao,
) : GenreRepo {

    override fun getAllGenres(): Flow<List<Genre>> =
        genreDao.getAllGenres()

    override fun getGenreById(id: Long): Flow<Genre> =
        genreDao.getGenreById(id)

    override fun sortGenresByNameAsc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresByNameAsc(limit)

    override fun sortGenresByNameDesc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresByNameDesc(limit)

    override fun sortGenresByAlbumCountAsc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresByAlbumCountAsc(limit)

    override fun sortGenresByAlbumCountDesc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresByAlbumCountDesc(limit)

    override fun sortGenresBySongCountAsc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresBySongCountAsc(limit)

    override fun sortGenresBySongCountDesc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresBySongCountDesc(limit)

    override fun sortAlbumsInGenreByTitleAsc(genreId: Long, limit: Int): Flow<List<Album>> =
        albumDao.sortAlbumsInGenreByTitleAsc(genreId, limit)

    override fun sortAlbumsInGenreByTitleDesc(genreId: Long, limit: Int): Flow<List<Album>> =
        albumDao.sortAlbumsInGenreByTitleDesc(genreId, limit)

    override fun sortArtistsInGenreByNameAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameAsc(genreId, limit)

    override fun sortArtistsInGenreByNameDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameDesc(genreId, limit)

    /* override fun sortArtistsInGenreBySongCountAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountAsc(genreId, limit) */

    /* override fun sortArtistsInGenreBySongCountDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountDesc(genreId, limit) */

    override fun sortSongsInGenreByTitleAsc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByTitleAsc(genreId, limit)

    override fun sortSongsInGenreByTitleDesc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByTitleDesc(genreId, limit)

    override fun sortSongsInGenreByDateLastPlayedAsc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByDateLastPlayedAsc(genreId, limit)

    override fun sortSongsInGenreByDateLastPlayedDesc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByDateLastPlayedDesc(genreId, limit)

    //equivalent of categories episodesFromPodcastsInCategory
    /* override fun songsAndAlbumsInGenre(genreId: Long, limit: Int): Flow<List<SongToAlbum>> =
        songDao.getSongsAndAlbumsInGenreSortedByLastPlayed(genreId, limit) */

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun songsAndAlbumsInGenre(genreId: Long, limit: Int): Flow<List<SongToAlbum>> {
        val flowAlbums = albumDao.sortAlbumsInGenreByTitleAsc(genreId, limit)
        //flowAlbums.transform(suspend List<Album>())
        val songs = combine(flowAlbums) { arrayAlbum ->
            arrayAlbum.forEach { listAlbum ->
                listAlbum.forEach {
                    return@combine songDao.getSongsAndAlbumByAlbumId(it.id)
                }
            }
        }
        return songs.transform { List<SongToAlbum>(
            songs.count(),
            init = TODO()
        ) }
    }


    override suspend fun addGenre(genre: Genre): Long = genreDao.insert(genre)

    override suspend fun count(): Int = genreDao.count()

    override suspend fun isEmpty(): Boolean = genreDao.count() == 0

}
