package com.example.music.data.mediaresolver.model

import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.Stable

private const val TAG = "MediaResolver Artist"

/**
 * Data model Artist for Resolver
 */
@Stable
data class Artist(
    @JvmField val id: Long,
    @JvmField val name: String,
    @JvmField val numTracks: Int,
    @JvmField val numAlbums: Int,
    @JvmField val sort: String,
)

/**
 * Transform Cursor to type Artist
 */
fun Cursor.toArtist(): Artist {
    Log.i(TAG, "Cursor to Artist: \n" +
        "ID: ${getLong(0)}\n" +
        "Name: ${getString(1) ?: MediaStore.UNKNOWN_STRING}"
    )
    return Artist(
        id = getLong(0),
        name = getString(1) ?: MediaStore.UNKNOWN_STRING,
        numTracks = getInt(2),
        numAlbums = getInt(3),
        sort = getString(4) ?: MediaStore.UNKNOWN_STRING,
    )
}