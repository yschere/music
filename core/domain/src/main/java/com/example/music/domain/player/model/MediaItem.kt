package com.example.music.domain.player.model

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata.Builder
import androidx.media3.common.util.UnstableApi
import com.example.music.domain.model.SongInfo
import com.example.music.data.mediaresolver.model.Audio
import com.example.music.data.mediaresolver.model.artworkUri
import com.example.music.data.mediaresolver.model.uri

/**
 * Constant for the MIME type extra key in media items.
 */
private const val MEDIA_ITEM_EXTRA_MIME_TYPE = "media_item_extra_mime_type"

/**
 * Gets the artwork URI of a [MediaItem].
 */
val MediaItem.artworkUri get() = mediaMetadata.artworkUri

/**
 * Gets the title of a [MediaItem].
 */
val MediaItem.title get() = mediaMetadata.title
val MediaItem.artist get() = mediaMetadata.artist
val MediaItem.album get() = mediaMetadata.albumTitle
val MediaItem.albumArtist get() = mediaMetadata.albumArtist
val MediaItem.duration
    @OptIn(UnstableApi::class)
    get() = mediaMetadata.durationMs
val MediaItem.year get() = mediaMetadata.releaseYear
val MediaItem.mediaUri get() = requestMetadata.mediaUri
val MediaItem.mimeType get() = mediaMetadata.extras?.getString(MEDIA_ITEM_EXTRA_MIME_TYPE)

/**
 * Function to build a MediaItem
 * @param id
 * @param uri
 * @param title
 * @param artist
 * @param album
 * @param albumArtist
 * @param year
 * @param duration
 * @param artworkUri
 * @param mimeType
 */
@OptIn(UnstableApi::class)
private fun mediaSource(
    id: Long,
    uri: Uri,
    title: CharSequence,
    artist: CharSequence,
    album: CharSequence,
    albumArtist: CharSequence,
    year: Int,
    duration: Long,
    artworkUri: Uri? = null,
    mimeType: String? = null,
) = MediaItem.Builder()
    .setMediaId(id.toString())
    .setUri(uri)
    .setMimeType(mimeType)
    .setRequestMetadata(MediaItem.RequestMetadata.Builder().setMediaUri(uri).build())
    .setMediaMetadata(
        Builder().setIsBrowsable(false)
            .setIsPlayable(true)
            .setTitle(title)
            .setArtist(artist)
            .setExtras(Bundle().apply { putString(MEDIA_ITEM_EXTRA_MIME_TYPE, mimeType) })
            .setArtworkUri(artworkUri)
            .setAlbumTitle(album)
            .setAlbumArtist(albumArtist)
            .setReleaseYear(year)
            .setDurationMs(duration)
            .build()
    )
    .build()

/**
 * Sets the text in the parameter to typeface bold
 */
private fun Bold(value: CharSequence): CharSequence =
    SpannableStringBuilder(value).apply {
        setSpan(StyleSpan(Typeface.BOLD), 0, value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

/**
 * Transform media3 MediaItem to domain layer's MediaItem
 */
val MediaItem.asLocalMediaItem
    get() = mediaSource(
        id = mediaId.toLong(),
        uri = mediaUri!!,
        title = Bold(title ?: MediaStore.UNKNOWN_STRING),
        artist = artist ?: MediaStore.UNKNOWN_STRING,
        album = album ?: MediaStore.UNKNOWN_STRING,
        albumArtist = albumArtist ?: MediaStore.UNKNOWN_STRING,
        year = year ?: 0,
        duration = duration ?: 0,
        artworkUri = artworkUri,
        mimeType = mimeType
    )

/**
 * Transform SongInfo to domain layer's MediaItem
 */
val SongInfo.toMediaItem
    get() = mediaSource(
        id = id,
        uri = uri,
        title = Bold(title),
        artist = artistName,
        album = albumTitle,
        albumArtist = artistName,
        year = year ?: 0,
        duration = duration.toMillis(),
        artworkUri = artworkUri,
        mimeType = null,
    )

/**
 * Returns a [MediaItem] object that represents this audio file as a playable media item.
 *
 * @return the [MediaItem] object that represents this audio file
 */
val Audio.toMediaItem
    get() = mediaSource(
        id = id,
        uri = uri,
        title = title,
        artist = artist,
        album = album,
        albumArtist = albumArtist.toString(),
        year = year,
        duration = duration.toLong(),
        artworkUri = artworkUri,
        mimeType = mimeType,
    )

/**
 * Creates a playable [MediaItem]
 * @see mediaSource
 */
@OptIn(UnstableApi::class)
@JvmInline
value class MediaFile internal constructor(internal val value: MediaItem) {
    constructor(
        uri: Uri,
        title: String,
        artist: String,
        album: String,
        albumArtist: String,
        year: Int,
        duration: Long,
        artworkUri: Uri? = null,
        mimeType: String? = null,
    ) : this(
        MediaItem.Builder()
            .setMediaId("no-media-id")
            .setMimeType(mimeType)
            .setRequestMetadata(MediaItem.RequestMetadata.Builder().setMediaUri(uri).build())
            .setMediaMetadata(
                Builder()
                    .setArtworkUri(artworkUri)
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setAlbumArtist(albumArtist)
                    .setReleaseYear(year)
                    .setDurationMs(duration)
                    .setExtras(Bundle().apply { putString(MEDIA_ITEM_EXTRA_MIME_TYPE, mimeType) })
                    .setIsBrowsable(false)
                    .setIsPlayable(true)
                    .build()
            ).build()
    )

    val mediaUri get() = value.mediaUri
    val title get() = value.title?.toString()
    val artist get() = value.artist?.toString()
    val album get() = value.album?.toString()
    val albumArtist get() = value.albumArtist?.toString()
    val year get() = value.year
    val duration get() = value.duration
    val artworkUri get() = value.artworkUri
    val mimeType get() = value.mimeType
}