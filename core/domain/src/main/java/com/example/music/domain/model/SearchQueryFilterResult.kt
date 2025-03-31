package com.example.music.domain.model

import com.example.music.player.model.PlayerSong

/**
 * Domain model for representing search query results. Each property is the
 * list for the corresponding type's search results. They should all return independently
 */
data class SearchQueryFilterResult (
    val songs: List<SongInfo> = emptyList(),
    val pSongs: List<PlayerSong> = emptyList(),
    val artists: List<ArtistInfo> = emptyList(),
    val albums: List<AlbumInfo> = emptyList(),
    val composers: List<ComposerInfo> = emptyList()
    //val playlists: List<PlaylistInfo> = emptyList()
)