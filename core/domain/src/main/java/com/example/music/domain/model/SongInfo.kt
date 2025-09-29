package com.example.music.domain.model

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import com.example.music.data.database.model.Song
import com.example.music.domain.player.model.album
import com.example.music.domain.player.model.artist
import com.example.music.domain.player.model.artworkUri
import com.example.music.domain.player.model.duration
import com.example.music.domain.player.model.mediaUri
import com.example.music.domain.player.model.title
import com.example.music.domain.player.model.year
import com.example.music.data.mediaresolver.model.Audio
import com.example.music.data.mediaresolver.model.artworkUri
import com.example.music.data.mediaresolver.model.uri
import com.example.music.data.util.FLAG
import java.time.Duration
import java.time.OffsetDateTime

private const val TAG = "SongInfo"

/**
 * External data layer representation of a song.
 *
 * Intent: to represent a Playlist for the UI, with the ability to
 * order playlists by dateCreated, dateLastAccessed, dateLastPlayed, and song count.
 * @property id The song's unique ID
 * @property uri The song's uri
 * @property title The title of the song
 * @property artistId The unique ID for the song's artist, foreign key to the artists table
 * @property albumId The unique ID for the song's album, foreign key to the albums table
 * @property genreId The unique ID for the song's genre, foreign key to the genres table
 * @property composerId The unique ID for the song's composer, foreign key to the composers table
 * @property trackNumber The order of the song, default is the song entry albumTrackNumber,
 * otherwise can be from song playlist entry playlistTrackNumber
 * @property duration The length of the song
 * @property dateLastPlayed The datetime when the song was last played
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
    val duration: Duration = Duration.ZERO,
    val dateAdded: OffsetDateTime? = null,
    val dateModified: OffsetDateTime? = null,
    val dateLastPlayed: OffsetDateTime? = null,
    val size: Long = 0,
    val year: Int? = null,
    val trackNumber: Int? = null,
    val trackTotal: Int? = null,
    val discNumber: Int? = null,
    val discTotal: Int? = null,
    val artworkUri: Uri = Uri.parse(""),
    val artworkBitmap: Bitmap? = null,
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
        discNumber = this.mediaMetadata.discNumber,
        artworkUri = this.artworkUri ?: Uri.parse(""),
    )

/**
 * Transform resolver's Audio model to SongInfo
 */
fun Audio.asExternalModel(): SongInfo {
    /* // there's four currently known possible scenarios that determine the value for track total and disc total
    // 1. the cdTrackNumber had a normal value in x/y format, so the total is the y number
    // 2. the cdTrackNumber had a value in x/y/ format, so the total is still the y number, but it needs to also remove the second /
    // 3. the cdTrackNumber had a value in x/ format, so the total is null
    // 4. the cdTrackNumber value was null, so the total is also null */
    val totalTrack = this.cdTrackNumber?.substringAfter('/')?.removeSuffix("/")
    val totalDisc = this.discNumber?.substringAfter('/')?.removeSuffix("/")

    if (FLAG) Log.i(TAG, "Audio to SongInfo:" +
        "ID: ${this.id}\n" +
        "Title: ${this.title}\n" +
        "Artist: ${this.artist}\n" +
        "Album: ${this.album}\n" +
        "Track Number: ${this.cdTrackNumber?.substringBefore('/')?.toInt() ?: "null"}\n" +
        "Track Total: ${totalTrack ?: "null"}\n" +
        "Disc Number: ${this.discNumber?.substringBefore('/')?.toInt() ?: "null"}\n" +
        "Disc Total: ${totalDisc ?: "null"}")

    return  SongInfo(
        id = this.id,
        uri = this.uri,
        title = this.title,
        artistId = this.artistId,
        artistName = this.artist,
        albumId = this.albumId,
        albumTitle = this.album,
        genreId = this.genreId,
        duration = Duration.ofMillis(this.duration.toLong()),
        trackNumber = this.cdTrackNumber?.substringBefore('/')?.toInt(),
        trackTotal =
            if (totalTrack == "") null
            else totalTrack?.toInt(),
        discNumber = this.discNumber?.substringBefore('/')?.toInt(),
        discTotal =
            if (totalDisc == "") null
            else totalDisc?.toInt(),
        dateLastPlayed = OffsetDateTime.now(), //this.dateModified,
        year = this.year,
        artworkUri = this.artworkUri,
    )
}