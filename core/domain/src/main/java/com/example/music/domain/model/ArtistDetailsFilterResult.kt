package com.example.music.domain.model

/** Changelog:
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/**
 * A domain model for holding an artist object, its list of albums, and its list of songs.
 * Used in the UI for displaying ArtistDetailsScreen.
 */
data class ArtistDetailsFilterResult(
    val artist: ArtistInfo = ArtistInfo(),
    val albums: List<AlbumInfo> = emptyList(),
    val songs: List<SongInfo> = emptyList(),
)