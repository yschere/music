package com.example.music.domain.model

import com.example.music.domain.player.model.PlayerSong

/**
 * A domain model for holding a genre object and its list of songs.
 * Used in the UI for displaying GenreDetailsScreen.
 */
data class GenreDetailsFilterResult(
    val genre: GenreInfo = GenreInfo(),
    val songs: List<SongInfo> = emptyList(),
    //val pSongs: List<PlayerSong> = emptyList(),
)