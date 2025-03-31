package com.example.music.domain.model

/**
 * A domain model holding songs recently added to library and
 * separate recently played playlists. Playlists sort
 * based on dateLastPlayed. Songs sort based on dateAdded.
 * Used in the UI for displaying HomeScreen.
 */
data class FeaturedLibraryItemsFilterResult (
    val recentPlaylists: List<PlaylistInfo> = emptyList(),
    val recentlyAddedSongs: List<SongInfo> = emptyList()
)
