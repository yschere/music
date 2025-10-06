package com.example.music.domain.model

import android.net.Uri
import android.util.Log
import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.data.mediaresolver.model.artworkUri
import com.example.music.data.mediaresolver.model.Album as AlbumMR
import com.example.music.data.util.FLAG
import java.time.OffsetDateTime

private const val TAG = "AlbumInfo"

/**
 * External data layer representation of an album.
 *
 * Intent: to represent an Album for the UI, with the ability to show artwork,
 * order albums based on song's date last played and song count.
 * @property id The album's unique ID
 * @property title The title of the album
 * @property albumArtistId The unique ID for the album's artist, foreign key to the artists table
 * @property artworkUri The content uri to access the album's artwork
 * @property dateLastPlayed The datetime when a song within the album was last played,
 * currently set regardless of context where song was played
 * @property songCount The amount of songs in the album
 */
data class AlbumInfo(
    val id: Long = 0,
    val title: String = "",
    val albumArtistId: Long? = null,
    val albumArtistName: String? = null,
    val year: Int? = null,
    val trackTotal: Int? = null,
    val discTotal: Int? = null,
    val songCount: Int = 0,
    val artworkUri: Uri = Uri.parse(""),
    val dateLastPlayed: OffsetDateTime? = null,
)

/**
 * Transform Album table entry to AlbumInfo domain model
 */
fun Album.asExternalModel(): AlbumInfo {
    if (FLAG) Log.i(TAG, "Album to AlbumInfo external model constructor: \n ${this.id} + ${this.title} + ${this.albumArtistId}")
    return AlbumInfo(
        id = this.id,
        title = this.title,
        albumArtistId = this.albumArtistId,
        year = this.year,
        trackTotal = this.trackTotal,
        discTotal = this.discTotal,
        artworkUri = Uri.parse(this.artwork)
    )
}

/**
 * Transform Album table entry with Extra Info (dateLastPlayed, songCount) to AlbumInfo domain model
 */
fun AlbumWithExtraInfo.asExternalModel(): AlbumInfo {
    if (FLAG) Log.i(TAG, "AlbumWithExtraInfo to AlbumInfo external model constructor: \n ${this.dateLastPlayed} + ${this.songCount} + ${this.album}")
    return this.album.asExternalModel().copy(
        albumArtistName = albumArtistName,
        //dateLastPlayed = dateLastPlayed,
        songCount = songCount,
    )
}

fun AlbumMR.asExternalModel(): AlbumInfo {
    if (FLAG) Log.i(TAG, "AlbumMR to AlbumInfo:" +
        "ID: ${this.id}\n" +
        "Title: ${this.title}")

    return AlbumInfo(
        id = this.albumId,
        title = this.title,
        albumArtistId = this.artistId,
        albumArtistName = this.artist,
        year = this.lastYear,
        //trackTotal = this.numTracksByArtist, // TODO: is this accurate?
        //discTotal // TODO: not sure how to set discTotal. might need to figure that out within MediaRetriever
        songCount = this.numTracks,
        artworkUri = this.artworkUri,
        //dateLastPlayed = OffsetDateTime.now(),
    )
}
