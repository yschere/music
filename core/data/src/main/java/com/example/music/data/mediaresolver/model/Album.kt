package com.example.music.data.mediaresolver.model

import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.Stable
import com.example.music.data.mediaresolver.MediaRepo.Companion.toAlbumArtUri

private const val TAG = "MediaResolver Album"

/**
 * Data model Album for Resolver
 */
@Stable
data class Album(
    @JvmField val id: Long,
    @JvmField val title: String,
    @JvmField val albumId: Long,
    @JvmField val artist: String,
    @JvmField val artistId: Long,
    @JvmField val lastYear: Int,
    @JvmField val numTracks: Int,
    @JvmField val sort: String,
)

/**
 * Transform Cursor to type Album
 */
fun Cursor.toAlbum(): Album {
    Log.i(TAG, "Cursor to Album: \n" +
        "ID: ${getLong(0)} \n" +
        "Title: ${getString(1) ?: MediaStore.UNKNOWN_STRING}\n" +
        "Artist: ${getString(3) ?: MediaStore.UNKNOWN_STRING}"
    )
    return Album(
        id = getLong(0),
        title = getString(1) ?: MediaStore.UNKNOWN_STRING,
        albumId = getLong(2),
        artist = getString(3) ?: MediaStore.UNKNOWN_STRING,
        artistId = getLong(4),
        lastYear = getInt(5),
        numTracks = getInt(6),
        sort = getString(7) ?: MediaStore.UNKNOWN_STRING,
    )
}

/**
 * Returns the content URI for the album art image of this album, using the [MediaStore.Images.Media.EXTERNAL_CONTENT_URI]
 * and appending the album's unique ID to the end of the URI.
 *
 * @return the content URI for the album art image of this album
 */
val Album.uri
    get() = toAlbumArtUri(id)