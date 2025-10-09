package com.example.music.data.mediaresolver.model

import android.content.ContentUris
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.media3.common.MediaItem
import com.example.music.data.mediaresolver.MediaRepo.Companion.toAlbumArtUri
import com.example.music.data.util.FLAG

private const val TAG = "MediaResolver Audio"

/**
 * Data model Audio for Resolver
 */
@Stable
data class Audio(
    @JvmField val id: Long,
    @JvmField val title: String,
    @JvmField val mimeType: String,
    @JvmField val path: String,
    @JvmField val dateAdded: Long,
    @JvmField val dateModified: Long,
    @JvmField val size: Long,

    @JvmField val artist: String,
    @JvmField val artistId: Long,
    @JvmField val album: String,
    @JvmField val albumId: Long,
    @JvmField val albumArtist: String?,
    @JvmField val composer: String,
    @JvmField val genre: String,
    @JvmField val genreId: Long,
    @JvmField val year: Int,

    @JvmField val duration: Int,
    @JvmField val bitrate: Int,
    @JvmField val trackNumber: Int?,
    @JvmField val cdTrackNumber: String?,
    @JvmField val discNumber: String?,
)

/**
 * Transform Cursor to type Audio
 */
fun Cursor.toAudio(): Audio {
    if (FLAG) Log.i(TAG, "Cursor to Audio:\n" +
        "ID: ${getLong(0)}\n" +
        "Title: ${getString(1)}\n" +
        "Artist: ${getString(7)}\n" +
        "Artist ID: ${getLong(8)}\n" +
        "Album: ${getString(9)}\n" +
        "Album ID: ${getLong(10)}\n" +
        "Album Artist: ${getStringOrNull(11)}"
    )

    return Audio(
        id = getLong(0),
        title = getString(1) ?: MediaStore.UNKNOWN_STRING,
        mimeType = getString(2) ?: MediaStore.UNKNOWN_STRING,
        path = getString(3) ?: MediaStore.UNKNOWN_STRING,
        dateAdded = getLong(4) * 1000,
        dateModified = getLong(5) * 1000,
        size = getLong(6),

        artist = getString(7) ?: MediaStore.UNKNOWN_STRING,
        artistId = getLong(8),
        album = getString(9) ?: MediaStore.UNKNOWN_STRING,
        albumId = getLong(10),
        albumArtist = getStringOrNull(11),
        composer = getString(12) ?: MediaStore.UNKNOWN_STRING,
        genre = getString(13) ?: MediaStore.UNKNOWN_STRING,
        genreId = getLong(14),
        year = getInt(15),

        duration = getInt(16),
        bitrate = getInt(17),
        trackNumber = getIntOrNull(18),
        cdTrackNumber = getStringOrNull(19),
        discNumber = getStringOrNull(20),
    )
}

/**
 * Returns the content URI for this audio file, using the [MediaStore.Audio.Media.EXTERNAL_CONTENT_URI]
 * and appending the file's unique ID.
 *
 * @return the content URI for the audio file
 */
val Audio.uri
    get() = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

/**
 * Returns the content URI for this audio file as a string, using the [uri] property of the audio file.
 *
 * @return the content URI for this audio file as a string
 */
val Audio.key
    get() = uri.toString()

/**
 * Returns the content URI for the album art image of this audio file's album, using the
 * [MediaStore.Images.Media.EXTERNAL_CONTENT_URI] and appending the album ID to the end of the URI.
 *
 * @return the content URI for the album art image of this audio file's album
 */
val Audio.artworkUri
    get() = toAlbumArtUri(albumId)

/**
 * Returns a [MediaItem] object that represents this audio file as a playable media item.
 *
 * @return the [MediaItem] object that represents this audio file
 *
 * **created a different version of this within domain/player/model MediaItem
 */
//val Audio.toMediaItem // the other one had its own class MediaItem that it constructed here
//    get() = MediaItem.Builder()
//        .setMediaId("$id")
//        .setUri(uri)
//        .setMimeType(mimeType)

//.setMediaMetadata() uri, name, artist, "$id", albumUri
//inline val Audio.toMediaItem
//    get() = MediaItem(uri, name, artist, "$id", albumUri)
