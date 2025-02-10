package com.example.music.player

import com.example.music.data.repository.RepeatType
import com.example.music.player.model.PlayerSong
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import kotlin.reflect.KProperty

class MockSongPlayer(
    private val mainDispatcher: CoroutineDispatcher
) : SongPlayer {

    private val _playerState = MutableStateFlow(SongPlayerState())
    private val _currentSong = MutableStateFlow<PlayerSong?>(null)
    private val queue = MutableStateFlow<List<PlayerSong>>(emptyList())
    private val isPlaying = MutableStateFlow(false)
    private val isShuffled = MutableStateFlow(false)
    private val repeatingState = MutableStateFlow(RepeatType.OFF)
    private val timeElapsed = MutableStateFlow(Duration.ZERO)
    private val _playerSpeed = MutableStateFlow(DefaultPlaybackSpeed)
    private val coroutineScope = CoroutineScope(mainDispatcher)

    private var timerJob: Job? = null

    init {
        coroutineScope.launch {
            // Combine streams here
            combine(
                _currentSong,
                queue,
                isPlaying,
                timeElapsed,
                _playerSpeed
            ) { currentSong, queue, isPlaying, timeElapsed, playerSpeed ->
                SongPlayerState(
                    currentSong = currentSong,
                    queue = queue,
                    isPlaying = isPlaying,
                    timeElapsed = timeElapsed,
                    playbackSpeed = playerSpeed
                )
            }.catch {
                // TODO handle error state
                throw it
            }.collect {
                _playerState.value = it
            }
        }
    }

    override var playerSpeed: Duration = _playerSpeed.value

    override val playerState: StateFlow<SongPlayerState> = _playerState.asStateFlow()

    override var currentSong: PlayerSong? by _currentSong

    override fun addToQueue(song: PlayerSong) {
        queue.update {
            it + song
        }
    }

    override fun removeAllFromQueue() {
        queue.value = emptyList()
    }

    override fun play() {
        // Do nothing if already playing
        if (isPlaying.value) {
            return
        }

        val song = _currentSong.value ?: return

        isPlaying.value = true
        timerJob = coroutineScope.launch {
            // Increment timer by a second
            while (isActive && timeElapsed.value < song.duration) {
                delay(playerSpeed.toMillis())
                timeElapsed.update { it + playerSpeed }
            }

            // Once done playing, see if
            isPlaying.value = false
            timeElapsed.value = Duration.ZERO

            if (hasNext()) {
                next()
            }
        }
    }

    override fun play(playerSong: PlayerSong) {
        play(listOf(playerSong))
    }

    override fun play(playerSongs: List<PlayerSong>) {
        if (isPlaying.value) {
            pause()
        }

        // Keep the currently playing episode in the queue
        val playingSong = _currentSong.value
        var previousList: List<PlayerSong> = emptyList()
        queue.update { queue ->
            playerSongs.map { song ->
                if (queue.contains(song)) {
                    val mutableList = queue.toMutableList()
                    mutableList.remove(song)
                    previousList = mutableList
                } else {
                    previousList = queue
                }
            }
            if (playingSong != null) {
                playerSongs + listOf(playingSong) + previousList
            } else {
                playerSongs + previousList
            }
        }

        next()
    }

    override fun pause() {
        isPlaying.value = false

        timerJob?.cancel()
        timerJob = null
    }

    override fun stop() {
        isPlaying.value = false
        timeElapsed.value = Duration.ZERO

        timerJob?.cancel()
        timerJob = null
    }

    override fun advanceBy(duration: Duration) {
        val currentSongDuration = _currentSong.value?.duration ?: return
        timeElapsed.update {
            (it + duration).coerceAtMost(currentSongDuration)
        }
    }

    override fun rewindBy(duration: Duration) {
        timeElapsed.update {
            (it - duration).coerceAtLeast(Duration.ZERO)
        }
    }

    override fun onSeekingStarted() {
        // Need to pause the player so that it doesn't compete with timeline progression.
        pause()
    }

    override fun onSeekingFinished(duration: Duration) {
        val currentSongDuration = _currentSong.value?.duration ?: return
        timeElapsed.update { duration.coerceIn(Duration.ZERO, currentSongDuration) }
        play()
    }

    override fun onShuffle() {
        if (isShuffled.value) {
            isShuffled.value = false
            //TODO: change the queue to be in normal order
        }
        else {
            isShuffled.value = true
            //TODO: change the queue to be randomized order
        }
    }

    override fun onRepeat() {
        when(repeatingState.value) {
            RepeatType.ON -> {
                repeatingState.value = RepeatType.ONE
                //TODO: figure out how the queue / player needs to change
            }
            RepeatType.OFF -> {
                repeatingState.value = RepeatType.ON
                //TODO: figure out how the queue / player needs to change
            }
            RepeatType.ONE -> {
                repeatingState.value = RepeatType.OFF
                //TODO: figure out how the queue / player needs to change
            }
        }
    }

    override fun increaseSpeed(speed: Duration) {
        _playerSpeed.value += speed
    }

    override fun decreaseSpeed(speed: Duration) {
        _playerSpeed.value -= speed
    }

    override fun next() {
        val q = queue.value
        if (q.isEmpty()) {
            return
        }

        timeElapsed.value = Duration.ZERO
        val nextSong = q[0]
        currentSong = nextSong
        queue.value = q - nextSong
        play()
    }

    override fun previous() {
        timeElapsed.value = Duration.ZERO
        isPlaying.value = false
        timerJob?.cancel()
        timerJob = null
    }

    private fun hasNext(): Boolean {
        return queue.value.isNotEmpty()
    }
}
// Used to enable property delegation
private operator fun <T> MutableStateFlow<T>.setValue(
    thisObj: Any?,
    property: KProperty<*>,
    value: T
) {
    this.value = value
}

private operator fun <T> MutableStateFlow<T>.getValue(thisObj: Any?, property: KProperty<*>): T =
    this.value
