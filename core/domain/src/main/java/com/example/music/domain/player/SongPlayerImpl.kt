package com.example.music.player

import com.example.music.data.repository.RepeatType
import com.example.music.player.model.PlayerSong
import com.example.music.data.util.combine
import com.example.music.util.domainLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject
import kotlin.reflect.KProperty

//implementation for SongPlayer, like AlbumRepo to AlbumRepoImpl
//intended to be the 'viewmodel' for the player screen's playing object
//@UnstableApi
class SongPlayerImpl @Inject constructor(
    mainDispatcher: CoroutineDispatcher
) : SongPlayer {
    //private val getPlayerPreferencesUseCase = GetSortPreferencesUseCase(CurrentPreferencesRepository()),

    private val _playerState = MutableStateFlow(SongPlayerState())
    private val _currentSong = MutableStateFlow<PlayerSong?>(null)
    private val queue = MutableStateFlow<List<PlayerSong>>(emptyList())
    private val isPlaying = MutableStateFlow(false)
    private val isShuffled = MutableStateFlow(false)
    private val repeatState = MutableStateFlow(RepeatType.OFF)
    private val timeElapsed = MutableStateFlow(Duration.ZERO)
    private val _playerSpeed = MutableStateFlow(DefaultPlaybackSpeed)
    private val coroutineScope = CoroutineScope(mainDispatcher)

    private var timerJob: Job? = null

    init {
        coroutineScope.launch {
            //val playerPrefs = getPlayerPreferencesUseCase()
            // Combine streams here
            combine( //collects flow to generate SongPlayer's State
                _currentSong,
                queue,
                isPlaying,
                timeElapsed,
                _playerSpeed,
                repeatState,
                isShuffled,
            ) { currentSong, queue, isPlaying, timeElapsed, playerSpeed,repeatState,isShuffled,/*, playerPrefs*/ ->
                SongPlayerState(
                    currentSong = currentSong,
                    queue = queue,
                    isPlaying = isPlaying,
                    timeElapsed = timeElapsed,
                    playbackSpeed = playerSpeed,
                    repeatState = repeatState,
                    isShuffled = isShuffled,
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
        //mediaPlayer.play()
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
        isShuffled.value = !isShuffled.value
        if (isShuffled.value) { //aka shuffle turned on
            //TODO: change the queue to be randomized order
            domainLogger.info { "SHUFFLE QUEUE" }
            //ShuffleQueue(shType)

        }
        else { //aka shuffle turned off
            //TODO: change the queue to be in normal order
            domainLogger.info { "UNDO QUEUE SHUFFLE" }
            //unShuffleQueue()
        }
        //updatePlayerPreferences.updateShuffleType
    }

    override fun onRepeat() {

        when(repeatState.value) {
            //TODO: figure out how the queue / player needs to change
            RepeatType.ON -> {
                repeatState.value = RepeatType.ONE
                domainLogger.info { "REPEAT TYPE CHANGED TO ONE" }
                //want to keep queue as is, just include boolean logic to put onNext to play the song over
                //use same boolean logic/value for onPrevious to restart song over
            }
            RepeatType.OFF -> {
                repeatState.value = RepeatType.ON
                domainLogger.info { "REPEAT TYPE CHANGED TO ON" }
                //in checking the queue, if the current song is the last song of the queue, set the onNext to play the first song
            }
            RepeatType.ONE -> {
                repeatState.value = RepeatType.OFF
                domainLogger.info { "REPEAT TYPE CHANGED TO OFF" }
                //in checking the queue, if the current song is the last song of the queue, trigger the stop function to end the session/queue
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
