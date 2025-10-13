package com.example.music.data.mediaresolver.model

import android.content.ContentUris
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.example.music.data.util.FLAG

private const val TAG = "MediaResolver Playlist"

/**
 * Data model Playlist for Resolver
 */
@Stable
data class Playlist(
    @JvmField val id: Long,
    @JvmField val name: String,
    @JvmField val displayName: String,
    @JvmField val dateAdded: Long,
    @JvmField val dateModified: Long,
    @JvmField val path: String,
    @JvmField val numTracks: Int = 0,
)

/**
 * Data model PlaylistTrack for Resolver
 */
@Stable
data class PlaylistTrack(
    @JvmField val id: Long,
    @JvmField val title: String,
    @JvmField val playlistId: Long,
    @JvmField val artist: String?,
    @JvmField val artistId: Long?,
    @JvmField val albumArtist: String?,
    @JvmField val album: String?,
    @JvmField val albumId: Long?,
    @JvmField val duration: Long,
    @JvmField val dateAdded: Long,
    @JvmField val dateModified: Long,
    @JvmField val playOrder: Int,
    @JvmField val audioId: Long,
)

/**
 * Transform Cursor to type Playlist
 */
fun Cursor.toPlaylist(): Playlist {
    if (FLAG) Log.i(TAG, "Cursor to Playlist:\n" +
        "ID: ${getLong(0)}\n" +
        "Name: ${getString(2)}")

    return Playlist(
        id = getLong(0),
        name = getString(1) ?: MediaStore.UNKNOWN_STRING,
        displayName = getString(2) ?: MediaStore.UNKNOWN_STRING,
        dateAdded = getLong(3) * 1000,
        dateModified = getLong(4) * 1000,
        path = getString(5),
    )
}

fun Cursor.toPlaylist(count: Int): Playlist {
    return this.toPlaylist().copy(
        numTracks = count
    )
}

/**
 * Transform Cursor to type PlaylistTrack
 */
fun Cursor.toPlaylistTrack(): PlaylistTrack {
    if (FLAG) Log.i(TAG, "Cursor to PlaylistTrack:\n" +
        "ID: ${getLong(0)}\n" +
        "Name: ${getString(1)}\n" +
        "Playlist ID: ${getLong(2)}")

    return PlaylistTrack(
        id = getLong(0),
        title = getString(1) ?: MediaStore.UNKNOWN_STRING,
        playlistId = getLong(2),
        artist = getStringOrNull(3),
        artistId = getLongOrNull(4),
        albumArtist = getStringOrNull(5),
        album = getStringOrNull(6),
        albumId = getLongOrNull(7),
        duration = getLong(8),
        dateAdded = getLong(9) * 1000,
        dateModified = getLong(10) * 1000,
        playOrder = getInt(11),
        audioId = getLong(12),
    )
}

val PlaylistTrack.uri
    get() = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)