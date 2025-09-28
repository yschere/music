package com.example.music.domain.model

import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import java.time.OffsetDateTime

/**
 * External data layer representation of a playlist.
 * Intent: to represent a Playlist for the UI, with the ability to
 * order playlists by dateCreated, dateLastAccessed,
 * dateLastPlayed, and song count.
 * @property id The playlist's unique ID
 * @property name The name of the playlist
 * @property description The description of the playlist
 * @property dateCreated The datetime when the playlist was created
 * @property dateLastAccessed The datetime when the playlist was last accessed,
 * aka when playlist created or any updates/changes to playlist or its list of songs
 * @property dateLastPlayed The datetime when a song within the playlist was last played,
 * currently set regardless of context where song was played
 * @property songCount The amount of songs by the playlist
 */
data class PlaylistInfo(
    val id: Long = 0,
    val name: String = "",
    val description: String? = null,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateLastAccessed: OffsetDateTime = OffsetDateTime.now(),
    val dateLastPlayed: OffsetDateTime? = null,
    val songCount: Int = 0
)

/**
 * Transform Playlist table entry to PlaylistInfo domain model
 */
fun Playlist.asExternalModel(): PlaylistInfo =
    PlaylistInfo(
        id = this.id,
        name = this.name,
        description = this.description,
        dateCreated = this.dateCreated,
        dateLastAccessed = this.dateLastAccessed,
    )

/**
 * Transform Playlist table entry with Extra Info (dateLastPlayed, songCount) to PlaylistInfo domain model
 */
fun PlaylistWithExtraInfo.asExternalModel(): PlaylistInfo =
    this.playlist.asExternalModel().copy(
        dateLastPlayed = dateLastPlayed, //would be acquired from the song with the latest dateLastPlayed value
        songCount = songCount, //would be acquired from the total count of songs in playlist
    )
