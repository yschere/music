package com.example.music.model

/**
 * A model holding library songs and total songs in library
 */
data class SongSortModel(
    val songs: List<SongInfo> = emptyList(),
    val count: Int = 0
) {
    val isEmpty = songs.isEmpty() || count == 0
}
