package com.example.music.ui.player

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.example.music.domain.player.model.mediaUri
import com.example.music.service.MediaService

private const val TAG = "NowPlaying"

// extra
private const val EXTRA_TITLE = "com.example.music.ui.player.extra.MEDIA_TITLE" //putExtra(EXTRA_TITLE, mediaItem.mediaMetadata.title.toString())
private val EXTRA_ALBUM = "com.example.music.ui.player.extra.MEDIA_ALBUM" //putExtra(EXTRA_SUBTITLE, mediaItem.mediaMetadata.subtitle.toString())
private val EXTRA_ARTWORK = "com.example.music.ui.player.extra.MEDIA_ARTWORK" //val uri = mediaItem.mediaMetadata.artworkUri //putExtra(EXTRA_ARTWORK, uri)
private val EXTRA_DURATION = "com.example.music.ui.player.extra.MEDIA_DURATION" //putExtra(EXTRA_DURATION, player.duration)
private val EXTRA_POSITION = "com.example.music.ui.player.extra.MEDIA_PROGRESS" //putExtra(EXTRA_POSITION, player.currentPosition)
private val EXTRA_STATE = "com.example.music.ui.player.extra.MEDIA_STATE" //putExtra(EXTRA_STATE, player.playbackState)
private val EXTRAS_PLAY_WHEN_READY = "com.example.music.ui.player.extra.PLAY_WHEN_READY" //putExtra(EXTRAS_PLAY_WHEN_READY, player.playWhenReady)
private val EXTRA_WHEN = "com.example.music.ui.player.extra.WHEN" //putExtra(EXTRA_WHEN, System.currentTimeMillis())
private val EXTRA_SPEED = "com.example.music.ui.player.extra.SPEED" //putExtra(EXTRA_SPEED, player.playbackParameters.speed)
private val EXTRA_URI = "com.example.music.ui.player.extra.URI" //putExtra(EXTRA_URI, mediaItem.mediaUri)
private val EXTRA_YEAR = "com.example.music.ui.player.extra.MEDIA_YEAR"
private val EXTRA_MIME_TYPE = "com.example.music.ui.player.extra.MEDIA_MIME_TYPE"
private val EXTRA_ARTIST = "com.example.music.ui.player.extra.MEDIA_ARTIST"
private val EXTRA_ALBUM_ARTIST = "com.example.music.ui.player.extra.MEDIA_ALBUM_ARTIST"

/**
 * @property timeStamp - The time in mills when this notification was generated.
 * @property hasActiveMedia - represents if there is loaded media-item in player.
 */
@JvmInline
value class NowPlaying(private val value: Intent) {
    companion object {
        // action
        @JvmStatic
        val ACTION_TOGGLE_PLAY = "com.example.music.ui.player.action.TOGGLE_PLAY"

        @JvmStatic
        val ACTION_SEEK_TO = "com.example.music.ui.player.action.SEEK_TO"

        @JvmStatic
        val ACTION_NEXT = "com.example.music.ui.player.action.NEXT"

        @JvmStatic
        val ACTION_PREVIOUS = "com.example.music.ui.player.action.PREVIOUS"

        @JvmStatic
        val EXTRA_SEEK_PCT = "com.example.music.ui.player.extra.SEEK_TO"

        @JvmStatic
        val EXTRA_REPEAT_MODE = "com.example.music.ui.player.action.REPEAT_MODE" //putExtra(EXTRA_REPEAT_MODE, player.repeatMode)



        /**
         * Constructs the widget update intent from a [Player].
         */
        internal fun from(ctx: Context, player: Player): Intent {
            return Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                `package` = "com.example.music.ui.player"
                val ids = AppWidgetManager.getInstance(ctx.applicationContext)
                    .getAppWidgetIds(ComponentName(ctx.applicationContext, "com.example.music.glancewidget.MusicAppWidget"))

                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                val mediaItem = player.currentMediaItem ?: return@apply
                putExtra(EXTRA_TITLE, mediaItem.mediaMetadata.title.toString())
                putExtra(EXTRA_URI, mediaItem.mediaUri)
                putExtra(EXTRA_ALBUM, mediaItem.mediaMetadata.albumTitle.toString())
                putExtra(EXTRA_ARTIST, mediaItem.mediaMetadata.artist.toString())
                putExtra(EXTRA_ALBUM_ARTIST, mediaItem.mediaMetadata.albumArtist.toString())
                putExtra(EXTRA_YEAR, mediaItem.mediaMetadata.releaseYear)
                val uri = mediaItem.mediaMetadata.artworkUri
                putExtra(EXTRA_ARTWORK, uri)
                putExtra(EXTRA_DURATION, player.duration)
                putExtra(EXTRA_POSITION, player.currentPosition)
                putExtra(EXTRA_STATE, player.playbackState)
                putExtra(EXTRAS_PLAY_WHEN_READY, player.playWhenReady)
                putExtra(EXTRA_WHEN, System.currentTimeMillis())
                putExtra(EXTRA_SPEED, player.playbackParameters.speed)
                putExtra(EXTRA_REPEAT_MODE, player.repeatMode)
            }
        }

        /**
         * Represents the empty [NowPlaying] instance.
         */
        val EMPTY = NowPlaying(Intent())

        /**
         *
         */
        @OptIn(UnstableApi::class)
        inline fun trySend(ctx: Context, action: String? = null, args: Intent.() -> Unit = {}) {
            try {
                val intent = Intent(action, null, ctx, MediaService::class.java).apply(args)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ctx.startService(intent) else ctx.startService(intent)
            } catch (i: Exception) {
                Log.d("NowPlaying", "trySend: ${i.message}")
            }
        }
    }

    val timeStamp get() =
        value.getLongExtra(EXTRA_WHEN, -1L)
    val title
        get() = value.getStringExtra(EXTRA_TITLE)
    val album
        get() = value.getStringExtra(EXTRA_ALBUM)
    val artist
        get() = value.getStringExtra(EXTRA_ARTIST)
    val albumArtist
        get() = value.getStringExtra(EXTRA_ALBUM_ARTIST)
    val year
        get() = value.getIntExtra(EXTRA_YEAR, 0)
    val position
        get() = value.getLongExtra(EXTRA_POSITION, C.TIME_UNSET)
    val duration
        get() = value.getLongExtra(EXTRA_DURATION, C.TIME_UNSET)
    val artwork
        get() = value.getParcelableExtra(EXTRA_ARTWORK, Uri::class.java)
    val state
        get() = value.getIntExtra(EXTRA_STATE, Player.STATE_IDLE)
    val playWhenReady
        get() = value.getBooleanExtra(EXTRAS_PLAY_WHEN_READY, false)
    val speed
        get() = value.getFloatExtra(EXTRA_SPEED, 1f)
    val repeatMode
        get() = value.getIntExtra(EXTRA_REPEAT_MODE, Player.REPEAT_MODE_ALL)
    val hasActiveMedia
        get() = value.getParcelableExtra(EXTRA_URI, Uri::class.java) != null

    val playing get() = playWhenReady && state != Player.STATE_ENDED && state != Player.STATE_IDLE
}