package com.example.music.domain.model

/**
 * A domain model for holding a genre object and its list of songs.
 * Used in the UI for displaying GenreDetailsScreen.
 */
data class GenreDetailsFilterResult(
    val genre: GenreInfo = GenreInfo(),
    val songs: List<SongInfo> = emptyList(),
)