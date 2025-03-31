package com.example.music.domain.model

import com.example.music.data.database.model.Song
import com.example.music.domain.player.model.PlayerSong
import com.example.music.domain.util.Audio
import java.time.Duration
import java.time.OffsetDateTime

/**
 * External data layer representation of a song.Intent: to represent a Playlist for the UI, with the ability to
 * order playlists by dateCreated, dateLastAccessed, dateLastPlayed, and song count.
 * @property id [Long] The song's unique ID
 * @property title [String] The title of the song
 * @property artistId [Long] The unique ID for the song's artist, foreign key to the artists table
 * @property albumId [Long] The unique ID for the song's album, foreign key to the albums table
 * @property genreId [Long] The unique ID for the song's genre, foreign key to the genres table
 * @property composerId [Long] The unique ID for the song's composer, foreign key to the composers table
 * @property trackNumber [Int] The order of the song, default is the song entry albumTrackNumber,
 * otherwise can be from song playlist entry playlistTrackNumber
 * @property duration [Duration] The length of the song
 * @property dateLastPlayed [OffsetDateTime] The datetime when the song was last played
 */
data class SongInfo(
    val id: Long = 0,
    val title: String = "",
    val artistId: Long? = 0,
    val artistName: String? = null,
    val albumId: Long? = 0,
    val albumTitle: String? = null,
    val genreId: Long? = 0,
    val genreName: String? = null,
    val composerId: Long? = null,
    val composerName: String? = null,
    val trackNumber: Int? = null,
    val discNumber: Int = 0,
    val duration: Duration = Duration.ZERO,
    val dateAdded: OffsetDateTime? = null,
    val dateModified: OffsetDateTime? = null,
    val dateLastPlayed: OffsetDateTime? = null,
    val size: Long = 0,
    val year: Int? = null,
    val cdTrackNum: Int = 0,
    val srcTrackNum: Int = 0,
    //artwork, dateAdded, fileSize
)

/**
 * Transform Song table entry to SongInfo domain model
 */
fun Song.asExternalModel(): SongInfo =
    SongInfo(
        id = id,
        title = title,
        artistId = artistId,
        albumId = albumId,
        genreId = genreId,
        composerId = composerId,
        trackNumber = albumTrackNumber,
        duration = duration,
        dateLastPlayed = dateLastPlayed,
        year = year,
    )

/**
 * TODO: temporarily adding this in case there's a function where I need to use SongInfo
 *  instead of PlayerSong but retrofitting it to use PlayerSong would be a hassle
 */
fun PlayerSong.asExternalModel(): SongInfo =
    SongInfo(
        id = id,
        title = title,
        trackNumber = trackNumber,
        duration = duration,
    )

fun Audio.asExternalModel(): SongInfo =
    SongInfo(
        id = this.id,
        title = this.title,
        artistId = this.artistId,
        artistName = this.artist,
        albumId = this.albumId,
        albumTitle = this.album,
        genreId = this.genreId,
        duration = Duration.ofMillis(this.duration.toLong()),
        trackNumber = this.trackNumber,
        dateLastPlayed = OffsetDateTime.now(),
        //dateLastPlayed = this.dateModified,
    )