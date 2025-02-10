package com.example.music.model

/**
 * A model holding library artists and total artists in library
 */
data class ArtistSortModel(
    val artists: List<ArtistInfo> = emptyList(),
    val count: Int = 0
)
