package com.example.music.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SearchQueryFilterResult
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.GetAlbumDetails
import com.example.music.domain.usecases.GetArtistDetails
import com.example.music.domain.usecases.SearchQuery
import com.example.music.service.SongController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Search View Model"

@HiltViewModel
class SearchQueryViewModel @Inject constructor(
    private val getAlbumDetails: GetAlbumDetails,
    private val getArtistDetails: GetArtistDetails,
    private val searchQuery: SearchQuery,
    private val songController: SongController,
) : ViewModel() {

    private val _selectedSong = MutableStateFlow(SongInfo())
    val selectedSong: StateFlow<SongInfo>
        get() = _selectedSong

    private val _selectedArtist = MutableStateFlow(ArtistInfo())
    val selectedArtist: StateFlow<ArtistInfo>
        get() = _selectedArtist

    private val _selectedAlbum = MutableStateFlow(AlbumInfo())
    val selectedAlbum: StateFlow<AlbumInfo>
        get() = _selectedAlbum

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
                    return@collectLatest
                }
            }
        }
    }

    fun updateQuery(newQuery: String) {
        Log.i(TAG, "Update Query START -> $newQuery")
        _queryText.update { newQuery }

        if (newQuery.blankOrEmpty()) {
            Log.i(TAG, "Update Query: newQuery is blank/empty -> set uiState to Idle")
            _state.update { SearchUiState.Idle }
        }
    }

    private fun resetUiState() {
        Log.i(TAG, "Reset UI State START: set uiState to Idle")
        _state.update { SearchUiState.Idle }
    }

    // use to reset the queryText and update field state
    // called when tap on clear icon
    fun clearQuery() {
        Log.i(TAG, "Clear Query START: reset query to empty string")
        _queryText.update { "" }
        resetUiState()
    }

    fun sendQuery() {
        Log.i(TAG, "Send Query START -> query string: ${queryText.value}\n" +
            "set uiState to Loading")
        _state.update { SearchUiState.Loading }
        viewModelScope.launch {
            val results = searchQuery(queryText.value)
            Log.i(TAG, "Query Search Results:\n" +
                "${results.songs.size} songs\n" +
                "${results.artists.size} artists\n" +
                "${results.albums.size} albums")
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
        }
    }

    fun onMoreQuery(item: String) {
        Log.i(TAG, "onMoreQuery: ${queryText.value} -> $item")
        when (item) {
            "Songs" -> {}
            "Artists" -> {}
            "Albums" -> {}
        }
    }

    fun onMoreOptionsAction(action: MoreOptionsAction) {
        Log.i(TAG, "onSearchMoreOptionsAction - $action")
        when (action) {
            is MoreOptionsAction.PlaySong -> onPlaySong(action.song)
            is MoreOptionsAction.PlaySongNext -> onPlaySongNext(action.song)
            is MoreOptionsAction.QueueSong -> onQueueSong(action.song)

            is MoreOptionsAction.PlayArtist -> onPlayArtist(action.artist)
            is MoreOptionsAction.PlayArtistNext -> onPlayArtistNext(action.artist)
            is MoreOptionsAction.ShuffleArtist -> onShuffleArtist(action.artist)
            is MoreOptionsAction.QueueArtist -> onQueueArtist(action.artist)

            is MoreOptionsAction.PlayAlbum -> onPlayAlbum(action.album)
            is MoreOptionsAction.PlayAlbumNext -> onPlayAlbumNext(action.album)
            is MoreOptionsAction.ShuffleAlbum -> onShuffleAlbum(action.album)
            is MoreOptionsAction.QueueAlbum -> onQueueAlbum(action.album)
        }
    }

    fun onSongMoreOptionsClick(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionsClick -> ${song.title}")
        _selectedSong.update { song }
    }
    fun onArtistMoreOptionsClick(artist: ArtistInfo) {
        Log.i(TAG, "onArtistMoreOptionsClick -> ${artist.name}")
        _selectedArtist.update { artist }
    }
    fun onAlbumMoreOptionsClick(album: AlbumInfo) {
        Log.i(TAG, "onAlbumMoreOptionsClick -> ${album.title}")
        _selectedAlbum.update { album }
    }

    private fun onPlaySong(song: SongInfo) {
        Log.i(TAG, "onPlaySong -> ${song.title}")
        songController.play(song)
    }
    private fun onPlaySongNext(song: SongInfo) {
        Log.i(TAG, "onPlaySongNext -> ${song.title}")
        songController.addToQueueNext(song)
    }
    private fun onQueueSong(song: SongInfo) {
        Log.i(TAG, "onPlaySong -> ${song.title}")
        songController.addToQueue(song)
    }

    private fun onPlayAlbum(album: AlbumInfo) {
        Log.i(TAG, "onPlayAlbum -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetails(album.id).first().songs
            songController.play(songs)
        }
    }
    private fun onPlayAlbumNext(album: AlbumInfo) {
        Log.i(TAG, "onPlayAlbumNext -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetails(album.id).first().songs
            songController.addToQueueNext(songs)
        }
    }
    private fun onShuffleAlbum(album: AlbumInfo) {
        Log.i(TAG, "onShuffleAlbum -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetails(album.id).first().songs
            songController.shuffle(songs)
        }
    }
    private fun onQueueAlbum(album: AlbumInfo) {
        Log.i(TAG, "onQueueAlbum -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetails(album.id).first().songs
            songController.addToQueue(songs)
        }
    }

    private fun onPlayArtist(artist: ArtistInfo) {
        Log.i(TAG, "onPlayArtist -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetails(artist.id).first().songs
            songController.play(songs)
        }
    }
    private fun onPlayArtistNext(artist: ArtistInfo) {
        Log.i(TAG, "onPlayArtistNext -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetails(artist.id).first().songs
            songController.addToQueueNext(songs)
        }
    }
    private fun onShuffleArtist(artist: ArtistInfo) {
        Log.i(TAG, "onShuffleArtist -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetails(artist.id).first().songs
            songController.shuffle(songs)
        }
    }
    private fun onQueueArtist(artist: ArtistInfo) {
        Log.i(TAG, "onQueueArtist -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetails(artist.id).first().songs
            songController.addToQueue(songs)
        }
    }

    private fun String.blankOrEmpty() = this.isBlank() || this.isEmpty()
}

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data object Error : SearchUiState
    data object NoResults : SearchUiState
    data class SearchResultsFound(
        val results: SearchQueryFilterResult
    ) : SearchUiState
}

sealed interface MoreOptionsAction {
    data class PlaySong(val song: SongInfo) : MoreOptionsAction
    data class PlaySongNext(val song: SongInfo) : MoreOptionsAction
    data class QueueSong(val song: SongInfo) : MoreOptionsAction

    data class PlayArtist(val artist: ArtistInfo) : MoreOptionsAction
    data class PlayArtistNext(val artist: ArtistInfo) : MoreOptionsAction
    data class ShuffleArtist(val artist: ArtistInfo) : MoreOptionsAction
    data class QueueArtist(val artist: ArtistInfo) : MoreOptionsAction

    data class PlayAlbum(val album: AlbumInfo) : MoreOptionsAction
    data class PlayAlbumNext(val album: AlbumInfo) : MoreOptionsAction
    data class ShuffleAlbum(val album: AlbumInfo) : MoreOptionsAction
    data class QueueAlbum(val album: AlbumInfo) : MoreOptionsAction
}

data class SearchActions (
    val updateQuery: (String) -> Unit,
    val clearQuery: () -> Unit,
    val sendQuery: () -> Unit,
)

data class ResultActions (
    val onSongClick: (SongInfo) -> Unit,
    val onSongMoreOptionsClick: (SongInfo) -> Unit,
    val onArtistClick: (ArtistInfo) -> Unit,
    val onArtistMoreOptionsClick: (ArtistInfo) -> Unit,
    val onAlbumClick: (AlbumInfo) -> Unit,
    val onAlbumMoreOptionsClick: (AlbumInfo) -> Unit,
    val onMoreResultsClick: (String) -> Unit,
)
