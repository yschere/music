package com.example.music.player

import android.media.MediaPlayer
import com.example.music.data.database.model.Song
import com.example.music.data.repository.RepeatType
import com.example.music.player.model.PlayerSong
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration
import javax.inject.Inject

val DefaultPlaybackSpeed = Duration.ofSeconds(1)
//TODO: see if there is way to confirm what type DefaultPlaybackSpeed is supposed to be

data class SongPlayerState( //equivalent of music playe's MusicControllerUiState
    val currentSong: PlayerSong? = null,
    val queue: List<PlayerSong> = emptyList(),
    val playbackSpeed: Duration = DefaultPlaybackSpeed,
    val isPlaying: Boolean = false, //tracks the current playing state, instead of having playing, paused, stopped like music playe's PlayerState
    val timeElapsed: Duration = Duration.ZERO,
    val isShuffled: Boolean = false,
    val repeatingState: RepeatType = RepeatType.OFF
    //could include isShuffleEnabled here, or the shuffle type enabled here
    //could include isRepeatOneEnabled here, or the repeat type enabled here
)

/**
 * Interface definition for a song player defining high-level functions such as queuing
 * episodes, playing an episode, pausing, seeking, etc.
 */
interface SongPlayer { //equivalent of music playe's MusicController

    /**
     * A StateFlow that emits the [SongPlayerState] as controls as invoked on this player.
     */
    val playerState: StateFlow<SongPlayerState>

    /**
     * Gets the current episode playing, or to be played, by this player.
     */
    var currentSong: PlayerSong?

    /**
     * The speed of which the player increments
     */
    var playerSpeed: Duration

    /**
     * The object for providing mediaPlayer functionality to song player
     */
//    val mediaPlayer: MediaPlayer?
//        get() = null

    fun addToQueue(song: PlayerSong) //might be equivalent of addMediaItems(songs: List<Song>)

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
    fun play(playerSong: PlayerSong) //might be closest equivalent of play(mediaItemIndex: Int)

    /**
     * Plays the specified list of songs
     */
    fun play(playerSongs: List<PlayerSong>)

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
}
