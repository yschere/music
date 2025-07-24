package com.example.music.domain.model

/** Changelog:
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/**
 * A domain model for holding a composer object and its list of songs.
 * Used in the UI for displaying ComposerDetailsScreen.
 */
data class ComposerDetailsFilterResult(
    val composer: ComposerInfo = ComposerInfo(),
    val songs: List<SongInfo> = emptyList(),
)