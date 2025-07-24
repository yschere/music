package com.example.music.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.example.music.util.logger
import kotlinx.coroutines.launch

/** Changelog:
 *
 * 7/22-23/2025 - Deleted SongPlayer from domain layer.
 */

data class QueueScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
//    val queueSongs: List<SongInfo> = emptyList(),
//    val songPlayerState: SongPlayerState = SongPlayerState(),
//    val songPlayer: SongPlayer,
)

@HiltViewModel
class QueueViewModel @Inject constructor(
    //not sure what domain use cases needed here
    //likely need to get queueList from Player's songPlayer
) : ViewModel() {

//    private val playerState = MutableStateFlow(songPlayerState)

    // Holds our view state which the UI collects via [state]
//    private val _state = MutableStateFlow(QueueScreenUiState())

    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)

//    val state: StateFlow<QueueScreenUiState>
//        get() = _state

    init{
        logger.info { "Queue View Model - viewModelScope launch start" }
        viewModelScope.launch {

            /*
            combine(
                refreshing,
            ) { refreshing, ->

                QueueUiState(
                    isLoading = refreshing,
                )
            }.catch { throwable ->
                emit(
                    QueueUiState(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                )
            }.collect {
                _state.value = it
            } */
        }

        refresh(force = false)
        logger.info { "Queue View Model - init end" }
    }

    fun refresh(force: Boolean = true) {
        logger.info { "Queue View Model - refresh function start" }
        logger.info { "Queue View Model - refreshing: ${refreshing.value}" }
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
            }

            refreshing.value = false
        }
    }

    //functions for actions in QueueScreen or onQueueActions go here
}

sealed interface QueueAction {
    //data classes to describe the queueActions go here
}