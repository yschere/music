package com.example.music.model

/**
 * A model holding top songs (maybe replace with albums or artists??) and
 * matching songs when filtering based on a genre.
 * What makes a topAlbum? I could make it song count for now.
 */
data class ArtistGenreFilterResult(
    val topArtists: List<ArtistInfo> = emptyList(),
    val songs: List<SongInfo> = emptyList()
)
