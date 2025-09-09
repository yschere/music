package com.example.music.service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.music.data.repository.RepeatType
import com.example.music.domain.model.SongInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration

/** Changelog:
 *
 * 7/22-23/2025 - Revised currentSong and queue to be MediaItem based.
 * Changed the queue, play, mediaItem functions to use SongInfo.
 * Removed PlayerSong completely
 */

val DefaultPlaybackSpeed: Duration = Duration.ofSeconds(1)

/*data class SongControllerState(
    val currentSong: MediaItem? = null,
    //val playbackSpeed: Duration = DefaultPlaybackSpeed,
    val isPlaying: Boolean = false, //tracks the current playing state
    val timeElapsed: Duration = Duration.ZERO,
    //val isShuffled: Boolean = false,
    //val repeatState: RepeatType = RepeatType.OFF,
    val hasNext: Boolean = false,
    //val shuffleType: ShuffleType = ShuffleType.ONCE,
)*/

/**
 * Interface wrapper for a Media Controller to define high-level functions for
 * interacting with MediaService and a media3 Player.
 */
interface SongController {

    /**
     * The current playing song.
     */
    val currentSong: MediaItem?

    /**
     * Current playback position from MediaController
     */
    val position: Long

    /**
     * The playback length for the current playing song.
     */
    val duration: Long

    /**
     * If the Player is playing a song.
     */
    val isPlaying: Boolean

    /**
     * A MediaController setting to say that the Player has permission to play when
     * when it is done buffering/loading.
     */
    val playWhenReady: Boolean

    /**
     * If there is a song queued to play after the current song.
     */
    val hasNext: Boolean

    /**
     * If the queued media items list are in shuffled order.
     */
    val isShuffled: Boolean

    /**
     * If the queued media items list are will be repeated.
     */
    val repeatState: RepeatType

    /**
     * A reflection of the events occurring in Player
     */
    val events: Flow<Player.Events?>

    /**
     * A reflection of the loaded state for SongController
     */
    val loaded: Flow<Boolean>

    /**
     * The speed of which the player increments
     */
    //var playerSpeed: Duration

    /**
     * The media3 Player object that provides Player functionality to SongController
     */
    val player: Player?

    /**
     * Add song to end of queue
     */
    fun addToQueue(song: SongInfo)

    /**
     * Add multiple songs to end of queue
     */
    fun addToQueue(songs: List<SongInfo>)

    /**
     * Add song to beginning of queue. This is for "PlayNext" to both place the song after the
     * current song regardless if the queue is shuffled or not, it will be the next song. So when
     * the queue is un-shuffled, it will be set after the placement of where the current song is.
     * Which means this implementation would need to know the placement of the current song to be
     * able to iterate on.
     */
    fun addToQueueNext(song: SongInfo)

    /**
     * Add multiple songs to beginning of queue. Similar to [addToQueueNext] it's "PlayNext" but
     * on a list of songs. So when shuffled is on, does it shuffle the incoming list? or place them
     * next in its original order. survey says yes, place the new songs in order.
     */
    fun addToQueueNext(songs: List<SongInfo>)

    /**
     * Clear the current queue and set it with provided item
     */
    fun setMediaItem(song: SongInfo)

    /**
     * Clear the current queue and set it with provided items
     */
    fun setMediaItems(songs: List<SongInfo>)

    /**
     * Prepare the media player
     */
    fun preparePlayer()

    /**
    * Flushes the queue
    */
    fun clearQueue()

    /**
     * Get the player to play. Effectively this should be the one that affects the
     * isPlaying state as well as confirms the currentMediaItem within the mediaController.
     */
    fun play()

    fun play(playWhenReady: Boolean)

    /**
     * Plays a specified song.
     * Note: If the given songInfo is from an item that is already within the queue,
     * it should play that item.
     */
    fun play(song: SongInfo)

    /**
     * Plays a specified list of songs.
     * Note: Similar to [play] for a list of songs, if it is invoked with Play or PlayNext,
     * it will remove the original context that began the queue, but keep the items that were in
     * queue from "AddToQueue". So if the queue started from "Play" or "Shuffle", it will get
     * replaced with the new item(s) being played or shuffled.
     */
    fun play(songs: List<SongInfo>)

    /**
     * Pauses the currently playing song
     */
    fun pause()

    /**
     * Stops the currently playing song
     */
    fun stop()

    /**
     * Sets the current playback to a given time specified in [Long].
     */
    fun seekTo(position: Long)

    /**
     * Plays the next song in the queue (if available)
     */
    fun next()

    /**
     * Plays the previous song in the queue (if available). Or if an song is currently
     * playing this will start the song from the beginning
     */
    fun previous()

    /**
     * Use to change the shuffle mode. This is from the Player screen when the user taps the
     * Shuffle btn so the currently playing queue toggles the shuffle order.
     */
    fun onShuffle()

    /**
     * Use to change the repeat mode. This is from the Player screen when the user taps the
     * Repeat btn so the current queue with not repeat, repeat one song, or repeat entire queue.
     */
    fun onRepeat()

    /**
     * Increases the speed of Player playback by a given time specified in [Duration].
     */
    //fun increaseSpeed(speed: Duration = Duration.ofMillis(500))

    /**
     * Decreases the speed of Player playback by a given time specified in [Duration].
     */
    //fun decreaseSpeed(speed: Duration = Duration.ofMillis(500))

    fun shuffle(songs: List<SongInfo>)

    fun isConnected() : Boolean
}
