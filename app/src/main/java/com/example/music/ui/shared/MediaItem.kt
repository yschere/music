package com.example.music.ui.shared

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.music.R
import java.io.File
import java.io.FileOutputStream

private const val MEDIA_ITEM_EXTRA_MIME_TYPE = "media_item_extra_mime_type"
private const val TAG = "UI MediaItem"

/**
 * Creates a new [MediaItem] instance using the provided parameters.
 *
 * @param uri The URI of the media item.
 * @param title The title of the media item.
 * @param subtitle The subtitle of the media item.
 * @param id The unique identifier of the media item. Defaults to [MediaItem.DEFAULT_MEDIA_ID].
 * @param artwork The URI of the artwork for the media item. Defaults to null.
 * @return The new [MediaItem] instance.
 *
 */
fun MediaItem(
    uri: Uri,
    title: String,
    subtitle: String,
    id: String = "non_empty",
    artwork: Uri? = null,
    mimeType: String? = null
): MediaItem = MediaItem.Builder()
    .setMediaId(id)
    .setMimeType(mimeType)
    .setRequestMetadata(MediaItem.RequestMetadata.Builder().setMediaUri(uri).build())
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setArtworkUri(artwork)
            .setTitle(title)
            .setSubtitle(subtitle)
            .setIsBrowsable(false)
            .setIsPlayable(true)
            .setExtras(Bundle().apply { putString(MEDIA_ITEM_EXTRA_MIME_TYPE, mimeType) })
            // .setExtras(bundleOf(ARTIST_ID to artistId, ALBUM_ID to albumId))
            .build()
    ).build()

val MediaItem.artworkUri get() = mediaMetadata.artworkUri
val MediaItem.title get() = mediaMetadata.title
val MediaItem.subtitle get() = mediaMetadata.subtitle
val MediaItem.mediaUri get() = requestMetadata.mediaUri

/**
 * Constructs a new [MediaItem] from the provided [uri].
 *
 * This factory method creates a [MediaItem] object from the given URI by extracting
 * media metadata such as title, subtitle, and artwork. It provides a more convenient
 * and flexible way to create [MediaItem] instances compared to [MediaItem.fromUri],
 * as it allows customization of metadata retrieval and caching of artwork.
 *
 * @param uri The URI of the media item.
 * @return A [MediaItem] object representing the media item.
 */
fun MediaItem(
    context: Context,
    uri: Uri,
    mimeType: String?
): MediaItem {
    // Create a MediaMetadataRetriever object and set the data source.
    // maybe it might cause crash; android is stupid.
    val retriever = runCatching(TAG) {
        MediaMetadataRetriever().also { it.setDataSource(context, uri) }
    }

    // Get the URI of the embedded image and cache it.
    val imageUri = runCatching(TAG) {
        val file = File(context.cacheDir, "tmp_artwork.png")
        // Delete the old cached artwork file, if it exists.
        // This ensures that the latest album artwork is used, even if the track previously lacked artwork.
        file.delete()
        val bytes = retriever?.embeddedPicture ?: return@runCatching null
        val fos = FileOutputStream(file)
        fos.write(bytes)
        fos.close()
        Uri.fromFile(file)
    }
    // Obtain title and subtitle
    val title = retriever?.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        ?: context.getString(R.string.unknown)
        //?: context.filename(uri)
    val subtitle = retriever?.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        ?: context.getString(R.string.unknown)
    // Construct a MediaItem using the obtained parameters.
    // (Currently, details about playback queue setup are missing.)
    return MediaItem(uri, title, subtitle, artwork = imageUri, mimeType = mimeType)
}
