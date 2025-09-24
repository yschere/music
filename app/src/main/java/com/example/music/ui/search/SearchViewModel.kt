package com.example.music.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
//import com.example.music.domain.player.SongPlayer
import com.example.music.domain.model.SearchQueryFilterV2
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.SearchQueryV2
import com.example.music.service.SongController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Search View Model"

@HiltViewModel
class SearchQueryViewModel @Inject constructor(
    private val searchQueryV2: SearchQueryV2,
    private val songController: SongController,
) : ViewModel() {

    private val _searchFieldState: MutableStateFlow<SearchFieldState> =
        MutableStateFlow(SearchFieldState.Idle)
    val searchFieldState: StateFlow<SearchFieldState>
        get() = _searchFieldState

    private val _state: MutableStateFlow<SearchUiState> =
        MutableStateFlow(SearchUiState.Idle)
    val state: StateFlow<SearchUiState>
        get() = _state

    private val _queryText: MutableStateFlow<String> =
        MutableStateFlow("")
    val queryText: StateFlow<String>
        get() = _queryText

    init {
        Log.i(TAG, "init START --- query string: ${queryText.value}")
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch START")
            queryText.collectLatest { query ->
                if (query.blankOrEmpty()) {
                    _state.update { SearchUiState.Idle }
                    _searchFieldState.update { SearchFieldState.Idle }
                    return@collectLatest
                }
            }
        }
    }

    fun updateQuery(newQuery: String) {
        _queryText.update { newQuery }
        changeFieldState() //want this to check if field should swap inputActive and emptyActive

        //should this also change UI state?
        // well, if I want the query text to be at the ready
        // for doing the search query, then yeah it would be reset to idle

        if (newQuery.blankOrEmpty() && searchFieldState.value == SearchFieldState.EmptyActive) {
            _state.update { SearchUiState.Idle }
        }
    }

    // update field state depending on query state
    fun changeFieldState() {
        if (queryText.value.blankOrEmpty().not())
            _searchFieldState.update { SearchFieldState.WithInputActive }
        else
            _searchFieldState.update { SearchFieldState.EmptyActive }
    }

    private fun resetFieldState() {
        _searchFieldState.update { SearchFieldState.Idle }
    }

    // call when user tap on chevron icon
    // want to put view state on initial, beginning state
    // and put field state on initial state
    // NOT THE SAME AS JUST SETTING UI STATE TO IDLE FOR DOING NEXT SEARCH QUERY
    // this is full screen reset
    fun resetUiState() {
        _state.update { SearchUiState.Idle }
        _queryText.update { "" }
        resetFieldState()
    }

    // use to reset the queryText and update field state
    // called when tap on clear icon
    fun clearQuery() {
        _state.update { SearchUiState.Idle }
        _queryText.update { "" }
        _searchFieldState.update { SearchFieldState.EmptyActive }
    }

    fun sendQuery() {
        Log.i(TAG, "query string: ${queryText.value}")
        _state.update { SearchUiState.Loading }
        viewModelScope.launch {
            val results = searchQueryV2(queryText.value)
            Log.i(TAG, "search results: \n${results.songs.size} songs \n${results.artists.size} artists \n${results.albums.size} albums")
            if ( results.songs.isEmpty() &&
                results.artists.isEmpty() &&
                results.albums.isEmpty()
            ) {
                _state.update { SearchUiState.NoResults }
                Log.i(TAG, "ui state updated to: ${state.value}")
            }
            else {
                _state.update { SearchUiState.SearchResultsFound(results = results) }
                Log.i(TAG, "ui state updated to: ${state.value}")
            }
            resetFieldState()
        }
    }

    fun onPlaySong(song: SongInfo) {
        Log.i(TAG, "onPlaySong -> ${song.title}")
        songController.play(song)
    }

    private fun String.blankOrEmpty() = this.isBlank() || this.isEmpty()
}

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data object Error : SearchUiState
    data object NoResults : SearchUiState
    data class SearchResultsFound(
        val results: SearchQueryFilterV2
    ) : SearchUiState
}

sealed interface SearchFieldState {
    data object Idle : SearchFieldState
    data object EmptyActive : SearchFieldState
    data object WithInputActive : SearchFieldState
}

data class SearchActions (
    val updateQuery: (String) -> Unit, // onSearchInputChanged
    val changeFieldState: () -> Unit, // onSearchFieldClicked
    val resetUiState: () -> Unit, // onChevronClicked
    val clearQuery: () -> Unit, // onClearInputClicked
    val sendQuery: () -> Unit, // onSendQuery
)

data class ResultActions (
    val onSongClicked: (SongInfo) -> Unit,
    //val onSongMoreOptionsClicked: () -> Unit,
    val onArtistClicked: (ArtistInfo) -> Unit,
    //val onArtistMoreOptionsClicked: () -> Unit,
    val onAlbumClicked: (AlbumInfo) -> Unit,
    //val onAlbumMoreOptionsClicked: () -> Unit,
)

// search screen states:
//// want this to be affected by field actions?
    // like on init screen load, screen state = idle, screen field to idle
    // idle -> loading on sendQuery
        // set field state to idle
    // loading -> error/noResults/searchResultsFound on sendQuery result
        // error if query failed
        // noResults if query return with nothing
        // searchResultsFound if query return with something
            // results = query result
// want to keep screen open to use even after initial search so that user can keep trying search


// three field states:
// idle (unfocused),
    // initialize as this
    // when else should this be set?
    // set to this after sendQuery? because user is not focused on field
// emptyActive (focused but query empty/blank),
    // set to this on init user press
    // set to this when query set to empty (either query is deleted by user keyboard, or on chevron click)
// withInputActive (focused and query not empty/blank)
    // set to this when query is not empty and user is focused on field
