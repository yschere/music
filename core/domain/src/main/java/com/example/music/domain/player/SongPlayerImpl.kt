package com.example.music.domain.player

import androidx.media3.session.MediaController
import com.example.music.data.repository.RepeatType
import com.example.music.domain.player.model.PlayerSong
import com.example.music.data.util.combine
import com.example.music.domain.util.domainLogger
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
import kotlin.random.Random
import kotlin.reflect.KProperty

private const val TAG = "SongPlayerImpl"
private const val RandSeed = 1 // seed for shuffle randomizer

//implementation for SongPlayer, like AlbumRepo to AlbumRepoImpl
//intended to be the 'viewmodel' for the player screen's playing object
//@UnstableApi

// need a way to connect this impl to MediaService in app module ... or need media service to invoke this?

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

    override fun addToQueue(playerSong: PlayerSong) {
        queue.update {
            it + playerSong
        }
    }

    override fun addToQueue(playerSongs: List<PlayerSong>) {
        queue.update {
            it + playerSongs
        }
    }

    // this is for "PlayNext" to both place the song after the current song
    // regardless if the queue is shuffled or not, it will be the next song
    // so when the queue is unshuffled, it will be set after the placement of
    // where the current song is. which means this implem would need to know
    // the placement of the current song to be able to iterate on
    override fun addToQueueNext(playerSong: PlayerSong) {
        queue.update {
            listOf(it[0]) + playerSong + it.subList(1,it.size-1)
        }
    }

    // same dead here, it's "PlayNext" but on a list of songs
    // but if shuffled is on, does it shuffle the incoming list? or place them
    // next in its original order. survey says yes, place the new songs in order
    override fun addToQueueNext(playerSongs: List<PlayerSong>) {
        queue.update {
            listOf(it[0]) + playerSongs + it.subList(1,it.size-1)
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

    // interesting implications part 2:
    // if this is playing from an item that is already in context of the queue, it just plays that item
    // ie songInfo.onClick
    override fun play(playerSong: PlayerSong) {
        play(listOf(playerSong))
    }

    // this also has interesting implications after learnings from shuffle
    // and playNext. for a list of songs, if it invoked with Play or PlayNext,
    // it will remove the original context that began the queue, but keep the items
    // that were in queue from "AddToQueue". so if the queue started from "Play" or "Shuffle"
    // it will get replaced with the new item(s) being played or shuffled
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
            domainLogger.info { "$TAG - SHUFFLE QUEUE" }
            shuffleQueue()
        }
        else { //aka shuffle turned off
            //TODO: change the queue to be in normal order
            domainLogger.info { "$TAG - UNDO QUEUE SHUFFLE" }
            //unShuffleQueue()
            // this can get real spicy to figure out how to achieve
            // if i want it to work the same way the play music one works, it would need to keep
            // the add to history intact, so that switching would just go from one to the other
            // and hitting shuffle would just throw out a new shuffle order, no need to save it
            // but to keep the unshuffled order ... would it take a temporary playlist queue?
            // and it would just have the songs' track number intrinsically?
            // because i dunno about keeping a history as a side thing ...
            // actually, if the queue can be manually reordered, then yeah it would be much better
            // to just directly give the songs in queue their list order
            // new concern: in play music, trying to reorder a song while unshuffled did not keep
            // that move after the queue was shuffled, then unshuffled. it returned to its original
            // placement when it was first added to the queue. maybe it really does use a history ...
            // or keeps the original placement and reordering uses a temporary shift
        }
        //updatePlayerPreferences.updateShuffleType
    }

    override fun onRepeat() {

        when(repeatState.value) {
            //TODO: figure out how the queue / player needs to change
            RepeatType.ON -> {
                repeatState.value = RepeatType.ONE
                domainLogger.info { "$TAG - REPEAT TYPE CHANGED TO ONE" }
                //want to keep queue as is, just include boolean logic to put onNext to play the song over
                //use same boolean logic/value for onPrevious to restart song over
            }
            RepeatType.OFF -> {
                repeatState.value = RepeatType.ON
                domainLogger.info { "$TAG - REPEAT TYPE CHANGED TO ON" }
                //in checking the queue, if the current song is the last song of the queue, set the onNext to play the first song
            }
            RepeatType.ONE -> {
                repeatState.value = RepeatType.OFF
                domainLogger.info { "$TAG - REPEAT TYPE CHANGED TO OFF" }
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

    private fun shuffleQueue() { //this would get called if the queue itself needs to be shuffled
        queue.update {
            it.shuffled(Random(RandSeed))
        }
    }

    override fun shuffle(playerSongs: List<PlayerSong>) {
        // ground rules
            // 1 if the songs here are the first items going into the queue, then the shuffled
                // order here is the originating order, aka it is the queue's default track order. hitting unshuffle will keep this order intact, and hitting shuffle can change the order but the original needs to remain untouched
            // 2 this does not change the shuffle type
            // 3 this does not set isShuffle to true
        // NOW I'M CONFUSION
        // CAUSE HITTING SHUFFLE WHILE THERE WAS MULTIPLE ITEMS ADDED TO QUEUE
        // REMOVED THE ORIGINAL ITEM THAT STARTED THE QUEUE, KEPT THE ITEMS THAT WERE ADDED AFTER,
        // AND PLACED THE NEW "SHUFFLE" ITEM AT THE TOP OF THE QUEUE
        // I THOUGHT IT CLEARED THE QUEUE???
        // THIS ALSO APPLIES TO NON PLAYLISTS, IE IF AN ALBUM'S ADDED USING SHUFFLE. THE NEW SORT IS THE DEFAULT
        // AND IT WILL BE AT THE TOP OF THE QUEUE, REMOVING THE ORIGINAL CONTEXT BUT KEEPING THE ITEMS THAT WERE
        // "ADD TO QUEUE"
        removeAllFromQueue()
        queue.update {
            playerSongs.shuffled(Random(RandSeed))
        }
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
