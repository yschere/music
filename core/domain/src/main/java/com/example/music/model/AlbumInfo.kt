package com.example.music.model

import com.example.music.data.database.model.Album
import com.example.music.data.database.model.AlbumWithExtraInfo
import java.time.OffsetDateTime

/**
 * External data layer representation of an album.
 */
data class AlbumInfo(
    val id: Long = 0,
    val title: String = "",
    val albumArtistId: Long? = 0,
    val artwork: String? = "",
    val songCount: Int = 0,
    val dateLastPlayed: OffsetDateTime? = null
)

fun Album.asExternalModel(): AlbumInfo =
    AlbumInfo(
        id = this.id,
        title = this.title,
        albumArtistId = this.albumArtistId?: 0,
        artwork = this.artwork ?: "",
    )

fun AlbumWithExtraInfo.asExternalModel(): AlbumInfo =
    this.album.asExternalModel().copy(
        songCount = songCount,
        dateLastPlayed = dateLastPlayed,
    )
