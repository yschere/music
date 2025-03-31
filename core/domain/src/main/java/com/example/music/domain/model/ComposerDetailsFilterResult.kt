package com.example.music.domain.model

import com.example.music.player.model.PlayerSong

/**
 * A domain model for holding a composer object and its list of songs.
 * Used in the UI for displaying ComposerDetailsScreen.
 */
data class ComposerDetailsFilterResult(
    val composer: ComposerInfo = ComposerInfo(),
    val songs: List<SongInfo> = emptyList(),
    val pSongs: List<PlayerSong> = emptyList(),
)