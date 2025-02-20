package com.example.music.data.repository

import com.example.music.data.database.dao.SongsDao
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Interface for [SongsDao] abstract functions
 */
interface SongRepo {

    fun getAllSongs(): Flow<List<Song>>

    /**
    Return flow containing song given [id]
     */
    fun getSongById(id: Long): Flow<Song>

    /**
    Return flow containing song given [title]
     */
    fun getSongByTitle(title: String): Flow<Song>

    /**
     * Returns a flow containing the song and corresponding album given a [songId].
     */
    fun getSongAndAlbumBySongId(songId: Long): Flow<SongToAlbum> //equivalent of episodeAndPodcastWithUri

    fun sortSongsByTitleAsc(limit: Int = Integer.MAX_VALUE): Flow<List<Song>>
    fun sortSongsByTitleDesc(limit: Int = Integer.MAX_VALUE): Flow<List<Song>>

    fun sortSongsByArtistAsc(limit: Int = Integer.MAX_VALUE): Flow<List<Song>>
    fun sortSongsByArtistDesc(limit: Int = Integer.MAX_VALUE): Flow<List<Song>>

    fun sortSongsByAlbumAsc(limit: Int = Integer.MAX_VALUE): Flow<List<Song>>
    fun sortSongsByAlbumDesc(limit: Int = Integer.MAX_VALUE): Flow<List<Song>>

    fun sortSongsByDateAddedAsc(limit: Int = Integer.MAX_VALUE): Flow<List<Song>>

    fun sortSongsByDateAddedDesc(limit: Int = Integer.MAX_VALUE): Flow<List<Song>>

    fun sortSongsByDateLastPlayedAsc(limit: Int = Integer.MAX_VALUE): Flow<List<Song>>

    fun sortSongsByDateLastPlayedDesc(limit: Int = Integer.MAX_VALUE): Flow<List<Song>>

    fun getSongsByArtistId(artistId: Long, limit: Int = Integer.MAX_VALUE): Flow<List<Song>>
    fun getSongsByArtistIds(artistIds: List<Long>, limit: Int = Integer.MAX_VALUE): Flow<List<Song>>
    fun sortSongsInArtistBySongTitleAsc(artistId: Long): Flow<List<Song>>
    fun sortSongsInArtistBySongTitleDesc(artistId: Long): Flow<List<Song>>
    fun sortSongsInArtistByAlbumTitleAsc(artistId: Long): Flow<List<Song>>
    fun sortSongsInArtistByAlbumTitleDesc(artistId: Long): Flow<List<Song>>

    fun getSongsByAlbumId(albumId: Long, limit: Int = Integer.MAX_VALUE): Flow<List<Song>>
    fun getSongsAndAlbumByAlbumId(albumId: Long): Flow<List<SongToAlbum>>  //equivalent of episodeStore.episodesInPodcast
    fun getSongsAndAlbumsByAlbumIds(albumIds: List<Long>, limit: Int = Integer.MAX_VALUE): Flow<List<SongToAlbum>> //equivalent of episodeStore.episodesInPodcasts
    fun sortSongsInAlbumBySongTitleAsc(albumId: Long): Flow<List<Song>>
    fun sortSongsInAlbumBySongTitleDesc(albumId: Long): Flow<List<Song>>
    fun sortSongsInAlbumByTrackNumberAsc(albumId: Long): Flow<List<Song>>
    fun sortSongsInAlbumByTrackNumberDesc(albumId: Long): Flow<List<Song>>

    fun getSongsByComposerId(composerId: Long, limit: Int = Integer.MAX_VALUE): Flow<List<Song>>
    fun sortSongsInComposerByTitleAsc(composerId: Long, limit: Int = Integer.MAX_VALUE): Flow<List<Song>>
    fun sortSongsInComposerByTitleDesc(composerId: Long, limit: Int = Integer.MAX_VALUE): Flow<List<Song>>

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

    /* fun getSongsAndAlbumsInGenreSortedByLastPlayed(
        genreId: Long,
        limit: Int = Integer.MAX_VALUE,
    ): Flow<List<SongToAlbum>> */

    /**
     * Add a new [Song] to this store.
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addSong(song: Song): Long
    suspend fun addSongs(songs: Collection<Song>)

    suspend fun count(): Int
    suspend fun isEmpty(): Boolean
}

/**
 * A data repository for [Song] instances.
 */
class SongRepoImpl @Inject constructor(
    private val songDao: SongsDao
) : SongRepo {

    override fun getAllSongs(): Flow<List<Song>> =
        songDao.getAllSongs()

    //equivalent of episodeStore.episodeWithUri
    override fun getSongById(id: Long): Flow<Song> =
        songDao.getSongById(id) //equivalent of episodesDao.episode

    override fun getSongByTitle(title: String): Flow<Song> =
        songDao.getSongByTitle(title)

    //equivalent of episodeStore.episodeAndPodcastWithUri
    override fun getSongAndAlbumBySongId(songId: Long): Flow<SongToAlbum> =
        songDao.getSongAndAlbumBySongId(songId) //equivalent of episodesDao.episodeAndPodcast

    override fun sortSongsByTitleAsc(limit: Int): Flow<List<Song>> =
        songDao.sortSongsByTitleAsc(limit)

    override fun sortSongsByTitleDesc(limit: Int): Flow<List<Song>> =
        songDao.sortSongsByTitleDesc(limit)

    override fun sortSongsByArtistAsc(limit: Int): Flow<List<Song>> =
        songDao.sortSongsByArtistAsc(limit)

    override fun sortSongsByArtistDesc(limit: Int): Flow<List<Song>> =
        songDao.sortSongsByArtistDesc(limit)

    override fun sortSongsByAlbumAsc(limit: Int): Flow<List<Song>> =
        songDao.sortSongsByAlbumAsc(limit)

    override fun sortSongsByAlbumDesc(limit: Int): Flow<List<Song>> =
        songDao.sortSongsByAlbumDesc(limit)

    override fun sortSongsByDateAddedAsc(limit: Int): Flow<List<Song>> =
        songDao.sortSongsByDateAddedAsc(limit)

    override fun sortSongsByDateAddedDesc(limit: Int): Flow<List<Song>> =
        songDao.sortSongsByDateAddedDesc(limit)

    override fun sortSongsByDateLastPlayedAsc(limit: Int): Flow<List<Song>> =
        songDao.sortSongsByDateLastPlayedAsc(limit)

    override fun sortSongsByDateLastPlayedDesc(limit: Int): Flow<List<Song>> =
        songDao.sortSongsByDateLastPlayedDesc(limit)

    override fun getSongsByArtistId(artistId: Long, limit: Int): Flow<List<Song>> =
        songDao.getSongsByArtistId(artistId, limit)

    override fun getSongsByArtistIds(artistIds: List<Long>, limit: Int): Flow<List<Song>> =
        songDao.getSongsByArtistIds(artistIds, limit)

    override fun sortSongsInArtistBySongTitleAsc(artistId: Long): Flow<List<Song>> =
        songDao.sortSongsInArtistBySongTitleAsc(artistId)

    override fun sortSongsInArtistBySongTitleDesc(artistId: Long): Flow<List<Song>> =
        songDao.sortSongsInArtistBySongTitleDesc(artistId)

    override fun sortSongsInArtistByAlbumTitleAsc(artistId: Long): Flow<List<Song>> =
        songDao.sortSongsInArtistByAlbumTitleAsc(artistId)

    override fun sortSongsInArtistByAlbumTitleDesc(artistId: Long): Flow<List<Song>> =
        songDao.sortSongsInArtistByAlbumTitleDesc(artistId)

    override fun getSongsByAlbumId(albumId: Long, limit: Int): Flow<List<Song>> =
        songDao.getSongsByAlbumId(albumId, limit)

    //equivalent of episodeStore.episodesInPodcast
    override fun getSongsAndAlbumByAlbumId(albumId: Long): Flow<List<SongToAlbum>> =
        songDao.getSongsAndAlbumByAlbumId(albumId) //equivalent of episodesDao.episodesForPodcastUri

    //equivalent of episodeStore.episodesInPodcasts
    override fun getSongsAndAlbumsByAlbumIds(
        albumIds: List<Long>,
        limit: Int,
    ): Flow<List<SongToAlbum>> =
        songDao.getSongsAndAlbumsByAlbumIds(albumIds, limit)
    //equivalent of episodesDao.episodesForPodcasts

    override fun sortSongsInAlbumBySongTitleAsc(albumId: Long): Flow<List<Song>> =
        songDao.sortSongsInAlbumBySongTitleAsc(albumId)

    override fun sortSongsInAlbumBySongTitleDesc(albumId: Long): Flow<List<Song>> =
        songDao.sortSongsInAlbumBySongTitleDesc(albumId)

    override fun sortSongsInAlbumByTrackNumberAsc(albumId: Long): Flow<List<Song>> =
        songDao.sortSongsInAlbumByTrackNumberAsc(albumId)

    override fun sortSongsInAlbumByTrackNumberDesc(albumId: Long): Flow<List<Song>> =
        songDao.sortSongsInAlbumByTrackNumberDesc(albumId)

    override fun getSongsByComposerId(composerId: Long, limit: Int): Flow<List<Song>> =
        songDao.getSongsByComposerId(composerId, limit)

    override fun sortSongsInComposerByTitleAsc(composerId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInComposerByTitleAsc(composerId, limit)

    override fun sortSongsInComposerByTitleDesc(composerId: Long, limit: Int): Flow<List<Song>> =
        songDao.sortSongsInComposerByTitleDesc(composerId, limit)

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

    //equivalent of categoriesDao.episodesFromPodcastsInCategory
    /* override fun sortSongsAndAlbumsInGenreByDateLastPlayed(
        genreId: Long,
        limit: Int,
    ): Flow<List<SongToAlbum>> = songDao.sortSongsAndAlbumsInGenreByDateLastPlayed(genreId, limit) */

    /**
     * Add a new [Song] to this store.
     * This automatically switches to the main thread to maintain thread consistency.
     */
    override suspend fun addSong(song: Song): Long = songDao.insert(song)

    override suspend fun addSongs(songs: Collection<Song>) =
        songDao.insertAll(songs)

    override suspend fun count(): Int = songDao.count()
    override suspend fun isEmpty(): Boolean = songDao.count() == 0
}
