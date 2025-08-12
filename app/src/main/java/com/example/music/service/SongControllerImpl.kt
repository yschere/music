package com.example.music.service

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.music.data.repository.AppPreferencesRepo
import com.example.music.data.repository.RepeatType
import com.example.music.data.util.combine
import com.example.music.domain.model.SongInfo
import com.example.music.service.SongController
import com.example.music.service.SongControllerState
import com.example.music.domain.player.model.asLocalMediaItem
import com.example.music.domain.player.model.duration
import com.example.music.domain.player.model.title
import com.example.music.domain.player.model.toMediaItem
import com.example.music.ui.shared.mediaItems
import com.example.music.ui.shared.queue
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
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
private const val RandSeed = 1 // seed for shuffle randomizer

@UnstableApi
class SongControllerImpl @Inject constructor(
    context: Context,
    mainDispatcher: CoroutineDispatcher
) : SongController {

//    @Inject
//    lateinit var appPreferences: AppPreferencesRepo

    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    private val _playerState = MutableStateFlow(SongControllerState())


//    private val _currentSong = MutableStateFlow<SongInfo?>(null)
    private val _currentSong = MutableStateFlow<MediaItem?>(null)
    private val queue = MutableStateFlow<List<MediaItem>>(emptyList())
    private val isPlaying = MutableStateFlow(false)
    private val isShuffled = MutableStateFlow(false)
    private val repeatState = MutableStateFlow(RepeatType.OFF)
    private val timeElapsed = MutableStateFlow(Duration.ZERO)
    private val _playerSpeed = MutableStateFlow(DefaultPlaybackSpeed)
    // rethink from here which of these values are still needed for propagation between media service and rest of app
    // or if that is even needed as well

    private val coroutineScope = CoroutineScope(mainDispatcher)

    private var timerJob: Job? = null

    //override var mediaControllerCallback: (
    //        (
    //        playerState: PlayerState,
    //        currentMusic: Song?,
    //        currentPosition: Long,
    //        totalDuration: Long,
    //        isShuffleEnabled: Boolean,
    //        isRepeatOneEnabled: Boolean
    //    ) -> Unit
    //    )? = null

//    private val appPref = appPreferences.appPreferencesFlow
    init {
        val sessionToken = SessionToken(context, ComponentName(context, MediaService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener(
            {
                // Call controllerFuture.get() to retrieve the MediaController.
                // MediaController implements the Player interface, so it can be
                // attached to the PlayerView UI component.

                Log.i(TAG, "SessionToken MediaController - ${mediaControllerFuture.get()}")
                controllerListener()
            },
            MoreExecutors.directExecutor()
        )

        coroutineScope.launch {
            // Combine streams here
            //val appPref = appPreferences.appPreferencesFlow
//            isShuffled.value = appPreferences.isShuffleEnabled()
//            repeatState.value = appPreferences.getRepeatType()
            combine(
                //collects flow to generate SongPlayer's State
                _currentSong,
                queue,
                isPlaying,
                timeElapsed,
                _playerSpeed,
                repeatState,
                isShuffled,
//                appPref,
            ) { currentSong, queue, isPlaying, timeElapsed, playerSpeed, repeatState, isShuffled ->
                Log.i(TAG, "Song Controller State launch: ${currentSong?.title} " +
                        "\n queue: ${queue.size}" +
                        "\n isPlaying: $isPlaying" +
                        "\n timeElapsed: $timeElapsed" +
                        "\n playbackSpeed: $playerSpeed" +
                        "\n repeatState: $repeatState" +
                        "\n isShuffled: $isShuffled" )
                SongControllerState(
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

    private fun controllerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                //when the logger is out here it is getting called every millisecond with the player data, still not filled with a duration but still with player state 1
                //Log.i(TAG, "Controller Listener player state: ${player.playbackState}; player details: ${player.currentMediaItem?.mediaId}; ${player.deviceInfo.playbackType};"}// ${player.duration}; " }

                // --- Playback States
                // STATE_IDLE = 1
                // STATE_BUFFERING = 2
                // STATE_READY = 3
                // STATE_ENDED = 4
                //duration is only available if playback state is in ready

                // playerState.update used to set the songController state and all the properties along with it
                //_playerState.update {
                //but then the loggger in here was only called on the actual play state change? or at least when the play button on PlayerScreen was clicked.
                //why the difference? AND why is it that now the player screen itself doesn't change state at all, but the internal logs are still going as normal

                    //should player be updating song controller ... or should song controller be updating the player?
                    // because from here which one is actually populated?
                    // as of 5/3/2025, player is null and has nothing, so it would just overwrite song controller with null
                    //Log.i(TAG, "Controller Listener _player state update: ${player.currentMediaItem} \n ${player.duration} \n ${player.shuffleModeEnabled}" }
//                    SongControllerState(
//                        currentSong = player.currentMediaItem?.asExternalModel(),
//                        queue = player.queue.map { it.asExternalModel() },
//                        isPlaying = player.isPlaying,
//                        timeElapsed = Duration.ofMillis(player.duration),
//                        playbackSpeed = playerSpeed,
//                        repeatState = RepeatType.entries[player.repeatMode],
//                        isShuffled = player.shuffleModeEnabled,
//                    )
                //}
                //i think this sets the mediaControllerCallback player state vals in MusicPlayer, so i need to set the songcontrollerstate
            }
        })
    }

    override var playerSpeed: Duration = _playerSpeed.value

    override val playerState: StateFlow<SongControllerState> = _playerState.asStateFlow()

    override var currentSong: MediaItem? by _currentSong

    override fun addMediaItem(item: SongInfo) {
        addToQueue(item)
        Log.i(TAG, "media item: ${item.id}")
        mediaController?.setMediaItem(item.toMediaItem)
    }

    override fun addMediaItems(items: List<SongInfo>) {
        addToQueue(items)
        mediaController?.setMediaItems(
            items.map {
                it.toMediaItem
            }
        )
    }

    override fun preparePlayer() {
        mediaController?.prepare()
    }

    override fun addToQueue(songInfo: SongInfo) {
        //queue.update {
            //it + songInfo
        //}
        mediaController?.addMediaItem(songInfo.toMediaItem)
    }

    override fun addToQueue(songInfos: List<SongInfo>) {
//        queue.update {
//            it + songInfos
//        }
        mediaController?.addMediaItems( songInfos.map { it.toMediaItem } )
    }

    // this is for "PlayNext" to both place the song after the current song
    // regardless if the queue is shuffled or not, it will be the next song
    // so when the queue is unshuffled, it will be set after the placement of
    // where the current song is. which means this implem would need to know
    // the placement of the current song to be able to iterate on
    override fun addToQueueNext(songInfo: SongInfo) {
//        queue.update {
//            listOf(it[0]) + songInfo + it.subList(1,it.size-1)
//        }
        mediaController?.addMediaItem(1,songInfo.toMediaItem)
    }

    // same deal here, it's "PlayNext" but on a list of songs
    // but if shuffled is on, does it shuffle the incoming list? or place them
    // next in its original order. survey says yes, place the new songs in order
    override fun addToQueueNext(songInfos: List<SongInfo>) {
//        queue.update {
//            listOf(it[0]) + songInfos + it.subList(1,it.size-1)
//        }
        mediaController?.addMediaItems(1,songInfos.map { it.toMediaItem })
    }

    override fun setMediaItem(item: SongInfo) {
//        addToQueue(item)
        Log.i(TAG, "media item: ${item.id}")
        play(item)
    }

    override fun removeAllFromQueue() {
        //queue.value = emptyList()
//        mediaController?.setMediaItems(
//            emptyList()
//        )
        mediaController?.clearMediaItems()
    }

    override fun play() {
        //mediaPlayer.play()
        Log.i(TAG, "Entering the play() method. isPlaying is set to $isPlaying. Current song is ${_currentSong.value?.title}")

        // Do nothing if already playing
        if (isPlaying.value) {
            return
        }
        Log.i(TAG, "Starting Song Controller Impl Play()")

        val song = _currentSong.value ?: return
        val item = mediaController?.currentMediaItem
        Log.i(TAG, "Current media item is ${item?.title}")

        // This is almost definitely in the wrong place.
        //play(currentSong!!.asExternalModel())
        isPlaying.value = true
        //mediaController?.mediaItemCount
        timerJob = coroutineScope.launch {
            // Increment timer by a second
            Log.i(TAG, "Song Controller Impl Play() coroutine")
            while (isActive && timeElapsed.value < Duration.ofMillis(song.duration?:0)) {
                delay(playerSpeed.toMillis())
                timeElapsed.update { it + playerSpeed }
            }

            // Once done playing, see if
            isPlaying.value = false
            timeElapsed.value = Duration.ZERO

            if (hasNext() == true) {
                next()
            }
        }


        if (mediaController?.isPlaying == true) {
            Log.i(TAG, "Song Controller Impl IS PLAYING")
            return
        }



        if (mediaController?.isPlaying == true)
            mediaController?.pause()
        else
            mediaController?.play()
    }

    // interesting implications part 2:
    // if this is playing from an item that is already in context of the queue, it just plays that item
    // ie songInfo.onClick
    override fun play(songInfo: SongInfo) {
        play(listOf(songInfo))
    }

    // this also has interesting implications after learnings from shuffle
    // and playNext. for a list of songs, if it invoked with Play or PlayNext,
    // it will remove the original context that began the queue, but keep the items
    // that were in queue from "AddToQueue". so if the queue started from "Play" or "Shuffle"
    // it will get replaced with the new item(s) being played or shuffled
    override fun play(songInfos: List<SongInfo>) {
        Log.e(TAG, "In play(songInfos)")
        if (isPlaying.value) {
            pause()
            mediaController?.pause()
        }

        // Keep the currently playing episode in the queue
//        val playingSong = _currentSong.value
        //val playingSong = mediaController?.currentMediaItem

//        var previousList: List<SongInfo> = emptyList()
        //var previousList: List<MediaItem> = emptyList()

//        queue.update { queue ->
//            songInfos.map { song ->
//                if (queue.contains(song)) {
//                    val mutableList = queue.toMutableList()
//                    mutableList.remove(song)
//                    previousList = mutableList
//                } else {
//                    previousList = queue
//                }
//            }
//            if (playingSong != null) {
//                songInfos + listOf(playingSong) + previousList
//            } else {
//                songInfos + previousList
//            }
//        }
//        next()
//
//         songInfos.forEach { song ->
//            if (mediaController?.queue?.contains(song.toMediaSource) == true) {
//                val mutableList = mediaController?.queue?.toMutableList()
//                mutableList?.remove(song.toMediaSource)
//                previousList = mutableList ?: emptyList()
//            } else {
//                previousList = mediaController?.queue ?: emptyList()
//            }
//
//            if (playingSong != null) {
//                songInfos.map{ it.toMediaSource } + listOf(playingSong) + previousList
//            } else {
//                songInfos.map{ it.toMediaSource } + previousList
//            }
//        }
//
//        val temp2 = songInfos.map { it.toMediaSource }
////
////        val temp3 = listOf(playingSong).plus(mediaController?.queue?.filter { item ->
////            !temp2.contains(item)
////        } as MediaItem)
//
//        // want to create new queue by first checking if the playersongs are currently in the queue, removing them, then setting the queue to the new list
//        //mediaController?.setMediaItems(temp)
//        if (temp3 != null) {
//            mediaController?.setMediaItems(temp3 as MutableList<MediaItem>)
//        }
        val queue = songInfos.map{it.toMediaItem}
        mediaController?.setMediaItems(queue)
        Log.e(TAG, "Current queue has ${queue.size} items.")
        Log.e(TAG, "Current media controller state before apply is ${mediaController?.playbackState}.")

        mediaController?.apply {
            seekToDefaultPosition()
            playWhenReady = true
            prepare()
//            play()
        }
        Log.e(TAG, "Current media controller state after apply is ${mediaController?.playbackState}.")

    }

    override fun pause() {
        isPlaying.value = false
        mediaController?.pause()

        timerJob?.cancel()
        timerJob = null
    }

    override fun stop() {
        isPlaying.value = false
        mediaController?.stop()

        timeElapsed.value = Duration.ZERO

        timerJob?.cancel()
        timerJob = null
    }

    override fun advanceBy(duration: Duration) {
        val currentSongDuration = _currentSong.value?.duration ?: return
//        timeElapsed.update {
//            (it + duration).coerceAtMost(currentSongDuration)
//        }

        mediaController?.seekTo(duration.toMillis())
    }

    override fun rewindBy(duration: Duration) {
//        timeElapsed.update {
//            (it - duration).coerceAtLeast(Duration.ZERO)
//        }

        mediaController?.seekTo(duration.toMillis())
    }

    override fun onSeekingStarted() {
        // Need to pause the player so that it doesn't compete with timeline progression.
        // this is called to pause the player so that it's' prepared for seekTo
        pause()
    }

    override fun onSeekingFinished(duration: Duration) {
        //val currentSongDuration = _currentSong.value?.duration ?: return
        //timeElapsed.update { duration.coerceIn(Duration.ZERO, currentSongDuration) }
        //play()
        Log.i(TAG, "In the controller's onSeekingFinished() function, presumably after a song has finished.")

        play()
    }

    override fun onShuffle() {
        isShuffled.value = !isShuffled.value
        if (isShuffled.value) { //aka shuffle turned on
            //TODO: change the queue to be randomized order
            Log.i(TAG, "SHUFFLE QUEUE")
            shuffleQueue()
        }
        else { //aka shuffle turned off
            //TODO: change the queue to be in normal order
            Log.i(TAG, "UNDO QUEUE SHUFFLE")
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
            RepeatType.OFF -> {
                repeatState.value = RepeatType.ON
                Log.i(TAG, "REPEAT TYPE CHANGED TO ON")
                //in checking the queue, if the current song is the last song of the queue, set the onNext to play the first song
            }
            RepeatType.ON -> {
                repeatState.value = RepeatType.ONE
                Log.i(TAG, "REPEAT TYPE CHANGED TO ONE")
                //want to keep queue as is, just include boolean logic to put onNext to play the song over
                //use same boolean logic/value for onPrevious to restart song over
            }
            RepeatType.ONE -> {
                repeatState.value = RepeatType.OFF
                Log.i(TAG, "REPEAT TYPE CHANGED TO OFF")
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
        Log.i(TAG, "In the controller's next() function, presumably after a song is skipped.")
        //val q = queue.value
        val q = mediaController?.queue
        if (q?.isEmpty() == true) {
            return
        }

        timeElapsed.value = Duration.ZERO
        val nextSong = q?.get(0)
        currentSong = nextSong
        //queue.value = q - nextSong
        mediaController?.removeMediaItem(0)
        mediaController?.seekToNextMediaItem()
        play()
    }

    override fun previous() {
        timeElapsed.value = Duration.ZERO
        isPlaying.value = false

        mediaController?.seekToPreviousMediaItem()

        timerJob?.cancel()
        timerJob = null
    }

    private fun hasNext(): Boolean? {
        //return queue.value.isNotEmpty()
        return mediaController?.hasNextMediaItem()
        //androidx. media3.common. Player Returns whether a next MediaItem exists, which may depend on the current repeat mode and whether shuffle mode is enabled.
        //Note: When the repeat mode is REPEAT_MODE_ONE, this method behaves the same as when the current repeat mode is REPEAT_MODE_OFF. See REPEAT_MODE_ONE for more details.
        //This method must only be called if COMMAND_GET_TIMELINE is available.
    }

    private fun shuffleQueue() { //this would get called if the queue itself needs to be shuffled
//        queue.update {
//            it.shuffled(Random(RandSeed))
//        }
        val temp = mediaController?.queue
        temp?.shuffled( Random(RandSeed) )?.let {
            mediaController?.setMediaItems(
                it
            )
        }
    }

    override fun shuffle(songInfos: List<SongInfo>) {
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
//        queue.update {
//            songInfos.shuffled(Random(RandSeed))
//        }
        removeAllFromQueue()

        mediaController?.setMediaItems(
            songInfos.map {
                it.toMediaItem
            }.shuffled( Random(RandSeed) )
        )
    }

    override fun getIsPlaying() : Boolean {
        return mediaController?.isPlaying ?: false
    }

    override fun getIsShuffled() : Boolean {
        return isShuffled.value
    }

    override fun getRepeatState() : RepeatType {
        return repeatState.value
    }

    override fun getTimeElapsed() : Duration {
        return timeElapsed.value
    }

    override fun getHasNext(): Boolean {
        return mediaController?.hasNextMediaItem() ?: false
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
