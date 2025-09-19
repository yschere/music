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
import com.example.music.data.repository.RepeatType
import com.example.music.domain.model.SongInfo
import com.example.music.domain.player.model.title
import com.example.music.domain.player.model.toMediaItem
import com.example.music.ui.shared.mediaItems
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
import javax.inject.Inject
import kotlin.random.Random
import kotlin.reflect.KProperty

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
    override val isActive: Boolean
        get() = (mediaController?.playbackState == Player.STATE_READY) || (mediaController?.playbackState == Player.STATE_BUFFERING)

    override val isShuffled: Boolean
        get() = mediaController?.shuffleModeEnabled ?: false
    override val repeatState: RepeatType
        get() = RepeatType.entries[mediaController?.repeatMode ?: 0]

    // attempt to save un-shuffled list
    private var tempPlayOrder: List<SongInfo> = emptyList()

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
        Log.d(TAG, "in play( Boolean ): START -- playWhenReady $playWhenReady")
        val mediaController = mediaController ?: return
        mediaController.playWhenReady = playWhenReady
        mediaController.play()
        Log.d(TAG, "in play( Boolean ): END")
    }

    override fun play(song: SongInfo) {
        Log.d(TAG, "In play( SongInfo ): START")
        play(listOf(song))
        Log.d(TAG, "In play( SongInfo ): END")
    }

    override fun play(songs: List<SongInfo>) {
        Log.d(TAG, "In play( List<SongInfo> ): START")
        val mediaController = mediaController ?: return
        playState()

        Log.d(TAG, "Count of items to queue: ${songs.size} items.")
        mediaController.shuffleModeEnabled = false
        tempPlayOrder = songs
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
        Log.d(TAG, "in pause() START --- isPlaying is $isPlaying")
        val mediaController = mediaController ?: return
        mediaController.pause()
        Log.d(TAG, "in pause() END --- isPlaying is set to $isPlaying")
    }

    override fun shuffle(songs: List<SongInfo>) {
        Log.d(TAG, "in shuffle( List<SongInfo> ): START")
        val mediaController = mediaController ?: return
        playState()

        Log.d(TAG, "Count of items to queue: ${songs.size} items.")
        mediaController.shuffleModeEnabled = true
        tempPlayOrder = songs
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
        Log.d(TAG, "in shuffle( List<SongInfo> ): END")
    }

    override fun stop() {
        Log.d(TAG, "in stop() START --- isPlaying is $isPlaying")
        val mediaController = mediaController ?: return
        mediaController.stop()
        Log.d(TAG, "in stop() END --- isPlaying is set to $isPlaying")
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

    override fun onRepeat() {
        val mediaController = mediaController ?: return
        Log.d(TAG, "in onRepeat(): START --- repeatState is ${repeatState.name}")
        when(repeatState) {
            RepeatType.OFF -> {
                mediaController.repeatMode = RepeatType.ONE.ordinal
                Log.i(TAG, "REPEAT TYPE CHANGED TO ONE")
                //in checking the queue, if the current song is the last song of the queue, set the onNext to play the first song
            }
            RepeatType.ONE -> {
                mediaController.repeatMode = RepeatType.ON.ordinal
                Log.i(TAG, "REPEAT TYPE CHANGED TO ON")
                //in checking the queue, if the current song is the last song of the queue, trigger the stop function to end the session/queue
            }
            RepeatType.ON -> {
                mediaController.repeatMode = RepeatType.OFF.ordinal
                Log.i(TAG, "REPEAT TYPE CHANGED TO OFF")
                //want to keep queue as is, just include boolean logic to put onNext to play the song over
                //use same boolean logic/value for onPrevious to restart song over
            }
        }
        Log.d(TAG, "in onRepeat(): END --- repeatState set to ${repeatState.name}")
    }

    override fun onShuffle() {
        val mediaController = mediaController ?: return
        Log.d(TAG, "in onShuffle(): START --- isShuffled is $isShuffled")
        if (isShuffled) {
            Log.d(TAG, "UN-SHUFFLE QUEUE")
            mediaController.shuffleModeEnabled = false
            unShuffleQueue()
        }
        else {
            Log.d(TAG, "BEGIN SHUFFLE QUEUE")
            mediaController.shuffleModeEnabled = true
            shuffleQueue()
        }
        Log.d(TAG, "is onShuffle(): END --- isShuffled set to $isShuffled")
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
        Log.d(TAG, "in shuffleQueue(): START") 
        setMediaItems(tempPlayOrder.shuffled(Random))
        play(true)
        Log.d(TAG, "in shuffleQueue(): END")
    }

    /**
     * Internal function to un-shuffle the MediaController queue.
     */
    private fun unShuffleQueue() {
        Log.d(TAG, "in unShuffleQueue(): START")
        /*
            // Goal for now: to have the queue change back to it's original playback order,
            // as well as keep the current media item untouched if it is currently
            // playing. So that when the order changes, the current item's placement can shift,
            // but it will still play.

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
        */
        setMediaItems(tempPlayOrder)
        play(true)
        Log.d(TAG, "in unShuffleQueue(): END")
    }

    override fun isConnected(): Boolean = mediaController?.connectedToken != null

    override fun logTrackNumber() {
        val mediaController = mediaController ?: return
        val currTrack = mediaController.currentMediaItemIndex + 1 // returns the index of the item from its original, ordered context
        val totalTrack = mediaController.mediaItemCount // total items in playback set
        Log.d(TAG, "Playing Track #$currTrack of #$totalTrack")
    }

    /**
     * Internal function to log the MediaController playback state.
     */
    private fun playState() {
        val mediaController = mediaController ?: return
        when(mediaController.playbackState) {
            Player.STATE_IDLE -> {
                Log.e(TAG, "Playback State is IDLE")
            } // 1
            Player.STATE_BUFFERING -> {
                Log.e(TAG, "Playback State is BUFFERING")
            } // 2
            Player.STATE_READY -> {
                Log.e(TAG, "Playback State is READY")
            } // 3
            Player.STATE_ENDED -> {
                Log.e(TAG, "Playback State is ENDED")
            } // 4
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
