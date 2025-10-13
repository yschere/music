package com.example.music.domain.model

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.example.music.data.database.model.Playlist
import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.mediaresolver.model.Playlist as PlaylistMR
import com.example.music.data.util.FLAG
import java.time.OffsetDateTime
import java.util.Date

private const val TAG = "PlaylistInfo"

/**
 * External data layer representation of a playlist.
 *
 * Intent: to represent a Playlist for the UI, with the ability to
 * order playlists by dateCreated, dateLastAccessed, dateLastPlayed, and song count.
 * @property id The playlist's unique ID
 * @property name The name of the playlist
 * @property description The description of the playlist
 * @property dateCreated The datetime when the playlist was created
 * @property dateLastAccessed The datetime when the playlist was last accessed,
 * aka when playlist created or any updates/changes to playlist or its list of songs
 * @property dateLastPlayed The datetime when a song within the playlist was last played,
 * currently set regardless of context where song was played
 * @property songCount The amount of songs in the playlist
 * @property playlistImage The list of Uris from the songs in the playlist
 */
data class PlaylistInfo(
    val id: Long = 0,
    val name: String = "",
    val description: String? = null,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateLastAccessed: OffsetDateTime = OffsetDateTime.now(),
    val dateLastPlayed: OffsetDateTime? = null,
    val songCount: Int = 0,
    val playlistImage: List<Uri> = emptyList(),
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

@SuppressLint("SimpleDateFormat")
fun PlaylistMR.asExternalModel(): PlaylistInfo {
    val localOffset = OffsetDateTime.now().offset
    val dateAdded = OffsetDateTime.parse(java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(
        Date(this.dateAdded)
    ) + localOffset)
    val dateModified = OffsetDateTime.parse(java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(
        Date(this.dateModified)
    ) + localOffset)

    if (FLAG) Log.i(TAG, "PlaylistMR to PlaylistInfo:\n" +
        "ID: ${this.id}\n" +
        "Name: ${this.name}\n" +
        "Description: ${this.displayName}\n" +
        "Date Created: ${this.dateAdded}\n" +
        "Date Last Accessed: ${this.dateModified}\n" +
        "Song Count: ${this.numTracks}")

    return PlaylistInfo(
        id = this.id,
        name = this.name,
        description = this.displayName,
        dateCreated = dateAdded,
        dateLastAccessed = dateModified,
        songCount = this.numTracks,
    )
}

/**
 * Get the artwork Uris of the first four songs of a playlist.
 * - If the playlist has 0 songs, it returns an emptyList().
 * - If there is less than four songs, then it will duplicate the existing Uris until the
 * list fills to four.
 * @param songs list of songs in the playlist
 * @return The list of [Uri] from `songs`
 */
fun PlaylistInfo.getArtworkUris(songs: List<SongInfo>): List<Uri> =
    if (songCount == 0) {
        emptyList()
    } // no thumbnail to make
    else if (songCount < 4) {
        val thumbnails = mutableListOf<Uri>()
        while (thumbnails.size < 4) {
            songs.forEach { song ->
                thumbnails.add(song.artworkUri)
            }
        }
        thumbnails.take(4)
    } // need to repeat song thumbnails to fill 4 spots
    else {
        songs.take(4)
            .map { song ->
                song.artworkUri
            }
    } // have 4 or more songs so use first 4
