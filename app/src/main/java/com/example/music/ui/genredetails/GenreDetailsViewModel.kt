package com.example.music.ui.genredetails

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.GetGenreDetailsV2
import com.example.music.domain.usecases.GetSongDataV2
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

private const val TAG = "Genre Details View Model"

data class GenreUiState (
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val genre: GenreInfo = GenreInfo(),
    val songs: List<SongInfo> = emptyList(),
    val selectSong: SongInfo = SongInfo()
)

/**
 * ViewModel that handles the business logic and screen state of the Genre Details screen
 */
@HiltViewModel
class GenreDetailsViewModel @Inject constructor(
    getGenreDetailsV2: GetGenreDetailsV2,
    savedStateHandle: SavedStateHandle,

    private val getSongDataV2: GetSongDataV2,
    private val songController: SongController,
) : ViewModel(), MiniPlayerState {

    private val _genreId: String = savedStateHandle.get<String>(Screen.ARG_GENRE_ID)!!
    private val genreId = _genreId.toLong()

    private val getGenreDetailsData = getGenreDetailsV2(genreId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val selectedSong = MutableStateFlow(SongInfo())

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

    private val _state = MutableStateFlow(GenreUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<GenreUiState>
        get() = _state

    init {
        Log.i(TAG, "init START --- genreId: $genreId")
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch START")

            Log.i(TAG, "SongController status:\n" +
                "isActive?: $isActive\n" +
                "player?: ${player?.playbackState}\n")

            combine(
                refreshing,
                getGenreDetailsData,
                selectedSong,
            ) {
                refreshing,
                genreDetailsFilterResult,
                selectSong ->
                Log.i(TAG, "GenreUiState combine START\n" +
                    "genreDetailsFilterResult ID: ${genreDetailsFilterResult.genre.id}\n" +
                    "genreDetailsFilterResult songs: ${genreDetailsFilterResult.songs.size}\n" +
                    "is SongController available: ${songController.isConnected()}\n" +
                    "isReady?: ${!refreshing}")

                getSongControllerState()

                GenreUiState(
                    isReady = !refreshing,
                    genre = genreDetailsFilterResult.genre,
                    songs = genreDetailsFilterResult.songs,
                    selectSong = selectSong ?: SongInfo(),
                )
            }.catch { throwable ->
                Log.i(TAG, "Error Caught: ${throwable.message}")
                emit(
                    GenreUiState(
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

                // if events is empty, take these actions to generate the needed values for populating MiniPlayer
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
                    "isPlaying set to $isPlaying" +
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
                    currentSong = getSongDataV2(id.toLong())
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
            currentSong = getSongDataV2(id.toLong())
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

    fun onGenreAction(action: GenreAction) {
        Log.i(TAG, "onGenreAction - $action")
        when (action) {
            is GenreAction.SongMoreOptionClicked -> onSongMoreOptionClicked(action.song)

            is GenreAction.PlaySong -> onPlaySong(action.song)
            is GenreAction.PlaySongNext -> onPlaySongNext(action.song)
            is GenreAction.QueueSong -> onQueueSong(action.song)

            is GenreAction.PlaySongs -> onPlaySongs(action.songs)
            is GenreAction.PlaySongsNext -> onPlaySongsNext(action.songs)
            is GenreAction.QueueSongs -> onQueueSongs(action.songs)
            is GenreAction.ShuffleSongs -> onShuffleSongs(action.songs)
        }
    }

    private fun onSongMoreOptionClicked(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionClick - ${song.title}")
        selectedSong.value = song
    }

    private fun onPlaySong(song: SongInfo) {
        Log.i(TAG, "onPlaySong - ${song.title}")
        songController.play(song)
    }
    private fun onPlaySongNext(song: SongInfo) {
        Log.i(TAG, "onPlaySongNext - ${song.title}")
        songController.addToQueueNext(song)
    }
    private fun onQueueSong(song: SongInfo) {
        Log.i(TAG, "onQueueSong - ${song.title}")
        songController.addToQueue(song)
    }

    private fun onPlaySongs(songs: List<SongInfo>) {
        Log.i(TAG, "onPlaySongs -> ${songs.size}")
        songController.play(songs)
    }
    private fun onPlaySongsNext(songs: List<SongInfo>) {
        Log.i(TAG, "onPlaySongsNext -> ${songs.size}")
        songController.addToQueueNext(songs)
    }
    private fun onShuffleSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onShuffleSongs - ${songs.size}")
        songController.shuffle(songs)
    }
    private fun onQueueSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongs - ${songs.size}")
        songController.addToQueue(songs)
    }
}

sealed interface GenreAction {
    data class SongMoreOptionClicked(val song: SongInfo) : GenreAction

    data class PlaySong(val song: SongInfo) : GenreAction
    data class PlaySongNext(val song: SongInfo) : GenreAction
    data class QueueSong(val song: SongInfo) : GenreAction

    data class PlaySongs(val songs: List<SongInfo>) : GenreAction
    data class PlaySongsNext(val songs: List<SongInfo>) : GenreAction
    data class ShuffleSongs(val songs: List<SongInfo>) : GenreAction
    data class QueueSongs(val songs: List<SongInfo>) : GenreAction
}