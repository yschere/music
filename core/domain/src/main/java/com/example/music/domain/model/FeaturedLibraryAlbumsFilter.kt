package com.example.music.domain.model

/**
 * A domain model holding songs recently added to library and
 * separate recently added albums.
 * Used in the UI for displaying HomeScreen.
 */
data class FeaturedLibraryAlbumsFilter(
    val recentAlbums: List<AlbumInfo> = emptyList(),
    val recentlyAddedSongs: List<SongInfo> = emptyList()
)
