package com.example.music.domain.model

import com.example.music.domain.player.model.PlayerSong

/**
 * A domain model for holding a playlist object and its list of songs.
 * Used in the UI for displaying PlaylistDetailsScreen.
 */
data class PlaylistDetailsFilterResult(
    val playlist: PlaylistInfo = PlaylistInfo(),
    val songs: List<SongInfo> = emptyList(),
    //val pSongs: List<PlayerSong> = emptyList(),
)