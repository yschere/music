package com.example.music.ui.genredetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.GetGenreDetailsV2
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

private const val TAG = "Genre Details View Model"

/** Changelog:
 * ---- TEST VERSION USING SAVEDSTATEHANDLE TO REPLICATE PLAYER SCREEN NAVIGATION
 * As of 2/10/2025, this version is in remote branch and working on
 * PlaylistDetailsScreen, PlaylistDetailsViewModel
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

data class GenreUiState (
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val genre: GenreInfo = GenreInfo(),
    val songs: /*Persistent*/List<SongInfo> = emptyList(),
    val selectSong: SongInfo = SongInfo()
)

/**
 * ViewModel that handles the business logic and screen state of the Genre Details screen
 */
@HiltViewModel
class GenreDetailsViewModel @Inject constructor(
    getGenreDetailsV2: GetGenreDetailsV2,
    private val songController: SongController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _genreId: String = savedStateHandle.get<String>(Screen.ARG_GENRE_ID)!!
    private val genreId = _genreId.toLong()

    //private val getGenreDetailsData = getGenreDetailsUseCase(genreId)
    private val getGenreDetailsData = getGenreDetailsV2(genreId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val selectedSong = MutableStateFlow<SongInfo?>(null)

    private val _state = MutableStateFlow(GenreUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<GenreUiState>
        get() = _state

    init {
        Log.i(TAG, "genreId: $genreId")
        viewModelScope.launch {
            Log.i(TAG, "init viewModelScope launch start")
            combine(
                refreshing,
                getGenreDetailsData,
                selectedSong,
            ) {
                refreshing,
                genreDetailsFilterResult,
                selectSong ->
                Log.i(TAG, "GenreUiState call")
                Log.i(TAG, "genreDetailsFilterResult ID: ${genreDetailsFilterResult.genre.id}")
                Log.i(TAG, "genreDetailsFilterResult songs: ${genreDetailsFilterResult.songs.size}")
                Log.i(TAG, "is SongController available: ${songController.isConnected()}")
                Log.i(TAG, "isReady?: ${!refreshing}")

                GenreUiState(
                    isReady = !refreshing,
                    genre = genreDetailsFilterResult.genre,
                    songs = genreDetailsFilterResult.songs,
                    selectSong = selectSong ?: SongInfo(),
                )
            }.catch { throwable ->
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

    fun onGenreAction(action: GenreAction) {
        Log.i(TAG, "onGenreAction - $action")
        when (action) {
            is GenreAction.QueueSong -> onQueueSong(action.song)
            is GenreAction.QueueSongs -> onQueueSongs(action.songs)
            is GenreAction.PlaySong -> onPlaySong(action.song)
            is GenreAction.PlaySongs -> onPlaySongs(action.songs)
            is GenreAction.ShuffleSongs -> onShuffleSongs(action.songs)
            is GenreAction.SongMoreOptionClicked -> onSongMoreOptionClicked(action.song)
        }
    }

    private fun onPlaySong(song: SongInfo) {
        Log.i(TAG, "onPlaySong - ${song.title}")
        songController.play(song)
    }

    private fun onPlaySongs(songs: List<SongInfo>) {
        Log.i(TAG, "onPlaySongs - ${songs.size}")
        songController.play(songs)
    }

    private fun onQueueSong(song: SongInfo) {
        Log.i(TAG, "onQueueSong - ${song.title}")
        songController.addToQueue(song)
    }

    private fun onQueueSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongs - ${songs.size}")
        songController.addToQueue(songs)
    }

    private fun onShuffleSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onShuffleSongs - ${songs.size}")
        songController.shuffle(songs)
    }

    private fun onSongMoreOptionClicked(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionClick - ${song.title}")
        selectedSong.value = song
    }
    
}

sealed interface GenreAction {
    data class PlaySong(val song: SongInfo) : GenreAction
    data class PlaySongs(val songs: List<SongInfo>) : GenreAction
    data class QueueSong(val song: SongInfo) : GenreAction
    data class QueueSongs(val songs: List<SongInfo>) : GenreAction
    data class ShuffleSongs(val songs: List<SongInfo>) : GenreAction
    data class SongMoreOptionClicked(val song: SongInfo) : GenreAction
}