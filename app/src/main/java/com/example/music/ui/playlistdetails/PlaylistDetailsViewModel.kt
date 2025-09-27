package com.example.music.ui.playlistdetails

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.ui.Screen
import com.example.music.data.util.combine
import com.example.music.domain.usecases.GetPlaylistDetailsV2
import com.example.music.domain.usecases.GetSongDataV2
import com.example.music.service.SongController
import com.example.music.ui.player.MiniPlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Playlist Details View Model"

data class PlaylistUiState(
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val playlist: PlaylistInfo = PlaylistInfo(),
    val songs: List<SongInfo> = emptyList(),
    val selectSong: SongInfo = SongInfo(),
)

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    getPlaylistDetailsV2: GetPlaylistDetailsV2,
    savedStateHandle: SavedStateHandle,

    private val getSongDataV2: GetSongDataV2,
    private val songController: SongController,
) : ViewModel(), MiniPlayerState {

    private val _playlistId: String = savedStateHandle.get<String>(Screen.ARG_PLAYLIST_ID)!!
    private val playlistId = _playlistId.toLong()

//    private val getPlaylistDetailsData = getPlaylistDetailsUseCase(playlistId)
    private val getPlaylistDetailsData = getPlaylistDetailsV2(playlistId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    /* ---- Initial version that uses playlistRepo directly to retrieve Flow data for Playlist Details
    val playlist = playlistRepo.getPlaylistWithExtraInfo(playlistId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    // original version for getting Song objects
    val songs = playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())*/

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

    private val _state = MutableStateFlow(PlaylistUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<PlaylistUiState>
        get() = _state

    init {
        Log.i(TAG, "init START --- playlistID: $playlistId")
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch START")

            Log.i(TAG, "SongController status:\n" +
                "isActive?: $isActive\n" +
                "player?: ${player?.playbackState}")

            combine(
                refreshing,
                getPlaylistDetailsData,
                selectedSong,
            ) {
                refreshing,
                playlistDetailsFilterResult,
                selectSong ->
                Log.i(TAG, "PlaylistUiState combine START\n" +
                    "playlistDetailsFilterResult ID: ${playlistDetailsFilterResult.playlist.id}\n" +
                    "playlistDetailsFilterResult songs: ${playlistDetailsFilterResult.songs.size}\n" +
                    "is SongController available: ${songController.isConnected()}\n" +
                    "isReady?: ${!refreshing}")

                getSongControllerState()

                PlaylistUiState(
                    isReady = !refreshing,
                    playlist = playlistDetailsFilterResult.playlist,
                    songs = playlistDetailsFilterResult.songs,
                    selectSong = selectSong,
                )
            }
            .catch { throwable ->
                Log.i(TAG, "Error Caught: ${throwable.message}")
                emit(
                    PlaylistUiState(
                        isReady = true,
                        errorMessage = throwable.message,
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

    fun onPlaylistAction(action: PlaylistAction) {
        Log.i(TAG, "onPlaylistAction - $action")
        when (action) {
            is PlaylistAction.SongMoreOptionClicked -> onSongMoreOptionClick(action.song)

            is PlaylistAction.PlaySong -> onPlaySong(action.song)
            is PlaylistAction.PlaySongNext -> onPlaySongNext(action.song)
            is PlaylistAction.QueueSong -> onQueueSong(action.song)

            is PlaylistAction.PlaySongs -> onPlaySongs(action.songs)
            is PlaylistAction.PlaySongsNext -> onPlaySongsNext(action.songs)
            is PlaylistAction.ShuffleSongs -> onShuffleSongs(action.songs)
            is PlaylistAction.QueueSongs -> onQueueSongs(action.songs)
        }
    }

    private fun onSongMoreOptionClick(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionClick -> ${song.title}")
        selectedSong.value = song
    }

    private fun onPlaySong(song: SongInfo) {
        Log.i(TAG, "onPlaySong -> ${song.title}")
        songController.play(song)
    }
    private fun onPlaySongNext(song: SongInfo) {
        Log.i(TAG, "onPlaySongNext -> ${song.title}")
        songController.addToQueueNext(song)
    }
    private fun onQueueSong(song: SongInfo) {
        Log.i(TAG, "onQueueSong -> ${song.title}")
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
        Log.i(TAG, "onShuffleSongs -> ${songs.size}")
        songController.shuffle(songs)
    }
    private fun onQueueSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongs -> ${songs.size}")
        songController.addToQueue(songs)
    }
}

sealed interface PlaylistAction {
    data class SongMoreOptionClicked(val song: SongInfo) : PlaylistAction

    data class PlaySong(val song: SongInfo) : PlaylistAction
    data class PlaySongNext(val song: SongInfo) : PlaylistAction
    data class QueueSong(val song: SongInfo) : PlaylistAction

    data class PlaySongs(val songs: List<SongInfo>) : PlaylistAction
    data class PlaySongsNext(val songs: List<SongInfo>) : PlaylistAction
    data class ShuffleSongs(val songs: List<SongInfo>) : PlaylistAction
    data class QueueSongs(val songs: List<SongInfo>) : PlaylistAction
}


/**
 * ---------ORIGINAL VERSION: ViewModel that handles the business logic and screen state of the Playlist details screen.
 * Note: currently using this screen and view model to compare SongInfo and PlayerSong and which one is better to use across screens that have SongListItem
 */
/*
sealed interface PlaylistUiState {
    data object Loading : PlaylistUiState
    data class Ready(
        val playlist: PlaylistInfo,
        val songs: List<SongInfo>,
    ) : PlaylistUiState
}

@HiltViewModel(assistedFactory = PlaylistDetailsViewModel.PlaylistDetailsViewModelFactory::class)
class PlaylistDetailsViewModel @AssistedInject constructor(
    private val getSongDataUseCase: GetSongDataUseCase,
    private val songPlayer: SongPlayer,
    private val playlistRepo: PlaylistRepo,
    @Assisted val playlistId: Long,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    @AssistedFactory
    interface PlaylistDetailsViewModelFactory {
        fun create(playlistId: Long): PlaylistDetailsViewModel
    }

    val playlist = playlistRepo.getPlaylistWithExtraInfo(playlistId)

    val state: StateFlow<PlaylistUiState> =
        combine( //want to use this to store the information needed to correctly determine the album and songs to view
            //playlistRepo.getPlaylistWithExtraInfo(playlistId), //original code: gets Flow<PlaylistWithExtraInfo>
            //playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId) //original code: gets Flow<List<SongInfo>>
            playlist,
            playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId), //keeping this for now for comparison of using SongInfo against using PlayerSong to populate SongListItems
        ) { playlist, songs, ->
            PlaylistUiState.Ready(
                //original code: playlist = playlist.asExternalModel(),
                //original code: songs = songs.map{ it.asExternalModel() },
                playlist = playlist.asExternalModel(),
                songs = songs.map{ it.asExternalModel() },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaylistUiState.Loading
        )

    fun onQueueSong(songInfo: SongInfo) {
        songPlayer.addToQueue(songInfo)
    }

}*/
