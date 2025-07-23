package com.example.music.ui.shared

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder
import androidx.media3.session.SessionResult
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Scale
import com.example.music.util.logger

internal val Uri.isThirdPartyUri
    get() = scheme == ContentResolver.SCHEME_CONTENT && authority != MediaStore.AUTHORITY

/**
 * returns the positions array from the [DefaultShuffleOrder]
 *
 * FixMe: Extracts array from player using reflection.
 */
internal val Player.orders: IntArray
    @OptIn(UnstableApi::class)
    get() {
        require(this is ExoPlayer)
        val f1 = this.javaClass.getDeclaredField("shuffleOrder")
        f1.isAccessible = true
        val order2 = f1.get(this)
        require(order2 is ShuffleOrder.DefaultShuffleOrder)
        val f2 = order2.javaClass.getDeclaredField("shuffled")
        f2.isAccessible = true
        return f2.get(order2) as IntArray
    }

/**
 * Returns all the [MediaItem]s of [Player] in their natural order.
 *
 * @return A list of [MediaItem]s in the player's natural order.
 */
inline val Player.mediaItems
    get() = List(this.mediaItemCount) {
        getMediaItemAt(it)
    }

/**
 * The queue property represents the list of media items in the player's queue.
 * If shuffle mode is not enabled, the queue will contain the media items in their natural order.
 * If shuffle mode is enabled, the queue will contain the media items in the order specified by the 'orders' list.
 *
 * @return The list of media items in the player's queue.
 */
val Player.queue get() = if (!shuffleModeEnabled) mediaItems else orders.map(::getMediaItemAt)

//suspend fun Context.getAlbumArt(uri: Uri, size: Int = 512): Drawable? {
//    val request = ImageRequest.Builder(context = applicationContext).data(uri)
//        // We scale the image to cover 128px x 128px (i.e. min dimension == 128px)
//        .size(size).scale(Scale.FILL)
//        // Disable hardware bitmaps, since Palette uses Bitmap.getPixels()
//        .allowHardware(false).build()
//    return when (val result = request.context.imageLoader.execute(request)) {
//        is SuccessResult -> result.drawable
//        else -> null
//    }
//}

internal inline fun SessionResult(code: Int, args: Bundle.() -> Unit) =
    SessionResult(code, Bundle().apply(args))


/**
 * Factory function that creates a `Member` object with the given `MediaItem` object and some additional metadata.
 *
 * @param from The `MediaItem` object to create the `Member` from.
 * @param playlistId The ID of the playlist to which this `Member` belongs.
 * @param order The order of this `Member` within the playlist.
 * @return A new `Member` object with the given parameters.
 */
//internal fun Member(from: MediaItem, playlistId: Long, order: Int) =
//    Playlist.Track(
//        playlistID = playlistId,
//        order = order,
//        uri = from.requestMetadata.mediaUri!!.toString(),
//        title = from.mediaMetadata.title.toString(),
//        subtitle = from.mediaMetadata.subtitle.toString(),
//        artwork = from.mediaMetadata.artworkUri?.toString(),
//        mimeType = from.mimeType
//    )

/**
 * Adds a [MediaItem] to the list of recent items.
 *
 * This extension function is designed to be used within the context of the [Playback] service
 * and should not be accessed externally.
 *
 * @receiver Playback: The playback service to which this function is scoped.
 * @param item The [MediaItem] to be added to the list of recent items.
 * @param limit The maximum number of recent items to retain.
 *
 * @throws IllegalArgumentException if [limit] is less than or equal to zero.
 *
 * @see Playback
 */
//internal suspend fun Playlists.addToRecent(item: MediaItem, limit: Long) {
//    val playlistId =
//        get(Playback.PLAYLIST_RECENT)?.id ?: insert(Playlist(name = Playback.PLAYLIST_RECENT))
//    // here two cases arise
//    // case 1 the member already exists:
//    // in this case we just have to update the order and nothing else
//    // case 2 the member needs to be inserted.
//    // In both cases the playlist's dateModified needs to be updated.
//    val playlist = get(playlistId)!!
//    update(playlist = playlist.copy(dateModified = System.currentTimeMillis()))
//
//    val member = get(playlistId, item.requestMetadata.mediaUri.toString())
//
//    when (member != null) {
//        // exists
//        true -> {
//            //localDb.members.update(member.copy(order = 0))
//            // updating the member doesn't work as expected.
//            // localDb.members.delete(member)
//            update(member = member.copy(order = 0))
//        }
//
//        else -> {
//            // delete above member
//            delete(playlistId, limit)
//            insert(Member(item, playlistId, 0))
//        }
//    }
//}

/**
 * Replaces the current queue with the provided list of [items].
 *
 * This extension function is designed to be used within the context of the [Playback] service
 * and should not be accessed externally.
 *
 * @receiver Playback: The playback service to which this function is scoped.
 * @param items The list of [MediaItem] to replace the current queue with.
 *
 * @see Playback
 */
//suspend fun Playlists.save(items: List<MediaItem>) {
//    val id = get(Playback.PLAYLIST_QUEUE)?.id ?: insert(
//        Playlist(name = Playback.PLAYLIST_QUEUE)
//    )
//    // delete all old
//    delete(id, 0)
//    var order = 0
//    val members = items.map { Member(it, id, order++) }
//    insert(members)
//}


/**
 * Calls the specified function [block] and returns its result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution and
 * returning null it as a failure.
 *
 * @param tag: The tag to use for logging the exception message.
 * @param block: The function to execute and catch exceptions for.
 *
 * @return The result of the function if success else null.
 */
inline fun <R> runCatching(tag: String, block: () -> R): R? {
    return try {
        block()
    } catch (e: Throwable) {
//        Log.e(tag, "runCatching: ${e.message}")
        logger.info { "$tag - runCatching: ${e.message}" }
        null
    }
}
