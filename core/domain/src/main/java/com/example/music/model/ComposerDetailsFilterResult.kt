package com.example.music.model

import com.example.music.player.model.PlayerSong

/**
 * A model holding top songs (maybe replace with albums or artists??) and
 * matching songs when filtering based on a genre.
 * What makes a topAlbum? I could make it song count for now.
 */
data class ComposerDetailsFilterResult(
    val playlist: PlaylistInfo = PlaylistInfo(),
    val songs: List<SongInfo> = emptyList(),
    val pSongs: List<PlayerSong> = emptyList(),
)