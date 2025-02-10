package com.example.music.model

/**
 * A model holding library albums and total albums in library
 */
data class AlbumSortModel(
    val albums: List<AlbumInfo> = emptyList(),
    val count: Int = 0
)
