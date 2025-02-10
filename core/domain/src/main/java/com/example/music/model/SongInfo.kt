package com.example.music.model

import com.example.music.data.database.model.Song
import com.example.music.player.model.PlayerSong
import java.time.Duration
import java.time.OffsetDateTime

/**
 * External data layer representation of a song.
 */
data class SongInfo(
    val id: Long = 0,
    val title: String = "",
    val artistId: Long? = 0,
    val albumId: Long? = 0,
    val genreId: Long? = 0,
    val albumTrackNumber: Int? = 0,
    val duration: Duration? = null,
    val dateLastPlayed: OffsetDateTime? = null,
    //artwork, dateAdded, fileSize
)

fun Song.asExternalModel(): SongInfo =
    SongInfo(
        id = id,
        title = title,
        artistId = artistId,
        albumId = albumId,
        genreId = genreId,
        albumTrackNumber = albumTrackNumber,
        duration = duration,
        dateLastPlayed = dateLastPlayed,
    )

//TODO: temporarily adding this in case there's a spot where I need to use SongInfo, but retrofitting it to PlayerSong would be a hassle
fun PlayerSong.asExternalModel(): SongInfo =
    SongInfo(
        id = id,
        title = title,
    )