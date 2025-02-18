package com.example.music.ui.composerdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.GetComposerDetailsUseCase
import com.example.music.model.ComposerInfo
import com.example.music.model.SongInfo
import com.example.music.player.SongPlayer
import com.example.music.player.model.PlayerSong
import com.example.music.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.music.util.logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ---- TEST VERSION USING SAVEDSTATEHANDLE TO REPLICATE PLAYER SCREEN NAVIGATION
 * As of 2/10/2025, this version is in remote branch and working on
 * PlaylistDetailsScreen, PlaylistDetailsViewModel
 */

data class ComposerUiState (
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val composer: ComposerInfo = ComposerInfo(),
    val songs: List<SongInfo> = emptyList(),
    val pSongs: List<PlayerSong> = emptyList(),
)

@HiltViewModel
class ComposerDetailsViewModel @Inject constructor(
    getComposerDetailsUseCase: GetComposerDetailsUseCase,
    private val songPlayer: SongPlayer,
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
        logger.info { "Composer Details View Model - composerId: $composerId" }
        viewModelScope.launch {
            logger.info { "Composer Details View Model - init viewModelScope launch start" }
            combine(
                refreshing,
                getComposerDetailsData,
            ) {
                refreshing,
                composerDetailsFilterResult, ->
                logger.info { "Composer Details View Model - ComposerUiState call" }
                logger.info { "Composer Details View Model - composerDetailsFilterResult ID: ${composerDetailsFilterResult.composer.id}" }
                logger.info { "Composer Details View Model - composerDetailsFilterResult songs: ${composerDetailsFilterResult.songs.size}" }
                logger.info { "Composer Details View Model - composerDetailsFilterResult pSongs: ${composerDetailsFilterResult.pSongs.size}" }
                logger.info { "Composer Details View Model - isReady?: ${!refreshing}" }

                ComposerUiState(
                    isReady = !refreshing,
                    composer = composerDetailsFilterResult.composer,
                    songs = composerDetailsFilterResult.songs,
                    pSongs = composerDetailsFilterResult.pSongs
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
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }
}
