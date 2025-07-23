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
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.Builder
import androidx.media3.common.util.UnstableApi

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
val MediaItem.mimeType get() = mediaMetadata.extras?.get(MEDIA_ITEM_EXTRA_MIME_TYPE) as? String


@OptIn(UnstableApi::class)
fun mediaSource(
    id: Long,
    uri: Uri,
    title: CharSequence,
    artist: CharSequence,
    album: CharSequence,
    albumArtist: CharSequence,
    year: Int,
    duration: Long,
    artwork: Uri? = null,
    mimeType: String? = null,
) = MediaItem.Builder()
    .setMediaId(id.toString())
    .setUri(uri)
    .setMimeType(mimeType)
    .setRequestMetadata(MediaItem.RequestMetadata.Builder().setMediaUri(uri).build())
    .setMediaMetadata(
        MediaMetadata.Builder().setIsBrowsable(false)
            .setIsPlayable(true)
            .setTitle(title)
            .setArtist(artist)
            .setExtras(Bundle().apply { putString(MEDIA_ITEM_EXTRA_MIME_TYPE, mimeType) })
            .setArtworkUri(artwork)
            .setAlbumTitle(album)
            .setAlbumArtist(albumArtist)
            .setReleaseYear(year)
            .setDurationMs(duration)
            .build()
    )
    .build()

fun MediaRoot(value: String): MediaItem =
    MediaItem.Builder()
        .setMediaId(value)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setIsBrowsable(true)
                .setIsPlayable(false)
                .build()
        )
        .build()

//TODO what is Bold used for
private fun Bold(value: CharSequence): CharSequence =
    SpannableStringBuilder(value).apply {
        setSpan(StyleSpan(Typeface.BOLD), 0, value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }


// how do i want a MediaItem to be represented
// what are the properties of a MediaItem
val MediaItem.asMediaSource
    get() = mediaSource(
        id = mediaId.toLong(),
        uri = Uri.parse("/storage/emulated/0/Music/Homestuck/Homestuck - Strife!/01 Stormspirit.mp3"),
        title = Bold(title ?: MediaStore.UNKNOWN_STRING),
        artist = artist ?: MediaStore.UNKNOWN_STRING,
        album = album ?: MediaStore.UNKNOWN_STRING,
        albumArtist = albumArtist ?: MediaStore.UNKNOWN_STRING,
        year = year ?: 0,
        duration = duration ?: 0,
        artwork = artworkUri,
        mimeType = mimeType
    )

// PlayerSong was used as the data type for when an audio/song is transferred to the Player Screen
val PlayerSong.toMediaSource
    get() = mediaSource(
        id = id,
        uri = Uri.parse("/storage/emulated/0/Music/Homestuck/Homestuck - Strife!/01 Stormspirit.mp3"),
        title = Bold(title ?: MediaStore.UNKNOWN_STRING),
        artist = artistName,
        album = albumTitle,
        albumArtist = artistName,
        year = 0,
        duration = duration.toMillis(),
        artwork = Uri.parse(artwork),
        mimeType = null,
    )

/**
 * Creates a playable [MediaItem]
 * @see MediaSource
 */
@JvmInline
value class MediaFile internal constructor(internal val value: MediaItem) {
    @OptIn(UnstableApi::class)
    constructor(
        uri: Uri,
        title: String,
        artist: String,
        album: String,
        albumArtist: String,
        year: Int,
        duration: Long,
        artwork: Uri? = null,
        mimeType: String? = null,
    ) : this(
        MediaItem.Builder()
            .setMediaId("no-media-id")
            .setMimeType(mimeType)
            .setRequestMetadata(MediaItem.RequestMetadata.Builder().setMediaUri(uri).build())
            .setMediaMetadata(
                Builder()
                    .setArtworkUri(artwork)
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