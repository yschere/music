package com.example.music.ui.composerdetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.usecases.GetComposerDetailsUseCase
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.SongInfo
//import com.example.music.domain.player.SongPlayer
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

private const val TAG = "Composer Details View Model"

/** ---- TEST VERSION USING SAVEDSTATEHANDLE TO REPLICATE PLAYER SCREEN NAVIGATION
 * As of 2/10/2025, this version is in remote branch and working on
 * PlaylistDetailsScreen, PlaylistDetailsViewModel
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

data class ComposerUiState (
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val composer: ComposerInfo = ComposerInfo(),
    val songs: List<SongInfo> = emptyList(),
)

@HiltViewModel
class ComposerDetailsViewModel @Inject constructor(
    getComposerDetailsUseCase: GetComposerDetailsUseCase,
    //private val songPlayer: SongPlayer,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _composerId: String = savedStateHandle.get<String>(Screen.ARG_COMPOSER_ID)!!
    private val composerId = _composerId.toLong()

    private val getComposerDetailsData = getComposerDetailsUseCase(composerId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val _state = MutableStateFlow(ComposerUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<ComposerUiState>
        get() = _state

    init {
        Log.i(TAG, "composerId: $composerId")
        viewModelScope.launch {
            Log.i(TAG, "init viewModelScope launch start")
            combine(
                refreshing,
                getComposerDetailsData,
            ) {
                refreshing,
                composerDetailsFilterResult, ->
                Log.i(TAG, "ComposerUiState call")
                Log.i(TAG, "composerDetailsFilterResult ID: ${composerDetailsFilterResult.composer.id}")
                Log.i(TAG, "composerDetailsFilterResult songs: ${composerDetailsFilterResult.songs.size}")
                Log.i(TAG, "isReady?: ${!refreshing}")

                ComposerUiState(
                    isReady = !refreshing,
                    composer = composerDetailsFilterResult.composer,
                    songs = composerDetailsFilterResult.songs,
                )
            }.catch { throwable ->
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
}
