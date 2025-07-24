package com.example.music.ui.playlistdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.usecases.GetPlaylistDetailsUseCase
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
//import com.example.music.domain.player.SongPlayer

import com.example.music.ui.Screen
import com.example.music.data.util.combine
//
import com.example.music.domain.usecases.GetPlaylistDetailsV2
import com.example.music.util.logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Changelog:
 *
 * ---- TEST VERSION USING SAVEDSTATEHANDLE TO REPLICATE PLAYER SCREEN NAVIGATION
 * THIS WILL REPLACE NEED FOR ASSISTED INJECTION AND CHANGE INTERACTION WITH UISTATE
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 4/5/2025 - Testing out accessing Songs through MediaStore with GetPlaylistDetailsV2
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

data class PlaylistUiState(
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val playlist: PlaylistInfo = PlaylistInfo(),
    val songs: List<SongInfo> = emptyList(),
)

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    getPlaylistDetailsUseCase: GetPlaylistDetailsUseCase,
    getPlaylistDetailsV2: GetPlaylistDetailsV2,
//    private val songPlayer: SongPlayer,
    savedStateHandle: SavedStateHandle
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
        logger.info { "Playlist Details View Model - playlistID: $playlistId"}
        viewModelScope.launch {
            logger.info { "Playlist Details View Model - init viewModelScope launch start" }
            combine(
                refreshing,
                getPlaylistDetailsData,
            ) {
                refreshing,
                playlistDetailsFilterResult, ->
                logger.info { "Playlist Details View Model - PlaylistUiState call" }
                logger.info { "Playlist Details View Model - playlistDetailsFilterResult ID: ${playlistDetailsFilterResult.playlist.id}" }
                logger.info { "Playlist Details View Model - playlistDetailsFilterResult songs: ${playlistDetailsFilterResult.songs.size}" }
                logger.info { "Playlist Details View Model - isReady?: ${!refreshing}" }
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
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }

    fun onQueueSong(song: SongInfo) {
        //songPlayer.addToQueue(song)
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
