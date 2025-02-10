package com.example.music.model

/**
 * A model holding songs recently added to library and
 * separate recently played playlists. Playlists sort
 * based on dateLastPlayed. Songs sort based on dateAdded
 */
data class FeaturedLibraryItemsFilterResult (
    val recentPlaylists: List<PlaylistInfo> = emptyList(),
    val recentlyAddedSongs: List<SongInfo> = emptyList()
)
