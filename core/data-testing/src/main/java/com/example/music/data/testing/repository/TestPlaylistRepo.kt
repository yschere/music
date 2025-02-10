package com.example.music.data.testing.repository

import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.database.model.Song
import com.example.music.data.repository.PlaylistRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * A [PlaylistRepo] used for testing.
 */
class TestPlaylistRepo : PlaylistRepo {

    private val playlistsFlow =
        MutableStateFlow<List<Playlist>>(emptyList())
    private val playlistsExtraFlow =
        MutableStateFlow<List<PlaylistWithExtraInfo>>(emptyList())
    private val songsFlow = MutableStateFlow<List<Song>>(emptyList())
    private val songsInPlaylistFlow =
        MutableStateFlow<Map<Long, List<Song>>>(emptyMap())
    private val playlistAndSongsFlow =
        MutableStateFlow<Map<Playlist, List<Song>>>(emptyMap())
//    private val podcastsInCategoryFlow =
//        MutableStateFlow<Map<Long, List<PodcastWithExtraInfo>>>(emptyMap())
//    private val episodesFromPodcasts =
//        MutableStateFlow<Map<Long, List<EpisodeToPodcast>>>(emptyMap())

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        TODO("Not yet implemented")
    }

    override fun getPlaylistById(id: Long): Flow<Playlist> = playlistsFlow.map { playlists ->
        playlists.first {it.id == id}
    }

    override fun getPlaylistsByIds(ids: List<Long>): Flow<List<Playlist>> {
        TODO("Not yet implemented")
    }

    override fun observePlaylist(name: String): Flow<Playlist> = playlistsFlow.map { playlists ->
        playlists.first {it.name == name}
    }

    override fun getPlaylistExtraInfo(playlistId: Long): Flow<PlaylistWithExtraInfo> = playlistsExtraFlow.map { playlists ->
        playlists.first {it.playlist.id == playlistId}
    }

    override fun sortPlaylistsByNameAsc(
        limit: Int
    ): Flow<List<Playlist>> = playlistsFlow

    override fun sortPlaylistsByNameDesc(
        limit: Int
    ): Flow<List<Playlist>> = playlistsFlow

    override fun sortPlaylistsByDateCreatedAsc(
        limit: Int
    ): Flow<List<Playlist>> = playlistsFlow

    override fun sortPlaylistsByDateCreatedDesc(
        limit: Int
    ): Flow<List<Playlist>> = playlistsFlow

    override fun sortPlaylistsByDateLastAccessedAsc(
        limit: Int
    ): Flow<List<Playlist>> = playlistsFlow

    override fun sortPlaylistsByDateLastAccessedDesc(
        limit: Int
    ): Flow<List<Playlist>> = playlistsFlow

    override fun sortPlaylistsByDateLastPlayedAsc(
        limit: Int
    ): Flow<List<PlaylistWithExtraInfo>> = playlistsExtraFlow

    override fun sortPlaylistsByDateLastPlayedDesc(
        limit: Int
    ): Flow<List<PlaylistWithExtraInfo>> = playlistsExtraFlow

    override fun sortPlaylistsBySongCountAsc(
        limit: Int
    ): Flow<List<PlaylistWithExtraInfo>> = playlistsExtraFlow

    override fun sortPlaylistsBySongCountDesc(
        limit: Int
    ): Flow<List<PlaylistWithExtraInfo>> = playlistsExtraFlow

    override fun sortSongsInPlaylistByTrackNumberAsc(
        playlistId: Long
    ): Flow<List<Song>> = songsFlow

    override fun sortSongsInPlaylistByTrackNumberDesc(
        playlistId: Long
    ): Flow<List<Song>> = songsFlow

    override fun sortSongsInPlaylistByTitleAsc(
        playlistId: Long
    ): Flow<List<Song>> = songsFlow

    override fun sortSongsInPlaylistByTitleDesc(
        playlistId: Long
    ): Flow<List<Song>> = songsFlow

    override fun sortSongsAndPlaylistByTrackNumberAsc(
        playlistId: Long
    ): Map<Playlist, List<Song>> {
        //playlistAndSongsFlow
        return emptyMap() //TODO: fix this
    }

    /* //TODO: not sure what to do here / how to fix
    override fun getSongsAndPlaylistSortedByTrackNumberAsc(playlistId: Long): Map<Playlist, List<Song>> =
        playlistAndSongsFlow.map {} */

//    override fun getCategory(name: String): Flow<Category?> = flowOf()

//    /**
//     * Test-only API for setting the list of playlists backed by this [TestPlaylistRepo].
//     */
//    fun setPlaylists(playlists: List<Playlist>) {
//        playlistsFlow.value = playlists
//    }

//    /**
//     * Test-only API for setting the list of songs in a playlist backed by this
//     * [TestPlaylistRepo].
//     */
//    fun setPodcastsInCategory(categoryId: Long, podcastsInCategory: List<PodcastWithExtraInfo>) {
//        podcastsInCategoryFlow.update {
//            it + Pair(categoryId, podcastsInCategory)
//        }
//    }

//    /**
//     * Test-only API for setting the list of podcasts in a category backed by this
//     * [TestPlaylistRepo].
//     */
//    fun setEpisodesFromPodcast(categoryId: Long, podcastsInCategory: List<EpisodeToPodcast>) {
//        episodesFromPodcasts.update {
//            it + Pair(categoryId, podcastsInCategory)
//        }
//    }

    override suspend fun addPlaylist(playlist: Playlist): Long = -1

    //override suspend fun addPlaylist(playlist: Playlist) =
        //playlistFlow.update { it + playlist } //TODO: need to fix this to properly return

    override suspend fun addPlaylists(playlists: Collection<Playlist>) =
        playlistsFlow.update {
            it + playlists
        }

    override suspend fun count(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun isEmpty(): Boolean =
        playlistsFlow.first().isEmpty()
}
