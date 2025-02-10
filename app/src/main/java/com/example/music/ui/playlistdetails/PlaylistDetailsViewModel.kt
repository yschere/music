package com.example.music.ui.playlistdetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.music.data.repository.PlaylistRepo
import com.example.music.domain.GetPlaylistDetailsUseCase
import com.example.music.domain.GetSongDataUseCase
import com.example.music.model.PlaylistInfo
import com.example.music.model.SongInfo
import com.example.music.model.asExternalModel
import com.example.music.player.SongPlayer
import com.example.music.player.model.PlayerSong
import com.example.music.ui.Screen
import com.example.music.ui.player.PlayerUiState
import com.example.music.util.combine
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ---- TEST VERSION USING SAVEDSTATEHANDLE TO REPLICATE PLAYER SCREEN NAVIGATION
 * THIS WILL REPLACE NEED FOR ASSISTED INJECTION AND CHANGE INTERACTION WITH UISTATE
 */

data class PlaylistUiState(
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val playlist: PlaylistInfo = PlaylistInfo(),
    val songs: List<SongInfo> = emptyList(),
    val pSongs: List<PlayerSong> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    //private val getSongDataUseCase: GetSongDataUseCase,
    //playlistRepo: PlaylistRepo,
    private val getPlaylistDetailsUseCase: GetPlaylistDetailsUseCase,
    private val songPlayer: SongPlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _playlistId: String = savedStateHandle.get<String>(Screen.ARG_PLAYLIST_ID)!!
    private val playlistId = _playlistId.toLong()
    /* -----INITIAL ATTEMPT TO COPY PLAYERVIEWMODEL's UISTATE -------
    var uiState by mutableStateOf(PlaylistUiState())
        private set */

    private val getPlaylistDetailsData = getPlaylistDetailsUseCase(playlistId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    /* ---- Initial version that uses playlistRepo directly to retrieve Flow data for Playlist Details
    val playlist = playlistRepo.getPlaylistExtraInfo(playlistId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    //TODO: PlayerSong support
    private var pSongsList = playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId)
        .flatMapLatest { item ->
            getSongDataUseCase(item)
        }.shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    // original version for getting Song objects
    val songs = playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())*/

    private val _state = MutableStateFlow(PlaylistUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<PlaylistUiState>
            get() = _state

    init {
        viewModelScope.launch {
            combine(
                refreshing,
                getPlaylistDetailsData,
            ) {
                refreshing,
                playlistDetailsFilterResult, ->

                PlaylistUiState(
                    isReady = !refreshing,
                    playlist = playlistDetailsFilterResult.playlist,
                    songs = playlistDetailsFilterResult.songs,
                    pSongs = playlistDetailsFilterResult.pSongs,
                )
            }
            /*combine(
                refreshing,
                playlist,
                pSongsList,
                songs,
            ) {
                refreshing,
                plist,
                pSongs,
                songs ->
                PlaylistUiState(
                    isReady = !refreshing,
                    playlist = plist.asExternalModel(),
                    pSongs = pSongs,
                    songs = songs.map{ it.asExternalModel() },
                )
            }.*/
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
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }

    fun onQueueSong(playerSong: PlayerSong) {
        songPlayer.addToQueue(playerSong)
    }

}


/**
 * ---------ORIGINAL VERSION: ViewModel that handles the business logic and screen state of the Playlist details screen.
 * TODO: currently using this screen and view model to compare SongInfo and PlayerSong and which one is better to use across screens that have SongListItem
 */
/*
sealed interface PlaylistUiState {
    data object Loading : PlaylistUiState
    data class Ready(
        val playlist: PlaylistInfo,
        val songs: List<SongInfo>,
        val pSongs: List<PlayerSong>,
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

    val playlist = playlistRepo.getPlaylistExtraInfo(playlistId)

    @OptIn(ExperimentalCoroutinesApi::class)
    private var pSongsList = playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId).flatMapLatest { item ->
        getSongDataUseCase(item)
    }

    val state: StateFlow<PlaylistUiState> =
        combine( //want to use this to store the information needed to correctly determine the album and songs to view
            //playlistRepo.getPlaylistExtraInfo(playlistId), //original code: gets Flow<PlaylistWithExtraInfo>
            //playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId) //original code: gets Flow<List<SongInfo>>
            playlist,
            playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId), //keeping this for now for comparison of using SongInfo against using PlayerSong to populate SongListItems
            pSongsList
        ) { playlist, songs, pSongs ->
            PlaylistUiState.Ready(
                //original code: playlist = playlist.asExternalModel(),
                //original code: songs = songs.map{ it.asExternalModel() },
                playlist = playlist.asExternalModel(),
                songs = songs.map{ it.asExternalModel() },
                pSongs = pSongs
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaylistUiState.Loading
        )

    fun onQueueSong(playerSong: PlayerSong) {
        songPlayer.addToQueue(playerSong)
    }

}*/
