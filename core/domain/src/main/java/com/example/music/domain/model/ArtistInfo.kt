package com.example.music.domain.model

import android.util.Log
import com.example.music.data.database.model.Artist
import com.example.music.data.database.model.ArtistWithExtraInfo
import com.example.music.data.mediaresolver.model.Artist as ArtistMR
import com.example.music.data.util.FLAG

private const val TAG = "ArtistInfo"

/**
 * External data layer representation of an artist.
 *
 * Intent: to represent an Artist for the UI, with the ability to
 * order artists by album count and song count.
 * @property id The artist's unique ID
 * @property name The name of the artist
 * @property albumCount The amount of albums by the artist
 * @property songCount The amount of songs by the artist
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
    if (FLAG) Log.i(TAG, "Artist to ArtistInfo external model constructor: \n ${this.id} + ${this.name}")
    return ArtistInfo(
        id = id,
        name = name,
    )
}

/**
 * Transform Artist table entry with Extra Info (albumCount, songCount) to ArtistInfo domain model
 */
fun ArtistWithExtraInfo.asExternalModel(): ArtistInfo {
    if (FLAG) Log.i(TAG, "ArtistWithExtraInfo to ArtistInfo external model constructor: \n ${this.artist} + ${this.songCount} + ${this.albumCount}")
    return this.artist.asExternalModel().copy(
        albumCount = albumCount,
        songCount = songCount,
    )
}

/**
 * Transform Artist from MediaResolver to ArtistInfo domain model
 */
fun ArtistMR.asExternalModel(): ArtistInfo {
    if (FLAG) Log.i(TAG, "ArtistMR to ArtistInfo:\n" +
        "ID: ${this.id}\n" +
        "Name: ${this.name}\n" +
        "Album count: ${this.numAlbums}\n" +
        "Song count: ${this.numTracks}")

    return ArtistInfo(
        id = this.id,
        name = this.name,
        albumCount = this.numAlbums,
        songCount = this.numTracks,
    )
}