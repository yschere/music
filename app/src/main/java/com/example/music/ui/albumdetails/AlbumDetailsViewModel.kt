package com.example.music.ui.albumdetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.GetAlbumDetailsV2
import com.example.music.service.SongController
import com.example.music.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Album Details View Model"

/** Changelog:
 * ---- TEST VERSION USING SAVEDSTATEHANDLE TO REPLICATE PLAYER SCREEN NAVIGATION
 * As of 2/10/2025, this version is in remote branch and working on
 * PlaylistDetailsScreen, PlaylistDetailsViewModel
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 7/22-23/2025 - Deleted SongPlayer from domain layer.
 */

data class AlbumUiState (
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val album: AlbumInfo = AlbumInfo(),
    val artist: ArtistInfo = ArtistInfo(),
    val songs: List<SongInfo> = emptyList(),
    val selectSong: SongInfo = SongInfo(),
)

/**
 * ViewModel that handles the business logic and screen state of the Album Details screen
 */
@UnstableApi
@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    getAlbumDetailsV2: GetAlbumDetailsV2,
    private val songController: SongController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _albumId: String = savedStateHandle.get<String>(Screen.ARG_ALBUM_ID)!!
    private val albumId = _albumId.toLong()

    private val getAlbumDetailsData = getAlbumDetailsV2(albumId)
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val selectedSong = MutableStateFlow<SongInfo?>(null)

    private val _state = MutableStateFlow(AlbumUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<AlbumUiState>
        get() = _state

    init {
        Log.i(TAG,"albumId: $albumId")
        viewModelScope.launch {
            Log.i(TAG, "init viewModelScope launch start")
            combine(
                refreshing,
                getAlbumDetailsData,
                selectedSong,
            ) {
                refreshing,
                albumDetailsFilterResult,
                selectSong ->
                Log.i(TAG, "AlbumUiState call")
                Log.i(TAG, "albumDetailsFilterResult ID: ${albumDetailsFilterResult.album.id}")
                Log.i(TAG, "albumDetailsFilterResult songs: ${albumDetailsFilterResult.songs.size}")
                Log.i(TAG, "is SongController available: ${songController.isConnected()}")
                Log.i(TAG, "isReady?: ${!refreshing}")

                AlbumUiState(
                    isReady = !refreshing,
                    album = albumDetailsFilterResult.album,
                    artist = albumDetailsFilterResult.artist,
                    songs = albumDetailsFilterResult.songs,
                    selectSong = selectSong ?: SongInfo(),
                )
            }.catch { throwable ->
                emit(
                    AlbumUiState(
                        isReady = true,
                        errorMessage = throwable.message
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

    fun onAlbumAction(action: AlbumAction) {
        Log.i(TAG, "onAlbumAction - $action")
        when (action) {
            //is AlbumAction.AddSongToPlaylist -> onAddToPlaylist(action.song)
            //is AlbumAction.AddAlbumToPlaylist -> onAddToPlaylist(action.songs)
            is AlbumAction.QueueSong -> onQueueSong(action.song)
            is AlbumAction.QueueSongs -> onQueueSongs(action.songs)
            is AlbumAction.SongMoreOptionClicked -> onSongMoreOptionClick(action.song)
            is AlbumAction.ShuffleAlbum -> onShuffleAlbum(action.songs)
            is AlbumAction.PlayAlbum -> onPlayAlbum(action.songs)
            is AlbumAction.SongClicked -> onSongClicked(action.song)
        }
    }

    private fun onQueueSong(song: SongInfo) {
        Log.i(TAG, "onQueueSong -> ${song.title}")
        songController.addToQueue(song)
    }

    private fun onQueueSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongs -> ${songs.size}")
        songController.addToQueue(songs)
    }

    private fun onPlayAlbum(songs: List<SongInfo>) {
        Log.i(TAG, "onPlayAlbum -> ${songs.size}")
        songController.play(songs)
    }

    private fun onShuffleAlbum(songs: List<SongInfo>) {
        Log.i(TAG, "onShuffleAlbum -> ${songs.size}")
        songController.shuffle(songs)
    }

    private fun onSongClicked(song: SongInfo) {
        Log.i(TAG, "onSongClicked -> ${song.title}")
        songController.play(song)
    }

    private fun onSongMoreOptionClick(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionClick -> ${song.title}")
        selectedSong.value = song
    }
}

sealed interface AlbumAction {
    data class QueueSong(val song: SongInfo) : AlbumAction
    data class QueueSongs(val songs: List<SongInfo>) : AlbumAction
    data class PlayAlbum(val songs: List<SongInfo>) : AlbumAction
    data class ShuffleAlbum(val songs: List<SongInfo>) : AlbumAction
    data class SongClicked(val song: SongInfo) : AlbumAction
    data class SongMoreOptionClicked(val song: SongInfo) : AlbumAction
}

/**
 * ---------ORIGINAL VERSION: ViewModel that handles the business logic and screen state of the Artist details screen.
 */
/*
sealed interface AlbumUiState {
    data object Loading : AlbumUiState
    data class Ready(
        val album: AlbumInfo,
        val songs: List<SongInfo>, //PersistentList<SongInfo> = persistentListOf(),
    ) : AlbumUiState
}

/**
 * ViewModel that handles the business logic and screen state of the Album details screen.
 */
@HiltViewModel(assistedFactory = AlbumDetailsViewModel.AlbumDetailsViewModelFactory::class)
class AlbumDetailsViewModel @AssistedInject constructor(
    private val songRepo: SongRepo,
    private val songPlayer: SongPlayer,
    private val albumRepo: AlbumRepo,
    @Assisted val albumId: Long,
    //albumId is an argument needed for the selected album details to view
) : ViewModel() {

    @AssistedFactory
    interface AlbumDetailsViewModelFactory {
        fun create(albumId: Long): AlbumDetailsViewModel
    }

    val state: StateFlow<AlbumUiState> =
        combine( //want to use this to store the information needed to correctly determine the album and songs to view
            albumRepo.getAlbumWithExtraInfo(albumId),
            songRepo.getSongsAndAlbumByAlbumId(albumId)
        ) { album, songsToAlbum ->
            val songs = songsToAlbum.map { it.song.asExternalModel() }
            AlbumUiState.Ready(
                album = album.album.asExternalModel(),
                songs = songs,//toPersistentList(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AlbumUiState.Loading
        )

    fun onQueueSong(playerSong: PlayerSong) {
        songPlayer.addToQueue(playerSong)
    }

}*/
