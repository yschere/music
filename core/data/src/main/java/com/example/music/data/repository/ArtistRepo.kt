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

    /* //sortArtistsByDateLastPlayedAsc not needed atm
    fun sortArtistsByDateLastPlayedAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    /* //sortArtistsByDateLastPlayedDesc not needed atm
    fun sortArtistsByDateLastPlayedDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    fun sortArtistsBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ArtistWithExtraInfo>>

    fun sortArtistsBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ArtistWithExtraInfo>>

    //why this one not in PodcastStore?
    /* //sortArtistsInGenreByDateLastPlayedAsc not needed atm
    fun sortArtistsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    /* //sortArtistsInGenreByDateLastPlayedDesc not needed atm
    fun sortArtistsInGenreByDateLastPlayedDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    /*fun sortArtistsInGenreByNameAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>*/

    /*fun sortArtistsInGenreByNameDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>>*/

    /* //not sure if sortArtistsInGenreBySongCountAsc is needed
    fun sortArtistsInGenreBySongCountAsc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    /* //not sure if sortArtistsInGenreBySongCountDesc is needed
    fun sortArtistsInGenreBySongCountDesc(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Artist>> */

    //fun artistsAndGenres(id: Long): Flow<Artist>

    fun searchArtistByName(
        query: String,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<ArtistWithExtraInfo>>

    fun getAlbumsByArtistId(
        artistId: Long,
        limit: Int = Integer.MAX_VALUE,
    ): Flow<List<AlbumWithExtraInfo>>

    fun getSongsByArtistId(
        artistId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    suspend fun addArtist(artist: Artist)

    suspend fun count(): Int

    suspend fun isEmpty(): Boolean
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

    /* //sortArtistsByDateLastPlayedAsc not needed atm
    override fun sortArtistsByDateLastPlayedAsc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsByDateLastPlayedAsc(limit) //use as replacement for mostRecentArtists */

    /* //sortArtistsByDateLastPlayedDesc not needed atm
    override fun sortArtistsByDateLastPlayedDesc(limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsByDateLastPlayedDesc(limit) //use as replacement for mostRecentArtists */

    override fun sortArtistsBySongCountAsc(limit: Int): Flow<List<ArtistWithExtraInfo>> =
        artistDao.sortArtistsBySongCountAsc(limit)

    override fun sortArtistsBySongCountDesc(limit: Int): Flow<List<ArtistWithExtraInfo>> =
        artistDao.sortArtistsBySongCountDesc(limit)

    /* //sortArtistsInGenreByDateLastPlayedAsc not needed atm
    override fun sortArtistsInGenreByDateLastPlayedAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByDateLastPlayedAsc(genreId, limit) */

    /* //sortArtistsInGenreByDateLastPlayedDesc not needed atm
    override fun sortArtistsInGenreByDateLastPlayedDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByDateLastPlayedDesc(genreId, limit) */

    /*override fun sortArtistsInGenreByNameAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameAsc(genreId, limit)*/

    /*override fun sortArtistsInGenreByNameDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreByNameDesc(genreId, limit)*/

    /* //not sure if sortArtistsInGenreBySongCountAsc is needed
    override fun sortArtistsInGenreBySongCountAsc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountAsc(genreId, limit) */

    /* //not sure if sortArtistsInGenreBySongCountDesc is needed
    override fun sortArtistsInGenreBySongCountDesc(genreId: Long, limit: Int): Flow<List<Artist>> =
        artistDao.sortArtistsInGenreBySongCountDesc(genreId, limit) */

    /* override fun artistsAndGenres(id: Long): Flow<ArtistWithGenre> =
        return artistDao.artistsAndGenres(id) */

    override fun searchArtistByName(
        query: String,
        limit: Int
    ): Flow<List<ArtistWithExtraInfo>> = artistDao.searchArtistByName(query, limit)

    override fun getAlbumsByArtistId(
        artistId: Long,
        limit: Int,
    ): Flow<List<AlbumWithExtraInfo>> = albumDao.getAlbumsWithExtraInfoByAlbumArtistId(artistId, limit)

    override fun getSongsByArtistId(
        artistId: Long,
        limit: Int,
    ): Flow<List<Song>> = songDao.getSongsByArtistId(artistId, limit)

    /**
     * Add a new [Artist] to this store.
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addArtist(artist: Artist) {
        artistDao.insert(artist)
    }

    override suspend fun count(): Int = artistDao.count()

    override suspend fun isEmpty(): Boolean = artistDao.count() == 0
}
