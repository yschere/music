package com.example.music.model

import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import java.time.OffsetDateTime

/**
 * External data layer representation of a playlist.
 */
data class PlaylistInfo(
    val id: Long = 0,
    val name: String = "",
    val description: String? = null,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateLastPlayed: OffsetDateTime? = null,
    val songCount: Int = 0
)

fun Playlist.asExternalModel(): PlaylistInfo =
    PlaylistInfo(
        id = this.id,
        name = this.name,
        description = this.description,
        dateCreated = this.dateCreated,
    )

fun PlaylistWithExtraInfo.asExternalModel(): PlaylistInfo =
    this.playlist.asExternalModel().copy(
        dateLastPlayed = dateLastPlayed, //would be acquired from the song with the latest dateLastPlayed value
        songCount = songCount, //would be acquired from the total count of songs in playlist
    )
