package com.example.music.ui.player

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.music.data.repository.RepeatType
import com.example.music.domain.model.SongInfo
import com.example.music.service.SongController
import com.example.music.domain.usecases.GetSongDataV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToLong

private const val TAG = "Player View Model"

interface MiniPlayerState {
    var currentSong: SongInfo
    var isPlaying: Boolean
    val player: Player?
}

interface ExpandedPlayerState {
    val currentSong: SongInfo
    var isPlaying: Boolean
    val player: Player?
    var isShuffled: Boolean
    var repeatState: RepeatType
}

interface PlayerState {
    val currentMedia: MediaItem?
    var isPlaying: Boolean
    val player: Player?
    var progress: Float
    var position: Long
    var isShuffled: Boolean
    var repeatState: RepeatType
}

/**
 * Tracks the current playback position as a fraction of the current duration.
 */
val SongController.progress
    get() =
        if (duration == C.TIME_UNSET || position == C.TIME_UNSET) -1f
        else position / duration.toFloat()

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getSongDataV2: GetSongDataV2,
    private val songController: SongController,
) : ViewModel(), PlayerState {

    private var _isPlaying by mutableStateOf(songController.isPlaying)
    private var _position by mutableLongStateOf(songController.position)
    private var _progress by mutableFloatStateOf(songController.progress)
    private var _isShuffled by mutableStateOf(songController.isShuffled)
    private var _repeatState by mutableStateOf(songController.repeatState)

    var currentSong by mutableStateOf(SongInfo())
    val hasNext by mutableStateOf(songController.hasNext)

    override var currentMedia: MediaItem? by mutableStateOf(songController.currentSong)
    override val player: Player?
        get() = songController.player
    override var isPlaying
        get() = _isPlaying
        set(value) {
            if (value) songController.play(true)
            else songController.pause()
        }
    override var progress: Float
        get() = _progress
        set(value) {
            if (_progress !in 0f..1f) return // invalid state
            _progress = value
            val millis = (songController.duration * value).roundToLong()
            songController.seekTo(millis)
        }
    override var position: Long
        get() = _position
        set(value) {
            if (_position > songController.duration) return // invalid state
            _position = value
        }

    override var isShuffled: Boolean
        get() = _isShuffled
        set(value) {
            _isShuffled = value
        }

    override var repeatState: RepeatType
        get() = _repeatState
        set(value) {
            _repeatState = value
        }

    private var timerJob: Job? = null

    private val refreshing = MutableStateFlow(false)

    init {
        Log.i(TAG, "init START")
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch START")

            // Use SongController events to generate or update the Player Screen as the Player object changes
            songController.events.collect {
                Log.d(TAG, "get SongController Player Event(s)")

                // if events is empty, take these actions to generate the needed values for populating the Player Screen
                if (it == null) {
                    Log.d(TAG, "init: running start up events to initialize PlayerVM")
                    onPlayerEvent(event = Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED)
                    onPlayerEvent(event = Player.EVENT_REPEAT_MODE_CHANGED)
                    onPlayerEvent(event = Player.EVENT_IS_LOADING_CHANGED)
                    onPlayerEvent(event = Player.EVENT_PLAY_WHEN_READY_CHANGED)
                    onPlayerEvent(event = Player.EVENT_MEDIA_ITEM_TRANSITION)
                    onPlayerEvent(event = Player.EVENT_IS_PLAYING_CHANGED)
                    return@collect
                }
                // else, repeat the onPlayerEvent call to enact each event
                repeat(it.size()) { index ->
                    onPlayerEvent(it.get(index))
                }
            }
        }
        Log.i(TAG, "init END")
    }

    /**
     * Subset of Player Events that impact the Player Screen, and so need to reference them
     * to retrieve their changes accordingly.
     */
    private fun onPlayerEvent(event: Int) {
        logPlayerEvent(event)
        when (event) {
            // Event for checking if the SongController is loaded and ready to read
            Player.EVENT_IS_LOADING_CHANGED -> {
                val loaded = songController.loaded
                if (loaded.equals(true)) {
                    refreshing.value = false
                }
                Log.d(TAG, "isPlaying set to $isPlaying")
            }

            // Event for checking if SongController is playing
            Player.EVENT_IS_PLAYING_CHANGED -> {
                _isPlaying = songController.isPlaying
                Log.d(TAG, "isPlaying set to $isPlaying")
            }

            // Event for checking if the current media item has changed
            Player.EVENT_MEDIA_ITEM_TRANSITION -> {
                val mediaItem = songController.currentSong
                viewModelScope.launch {
                    var id = mediaItem?.mediaId
                    while (id == null) {
                        delay(100)
                        id = mediaItem?.mediaId
                    }
                    currentSong = getSongDataV2(id.toLong())
                    Log.d(TAG, "Current Song set to ${currentSong.title}")
                    songController.logTrackNumber()
                }
            }

            Player.EVENT_TRACKS_CHANGED -> {
                songController.logTrackNumber()
            }

            // Event for checking if play when ready has changed
            Player.EVENT_PLAY_WHEN_READY_CHANGED -> {
                _isPlaying = songController.playWhenReady
                timerJob?.cancel()
                if (!songController.playWhenReady)
                    return
                launchTimer(isPlaying)
                Log.d(TAG, "Play When Ready set to $isPlaying")
            }

            // Event for checking if the current playback timer has changed
            Player.EVENT_TIMELINE_CHANGED -> {
                updateTimer()
            }

            // Event for checking if the repeat state has changed
            Player.EVENT_REPEAT_MODE_CHANGED -> {
                _repeatState = songController.repeatState
                Log.d(TAG, "repeatState set to ${repeatState.name}")
            }

            // Event for checking if the shuffle mode is enabled
            Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED -> {
                _isShuffled = songController.isShuffled
                Log.d(TAG, "isShuffled set to $isShuffled")
            }
        }
    }

    fun refresh(force: Boolean = true) {
        Log.e(TAG, "Refresh call")
        Log.e(TAG, "refreshing: ${refreshing.value}")
        viewModelScope.launch {
            runCatching {
                Log.e(TAG,"Refresh runCatching")
                refreshing.value = true
            }.onFailure {
                Log.e(TAG, "$it ::: runCatching failed (not sure what this means)")
            }

            Log.e(TAG,"refresh to be false -> sets screen to ready state")
            refreshing.value = false
        }
    }

    /**
     * Begin the timerJob coroutine for tracking the position and progress of the current item
     * playback. While play is true, keep updating every 1000ms.
     */
    private fun launchTimer(play: Boolean = true) {
        Log.i(TAG, "Start timer tracker coroutine")
        timerJob = viewModelScope.launch {
            while (play) {
                _progress = songController.progress
                _position = songController.position
                delay(1000)
            }
        }
    }

    /**
     * Update the progress and position of the current item playback. Does not
     * change the timerJob itself.
     */
    private fun updateTimer() {
        _progress = songController.progress
        _position = songController.position
    }

    /**
     * Stop the timerJob tracker.
     */
    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun onPlay() {
        Log.i(TAG,"Hit play btn on Player Screen.")
        songController.play(true)
    }

    fun onPause() {
        Log.i(TAG, "Hit pause btn on Player Screen")
        songController.pause()
    }

    fun onStop() {
        Log.i(TAG, "Stop the Player Screen")
        _isPlaying = false
        stopTimer()
        songController.stop()
    }

    fun onPrevious() {
        Log.i(TAG, "Hit previous btn on Player Screen")
        songController.previous()
        updateTimer()
    }

    fun onNext() {
        Log.i(TAG, "Hit next btn on Player Screen")
        songController.next()
        updateTimer()
    }

    fun onSeek(position: Long) {
        Log.i(TAG, "Seeking on Player Screen slider: new time -> $position")
        songController.seekTo(position)
        updateTimer()
    }

    fun onShuffle() {
        Log.i(TAG, "Hit shuffle btn on Player Screen")
        songController.onShuffle()
    }

    fun onRepeat() {
        Log.i(TAG, "Hit repeat btn on Player Screen")
        songController.onRepeat()
    }

    fun onDestroy() {
        stopTimer()
        player?.release()
    }

    private fun logPlayerEvent(event: Int) {
        var s = ""
        when (event) {
            Player.EVENT_TIMELINE_CHANGED -> {
                s = "event timeline changed"
            } // 0
            Player.EVENT_MEDIA_ITEM_TRANSITION -> {
                s = "event current media item changed or current item repeating"
            } // 1
            Player.EVENT_TRACKS_CHANGED -> {
                s = "event tracks changed"
            } // 2
            Player.EVENT_IS_LOADING_CHANGED -> {
                s = "event is loading changed"
            } // 3
            Player.EVENT_PLAYBACK_STATE_CHANGED -> {
                s = "event playback state changed"
            } // 4
            Player.EVENT_PLAY_WHEN_READY_CHANGED -> {
                s = "event play when ready changed"
            } // 5
            Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED -> {
                s = "event playback suppression reason changed"
            } // 6
            Player.EVENT_IS_PLAYING_CHANGED -> {
                s = "event is playing changed"
            } // 7
            Player.EVENT_REPEAT_MODE_CHANGED -> {
                s = "event repeat mode changed"
            } // 8
            Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED -> {
                s = "event shuffle mode enabled changed"
            } // 9
            Player.EVENT_PLAYER_ERROR -> {
                s = "event player error occurred"
            } // 10
            Player.EVENT_POSITION_DISCONTINUITY -> {
                s = "event position discontinuity occurred"
                /** Note:
                 * A position discontinuity occurs when the playing period changes, the playback
                 * position jumps within the period currently being played, or when the playing
                 * period has been skipped or removed.
                 * onEvents(Player, Player. Events) will also be called to report this event along
                 * with other events that happen in the same Looper message queue iteration.
                 */
            } // 11
            Player.EVENT_PLAYBACK_PARAMETERS_CHANGED -> {
                s = "event playback parameters changed"
            } // 12
            Player.EVENT_AVAILABLE_COMMANDS_CHANGED -> {
                s = "event player's command(s) availability changed"
            } // 13
            Player.EVENT_MEDIA_METADATA_CHANGED -> {
                s = "event media metadata changed"
            } // 14
            Player.EVENT_PLAYLIST_METADATA_CHANGED -> {
                s = "event playlist metadata changed"
            } // 15
            Player.EVENT_SEEK_BACK_INCREMENT_CHANGED -> {
                s = "event seek back increment changed"
            } // 16
            Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED -> {
                s = "event seek forward increment changed"
            } // 17
            Player.EVENT_MAX_SEEK_TO_PREVIOUS_POSITION_CHANGED -> {
                s = "event max seek to previous position changed"
            } // 18
            Player.EVENT_TRACK_SELECTION_PARAMETERS_CHANGED -> {
                s = "event track selection parameters changed"
            } // 19
            Player.EVENT_AUDIO_ATTRIBUTES_CHANGED -> {
                s = "event audio attributes changed"
            } // 20
            Player.EVENT_AUDIO_SESSION_ID -> {
                s = "event audio session id set"
            } // 21
            Player.EVENT_VOLUME_CHANGED -> {
                s = "event volume changed"
            } // 22
            Player.EVENT_SKIP_SILENCE_ENABLED_CHANGED -> {
                s = "event skip silence enabled changed"
            } // 23
            Player.EVENT_SURFACE_SIZE_CHANGED -> {
                s = "event surface size changed"
                /**
                 * Note:
                 * This is for video rendering, which is not a supported MIME type in the app.
                 * So this shouldn't occur.
                 */
            } // 24
            Player.EVENT_VIDEO_SIZE_CHANGED -> {
                s = "event video size changed"
                /**
                 * Note:
                 * This is for video rendering, which is not a supported MIME type in the app.
                 * So this shouldn't occur.
                 */
            } // 25
            Player.EVENT_RENDERED_FIRST_FRAME -> {
                s = "event first frame rendered"
                /**
                 * Note:
                 * This is for video rendering, which is not a supported MIME type in the app.
                 * So this shouldn't occur.
                 */
            } // 26
            Player.EVENT_CUES -> {
                s = "event cues changed"
            } // 27
            Player.EVENT_METADATA -> {
                s = "event metadata playback changed"
            } // 28
            Player.EVENT_DEVICE_INFO_CHANGED -> {
                s = "event device info changed"
            } // 29
            Player.EVENT_DEVICE_VOLUME_CHANGED -> {
                s = "event device volume changed"
            } // 30
        }
        Log.d(TAG, "SongController onEvent: -> $event :: $s")
    }
}