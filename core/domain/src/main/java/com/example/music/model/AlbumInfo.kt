package com.example.music.model

import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import java.time.OffsetDateTime
import com.example.music.util.domainLogger

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
        artwork = this.artwork
    )
}

/**
 * Transform Album table entry with Extra Info (dateLastPlayed, songCount) to AlbumInfo domain model
 */
fun AlbumWithExtraInfo.asExternalModel(): AlbumInfo {
    domainLogger.info { "AlbumWithExtraInfo to AlbumInfo external model constructor: \n ${this.dateLastPlayed} + ${this.songCount} + ${this.album}" }
    return this.album.asExternalModel().copy(
        dateLastPlayed = dateLastPlayed,
        songCount = songCount,
    )
}
