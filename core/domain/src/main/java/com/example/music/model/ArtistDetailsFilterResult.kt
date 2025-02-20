package com.example.music.model

import com.example.music.player.model.PlayerSong

/**
 * A domain model for holding an artist object, its list of albums, and its list of songs.
 * Used in the UI for displaying ArtistDetailsScreen.
 */
data class ArtistDetailsFilterResult(
    val artist: ArtistInfo = ArtistInfo(),
    val albums: List<AlbumInfo> = emptyList(),
    val songs: List<SongInfo> = emptyList(),
    val pSongs: List<PlayerSong> = emptyList(),
)