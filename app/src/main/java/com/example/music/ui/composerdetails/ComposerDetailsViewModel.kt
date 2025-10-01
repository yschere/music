package com.example.music.ui.composerdetails

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.music.domain.usecases.GetComposerDetailsUseCase
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.GetSongData
import com.example.music.service.SongController
import com.example.music.ui.Screen
import com.example.music.ui.player.MiniPlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Composer Details View Model"

data class ComposerUiState (
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val composer: ComposerInfo = ComposerInfo(),
    val songs: List<SongInfo> = emptyList(),
)

/**
 * ViewModel that handles the business logic and screen state of the Composer Details screen
 */
@HiltViewModel
class ComposerDetailsViewModel @Inject constructor(
    getComposerDetailsUseCase: GetComposerDetailsUseCase,
    savedStateHandle: SavedStateHandle,

    private val getSongData: GetSongData,
    private val songController: SongController,
) : ViewModel(), MiniPlayerState {

    private val _composerId: String = savedStateHandle.get<String>(Screen.ARG_COMPOSER_ID)!!
    private val composerId = _composerId.toLong()

    private val getComposerDetailsData = getComposerDetailsUseCase(composerId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    // bottom player section
    override var currentSong by mutableStateOf(SongInfo())
    private var _isActive by mutableStateOf(songController.isActive)
    var isActive
        get() = _isActive
        set(value) {
            _isActive = songController.isActive
            refresh(value)
        }

    override val player: Player?
        get() = songController.player
    private var _isPlaying by mutableStateOf(songController.isPlaying)
    override var isPlaying
        get() = _isPlaying
        set(value) {
            if (value) songController.play(true)
            else songController.pause()
        }

    private val _state = MutableStateFlow(ComposerUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<ComposerUiState>
        get() = _state

    init {
        Log.i(TAG, "init START --- composerId: $composerId")
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch START")

            Log.i(TAG, "SongController status:\n" +
                "isActive?: $isActive\n" +
                "player?: ${player?.playbackState}")

            combine(
                refreshing,
                getComposerDetailsData,
            ) {
                refreshing,
                composerDetailsFilterResult, ->
                Log.i(TAG, "ComposerUiState combine START\n" +
                    "composerDetailsFilterResult ID: ${composerDetailsFilterResult.composer.id}\n" +
                    "composerDetailsFilterResult songs: ${composerDetailsFilterResult.songs.size}\n" +
                    "is SongController available: ${songController.isConnected()}\n" +
                    "isReady?: ${!refreshing}")

                getSongControllerState()

                ComposerUiState(
                    isReady = !refreshing,
                    composer = composerDetailsFilterResult.composer,
                    songs = composerDetailsFilterResult.songs,
                )
            }.catch { throwable ->
                Log.i(TAG, "Error Caught: ${throwable.message}")
                emit(
                    ComposerUiState(
                        isReady = true,
                        errorMessage = throwable.message
                    )
                )
            }.collect{
                _state.value = it
            }
        }

        viewModelScope.launch {
            songController.events.collect {
                Log.d(TAG, "get SongController Player Event(s)")

                // if events is empty, take these actions to generate initial MiniPlayer values
                if (it == null) {
                    Log.d(TAG, "init: running start up events to initialize MiniPlayer")
                    getSongControllerState()
                    onPlayerEvent(event = Player.EVENT_IS_LOADING_CHANGED)
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

        refresh(force = false)
        Log.i(TAG, "init END")
    }

    private fun onPlayerEvent(event: Int) {
        when (event) {
            // Event for checking if the SongController is loaded and ready to read
            Player.EVENT_IS_LOADING_CHANGED -> {
                val loaded = songController.loaded
                if (loaded.equals(true)) {
                    refreshing.value = false
                    isActive = songController.isActive
                }
                Log.d(TAG, "isLoading changed:\n" +
                    "isPlaying set to $isPlaying\n" +
                    "isActive set to $isActive")
            }

            // Event for checking if SongController is playing
            Player.EVENT_IS_PLAYING_CHANGED -> {
                _isPlaying = songController.isPlaying
                isActive = songController.isActive
                Log.d(TAG, "isPlaying changed:\n" +
                    "isPlaying set to $isPlaying\n" +
                    "isActive set to $isActive")
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
                    currentSong = getSongData(id.toLong())
                    Log.d(TAG, "Current Song set to ${currentSong.title}")
                    songController.logTrackNumber()
                }
            }

            Player.EVENT_TRACKS_CHANGED -> {
                songController.logTrackNumber()
            }
        }
    }

    private suspend fun getSongControllerState() {
        val id = songController.currentSong?.mediaId
        if (id != null) {
            currentSong = getSongData(id.toLong())
        }
        _isPlaying = songController.isPlaying
        isActive = songController.isActive
    }

    fun refresh(force: Boolean = true) {
        Log.i(TAG, "Refresh call")
        Log.i(TAG, "refreshing: ${refreshing.value}")
        viewModelScope.launch {
            runCatching {
                Log.i(TAG, "Refresh runCatching")
                refreshing.value = true
            }.onFailure {
                Log.e(TAG, "$it ::: runCatching failed (not sure what this means)")
            }

            Log.i(TAG, "refresh to be false -> sets screen to ready state")
            refreshing.value = false
        }
    }

    fun onPlay() {
        Log.i(TAG,"Hit play btn")
        songController.play(true)
        _isPlaying = true
    }

    fun onPause() {
        Log.i(TAG, "Hit pause btn")
        songController.pause()
        _isPlaying = false
    }

    fun onComposerAction(action: ComposerAction) {
        Log.i(TAG, "onComposerAction - $action")
        when (action) {
            is ComposerAction.PlaySong -> onPlaySong(action.song)
            is ComposerAction.PlaySongs -> onPlaySongs(action.songs)
            is ComposerAction.QueueSong -> onQueueSong(action.song)
            is ComposerAction.QueueSongs -> onQueueSongs(action.songs)
            is ComposerAction.ShuffleSongs -> onShuffleSongs(action.songs)
            is ComposerAction.SongMoreOptionsClicked -> onSongMoreOptionsClick(action.song)
        }
    }

    private fun onPlaySong(song: SongInfo) {
        Log.i(TAG, "onPlaySong -> ${song.title}")
        songController.play(song)
    }
    private fun onPlaySongs(songs: List<SongInfo>) {
        Log.i(TAG, "onPlaySongs -> ${songs.size}")
        songController.play(songs)
    }
    private fun onQueueSong(song: SongInfo) {
        Log.i(TAG, "onQueueSong -> ${song.title}")
        songController.addToQueue(song)
    }
    private fun onQueueSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongs -> ${songs.size}")
        songController.addToQueue(songs)
    }
    private fun onShuffleSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onShuffleSongs -> ${songs.size}")
        songController.shuffle(songs)
    }
    private fun onSongMoreOptionsClick(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionsClick -> ${song.title}")
        //selectedSong.value = song
    }
}

sealed interface ComposerAction {
    data class PlaySong(val song: SongInfo) : ComposerAction
    data class PlaySongs(val songs: List<SongInfo>) : ComposerAction
    data class QueueSong(val song: SongInfo) : ComposerAction
    data class QueueSongs(val songs: List<SongInfo>) : ComposerAction
    data class ShuffleSongs(val songs: List<SongInfo>) : ComposerAction
    data class SongMoreOptionsClicked(val song: SongInfo) : ComposerAction
}