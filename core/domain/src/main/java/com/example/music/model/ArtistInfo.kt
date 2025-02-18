package com.example.music.model

import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.ArtistWithExtraInfo
import com.example.music.util.domainLogger

/**
 * External data layer representation of an artist.
 * Intent: to represent an Artist for the UI, with the ability to order
 * artists by song count and album count.
 * @property id [Long] is the artist's unique ID
 * @property name [String] is the name of the artist
 */
data class ArtistInfo(
    val id: Long = 0,
    val name: String = "",
    val songCount: Int? = 0,
    val albumCount: Int? = 0
)

fun Artist.asExternalModel(): ArtistInfo {
    domainLogger.info { "Artist to ArtistInfo external model constructor: \n ${this.id} + ${this.name}" }
    return ArtistInfo(
        id = id,
        name = name,
    )
}

fun ArtistWithExtraInfo.asExternalModel(): ArtistInfo {
    domainLogger.info { "ArtistWithExtraInfo to ArtistInfo external model constructor: \n ${this.artist} + ${this.songCount} + ${this.albumCount}" }
    return this.artist.asExternalModel().copy(
        songCount = songCount ?: 0,
        albumCount = albumCount ?: 0
    )
}