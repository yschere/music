package com.example.music.domain.model

/** Changelog:
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/**
 * Domain model for representing search query results. Each property is the
 * list for the corresponding type's search results. They should all return independently
 */
data class SearchQueryFilterResult (
    val songs: List<SongInfo> = emptyList(),
    val artists: List<ArtistInfo> = emptyList(),
    val albums: List<AlbumInfo> = emptyList(),
    val composers: List<ComposerInfo> = emptyList()
    //val playlists: List<PlaylistInfo> = emptyList()
)