package com.example.music.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.ShuffleType
import com.example.music.domain.usecases.GetTotalCountsUseCase
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

    private val selectedThemeMode = MutableStateFlow("System default")

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
                selectedShuffleType,
                selectedThemeMode,
            ) {
                refreshing,
                shuffle,
                theme, ->

                logger.info { "Shuffle type set to: ${shuffle.name}" }
                logger.info { "Theme mode set to: ${theme}" }
                SettingsUiState(
                    isLoading = refreshing,
                    selectedShuffleType = shuffle,
                    selectedThemeMode = theme,
                    totals = counts,
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
            is SettingsAction.ThemeModeSelected -> onThemeModeSelected(action.themeMode)
            is SettingsAction.ImportPlaylist -> onImportPlaylist()
            is SettingsAction.RefreshLibrary -> onRefresh()
        }
    }

    private fun onShuffleTypeSelected(shuffleType: ShuffleType){
        logger.info{ "Shuffle Type selected" }
        selectedShuffleType.value = shuffleType
    }

    private fun onThemeModeSelected(themeMode: String){
        logger.info{ "Theme Mode selected" }
        selectedThemeMode.value = themeMode
    }

    private fun onImportPlaylist(){
        logger.info{ "Import Playlist clicked" }
        //TODO: this will check a local device and search for files that describe playlists, and generate a playlist based on those files
        // could also show the list of found playlists to user, and allow them to select which ones to import
    }

    private fun onRefresh(){
        logger.info{ "Refresh Library clicked" }
        //TODO: this will check the database and compare if there are different values in the app versus in the database
        // how to check which is the source of truth?
    }

}

sealed interface SettingsAction {
    data class ShuffleTypeSelected(val shuffleType: ShuffleType) : SettingsAction
    data class ThemeModeSelected(val themeMode: String) : SettingsAction
    data object ImportPlaylist : SettingsAction
    data object RefreshLibrary : SettingsAction
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedShuffleType: ShuffleType = ShuffleType.ONCE,
    val selectedThemeMode: String = "System default",
    val totals: List<Int> = emptyList()
)