package com.example.music.domain.model

/** Changelog:
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/**
 * A domain model for holding an album object and its list of songs.
 * Used in the UI for displaying AlbumDetailsScreen.
 */
data class AlbumDetailsFilterResult(
    val album: AlbumInfo = AlbumInfo(),
    val artist: ArtistInfo = ArtistInfo(),
    val songs: List<SongInfo> = emptyList(),
)