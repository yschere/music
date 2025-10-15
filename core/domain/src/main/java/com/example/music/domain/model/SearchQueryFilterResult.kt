package com.example.music.domain.model

/**
 * Domain model to define search query results structure.
 */
data class SearchQueryFilterResult (
    val songs: List<SongInfo> = emptyList(),
    val songCount: Int = 0,
    val artists: List<ArtistInfo> = emptyList(),
    val artistCount: Int = 0,
    val albums: List<AlbumInfo> = emptyList(),
    val albumCount: Int = 0,
)