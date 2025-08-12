package com.example.music.domain.model

import android.util.Log
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.ArtistWithExtraInfo
import com.example.music.domain.util.Artist as ArtistV2
import com.example.music.domain.util.domainLogger

private const val TAG = "ArtistInfo"

/**
 * External data layer representation of an artist.
 * Intent: to represent an Artist for the UI, with the ability to
 * order artists by album count and song count.
 * @property id [Long] The artist's unique ID
 * @property name [String] The name of the artist
 * @property albumCount [Int] The amount of albums by the artist
 * @property songCount [Int] The amount of songs by the artist
 */
data class ArtistInfo(
    val id: Long = 0,
    val name: String = "",
    val albumCount: Int = 0,
    val songCount: Int = 0,
)

/**
 * Transform Artist table entry to ArtistInfo domain model
 */
fun Artist.asExternalModel(): ArtistInfo {
    Log.i(TAG, "Artist to ArtistInfo external model constructor: \n ${this.id} + ${this.name}")
    return ArtistInfo(
        id = id,
        name = name,
    )
}

/**
 * Transform Artist table entry with Extra Info (albumCount, songCount) to ArtistInfo domain model
 */
fun ArtistWithExtraInfo.asExternalModel(): ArtistInfo {
    Log.i(TAG, "ArtistWithExtraInfo to ArtistInfo external model constructor: \n ${this.artist} + ${this.songCount} + ${this.albumCount}")
    return this.artist.asExternalModel().copy(
        albumCount = albumCount,
        songCount = songCount,
    )
}

fun ArtistV2.asExternalModel(): ArtistInfo {
    Log.i(TAG, "ArtistV2 to ArtistInfo external model constructor: \n ${this.id} + ${this.name}")
    return ArtistInfo(
        id = this.id,
        name = this.name,
        albumCount = this.numAlbums,
        songCount = this.numTracks,
    )
}