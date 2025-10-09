package com.example.music.data.mediaresolver.model

import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.Stable
import com.example.music.data.util.FLAG

private const val TAG = "MediaResolver Artist"

/**
 * Data model Artist for Resolver
 */
@Stable
data class Artist(
    @JvmField val id: Long,
    @JvmField val name: String,
    @JvmField val numAlbums: Int,
    @JvmField val numTracks: Int,
)

/**
 * Transform Cursor to type Artist
 */
fun Cursor.toArtist(): Artist {
    if (FLAG) Log.i(TAG, "Cursor to Artist:\n" +
        "ID: ${getLong(0)}\n" +
        "Name: ${getString(1) ?: MediaStore.UNKNOWN_STRING}\n" +
        "Album count: ${getInt(2)}\n" +
        "Song count: ${getInt(3)}"
    )

    return Artist(
        id = getLong(0),
        name = getString(1) ?: MediaStore.UNKNOWN_STRING,
        numAlbums = getInt(2),
        numTracks = getInt(3),
    )
}