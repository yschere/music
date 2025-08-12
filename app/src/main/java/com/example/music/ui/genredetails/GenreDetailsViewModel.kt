package com.example.music.ui.genredetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.SongInfo
//import com.example.music.domain.player.SongPlayer
import com.example.music.domain.usecases.GetGenreDetailsV2
import com.example.music.ui.Screen
import com.example.music.util.logger
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
    //val albums: /*Persistent*/List<AlbumInfo> = emptyList(),
    val songs: /*Persistent*/List<SongInfo> = emptyList(),
)

@HiltViewModel
class GenreDetailsViewModel @Inject constructor(
    getGenreDetailsV2: GetGenreDetailsV2,
    //private val songPlayer: SongPlayer,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _genreId: String = savedStateHandle.get<String>(Screen.ARG_GENRE_ID)!!
    private val genreId = _genreId.toLong()

    //private val getGenreDetailsData = getGenreDetailsUseCase(genreId)
    private val getGenreDetailsData = getGenreDetailsV2(genreId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val _state = MutableStateFlow(GenreUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<GenreUiState>
        get() = _state

    init {
        logger.info { "$TAG - genreId: $genreId" }
        viewModelScope.launch {
            logger.info { "$TAG - init viewModelScope launch start" }
            combine(
                refreshing,
                getGenreDetailsData,
            ) {
                refreshing,
                genreDetailsFilterResult, ->
                logger.info { "Genre Details View Model - GenreUiState call" }
                logger.info { "Genre Details View Model - genreDetailsFilterResult ID: ${genreDetailsFilterResult.genre.id}" }
                //logger.info { "Genre Details View Model - genreDetailsFilterResult albums: ${genreDetailsFilterResult.albums.size}" }
                logger.info { "Genre Details View Model - genreDetailsFilterResult songs: ${genreDetailsFilterResult.songs.size}" }
                logger.info { "Genre Details View Model - isReady?: ${!refreshing}" }

                GenreUiState(
                    isReady = !refreshing,
                    genre = genreDetailsFilterResult.genre,
                    //albums = genreDetailsFilterResult.albums,
                    songs = genreDetailsFilterResult.songs,
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
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }
}
