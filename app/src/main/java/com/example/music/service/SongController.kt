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

val DefaultPlaybackSpeed = Duration.ofSeconds(1)
// FUTURE THOUGHT: see if there is way to confirm what type DefaultPlaybackSpeed is supposed to be

data class SongControllerState(
    val currentSong: MediaItem? = null,
    val queue: List<MediaItem> = emptyList(),
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

    fun addMediaItem(item: SongInfo)

    fun addMediaItems(items: List<SongInfo>)

    fun addToQueue(songInfo: SongInfo) //might be equivalent of addMediaItems(songs: List<Song>)

    fun addToQueue(songInfos: List<SongInfo>)

    fun addToQueueNext(songInfo: SongInfo)

    fun addToQueueNext(songInfos: List<SongInfo>)

    fun setMediaItem(item: SongInfo)

    fun preparePlayer()

    /**
    * Flushes the queue
    */
    fun removeAllFromQueue()

    /**
     * Plays the current song
     */
    fun play()

    /**
     * Plays the specified song
     */
    fun play(songInfo: SongInfo) //might be closest equivalent of play(mediaItemIndex: Int)

    /**
     * Plays the specified list of songs
     */
    fun play(songInfos: List<SongInfo>)

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
    fun next() //equivalent of skipToNextSong

    /**
     * Plays the previous song in the queue (if available). Or if an song is currently
     * playing this will start the song from the beginning
     */
    fun previous() //equivalent of skipToPreviousSong

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
    fun onSeekingFinished(duration: Duration) //might be equivalent of seekTo(position: Long)

    fun onShuffle() //use this to control the shuffle functionality

    fun onRepeat() //use this to control the repeat functionality

    /**
     * Increases the speed of Player playback by a given time specified in [Duration].
     */
    fun increaseSpeed(speed: Duration = Duration.ofMillis(500))

    /**
     * Decreases the speed of Player playback by a given time specified in [Duration].
     */
    fun decreaseSpeed(speed: Duration = Duration.ofMillis(500))

    fun shuffle(songInfos: List<SongInfo>)

    fun getIsPlaying() : Boolean

    fun getIsShuffled() : Boolean

    fun getRepeatState() : RepeatType

    fun getTimeElapsed() : Duration

    fun getHasNext() : Boolean
}
