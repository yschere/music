package com.example.music.service

import androidx.media3.common.MediaItem
import com.example.music.data.repository.RepeatType
import com.example.music.domain.model.SongInfo
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration

/** Changelog:
 *
 * 7/22-23/2025 - Revised currentSong and queue to be MediaItem based.
 * Changed the queue, play, mediaItem functions to use SongInfo.
 * Removed PlayerSong completely
 */

val DefaultPlaybackSpeed: Duration = Duration.ofSeconds(1)
// FUTURE THOUGHT: see if there is way to confirm what type DefaultPlaybackSpeed is supposed to be

data class SongControllerState(
    val currentSong: MediaItem? = null,
    val playbackSpeed: Duration = DefaultPlaybackSpeed,
    val isPlaying: Boolean = false, //tracks the current playing state
    val timeElapsed: Duration = Duration.ZERO,
    val isShuffled: Boolean = false,
    val repeatState: RepeatType = RepeatType.OFF,
    //val shuffleType: ShuffleType = ShuffleType.ONCE,
)

/**
 * Interface definition for a song player defining high-level functions such as queuing
 * episodes, playing an episode, pausing, seeking, etc.
 */
interface SongController {

    /**
     * A StateFlow that emits the [SongControllerState] as controls as invoked on this player.
     */
    val playerState: StateFlow<SongControllerState>

    /**
     * Gets the current episode playing, or to be played, by this player.
     */
    var currentSong: MediaItem?

    /**
     * The speed of which the player increments
     */
    var playerSpeed: Duration

    /**
     * The object for providing mediaPlayer functionality to song player
     */
    //val mediaPlayer: MediaPlayer?
        //get() = null

    fun addMediaItem(song: SongInfo)

    fun addMediaItems(songs: List<SongInfo>)

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
     * the queue is unshuffled, it will be set after the placement of where the current song is.
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
    fun removeAllFromQueue()

    /**
     * Get the player to play. Effectively this should be the one that affects the
     * isPlaying state as well as confirms the currentMediaItem within the mediaController.
     */
    fun play()

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
     * Pauses the currently played song
     */
    fun pause()

    /**
     * Stops the currently played song
     */
    fun stop()

    /**
     * Plays another song in the queue (if available)
     */
    fun next()

    /**
     * Plays the previous song in the queue (if available). Or if an song is currently
     * playing this will start the song from the beginning
     */
    fun previous()

    /**
     * Advances a currently played song by a given time interval specified in [duration].
     */
    fun advanceBy(duration: Duration)

    /**
     * Rewinds a currently played song by a given time interval specified in [duration].
     */
    fun rewindBy(duration: Duration)

    /**
     * Signal that user started seeking.
     */
    fun onSeekingStarted()

    /**
     * Seeks to a given time interval specified in [duration].
     */
    fun onSeekingFinished(duration: Duration)

    /**
     * Use to change the shuffle mode
     */
    fun onShuffle()

    /**
     * Use to change the repeat mode
     */
    fun onRepeat()

    /**
     * Increases the speed of Player playback by a given time specified in [Duration].
     */
    fun increaseSpeed(speed: Duration = Duration.ofMillis(500))

    /**
     * Decreases the speed of Player playback by a given time specified in [Duration].
     */
    fun decreaseSpeed(speed: Duration = Duration.ofMillis(500))

    fun shuffle(songs: List<SongInfo>)

    fun getIsPlaying() : Boolean

    fun getIsShuffled() : Boolean

    fun getRepeatState() : RepeatType

    fun getTimeElapsed() : Duration

    fun getHasNext() : Boolean

    fun isConnected() : Boolean
}
