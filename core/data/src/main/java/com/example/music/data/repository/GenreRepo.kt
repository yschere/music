package com.example.music.data.repository

import com.example.music.data.database.dao.GenresDao
import com.example.music.data.database.dao.SongsDao
//import com.example.music.data.database.dao.AlbumsDao
//import com.example.music.data.database.dao.ArtistsDao
//import com.example.music.data.database.model.Album
//import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.Genre
import com.example.music.data.database.model.GenreWithExtraInfo
import com.example.music.data.database.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Interface for [GenresDao] abstract functions
 */
interface GenreRepo {

    fun getAllGenres(): Flow<List<Genre>>

    fun getGenreById(id: Long): Flow<Genre>

    fun getGenreWithExtraInfo(id: Long): Flow<GenreWithExtraInfo>

    fun sortGenresByNameAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<GenreWithExtraInfo>>

    fun sortGenresByNameDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<GenreWithExtraInfo>>

    /**
     * Returns a flow containing a list of genres which are sorted by the
     * number of songs in each genre
     */
    fun sortGenresBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<GenreWithExtraInfo>>
    fun sortGenresBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<GenreWithExtraInfo>>

    fun getSongsByGenreId(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

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

    suspend fun addGenre(genre: Genre): Long

    suspend fun count(): Int

    suspend fun isEmpty(): Boolean

    /* //Queries not in use
    //queries below not in use till able to connect albums, artists to genres directly
    fun sortGenresByAlbumCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

    fun sortGenresByAlbumCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Genre>>

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

    fun sortArtistsInGenreBySongCountAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun sortArtistsInGenreBySongCountDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun songsAndAlbumsInGenre(//equivalent of categories episodesFromPodcastsInCategory
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<SongToAlbum>>

    fun songsAndAlbumsInGenre(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<SongToAlbum>>

     */
}

/**
 * A data repository for [Genre] instances.
 */
class GenreRepoImpl(
    private val genreDao: GenresDao,
//    private val artistDao: ArtistsDao,
//    private val albumDao: AlbumsDao,
    private val songDao: SongsDao,
) : GenreRepo {

    override fun getAllGenres(): Flow<List<Genre>> =
        genreDao.getAllGenres()

    override fun getGenreById(id: Long): Flow<Genre> =
        genreDao.getGenreById(id)

    override fun getGenreWithExtraInfo(id: Long): Flow<GenreWithExtraInfo> =
        genreDao.getGenreWithExtraInfo(id)

    override fun sortGenresByNameAsc(limit: Int): Flow<List<GenreWithExtraInfo>> =
        genreDao.sortGenresByNameAsc(limit)

    override fun sortGenresByNameDesc(limit: Int): Flow<List<GenreWithExtraInfo>> =
        genreDao.sortGenresByNameDesc(limit)

    override fun sortGenresBySongCountAsc(limit: Int): Flow<List<GenreWithExtraInfo>> =
        genreDao.sortGenresBySongCountAsc(limit)

    override fun sortGenresBySongCountDesc(limit: Int): Flow<List<GenreWithExtraInfo>> =
        genreDao.sortGenresBySongCountDesc(limit)

    override fun getSongsByGenreId(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.getSongsByGenreId(genreId, limit)

    override fun sortSongsInGenreByTitleAsc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByTitleAsc(genreId, limit)

    override fun sortSongsInGenreByTitleDesc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByTitleDesc(genreId, limit)

    override fun sortSongsInGenreByDateLastPlayedAsc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByDateLastPlayedAsc(genreId, limit)

    override fun sortSongsInGenreByDateLastPlayedDesc(genreId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInGenreByDateLastPlayedDesc(genreId, limit)

    override suspend fun addGenre(genre: Genre): Long = genreDao.insert(genre)

    override suspend fun count(): Int = genreDao.count()

    override suspend fun isEmpty(): Boolean = genreDao.count() == 0

    /* //Queries not in use
    //queries below not in use till able to connect albums to genres directly
    override fun sortGenresByAlbumCountAsc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresByAlbumCountAsc(limit)
    override fun sortGenresByAlbumCountDesc(limit: Int): Flow<List<Genre>> =
        genreDao.sortGenresByAlbumCountDesc(limit)
    override fun sortAlbumsInGenreByTitleAsc(genreId: Long, limit: Int): Flow<List<Album>> =
        albumDao.sortAlbumsInGenreByTitleAsc(genreId, limit)
    override fun sortAlbumsInGenreByTitleDesc(genreId: Long, limit: Int): Flow<List<Album>> =
        albumDao.sortAlbumsInGenreByTitleDesc(genreId, limit)
    override fun sortArtistsInGenreByNameAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameAsc(genreId, limit)
    override fun sortArtistsInGenreByNameDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameDesc(genreId, limit)
    override fun sortArtistsInGenreBySongCountAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountAsc(genreId, limit)
    override fun sortArtistsInGenreBySongCountDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountDesc(genreId, limit)

    //equivalent of categories episodesFromPodcastsInCategory
    override fun songsAndAlbumsInGenre(genreId: Long, limit: Int): Flow<List<SongToAlbum>> =
        songDao.getSongsAndAlbumsInGenreSortedByLastPlayed(genreId, limit)
     */

}
