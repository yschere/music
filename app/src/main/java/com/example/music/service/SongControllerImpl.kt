package com.example.music.service

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.music.domain.model.SongInfo
import com.example.music.domain.player.model.title
import com.example.music.domain.player.model.toMediaItem
import com.example.music.ui.shared.mediaItems
import com.example.music.ui.shared.queue
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject
import kotlin.random.Random
import kotlin.reflect.KProperty

/** Changelog:
 *
 * 4/??/2025 - Moved from domain module to app module, so it can access MediaService
 * without creating a compiler dependency error. And moved the MediaController to here
 * so that it can be used as the controller for the MediaPlayer within MediaService.
 *
 * 7/22-23/2025 - Adjusted play() functions logic so that excess queue logic is removed.
 * Added get functions to separate PlayerUiState's reliance on songControllerState.
 * Changed the queue, play, mediaItem functions to use SongInfo.
 * Removed PlayerSong completely
 */

private const val TAG = "SongControllerImpl"

@OptIn(UnstableApi::class)
private fun Context.mediaController(listener: MediaController.Listener) : ListenableFuture<MediaController> {
    Log.d(TAG, "calling Context.mediaController Builder")
    return MediaController.Builder(this, SessionToken(this, ComponentName(this, MediaService::class.java)))
        .setListener(listener)
        .buildAsync()
}

@UnstableApi
class SongControllerImpl @Inject constructor(
    context: Context,
    mainDispatcher: CoroutineDispatcher
) : SongController, MediaController.Listener {

    private val coroutineScope = CoroutineScope(mainDispatcher)

    private var mediaControllerFuture: ListenableFuture<MediaController> = context.mediaController(this)
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    override val currentSong: MediaItem?
        get() = mediaController?.currentMediaItem
    override val position: Long
        get() = mediaController?.currentPosition ?: C.TIME_UNSET
    override val duration: Long
        get() = mediaController?.contentDuration ?: C.TIME_UNSET
    override val isPlaying: Boolean
        get() = mediaController?.isPlaying ?: false
    override val playWhenReady: Boolean
        get() = mediaController?.playWhenReady ?: false
    override val hasNext: Boolean
        get() = mediaController?.hasNextMediaItem() ?: false
    override val player: Player?
        get() = mediaController
    override val events: Flow<Player.Events?>
        get() = callbackFlow {
            val observer = object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    trySend(events)
                }
            }
            val controller = mediaControllerFuture.await()
            trySend(null)
            controller.addListener(observer)
            awaitClose {
                controller.removeListener(observer)
            }
        }.flowOn(Dispatchers.Main)
    override val loaded: Flow<Boolean>
        get() = events.map { currentSong != null }

    override fun addToQueue(song: SongInfo) {
        val mediaController = mediaController ?: return
        Log.d(TAG, "Add To Queue - 1 song:\n" +
            "Song ID: ${song.id}; Song Title: ${song.title}")
        mediaController.addMediaItem(song.toMediaItem)
    }

    override fun addToQueue(songs: List<SongInfo>) {
        val mediaController = mediaController ?: return
        Log.d(TAG, "Add To Queue - multiple songs")
        mediaController.addMediaItems(
            songs.map {
                Log.i(TAG, "Song ID: ${it.id}; Song Title: ${it.title}")
                it.toMediaItem
            }
        )
    }

    override fun addToQueueNext(song: SongInfo) {
        val mediaController = mediaController ?: return
        Log.d(TAG, "Add to Queue Next - 1 song:\n" +
                "Song ID: ${song.id}; Song Title: ${song.title}")
        mediaController.addMediaItem(1,song.toMediaItem)
    }

    override fun addToQueueNext(songs: List<SongInfo>) {
        val mediaController = mediaController ?: return
        Log.d(TAG, "Add to Queue Next - multiple songs: ${songs.size}")
        mediaController.addMediaItems(1,songs.map {
            Log.d(TAG, "Song ID: ${it.id}; Song Title: ${it.title}")
            it.toMediaItem
        })
    }

    override fun setMediaItem(song: SongInfo) {
        val mediaController = mediaController ?: return
        Log.d(TAG, "Set Media Item: ${song.id}\n" +
            "Song ID: ${song.id}; Song Title: ${song.title}")
        mediaController.setMediaItem(song.toMediaItem)
    }

    override fun setMediaItems(songs: List<SongInfo>) {
        val mediaController = mediaController ?: return
        Log.d(TAG, "Set Media Items: ${songs.size}")
        mediaController.setMediaItems(
            songs.map {
                Log.d(TAG, "Song ID: ${it.id}; Song Title: ${it.title}")
                it.toMediaItem
            }
        )
    }

    override fun preparePlayer() {
        val mediaController = mediaController ?: return
        Log.i(TAG, "in preparePlayer(): Prepare Media Controller")
        mediaController.prepare()
        playState()
    }

    override fun clearQueue() {
        val mediaController = mediaController ?: return
        Log.i(TAG, "in clearQueue(): Remove all items from Queue")
        mediaController.clearMediaItems()
        playState()
    }

    override fun play() {
        val mediaController = mediaController ?: return
        Log.d(TAG, "in play(): START\n" +
                "SongController isPlaying is set to ${isPlaying}.\n" +
                "Current Song Controller song is ${currentSong?.title}")

        playState()

        val item = mediaController.currentMediaItem
        Log.d(TAG, "MediaController isPlaying is set to ${mediaController.isPlaying}\n" +
                "Current Media Controller item is ${item?.title}")

        /* // there's 4 playback states to check for: idle, buffering, ready, ended
        // ideally, if we're in here that means the app requested to play something, so should be in buffering
        // want to wait for the media controller to finish buffering, then when it is ready, get to play/pause */
        coroutineScope.launch {
            while (mediaController.playbackState == Player.STATE_BUFFERING) {
                delay(1000)
            }
        }

        if (mediaController.isPlaying) {
            Log.d(TAG, "Song Controller Impl IS PLAYING -- set to pause")
            pause()
        } else {
            Log.d(TAG, "Song Controller Impl IS PAUSED -- set to play")
            play(true)
            preparePlayer()
        }

        playState()
        Log.d(TAG, "in play(): END")
    }

    override fun play(playWhenReady: Boolean) {
        val mediaController = mediaController ?: return
        mediaController.playWhenReady = playWhenReady
        mediaController.play()
    }

    override fun play(song: SongInfo) {
        Log.d(TAG, "In play( SongInfo ): START")
        play(listOf(song))
        Log.d(TAG, "In play( SongInfo ): END")
    }

    override fun play(songs: List<SongInfo>) {
        val mediaController = mediaController ?: return
        Log.d(TAG, "In play( List<SongInfo> ): START")
        playState()

        Log.d(TAG, "Count of items to queue: ${songs.size} items.")
        setMediaItems(songs)

        Log.d(TAG, "Current media controller state before apply is ${mediaController.playbackState}.")

        mediaController.apply {
            seekToDefaultPosition()
            playWhenReady = true
            prepare()
        }
        Log.d(TAG, "Current media controller state after apply is ${mediaController.playbackState}.\n" +
                "Current media controller queue is ${mediaController.mediaItems.size} items\n" +
                "Current media item is ${mediaController.currentMediaItem?.title}")
        playState()
        play()
        Log.d(TAG, "In play( List<SongInfo> ): END")
    }

    override fun pause() {
        val mediaController = mediaController ?: return
        Log.d(TAG, "in pause() START --- isPlaying is $isPlaying")
        mediaController.pause()
        Log.d(TAG, "in pause() END --- isPlaying is set to $isPlaying")
    }

    override fun stop() {
        val mediaController = mediaController ?: return
        Log.d(TAG, "in stop() --- isPlaying is $isPlaying")
        mediaController.stop()
        Log.d(TAG, "in stop() --- isPlaying is set to $isPlaying")
    }

    override fun seekTo(position: Long) {
        val mediaController = mediaController ?: return
        Log.d(TAG, "in seekTo(Long) START: $position")
        mediaController.seekTo(position)
        Log.d(TAG, "in seekTo(Long) END: new position is ${mediaController.currentPosition}")
        play(true)
    }

    override fun next() {
        val mediaController = mediaController ?: return
        Log.d(TAG, "in next() function")
        mediaController.seekToNextMediaItem()
        play()
    }

    override fun previous() {
        val mediaController = mediaController ?: return
        Log.d(TAG, "in previous() function")

        if (position < 2000L && mediaController.hasPreviousMediaItem()) {
            // if media item passed less than 2s, restart position
            mediaController.seekToPreviousMediaItem()
        } else {
            // else there isn't any previous item, just restart position
            mediaController.seekToDefaultPosition()
        }
        play()
    }

    override fun onShuffle() {
        /* // v1
        Log.d(TAG, "in onShuffle() --- isShuffled is set to ${_isShuffled.value}")
        _isShuffled.value = !_isShuffled.value!!
        if (_isShuffled.value == true) { //aka shuffle turned on
            //TODO: change the queue to be randomized order
            Log.i(TAG, "BEGIN SHUFFLE QUEUE")
            shuffleQueue()

        } // v1 end */

        /* // v2
        Log.i(TAG, "in onShuffle() --- isShuffled is set to ${isShuffled.value}")
        isShuffled.value = !_isShuffled.value!!
        if (isShuffled.value == true) { //aka shuffle turned on
            //TODO: change the queue to be randomized order
            Log.i(TAG, "BEGIN SHUFFLE QUEUE")
            shuffleQueue()
        } // v2 end */
        /*else { //aka shuffle turned off
            //TODO: change the queue to be in normal order
            Log.i(TAG, "BEGIN UNDO QUEUE SHUFFLE")
            //unShuffleQueue()
            // this can get real spicy to figure out how to achieve
            // if i want it to work the same way the play music one works, it would need to keep
            // the add to history intact, so that switching would just go from one to the other
            // and hitting shuffle would just throw out a new shuffle order, no need to save it
            // but to keep the un-shuffled order ... would it take a temporary playlist queue?
            // and it would just have the songs' track number intrinsically?
            // because i dunno about keeping a history as a side thing ...
            // actually, if the queue can be manually reordered, then yeah it would be much better
            // to just directly give the songs in queue their list order
            // new concern: in play music, trying to reorder a song while un-shuffled did not keep
            // that move after the queue was shuffled, then un-shuffled. it returned to its original
            // placement when it was first added to the queue. maybe it really does use a history ...
            // or keeps the original placement and reordering uses a temporary shift
        }*/
        //updatePlayerPreferences.updateShuffleType
    }

    override fun onRepeat() {
        /*Log.d(TAG, "in onRepeat --- repeatState is set to ${_repeatState.value}") // v1
        // Log.i(TAG, "in onRepeat --- repeatState is set to ${repeatState.value}") // v2
        when(_repeatState.value) { // v1
        //when(repeatState.value) { // v2
            //TODO: figure out how the queue / player needs to change
            RepeatType.OFF -> {
                _repeatState.value = RepeatType.ON // v1
                // repeatState.value = RepeatType.ON // v2
                Log.i(TAG, "REPEAT TYPE CHANGED TO ON")
                //in checking the queue, if the current song is the last song of the queue, set the onNext to play the first song
            }
            RepeatType.ON -> {
                _repeatState.value = RepeatType.ONE // v1
                // repeatState.value = RepeatType.ONE // v2
                Log.i(TAG, "REPEAT TYPE CHANGED TO ONE")
                //want to keep queue as is, just include boolean logic to put onNext to play the song over
                //use same boolean logic/value for onPrevious to restart song over
            }
            RepeatType.ONE -> {
                _repeatState.value = RepeatType.OFF // v1
                // repeatState.value = RepeatType.OFF // v2
                Log.i(TAG, "REPEAT TYPE CHANGED TO OFF")
                //in checking the queue, if the current song is the last song of the queue, trigger the stop function to end the session/queue
            }
        }*/
    }

    /*override fun increaseSpeed(speed: Duration) {
        //_playerSpeed.value += speed
    }

    override fun decreaseSpeed(speed: Duration) {
        //_playerSpeed.value -= speed
    }*/

    /**
     * Internal function to shuffle the MediaController queue.
     */
    private fun shuffleQueue() { //this would get called if the queue itself needs to be shuffled
        val mediaController = mediaController ?: return
        val temp = mediaController.queue
        clearQueue()
        temp.shuffled( Random ).let {
            mediaController.setMediaItems( it )
        }
    }

    override fun shuffle(songs: List<SongInfo>) {
        val mediaController = mediaController ?: return
        mediaController.shuffleModeEnabled = true

        setMediaItems(songs.shuffled(Random))
        play(true)
    }

    override fun isConnected(): Boolean = mediaController?.connectedToken != null

    /**
     * Internal function to log the MediaController playback state.
     */
    private fun playState() {
        val mediaController = mediaController ?: return
        when(mediaController.playbackState) {
            Player.STATE_READY -> {
                Log.d(TAG, "Playback State is READY")
            }
            Player.STATE_IDLE -> {
                Log.d(TAG, "Playback State is IDLE")
            }
            Player.STATE_BUFFERING -> {
                Log.d(TAG, "Playback State is BUFFERING")
            }
            Player.STATE_ENDED -> {
                Log.d(TAG, "Playback State is ENDED")
            }
            else -> {
                Log.e(TAG, "Playback State error")
            }
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
