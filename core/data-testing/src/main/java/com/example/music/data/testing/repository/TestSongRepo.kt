package com.example.music.data.testing.repository
/*
import com.example.music.data.database.model.Song
import com.example.music.data.database.model.SongToAlbum
import com.example.music.data.repository.SongRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * A [SongRepo] used for testing.
 */
class TestSongRepo : SongRepo {

    private val songsFlow = MutableStateFlow<List<Song>>(emptyList())
    private val songsListFlow =
        MutableStateFlow<Map<Long, List<Song>>>(emptyMap())
    //need private val for all SongRepo properties
    //need override functions for all the SongRepo methods

    //methods this should have
    //getSongsInPlaylist
    //isEmpty (checks the songsFlow)

    override fun getAllSongs(): Flow<List<Song>> = songsFlow

    override fun getSongById(id: Long): Flow<Song> =
        songsFlow.map { songs ->
            songs.first { it.id == id }
        }

    override fun getSongByTitle(title: String): Flow<Song> =
        songsFlow.map { songs ->
            songs.first { it.title == title }
        }

    override fun getSongAndAlbumBySongId(songId: Long): Flow<SongToAlbum> =
        songsFlow.map { songs ->
            val s = songs.first {
                it.id == songId
            }
            SongToAlbum().apply {
                song = s
                _albums = emptyList()
            }
        }

    override fun sortSongsByTitleAsc(
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsByTitleDesc(
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsByArtistAsc(
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsByArtistDesc(
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsByAlbumAsc(
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsByAlbumDesc(
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsByDateAddedAsc(
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsByDateAddedDesc(
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsByDateLastPlayedAsc(
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsByDateLastPlayedDesc(
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun getSongsByArtistId(
        artistId: Long,
        limit: Int,
    ): Flow<List<Song>> = songsFlow

    override fun getSongsByArtistIds(
        artistIds: List<Long>,
        limit: Int,
    ): Flow<List<Song>> = songsFlow

    override fun sortSongsInArtistBySongTitleAsc(
        artistId: Long
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsInArtistBySongTitleDesc(
        artistId: Long
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsInArtistByAlbumTitleAsc(
        artistId: Long
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsInArtistByAlbumTitleDesc(
        artistId: Long
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun getSongsByAlbumId(
        albumId: Long,
        limit: Int,
    ): Flow<List<Song>> = songsFlow

    override fun getSongsAndAlbumByAlbumId(
        albumId: Long,
    ): Flow<List<SongToAlbum>> = songsFlow.map { songs ->
        songs.filter {
            it.albumId == albumId
        }.map { s ->
            SongToAlbum().apply {
                song = s
            }
        }
    }

    //equivalent of episodeRepo.episodesInPodcasts
    override fun getSongsAndAlbumsByAlbumIds(
        albumIds: List<Long>,
        limit: Int,
    ): Flow<List<SongToAlbum>>  =
        songsFlow.map { songs ->
            songs.filter {
                albumIds.contains(it.albumId)
            }.map { s ->
                SongToAlbum().apply {
                    song = s
                }
            }
        }

    override fun sortSongsInAlbumBySongTitleAsc(
        albumId: Long
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsInAlbumBySongTitleDesc(
        albumId: Long
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsInAlbumByTrackNumberAsc(
        albumId: Long
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsInAlbumByTrackNumberDesc(
        albumId: Long
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun getSongsByComposerId(
        composerId: Long,
        limit: Int,
    ): Flow<List<Song>> = songsFlow

    override fun sortSongsInComposerByTitleAsc(
        composerId: Long,
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsInComposerByTitleDesc(
        composerId: Long,
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun getSongsByGenreId(
        genreId: Long,
        limit: Int
    ): Flow<List<Song>> = songsFlow

    override fun sortSongsInGenreByTitleAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsInGenreByTitleDesc(
        genreId: Long,
        limit: Int
    ): Flow<List<Song>> =
        songsFlow //TODO: not sure what the actual test should be here

    override fun sortSongsInGenreByDateLastPlayedAsc(
        genreId: Long,
        limit: Int
    ): Flow<List<Song>> = songsListFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

    override fun sortSongsInGenreByDateLastPlayedDesc(
        genreId: Long,
        limit: Int
    ): Flow<List<Song>> = songsListFlow.map {
        it[genreId]?.take(limit) ?: emptyList()
    }

    /* override fun sortSongsInGenresByDateLastPlayedAsc(
        genreIds: List<Long>,
        limit: Int
    ): Flow<List<Song>> =
        songsFlow.map { songs ->
            songs.filter {
                genreIds.contains(it.genreId)
            }
        } */

    /* override fun sortSongsInGenresByDateLastPlayedDesc(
        genreIds: List<Long>,
        limit: Int
    ): Flow<List<Song>> =
        songsFlow.map { songs ->
            songs.filter {
                genreIds.contains(it.genreId)
            }
        } */

    /* override fun getSongsAndAlbumsInGenreSortedByLastPlayed(
        genreId: Long,
        limit: Int,
    ): Flow<List<SongToAlbum>> =
        songsFlow.map { songs ->
            songs.filter {
                it.genreId == genreId
            }.map { s ->
                SongToAlbum().apply {
                    song = s
                }
            }
        } */

    //this version relies on addSong returning with Any type
    //current version relies on addSong returning as Long
    //because of BaseDAO object that uses insert() with Long return
    override suspend fun addSong(song: Song): Long = -1
    //TODO: fix this so it will correctly addSong with result coming back as long
    override suspend fun addSongs(songs: Collection<Song>) =
        songsFlow.update {
            it + songs
        }

    override suspend fun count(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun isEmpty(): Boolean =
        songsFlow.first().isEmpty()
}
*/