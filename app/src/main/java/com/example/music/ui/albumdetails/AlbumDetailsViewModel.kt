package com.example.music.ui.albumdetails

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.GetAlbumDetails
import com.example.music.domain.usecases.GetSongData
import com.example.music.service.SongController
import com.example.music.ui.Screen
import com.example.music.ui.player.MiniPlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Album Details View Model"

data class AlbumUiState (
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val album: AlbumInfo = AlbumInfo(),
    val artist: ArtistInfo = ArtistInfo(),
    val songs: List<SongInfo> = emptyList(),
    val selectSong: SongInfo = SongInfo(),
    val selectSortOrder: Pair<String, Boolean> = Pair("Track Number",true),
)

val AlbumSongSortOptions = listOf(
    "Track Number",
    "Title",
    "Date Added",
    "Date Modified",
    "Duration"
)

/**
 * ViewModel that handles the business logic and screen state of the Album Details screen
 */
@UnstableApi
@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    getAlbumDetails: GetAlbumDetails,
    savedStateHandle: SavedStateHandle,

    private val getSongData: GetSongData,
    private val songController: SongController,
) : ViewModel(), MiniPlayerState {

    private val _albumId: String = savedStateHandle.get<String>(Screen.ARG_ALBUM_ID)!!
    private val albumId = _albumId.toLong()

    private val getAlbumDetailsData = getAlbumDetails(albumId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val selectedSong = MutableStateFlow(SongInfo())

    // sets sort default
    private var selectedSortOrder = MutableStateFlow(Pair("Track Number", true))

    // bottom player section
    override var currentSong by mutableStateOf(SongInfo())
    private var _isActive by mutableStateOf(songController.isActive)
    var isActive
        get() = _isActive
        set(value) {
            _isActive = songController.isActive
            refresh(value)
        }

    override val player: Player?
        get() = songController.player
    private var _isPlaying by mutableStateOf(songController.isPlaying)
    override var isPlaying
        get() = _isPlaying
        set(value) {
            if (value) songController.play(true)
            else songController.pause()
        }

    private val _state = MutableStateFlow(AlbumUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<AlbumUiState>
        get() = _state

    init {
        Log.i(TAG,"init START --- albumId: $albumId")
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch START")

            Log.i(TAG, "SongController status:\n" +
                "isActive?: $isActive\n" +
                "player?: ${player?.playbackState}")

            combine(
                refreshing,
                getAlbumDetailsData,
                selectedSong,
                selectedSortOrder,
            ) {
                refreshing,
                albumDetailsFilterResult,
                selectSong,
                selectSort, ->
                Log.i(TAG, "AlbumUiState combine START\n" +
                    "albumDetailsFilterResult ID: ${albumDetailsFilterResult.album.id}\n" +
                    "albumDetailsFilterResult songs: ${albumDetailsFilterResult.songs.size}\n" +
                    "albumDetails sort order: ${selectSort.first} + ${selectSort.second}\n" +
                    "is SongController available: ${songController.isConnected()}\n" +
                    "isReady?: ${!refreshing}")

                getSongControllerState()
                val sortedSongs = when(selectSort.first) {
                    "Track Number" -> {
                        if (selectSort.second) albumDetailsFilterResult.songs
                        else albumDetailsFilterResult.songs.reversed()
                    }
                    "Title" -> {
                        if (selectSort.second) albumDetailsFilterResult.songs
                            .sortedBy { it.title.lowercase() }
                        else albumDetailsFilterResult.songs
                            .sortedByDescending { it.title.lowercase() }
                    }
                    "Date Added" -> {
                        if (selectSort.second) albumDetailsFilterResult.songs
                            .sortedBy { it.dateAdded }
                        else albumDetailsFilterResult.songs
                            .sortedByDescending { it.dateAdded }
                    }
                    "Date Modified" -> {
                        if (selectSort.second) albumDetailsFilterResult.songs
                            .sortedBy { it.dateModified }
                        else albumDetailsFilterResult.songs
                            .sortedByDescending { it.dateModified }
                    }
                    "Duration" -> {
                        if (selectSort.second) albumDetailsFilterResult.songs
                            .sortedBy { it.duration }
                        else albumDetailsFilterResult.songs
                            .sortedByDescending { it.duration }
                    }
                    else -> { albumDetailsFilterResult.songs }
                }

                AlbumUiState(
                    isReady = !refreshing,
                    album = albumDetailsFilterResult.album,
                    artist = albumDetailsFilterResult.artist,
                    songs = sortedSongs,
                    selectSong = selectSong,
                    selectSortOrder = selectSort,
                )
            }.catch { throwable ->
                Log.i(TAG, "Error Caught: ${throwable.message}")
                emit(
                    AlbumUiState(
                        isReady = true,
                        errorMessage = throwable.message
                    )
                )
            }.collect{ _state.value = it }
        }

        viewModelScope.launch {
            songController.events.collect {
                Log.d(TAG, "get SongController Player Event(s)")

                // if events is empty, take these actions to generate initial MiniPlayer values
                if (it == null) {
                    Log.d(TAG, "init: running start up events to initialize MiniPlayer")
                    getSongControllerState()
                    onPlayerEvent(event = Player.EVENT_IS_LOADING_CHANGED)
                    onPlayerEvent(event = Player.EVENT_MEDIA_ITEM_TRANSITION)
                    onPlayerEvent(event = Player.EVENT_IS_PLAYING_CHANGED)
                    return@collect
                }
                // else, repeat the onPlayerEvent call to enact each event
                repeat(it.size()) { index ->
                    onPlayerEvent(it.get(index))
                }
            }
        }

        refresh(force = false)
        Log.i(TAG, "init END")
    }

    private fun onPlayerEvent(event: Int) {
        when (event) {
            // Event for checking if the SongController is loaded and ready to read
            Player.EVENT_IS_LOADING_CHANGED -> {
                val loaded = songController.loaded
                if (loaded.equals(true)) {
                    refreshing.value = false
                    isActive = songController.isActive
                }
                Log.d(TAG, "isLoading changed:\n" +
                    "isPlaying set to $isPlaying\n" +
                    "isActive set to $isActive")
            }

            // Event for checking if SongController is playing
            Player.EVENT_IS_PLAYING_CHANGED -> {
                _isPlaying = songController.isPlaying
                isActive = songController.isActive
                Log.d(TAG, "isPlaying changed:\n" +
                    "isPlaying set to $isPlaying\n" +
                    "isActive set to $isActive")
            }

            // Event for checking if the current media item has changed
            Player.EVENT_MEDIA_ITEM_TRANSITION -> {
                val mediaItem = songController.currentSong
                viewModelScope.launch {
                    var id = mediaItem?.mediaId
                    while (id == null) {
                        delay(100)
                        id = mediaItem?.mediaId
                    }
                    currentSong = getSongData(id.toLong())
                    Log.d(TAG, "Current Song set to ${currentSong.title}")
                    songController.logTrackNumber()
                }
            }

            Player.EVENT_TRACKS_CHANGED -> {
                songController.logTrackNumber()
            }
        }
    }

    private suspend fun getSongControllerState() {
        val id = songController.currentSong?.mediaId
        if (id != null) {
            currentSong = getSongData(id.toLong())
        }
        _isPlaying = songController.isPlaying
        isActive = songController.isActive
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

    fun onPlay() {
        Log.i(TAG,"Hit play btn")
        songController.play(true)
        _isPlaying = true
    }

    fun onPause() {
        Log.i(TAG, "Hit pause btn")
        songController.pause()
        _isPlaying = false
    }

    fun onAlbumAction(action: AlbumAction) {
        Log.i(TAG, "onAlbumAction - $action")
        when (action) {
            is AlbumAction.SongMoreOptionsClicked -> onSongMoreOptionsClick(action.song)
            is AlbumAction.SongSortUpdate -> onSongSortUpdate(action.newSort)

            is AlbumAction.PlaySong -> onPlaySong(action.song)
            is AlbumAction.PlaySongNext -> onPlaySongNext(action.song)
            is AlbumAction.QueueSong -> onQueueSong(action.song)

            is AlbumAction.PlaySongs -> onPlaySongs(action.songs)
            is AlbumAction.PlaySongsNext -> onPlaySongsNext(action.songs)
            is AlbumAction.ShuffleSongs -> onShuffleSongs(action.songs)
            is AlbumAction.QueueSongs -> onQueueSongs(action.songs)
        }
    }

    private fun onSongMoreOptionsClick(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionsClick -> ${song.title}")
        selectedSong.value = song
    }
    private fun onSongSortUpdate(newSort: Pair<String, Boolean>) {
        Log.i(TAG, "onSongSortUpdate -> ${newSort.first} + ${newSort.second}")
        selectedSortOrder.value = newSort
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
        Log.i(TAG, "onQueueSong -> ${song.title}")
        songController.addToQueue(song)
    }

    private fun onPlaySongs(songs: List<SongInfo>) {
        Log.i(TAG, "onPlaySongs -> ${songs.size}")
        songController.play(songs)
    }
    private fun onPlaySongsNext(songs: List<SongInfo>) {
        Log.i(TAG, "onPlaySongsNext - ${songs.size}")
        songController.addToQueueNext(songs)
    }
    private fun onShuffleSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onShuffleSongs -> ${songs.size}")
        songController.shuffle(songs)
    }
    private fun onQueueSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongs -> ${songs.size}")
        songController.addToQueue(songs)
    }
}

sealed interface AlbumAction {
    data class SongMoreOptionsClicked(val song: SongInfo) : AlbumAction
    data class SongSortUpdate(val newSort: Pair<String, Boolean>) : AlbumAction

    data class PlaySong(val song: SongInfo) : AlbumAction
    data class PlaySongNext(val song: SongInfo) : AlbumAction
    data class QueueSong(val song: SongInfo) : AlbumAction

    data class PlaySongs(val songs: List<SongInfo>) : AlbumAction
    data class PlaySongsNext(val songs: List<SongInfo>) : AlbumAction
    data class ShuffleSongs(val songs: List<SongInfo>) : AlbumAction
    data class QueueSongs(val songs: List<SongInfo>) : AlbumAction
}
