package com.example.music.data.mediaresolver.model

import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.Stable

private const val TAG = "MediaResolver Genre"

/**
 * Data model Genre for Resolver
 */
@Stable
data class Genre(
    @JvmField val id: Long,
    @JvmField val name: String,
    @JvmField val sort: String,
    @JvmField val numTracks: Int = -1
)

/**
 * Transform Cursor to type Genre
 */
fun Cursor.toGenre(): Genre {
    Log.i(TAG, "Cursor to Genre: \n" +
        "ID: ${getLong(0)} \n" +
        "Name: ${getString(1) ?: MediaStore.UNKNOWN_STRING}\n" +
        "Sort: ${getString(2) ?: MediaStore.UNKNOWN_STRING}"
    )
    return Genre(
        id = getLong(0),
        name = getString(1) ?: MediaStore.UNKNOWN_STRING,
        sort = getString(2) ?: MediaStore.UNKNOWN_STRING,
    )
}

fun Cursor.toGenre(count: Int): Genre {
    return this.toGenre().copy(
        numTracks = count
    )
}