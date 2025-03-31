package com.example.music.domain.model

import com.example.music.player.model.PlayerSong

/**
 * A domain model for holding an album object and its list of songs.
 * Used in the UI for displaying AlbumDetailsScreen.
 */
data class AlbumDetailsFilterResult(
    val album: AlbumInfo = AlbumInfo(),
    val artist: ArtistInfo = ArtistInfo(),
    val songs: List<SongInfo> = emptyList(),
    val pSongs: List<PlayerSong> = emptyList(),
)