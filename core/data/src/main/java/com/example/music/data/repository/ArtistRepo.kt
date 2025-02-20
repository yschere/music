package com.example.music.data.repository

import com.example.music.data.database.dao.AlbumsDao
import com.example.music.data.database.dao.ArtistsDao
import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.ArtistWithExtraInfo
import com.example.music.data.database.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Interface for [ArtistsDao] abstract functions
 */
interface ArtistRepo {

    fun getAllArtists(): Flow<List<Artist>>

    fun getArtistById(id: Long): Flow<Artist>

    fun getArtistWithExtraInfo(id: Long): Flow<ArtistWithExtraInfo>

    fun sortArtistsByNameAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ArtistWithExtraInfo>>

    fun sortArtistsByNameDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ArtistWithExtraInfo>>

    fun sortArtistsByAlbumCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ArtistWithExtraInfo>>

    fun sortArtistsByAlbumCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ArtistWithExtraInfo>>

    fun sortArtistsBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ArtistWithExtraInfo>>

    fun sortArtistsBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ArtistWithExtraInfo>>

    fun searchArtistByName(
        query: String,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ArtistWithExtraInfo>>

    fun getAlbumsByArtistId(
        artistId: Long,
        limit: Int = Integer.MAX_VALUE,
    ): Flow<List<AlbumWithExtraInfo>>

    /*
    sortAlbumsInArtistBySongTitle asc, desc
    sortAlbumsInArtistByAlbumTitle asc, desc
     */

    fun getSongsByArtistId(
        artistId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun sortSongsInArtistBySongTitleAsc(artistId: Long): Flow<List<Song>>
    fun sortSongsInArtistBySongTitleDesc(artistId: Long): Flow<List<Song>>
    fun sortSongsInArtistByAlbumTitleAsc(artistId: Long): Flow<List<Song>>
    fun sortSongsInArtistByAlbumTitleDesc(artistId: Long): Flow<List<Song>>

    suspend fun addArtist(artist: Artist)

    suspend fun count(): Int

    suspend fun isEmpty(): Boolean

    /* //Queries not in use
    //sortArtistsByDateLastPlayed not needed
    fun sortArtistsByDateLastPlayedAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun sortArtistsByDateLastPlayedDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    //sortArtistsInGenre could be in need but only if able to connect artist to genre
    fun sortArtistsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

    fun sortArtistsInGenreByDateLastPlayedDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>

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

    //fun artistsAndGenres(id: Long): Flow<Artist>
     */
}

/**
* A data repository for [Artist] instances.
*/
class ArtistRepoImpl(
private val artistDao: ArtistsDao,
private val albumDao: AlbumsDao,
private val songDao: SongsDao,
) : ArtistRepo {

    override fun getAllArtists(): Flow<List<Artist>> =
        artistDao.getAllArtists()

    override fun getArtistById(id: Long): Flow<Artist> =
        artistDao.getArtistById(id)

    override fun getArtistWithExtraInfo(id: Long): Flow<ArtistWithExtraInfo> =
        artistDao.getArtistWithExtraInfo(id)

    override fun sortArtistsByNameAsc(limit: Int): Flow<List<ArtistWithExtraInfo>> =
        artistDao.sortArtistsByNameAsc(limit)

    override fun sortArtistsByNameDesc(limit: Int): Flow<List<ArtistWithExtraInfo>> =
        artistDao.sortArtistsByNameDesc(limit)

    override fun sortArtistsByAlbumCountAsc(limit: Int): Flow<List<ArtistWithExtraInfo>> =
        artistDao.sortArtistsByAlbumCountAsc(limit)

    override fun sortArtistsByAlbumCountDesc(limit: Int): Flow<List<ArtistWithExtraInfo>> =
        artistDao.sortArtistsByAlbumCountDesc(limit)

    override fun sortArtistsBySongCountAsc(limit: Int): Flow<List<ArtistWithExtraInfo>> =
        artistDao.sortArtistsBySongCountAsc(limit)

    override fun sortArtistsBySongCountDesc(limit: Int): Flow<List<ArtistWithExtraInfo>> =
        artistDao.sortArtistsBySongCountDesc(limit)

    override fun searchArtistByName(
        query: String,
        limit: Int
    ): Flow<List<ArtistWithExtraInfo>> = artistDao.searchArtistByName(query, limit)

    override fun getAlbumsByArtistId(
        artistId: Long,
        limit: Int,
    ): Flow<List<AlbumWithExtraInfo>> = albumDao.getAlbumsWithExtraInfoByAlbumArtistId(artistId, limit)

    //this would need function in albumDao to sort albums by column filtered by artist id
    //sortAlbumsInArtistBySongTitleAsc
    //sortAlbumsInArtistBySongTitleDesc
    //sortAlbumsInArtistByAlbumTitleAsc
    //sortAlbumsInArtistByAlbumTitleDesc
//    override fun sortAlbumsInArtistByDateLastPlayedAsc(limit: Int): Flow<List<AlbumWithExtraInfo>> =
//        albumDao.sortAlbumsByDateLastPlayedAsc(limit)

//    override fun sortAlbumsInArtistByDateLastPlayedDesc(limit: Int): Flow<List<AlbumWithExtraInfo>> =
//        albumDao.sortAlbumsByDateLastPlayedDesc(limit) //equivalent of podcastsDao.podcastsSortedByLastEpisode //use as replacement for mostRecentAlbums


    override fun getSongsByArtistId(
        artistId: Long,
        limit: Int,
    ): Flow<List<Song>> = songDao.getSongsByArtistId(artistId, limit)

    override fun sortSongsInArtistBySongTitleAsc(artistId: Long): Flow<List<Song>> =
        songDao.sortSongsInArtistBySongTitleAsc(artistId)

    override fun sortSongsInArtistBySongTitleDesc(artistId: Long): Flow<List<Song>> =
        songDao.sortSongsInArtistBySongTitleDesc(artistId)

    override fun sortSongsInArtistByAlbumTitleAsc(artistId: Long): Flow<List<Song>> =
        songDao.sortSongsInArtistByAlbumTitleAsc(artistId)

    override fun sortSongsInArtistByAlbumTitleDesc(artistId: Long): Flow<List<Song>> =
        songDao.sortSongsInArtistByAlbumTitleDesc(artistId)

    /**
     * Add a new [Artist] to this store.
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addArtist(artist: Artist) {
        artistDao.insert(artist)
    }

    override suspend fun count(): Int = artistDao.count()

    override suspend fun isEmpty(): Boolean = artistDao.count() == 0

    /* //Queries not in use
    //sortArtistsByDateLastPlayed not needed
    override fun sortArtistsByDateLastPlayedAsc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsByDateLastPlayedAsc(limit) //use as replacement for mostRecentArtists
    override fun sortArtistsByDateLastPlayedDesc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsByDateLastPlayedDesc(limit) //use as replacement for mostRecentArtists

    //sortArtistsInGenre could be in need but only if able to connect artist to genre
    override fun sortArtistsInGenreByDateLastPlayedAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByDateLastPlayedAsc(genreId, limit)
    override fun sortArtistsInGenreByDateLastPlayedDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByDateLastPlayedDesc(genreId, limit)
    override fun sortArtistsInGenreByNameAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameAsc(genreId, limit)
    override fun sortArtistsInGenreByNameDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameDesc(genreId, limit)
    override fun sortArtistsInGenreBySongCountAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountAsc(genreId, limit)
    override fun sortArtistsInGenreBySongCountDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountDesc(genreId, limit)
    override fun artistsAndGenres(id: Long): Flow<ArtistWithGenre> =
        return artistDao.artistsAndGenres(id)
    */
}
