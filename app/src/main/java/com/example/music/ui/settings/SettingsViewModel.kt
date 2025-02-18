package com.example.music.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.ShuffleType
import com.example.music.domain.GetTotalCountsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.example.music.util.logger
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    //need to be able to access CurrentPreferencesDataStore
    //need own set of ScreenActions that trigger like onClick
    getTotalCountsUseCase: GetTotalCountsUseCase,
) : ViewModel() {

    private val selectedShuffleType = MutableStateFlow(ShuffleType.ONCE)

    private val _state = MutableStateFlow(SettingsUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<SettingsUiState>
        get() = _state

    init{
        logger.info { "Settings View Model - viewModelScope launch start" }
        viewModelScope.launch {
            val counts = getTotalCountsUseCase()
            combine(
                refreshing,
                selectedShuffleType
            ) {
                refreshing,
                shuffle ->

                logger.info { "Shuffle type set to: ${shuffle.name}" }
                SettingsUiState(
                    isLoading = refreshing,
                    selectedShuffleType = shuffle,
                    totals = counts
                )
            }.catch { throwable ->
                emit(
                    SettingsUiState(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                )
            }.collect {
                _state.value = it
            }
        }
        refresh(force = false)
        logger.info { "Settings View Model - init end" }
    }

    fun refresh(force: Boolean = true) {
        logger.info { "Settings View Model - refresh function start" }
        logger.info { "Settings View Model - refreshing: ${refreshing.value}" }
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }

    fun onSettingsAction(action: SettingsAction) {
        when(action){
            is SettingsAction.ShuffleTypeSelected -> onShuffleTypeSelected(action.shuffleType)
            is SettingsAction.ImportPlaylist -> onImportPlaylist()
            is SettingsAction.RefreshLibrary -> onRefresh()
        }
    }

    private fun onShuffleTypeSelected(shuffleType: ShuffleType){
        selectedShuffleType.value = shuffleType
    }

    private fun onImportPlaylist(){
        //TODO: this will check a local device and search for files that describe playlists, and generate a playlist based on those files
        // could also show the list of found playlists to user, and allow them to select which ones to import
    }

    private fun onRefresh(){
        //TODO: this will check the database and compare if there are different values in the app versus in the database
        // how to check which is the source of truth?
    }

}

sealed interface SettingsAction {
    data class ShuffleTypeSelected(val shuffleType: ShuffleType) : SettingsAction
    data object ImportPlaylist : SettingsAction
    data object RefreshLibrary : SettingsAction
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedShuffleType: ShuffleType = ShuffleType.ONCE,
    val totals: List<Int> = emptyList()
)