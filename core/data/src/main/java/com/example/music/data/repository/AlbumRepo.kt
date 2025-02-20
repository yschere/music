package com.example.music.data.repository

import com.example.music.data.database.dao.AlbumsDao
import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import kotlinx.coroutines.flow.Flow

/**
 * Interface for [AlbumsDao] abstract functions
 */
interface AlbumRepo {

    fun getAllAlbums(): List<Album>

    fun getAlbumById(id: Long): Flow<Album>

    fun getAlbumsByAlbumArtistId(
        albumArtistId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Album>>

    fun getAlbumWithExtraInfo(albumId: Long): Flow<AlbumWithExtraInfo>

    fun sortAlbumsByAlbumArtistAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun sortAlbumsByAlbumArtistDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun sortAlbumsByDateLastPlayedAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun sortAlbumsByDateLastPlayedDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun sortAlbumsBySongCountAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun sortAlbumsBySongCountDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun sortAlbumsByTitleAsc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun sortAlbumsByTitleDesc(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun searchAlbumByTitle(
        query: String,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<AlbumWithExtraInfo>>

    fun getSongsByAlbumId(
        albumId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Song>>

    fun sortSongsInAlbumBySongTitleAsc(albumId: Long): Flow<List<Song>>
    fun sortSongsInAlbumBySongTitleDesc(albumId: Long): Flow<List<Song>>
    fun sortSongsInAlbumByTrackNumberAsc(albumId: Long): Flow<List<Song>>
    fun sortSongsInAlbumByTrackNumberDesc(albumId: Long): Flow<List<Song>>

    fun songsInAlbum(albumId: Long): Flow<List<SongToAlbum>>

    suspend fun addAlbum(album: Album)

    suspend fun count(): Int

    suspend fun isEmpty(): Boolean

    /* //Queries not in use
    fun sortAlbumsInGenreByTitleAsc(genreId: Long,limit: Int = Integer.MAX_VALUE): Flow<List<Album>>
    fun sortAlbumsInGenreByTitleDesc(genreId: Long,limit: Int = Integer.MAX_VALUE): Flow<List<Album>>

    //sortAlbumsInGenreByDateLastPlayed not needed atm
    fun sortAlbumsInGenreByDateLastPlayedAsc(genreId: Long,limit: Int = Integer.MAX_VALUE): Flow<List<AlbumWithExtraInfo>>
    fun sortAlbumsInGenreByDateLastPlayedDesc(genreId: Long,limit: Int = Integer.MAX_VALUE): Flow<List<AlbumWithExtraInfo>>

    //not sure if searchAlbumByTitleAndGenre is needed
    fun searchAlbumByTitleAndGenre(query: String,genreIdList: List<Long>,limit: Int = Integer.MAX_VALUE): Flow<List<AlbumWithExtraInfo>>
    */
}

/**
 * A data repository for [Album] instances.
 */
class AlbumRepoImpl(
    private val albumDao: AlbumsDao,
    private val songDao: SongsDao,
) : AlbumRepo {

    override fun getAllAlbums(): List<Album> =
        albumDao.getAllAlbums()

    //equivalent of podcastStore.podcastWithUri
    override fun getAlbumById(id: Long): Flow<Album> =
        albumDao.getAlbumById(id) //equivalent of podcastsDao.podcastWithUri

    override fun getAlbumsByAlbumArtistId(
        albumArtistId: Long,
        limit: Int
    ): Flow<List<Album>> =
        albumDao.getAlbumsByAlbumArtistId(albumArtistId, limit)

    //equivalent of podcastStore.podcastWithExtraInfo
    override fun getAlbumWithExtraInfo(albumId: Long): Flow<AlbumWithExtraInfo> =
        albumDao.getAlbumWithExtraInfo(albumId)

    override fun sortAlbumsByAlbumArtistAsc(limit: Int): Flow<List<AlbumWithExtraInfo>> =
        albumDao.sortAlbumsByAlbumArtistAsc(limit)

    override fun sortAlbumsByAlbumArtistDesc(limit: Int): Flow<List<AlbumWithExtraInfo>> =
        albumDao.sortAlbumsByAlbumArtistDesc(limit)

    override fun sortAlbumsByDateLastPlayedAsc(limit: Int): Flow<List<AlbumWithExtraInfo>> =
        albumDao.sortAlbumsByDateLastPlayedAsc(limit)

    //equivalent of podcastStore.podcastsSortedByLastEpisode
    override fun sortAlbumsByDateLastPlayedDesc(limit: Int): Flow<List<AlbumWithExtraInfo>> =
        albumDao.sortAlbumsByDateLastPlayedDesc(limit) //equivalent of podcastsDao.podcastsSortedByLastEpisode //use as replacement for mostRecentAlbums

    override fun sortAlbumsBySongCountAsc(limit: Int): Flow<List<AlbumWithExtraInfo>> =
        albumDao.sortAlbumsBySongCountAsc(limit)

    override fun sortAlbumsBySongCountDesc(limit: Int): Flow<List<AlbumWithExtraInfo>> =
        albumDao.sortAlbumsBySongCountDesc(limit)

    override fun sortAlbumsByTitleAsc(limit: Int): Flow<List<AlbumWithExtraInfo>> =
        albumDao.sortAlbumsByTitleAsc(limit)

    override fun sortAlbumsByTitleDesc(limit: Int): Flow<List<AlbumWithExtraInfo>> =
        albumDao.sortAlbumsByTitleDesc(limit)

    //equivalent of podcastStore.searchPodcastByTitle
    override fun searchAlbumByTitle(
        query: String,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>> =
        albumDao.searchAlbumByTitle(query, limit) //equivalent of podcastsDao.searchPodcastByTitle

    override fun getSongsByAlbumId(
        albumId: Long,
        limit: Int
    ): Flow<List<Song>> = songDao.getSongsByAlbumId(albumId, limit)

    override fun sortSongsInAlbumBySongTitleAsc(albumId: Long): Flow<List<Song>> =
        songDao.sortSongsInAlbumBySongTitleAsc(albumId)

    override fun sortSongsInAlbumBySongTitleDesc(albumId: Long): Flow<List<Song>> =
        songDao.sortSongsInAlbumBySongTitleDesc(albumId)

    override fun sortSongsInAlbumByTrackNumberAsc(albumId: Long): Flow<List<Song>> =
        songDao.sortSongsInAlbumByTrackNumberAsc(albumId)

    override fun sortSongsInAlbumByTrackNumberDesc(albumId: Long): Flow<List<Song>> =
        songDao.sortSongsInAlbumByTrackNumberDesc(albumId)

    override fun songsInAlbum(albumId: Long): Flow<List<SongToAlbum>> =
        songDao.getSongsAndAlbumByAlbumId(albumId)

    /**
     * Add a new [Album] to this store.
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addAlbum(album: Album) {
        albumDao.insert(album)
    }

    override suspend fun count(): Int = albumDao.count()

    override suspend fun isEmpty(): Boolean = albumDao.count() == 0

    /* //Queries not in use
    //sortAlbumsInGenre could be in need but only if able to connect album to genre
    override fun sortAlbumsInGenreByTitleAsc(genreId: Long, limit: Int): Flow<List<Album>> =
        albumDao.sortAlbumsInGenreByTitleAsc(genreId, limit)
    override fun sortAlbumsInGenreByTitleDesc(genreId: Long, limit: Int): Flow<List<Album>> =
        albumDao.sortAlbumsInGenreByTitleDesc(genreId, limit)
    override fun sortAlbumsInGenreByDateLastPlayedAsc(genreId: Long,limit: Int): Flow<List<AlbumWithExtraInfo>> =
        albumDao.sortAlbumsInGenreByDateLastPlayedAsc(genreId, limit)
    override fun sortAlbumsInGenreByDateLastPlayedDesc(genreId: Long,limit: Int,): Flow<List<AlbumWithExtraInfo>> =
        albumDao.sortAlbumsInGenreByDateLastPlayedDesc(genreId, limit)

    //equivalent of podcastStore.searchPodcastByTitleAndAlbum
    //not sure if searchAlbumByTitleAndGenre is needed
    override fun searchAlbumByTitleAndGenre(
        query: String,
        genreIdList: List<Long>,
        limit: Int
    ): Flow<List<AlbumWithExtraInfo>> {
        return albumDao.searchAlbumByTitleAndGenre(query, genreIdList, limit)
        //equivalent of podcastStore.searchPodcastByTitleAndCategory
    }
     */
}
