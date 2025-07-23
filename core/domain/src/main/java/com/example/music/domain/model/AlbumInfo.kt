package com.example.music.domain.model

import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import com.example.music.domain.util.Album as AlbumV2
import java.time.OffsetDateTime
import com.example.music.domain.util.domainLogger

/**
 * External data layer representation of an album.
 * Intent: to represent an Album for the UI, with the ability to show artwork,
 * order albums based on song's date last played and song count.
 * @property id [Long] The album's unique ID
 * @property title [String] The title of the album
 * @property albumArtistId [Long] The unique ID for the album's artist, foreign key to the artists table
 * @property artwork [String] The descriptor for the album's artwork
 * @property dateLastPlayed [OffsetDateTime] The datetime when a song within the album was last played,
 * currently set regardless of context where song was played
 * @property songCount [Int] The amount of songs in the album
 */
data class AlbumInfo(
    val id: Long = 0,
    val title: String = "",
    val albumArtistId: Long? = null,
    val albumArtistName: String? = null,
    val year: Int? = null,
    val trackTotal: Int? = null,
    val discTotal: Int? = null,
    val artwork: String? = null,
    val dateLastPlayed: OffsetDateTime? = null,
    val songCount: Int = 0,
)

/**
 * Transform Album table entry to AlbumInfo domain model
 */
fun Album.asExternalModel(): AlbumInfo {
    domainLogger.info { "Album to AlbumInfo external model constructor: \n ${this.id} + ${this.title} + ${this.albumArtistId}" }
    return AlbumInfo(
        id = this.id,
        title = this.title,
        albumArtistId = this.albumArtistId,
        year = this.year,
        trackTotal = this.trackTotal,
        discTotal = this.discTotal,
        artwork = this.artwork
    )
}

/**
 * Transform Album table entry with Extra Info (dateLastPlayed, songCount) to AlbumInfo domain model
 */
fun AlbumWithExtraInfo.asExternalModel(): AlbumInfo {
    domainLogger.info { "AlbumWithExtraInfo to AlbumInfo external model constructor: \n ${this.dateLastPlayed} + ${this.songCount} + ${this.album}" }
    return this.album.asExternalModel().copy(
        albumArtistName = albumArtistName,
        dateLastPlayed = dateLastPlayed,
        songCount = songCount,
    )
}

fun AlbumV2.asExternalModel(): AlbumInfo {
    domainLogger.info { "AlbumV2 to AlbumInfo external model constructor: \n ${this.id} + ${this.title}" }
    return AlbumInfo(
        id = this.albumId,
        title = this.title,
        albumArtistId = this.artistId,
        albumArtistName = this.artist,
        year = this.lastYear,
        //trackTotal = this.numTracksByArtist,
        //dateLastPlayed = OffsetDateTime.now(),
        songCount = this.numTracks, //not sure if this is numTracks or need to set this value in a different way
        //not sure how to set discTotal nor artwork . might need to figure that out within MediaRetriever
    )
}
