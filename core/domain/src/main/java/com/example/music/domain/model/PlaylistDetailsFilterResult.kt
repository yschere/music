package com.example.music.domain.model

/** Changelog:
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/**
 * A domain model for holding a playlist object and its list of songs.
 * Used in the UI for displaying PlaylistDetailsScreen.
 */
data class PlaylistDetailsFilterResult(
    val playlist: PlaylistInfo = PlaylistInfo(),
    val songs: List<SongInfo> = emptyList(),
)