package com.example.music.model

/**
 * Leftover domain model reinterpretation from Jetcaster to Musicality
 */
data class ArtistGenreFilterResult(
    val topArtists: List<ArtistInfo> = emptyList(),
    val songs: List<SongInfo> = emptyList()
)
