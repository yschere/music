package com.example.music.domain.model

/**
 * Domain model to define search query results structure.
 */
data class SearchQueryFilterResult (
    val songs: List<SongInfo> = emptyList(),
    val artists: List<ArtistInfo> = emptyList(),
    val albums: List<AlbumInfo> = emptyList(),
)