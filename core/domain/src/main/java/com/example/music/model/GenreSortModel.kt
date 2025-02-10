package com.example.music.model

/**
 * A model holding library genres and total genres in library
 */
data class GenreSortModel(
    val genres: List<GenreInfo> = emptyList(),
    val count: Int = 0
)
