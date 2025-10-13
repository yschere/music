package com.example.music.ui.playlistdetails

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.ui.Screen
import com.example.music.data.util.combine
import com.example.music.domain.usecases.GetPlaylistDetails
import com.example.music.domain.usecases.GetSongData
import com.example.music.service.SongController
import com.example.music.ui.player.MiniPlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Playlist Details View Model"

data class PlaylistUiState(
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val playlist: PlaylistInfo = PlaylistInfo(),
    val songs: List<SongInfo> = emptyList(),
    val selectSong: SongInfo = SongInfo(),
    val selectSortOrder: Pair<String,Boolean> = Pair("Track Number", true)
)

val PlaylistSongSortOptions = listOf(
    "Track Number",
    "Title",
    "Artist",
    "Album",
    "Date Added",
    "Date Modified",
    "Duration"
)

/**
 * ViewModel that handles the business logic and screen state of the Playlist Details screen
 */
@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    getPlaylistDetails: GetPlaylistDetails,
    savedStateHandle: SavedStateHandle,

    private val getSongData: GetSongData,
    private val songController: SongController,
) : ViewModel(), MiniPlayerState {

    private val _playlistId: String = savedStateHandle.get<String>(Screen.ARG_PLAYLIST_ID)!!
    private val playlistId = _playlistId.toLong()

    private val getPlaylistDetailsData = getPlaylistDetails(playlistId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    /* ---- Initial version that uses playlistRepo directly to retrieve Flow data for Playlist Details
    val playlist = playlistRepo.getPlaylistWithExtraInfo(playlistId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    // original version for getting Song objects
    val songs = playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())*/

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

    private val _state = MutableStateFlow(PlaylistUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<PlaylistUiState>
        get() = _state

    init {
        Log.i(TAG, "init START --- playlistID: $playlistId")
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch START")

            Log.i(TAG, "SongController status:\n" +
                "isActive?: $isActive\n" +
                "player?: ${player?.playbackState}")

            combine(
                refreshing,
                getPlaylistDetailsData,
                selectedSong,
                selectedSortOrder,
            ) {
                refreshing,
                playlistDetailsFilterResult,
                selectSong,
                selectSort, ->
                Log.i(TAG, "PlaylistUiState combine START\n" +
                    "playlistDetailsFilterResult ID: ${playlistDetailsFilterResult.playlist.id}\n" +
                    "playlistDetailsFilterResult songs: ${playlistDetailsFilterResult.songs.size}\n" +
                    "playlistDetails sort order: ${selectSort.first} + ${selectSort.second}\n" +
                    "is SongController available: ${songController.isConnected()}\n" +
                    "isReady?: ${!refreshing}")

                getSongControllerState()
                val sortedSongs = when(selectSort.first) {
                    "Track Number" -> {
                        if (selectSort.second) playlistDetailsFilterResult.songs
                        else playlistDetailsFilterResult.songs.reversed()
                    }
                    "Title" -> {
                        if (selectSort.second) playlistDetailsFilterResult.songs
                            .sortedBy { it.title.lowercase() }
                        else playlistDetailsFilterResult.songs
                            .sortedByDescending { it.title.lowercase() }
                    }
                    "Artist" -> {
                        if (selectSort.second) playlistDetailsFilterResult.songs
                            .sortedWith(
                                compareBy<SongInfo> { it.artistName.lowercase() }
                                    .thenBy { it.title.lowercase() }
                            )
                        else playlistDetailsFilterResult.songs
                            .sortedWith(
                                compareByDescending<SongInfo> { it.artistName.lowercase() }
                                    .thenByDescending { it.title.lowercase() }
                            )
                    }
                    "Album" -> {
                        if (selectSort.second) playlistDetailsFilterResult.songs
                            .sortedWith(
                                compareBy<SongInfo> { it.albumTitle.lowercase() }
                                    .thenBy { it.trackNumber }
                            )
                        else playlistDetailsFilterResult.songs
                            .sortedWith(
                                compareByDescending<SongInfo> { it.albumTitle.lowercase() }
                                    .thenByDescending { it.trackNumber }
                            )
                    }
                    "Date Added" -> {
                        if (selectSort.second) playlistDetailsFilterResult.songs
                            .sortedBy { it.dateAdded }
                        else playlistDetailsFilterResult.songs
                            .sortedByDescending { it.dateAdded }
                    }
                    "Date Modified" -> {
                        if (selectSort.second) playlistDetailsFilterResult.songs
                            .sortedBy { it.dateModified }
                        else playlistDetailsFilterResult.songs
                            .sortedByDescending { it.dateModified }
                    }
                    "Duration" -> {
                        if (selectSort.second) playlistDetailsFilterResult.songs
                            .sortedBy { it.duration }
                        else playlistDetailsFilterResult.songs
                            .sortedByDescending { it.duration }
                    }
                    else -> { playlistDetailsFilterResult.songs }
                }

                PlaylistUiState(
                    isReady = !refreshing,
                    playlist = playlistDetailsFilterResult.playlist,
                    songs = sortedSongs,
                    selectSong = selectSong,
                    selectSortOrder = selectSort,
                )
            }
            .catch { throwable ->
                Log.i(TAG, "Error Caught: ${throwable.message}")
                emit(
                    PlaylistUiState(
                        isReady = true,
                        errorMessage = throwable.message,
                    )
                )
            }.collect{ _state.value = it }
        }

        viewModelScope.launch {
            songController.events.collect {
                Log.d(TAG, "get SongController Player Event(s)")

                // if events is empty, take these actions to generate the needed values for populating MiniPlayer
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

    fun onPlaylistAction(action: PlaylistAction) {
        Log.i(TAG, "onPlaylistAction - $action")
        when (action) {
            is PlaylistAction.SongMoreOptionsClicked -> onSongMoreOptionsClick(action.song)
            is PlaylistAction.SongSortUpdate -> onSongSortUpdate(action.newSort)

            is PlaylistAction.PlaySong -> onPlaySong(action.song)
            is PlaylistAction.PlaySongNext -> onPlaySongNext(action.song)
            is PlaylistAction.QueueSong -> onQueueSong(action.song)

            is PlaylistAction.PlaySongs -> onPlaySongs(action.songs)
            is PlaylistAction.PlaySongsNext -> onPlaySongsNext(action.songs)
            is PlaylistAction.ShuffleSongs -> onShuffleSongs(action.songs)
            is PlaylistAction.QueueSongs -> onQueueSongs(action.songs)
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
        Log.i(TAG, "onPlaySongsNext -> ${songs.size}")
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

sealed interface PlaylistAction {
    data class SongMoreOptionsClicked(val song: SongInfo) : PlaylistAction
    data class SongSortUpdate(val newSort: Pair<String, Boolean>) : PlaylistAction

    data class PlaySong(val song: SongInfo) : PlaylistAction
    data class PlaySongNext(val song: SongInfo) : PlaylistAction
    data class QueueSong(val song: SongInfo) : PlaylistAction

    data class PlaySongs(val songs: List<SongInfo>) : PlaylistAction
    data class PlaySongsNext(val songs: List<SongInfo>) : PlaylistAction
    data class ShuffleSongs(val songs: List<SongInfo>) : PlaylistAction
    data class QueueSongs(val songs: List<SongInfo>) : PlaylistAction
}
