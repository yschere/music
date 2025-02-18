package com.example.music.model

import com.example.music.data.database.model.Artist

/**
 * External data layer representation of an artist.
 */
data class ArtistInfo(
    val id: Long = 0,
    val name: String = "",
)

fun Artist.asExternalModel(): ArtistInfo =
    ArtistInfo(
        id = this.id,
        name = this.name,
    )
