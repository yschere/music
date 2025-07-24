package com.example.music.domain.model

import android.net.Uri
import androidx.media3.common.MediaItem
import com.example.music.data.database.model.Song
import com.example.music.domain.player.model.album
import com.example.music.domain.player.model.artist
import com.example.music.domain.player.model.artworkUri
import com.example.music.domain.player.model.duration
import com.example.music.domain.player.model.mediaUri
import com.example.music.domain.player.model.title
import com.example.music.domain.player.model.year

import com.example.music.domain.util.Audio
import com.example.music.domain.util.uri
import java.time.Duration
import java.time.OffsetDateTime

/** Changelog:
 *
 * 7/22-23/2025 - Added Uri property to SongInfo.
 * Added transformer from MediaItem to SongInfo.
 * Removed PlayerSong completely
 */

/**
 * External data layer representation of a song.Intent: to represent a Playlist for the UI, with the ability to
 * order playlists by dateCreated, dateLastAccessed, dateLastPlayed, and song count.
 * @property id [Long] The song's unique ID
 * @property uri [Uri] The song's uri
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
    val uri: Uri = Uri.parse(""),
    val title: String = "",
    //val artistId: Long? = 0,
    val artistId: Long = 0,
    val artistName: String = "",
    //val albumId: Long? = 0,
    val albumId: Long = 0,
    val albumTitle: String = "",
    val genreId: Long? = 0,
    val genreName: String? = null,
    val composerId: Long? = null,
    val composerName: String? = null,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val duration: Duration = Duration.ZERO,
    val dateAdded: OffsetDateTime? = null,
    val dateModified: OffsetDateTime? = null,
    val dateLastPlayed: OffsetDateTime? = null,
    val size: Long = 0,
    val year: Int? = null,
    val cdTrackNum: Int? = null,
    val srcTrackNum: Int? = null,
    val artwork: Uri? = null,
    //fileSize
)

/**
 * Transform Song table entry to SongInfo domain model
 */
fun Song.asExternalModel(): SongInfo =
    SongInfo(
        id = id,
        title = title,
        artistId = artistId ?: 0,
        albumId = albumId ?: 0,
        genreId = genreId,
        composerId = composerId,
        trackNumber = albumTrackNumber,
        duration = duration,
        dateLastPlayed = dateLastPlayed,
        year = year,
    )

/**
 * Transform media3 MediaItem to SongInfo
 */
fun MediaItem.toSongInfo(): SongInfo =
    SongInfo(
        id = this.mediaId.toLong(),
        uri = this.mediaUri!!,
        title = this.title.toString(),
        artistName = this.artist.toString(),
        albumTitle = this.album.toString(),
        genreName = this.mediaMetadata.genre.toString(),
        duration = Duration.ofMillis(this.duration!!.toLong()),
        composerName = this.mediaMetadata.composer.toString(),
        trackNumber = this.mediaMetadata.trackNumber,
        //dateLastPlayed
        //size
        year = this.year,
        cdTrackNum = this.mediaMetadata.discNumber,
        //srcTrackNum
        artwork = this.artworkUri,
    )

/**
 * Transform resolver's Audio model to SongInfo
 */
fun Audio.asExternalModel(): SongInfo =
    SongInfo(
        id = this.id,
        uri = this.uri,
        title = this.title,
        artistId = this.artistId,
        artistName = this.artist,
        albumId = this.albumId,
        albumTitle = this.album,
        genreId = this.genreId,
        duration = Duration.ofMillis(this.duration.toLong()),
        //trackNumber = this.trackNumber, // i think this is the combined CD and Track numbers as xyyy
        trackNumber = this.cdTrackNumber,
        dateLastPlayed = OffsetDateTime.now(),
        //dateLastPlayed = this.dateModified,
        year = this.year,
    )