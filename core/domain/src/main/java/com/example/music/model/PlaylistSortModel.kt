package com.example.music.model

/**
 * A model holding library playlists and total playlists in library
 */
data class PlaylistSortModel(
    val playlists: List<PlaylistInfo> = emptyList(),
    val count: Int = 0
)
