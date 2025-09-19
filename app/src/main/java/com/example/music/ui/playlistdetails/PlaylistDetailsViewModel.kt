package com.example.music.ui.playlistdetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.ui.Screen
import com.example.music.data.util.combine
import com.example.music.domain.usecases.GetPlaylistDetailsV2
import com.example.music.service.SongController
import dagger.hilt.android.lifecycle.HiltViewModel
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
)

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    getPlaylistDetailsV2: GetPlaylistDetailsV2,
    savedStateHandle: SavedStateHandle,

    private val songController: SongController,
) : ViewModel() {

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

    private val _state = MutableStateFlow(PlaylistUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<PlaylistUiState>
        get() = _state

    init {
        Log.i(TAG, "playlistID: $playlistId")
        viewModelScope.launch {
            Log.i(TAG, "init viewModelScope launch start")
            combine(
                refreshing,
                getPlaylistDetailsData,
            ) {
                refreshing,
                playlistDetailsFilterResult, ->
                Log.i(TAG, "PlaylistUiState call")
                Log.i(TAG, "playlistDetailsFilterResult ID: ${playlistDetailsFilterResult.playlist.id}")
                Log.i(TAG, "playlistDetailsFilterResult songs: ${playlistDetailsFilterResult.songs.size}")
                Log.i(TAG, "is SongController available: ${songController.isConnected()}")
                Log.i(TAG, "isReady?: ${!refreshing}")

                PlaylistUiState(
                    isReady = !refreshing,
                    playlist = playlistDetailsFilterResult.playlist,
                    songs = playlistDetailsFilterResult.songs,
                )
            }
            .catch { throwable ->
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
        refresh(force = false)

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

    fun onPlaylistAction(action: PlaylistAction) {
        Log.i(TAG, "onPlaylistAction - $action")
        when (action) {
            is PlaylistAction.PlaySong -> onPlaySong(action.song)
            is PlaylistAction.PlaySongs -> onPlaySongs(action.songs)
            is PlaylistAction.QueueSong -> onQueueSong(action.song)
            is PlaylistAction.QueueSongs -> onQueueSongs(action.songs)
            is PlaylistAction.ShuffleSongs -> onShuffleSongs(action.songs)
            is PlaylistAction.SongMoreOptionClicked -> onSongMoreOptionClick(action.song)
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

    private fun onSongMoreOptionClick(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionClick -> ${song.title}")
        //selectedSong.value = song
    }

}

sealed interface PlaylistAction {
    data class PlaySong(val song: SongInfo) : PlaylistAction
    data class PlaySongs(val songs: List<SongInfo>) : PlaylistAction
    data class QueueSong(val song: SongInfo) : PlaylistAction
    data class QueueSongs(val songs: List<SongInfo>) : PlaylistAction
    data class ShuffleSongs(val songs: List<SongInfo>) : PlaylistAction
    data class SongMoreOptionClicked(val song: SongInfo) : PlaylistAction
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
