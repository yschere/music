package com.example.music.ui.home

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.music.data.util.FLAG
import com.example.music.data.util.combine
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.FeaturedLibraryAlbums
import com.example.music.domain.usecases.FeaturedLibraryItems
import com.example.music.domain.usecases.GetAlbumDetails
import com.example.music.domain.usecases.GetPlaylistDetails
import com.example.music.domain.usecases.GetSongData
import com.example.music.domain.usecases.GetTotalCounts
import com.example.music.service.SongController
import com.example.music.ui.player.MiniPlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Home View Model"

@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    //val featuredAlbums: List<AlbumInfo> = emptyList(),
    val featuredPlaylists: List<PlaylistInfo> = emptyList(),
    val featuredSongs: List<SongInfo> = emptyList(),
    val totals: List<Int> = emptyList(),
    //val selectAlbum: AlbumInfo = AlbumInfo(),
    val selectPlaylist: PlaylistInfo = PlaylistInfo(),
    val selectSong: SongInfo = SongInfo(),
)

/**
 * ViewModel that handles the business logic and screen state of the Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    //featuredLibraryAlbums: FeaturedLibraryAlbums,
    featuredLibraryItems: FeaturedLibraryItems,

    private val getAlbumDetails: GetAlbumDetails,
    private val getPlaylistDetails: GetPlaylistDetails,
    private val getSongData: GetSongData,
    private val getTotalCounts: GetTotalCounts,
    private val songController: SongController
) : ViewModel(), MiniPlayerState {

    // test version for using MediaStore, uses Album instead of playlist for now
    //private val featuredAlbumsData = featuredLibraryAlbums()
        //.shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    private val featuredItemsData = featuredLibraryItems()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    // Holds the song, album to show in more options modal
    private val selectedAlbum = MutableStateFlow(AlbumInfo())
    private val selectedPlaylist = MutableStateFlow(PlaylistInfo())
    private val selectedSong = MutableStateFlow(SongInfo())

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

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(HomeScreenUiState())

    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<HomeScreenUiState>
        get() = _state

    init {
        Log.i(TAG, "init START")
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch START")
            val counts = getTotalCounts()
            Log.i(TAG, "SongController status:\n" +
                "isActive?: $isActive\n" +
                "player?: ${player?.playbackState}")

            combine(
                refreshing,
                //featuredAlbumsData,
                featuredItemsData,
                //selectedAlbum,
                selectedPlaylist,
                selectedSong,
            ) {
                refreshing,
                //albumItems,
                libraryItems,
                //selectAlbum,
                selectPlaylist,
                selectSong, ->
                Log.i(TAG, "HomeUiState combine START\n" +
                    "refreshing: $refreshing\n" +
                    //"libraryItemsAlbums: ${albumItems.recentAlbums.size}\n" +
                    "libraryItemsPlaylists: ${libraryItems.recentPlaylists.size}\n" +
                    "libraryItemsSongs: ${libraryItems.recentlyAddedSongs.size}\n" +
                    "is SongController available: ${songController.isConnected()}")

                getSongControllerState()

                HomeScreenUiState(
                    isLoading = refreshing,
                    //featuredAlbums = albumItems.recentAlbums,
                    featuredPlaylists = libraryItems.recentPlaylists,
                    featuredSongs = libraryItems.recentlyAddedSongs,
                    totals = counts,
                    //selectAlbum = selectAlbum,
                    selectPlaylist = selectPlaylist,
                    selectSong = selectSong,
                )
            }.catch { throwable ->
                Log.e(TAG, "Error Caught: ${throwable.message}")
                emit(
                    HomeScreenUiState(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                )
            }.collect {
                _state.value = it
            }
        }

        viewModelScope.launch {
            songController.events.collect {
                Log.d(TAG, "get SongController Player Event(s)")

                // if events is empty, take these actions to generate the needed values for populating MiniPlayer
                if (it == null) {
                    Log.d(TAG, "init: running start up events to initialize MiniPlayer")
                    getSongControllerState()
                    onPlayerEvent(event = Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED)
                    onPlayerEvent(event = Player.EVENT_REPEAT_MODE_CHANGED)
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
        Log.i(TAG, "Refresh call -> refreshing: ${refreshing.value}")
        viewModelScope.launch {
            runCatching {
                if (FLAG) Log.i(TAG, "Refresh runCatching")
                refreshing.value = true
            }.onFailure {
                Log.e(TAG, "$it ::: runCatching failed (not sure what this means)")
            }

            Log.i(TAG, "refresh to be false -> sets screen to ready state")
            refreshing.value = false

            if (force) { // onRetry call when the screen errors
                homeRelaunch()
            }
        }
    }

    private suspend fun homeRelaunch() {
        Log.i(TAG, "homeRelaunch START")
        val counts = getTotalCounts()
        Log.i(TAG, "SongController status:\n" +
                "isActive?: $isActive\n" +
                "player?: ${player?.playbackState}")

        combine(
            //featuredAlbumsData,
            featuredItemsData,
            //selectedAlbum,
            selectedPlaylist,
            selectedSong,
        ) {
            //albumItems,
            libraryItems,
            //selectAlbum,
            selectPlaylist,
            selectSong: SongInfo,->
            Log.i(TAG, "HomeUiState combine START\n" +
                "refreshing: ${refreshing.value}\n" +
                //"libraryItemsAlbums: ${albumItems.recentAlbums.size}\n" +
                "libraryItemsPlaylists: ${libraryItems.recentPlaylists.size}\n" +
                "libraryItemsSongs: ${libraryItems.recentlyAddedSongs.size}\n" +
                "is SongController available: ${songController.isConnected()}")

            getSongControllerState()

            HomeScreenUiState(
                isLoading = refreshing.value,
                //featuredAlbums = albumItems.recentAlbums,
                featuredPlaylists = libraryItems.recentPlaylists,
                featuredSongs = libraryItems.recentlyAddedSongs,
                totals = counts,
                //selectAlbum = selectAlbum,
                selectPlaylist = selectPlaylist,
                selectSong = selectSong,
            )
        }.catch { throwable ->
            Log.e(TAG, "Error Caught: ${throwable.message}")
            emit(
                HomeScreenUiState(
                    isLoading = false,
                    errorMessage = throwable.message
                )
            )
        }.collect {
            _state.value = it
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

    fun onHomeAction(action: HomeAction) {
        Log.i(TAG, "onHomeAction - $action")
        when (action) {
            is HomeAction.EmptyLibraryView -> onEmptyPlaylistView()
            is HomeAction.AlbumMoreOptionsClicked -> onAlbumMoreOptionsClick(action.album)
            is HomeAction.PlaylistMoreOptionsClicked -> onPlaylistMoreOptionsClick(action.playlist)
            is HomeAction.SongMoreOptionsClicked -> onSongMoreOptionsClick(action.song)

            is HomeAction.PlaySong -> onPlaySong(action.song)
            is HomeAction.PlaySongNext -> onPlaySongNext(action.song)
            is HomeAction.QueueSong -> onQueueSong(action.song)

            is HomeAction.PlayAlbum -> onPlayAlbum(action.album)
            is HomeAction.PlayAlbumNext -> onPlayAlbumNext(action.album)
            is HomeAction.ShuffleAlbum -> onShuffleAlbum(action.album)
            is HomeAction.QueueAlbum -> onQueueAlbum(action.album)

            is HomeAction.PlayPlaylist -> onPlayPlaylist(action.playlist)
            is HomeAction.PlayPlaylistNext -> onPlayPlaylistNext(action.playlist)
            is HomeAction.ShufflePlaylist -> onShufflePlaylist(action.playlist)
            is HomeAction.QueuePlaylist -> onQueuePlaylist(action.playlist)
        }
    }

    private fun onEmptyPlaylistView() {
        //featuredPlaylists = null
    }

    private fun onAlbumMoreOptionsClick(album: AlbumInfo) {
        Log.i(TAG, "onAlbumMoreOptionsClick -> ${album.title}")
        selectedAlbum.value = album
    }
    private fun onPlaylistMoreOptionsClick(playlist: PlaylistInfo) {
        Log.i(TAG, "onPlaylistMoreOptionsClick -> ${playlist.name}")
        selectedPlaylist.value = playlist
    }
    private fun onSongMoreOptionsClick(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionsClick -> ${song.title}")
        selectedSong.value = song
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

    private fun onPlayPlaylist(playlist: PlaylistInfo) {
        Log.i(TAG, "onPlayPlaylist -> ${playlist.name}")
        viewModelScope.launch {
            val songs = getPlaylistDetails(playlist.id).first().songs
            songController.play(songs)
        }
    }
    private fun onPlayPlaylistNext(playlist: PlaylistInfo) {
        Log.i(TAG, "onQueuePlaylistNext -> ${playlist.name}")
        viewModelScope.launch {
            val songs = getPlaylistDetails(playlist.id).first().songs
            songController.addToQueueNext(songs)
        }
    }
    private fun onShufflePlaylist(playlist: PlaylistInfo) {
        Log.i(TAG, "onShufflePlaylist -> ${playlist.name}")
        viewModelScope.launch {
            val songs = getPlaylistDetails(playlist.id).first().songs
            songController.shuffle(songs)
        }
    }
    private fun onQueuePlaylist(playlist: PlaylistInfo) {
        Log.i(TAG, "onQueuePlaylist -> ${playlist.name}")
        viewModelScope.launch {
            val songs = getPlaylistDetails(playlist.id).first().songs
            songController.addToQueue(songs)
        }
    }
}

@Immutable
sealed interface HomeAction {
    data class EmptyLibraryView(val playlist: PlaylistInfo) : HomeAction
    data class AlbumMoreOptionsClicked(val album: AlbumInfo) : HomeAction
    data class PlaylistMoreOptionsClicked(val playlist: PlaylistInfo) : HomeAction
    data class SongMoreOptionsClicked(val song: SongInfo) : HomeAction

    data class PlaySong(val song: SongInfo) : HomeAction
    data class PlaySongNext(val song: SongInfo) : HomeAction
    data class QueueSong(val song: SongInfo) : HomeAction

    data class PlayAlbum(val album: AlbumInfo) : HomeAction
    data class PlayAlbumNext(val album: AlbumInfo) : HomeAction
    data class ShuffleAlbum(val album: AlbumInfo) : HomeAction
    data class QueueAlbum(val album: AlbumInfo) : HomeAction

    data class PlayPlaylist(val playlist: PlaylistInfo) : HomeAction
    data class PlayPlaylistNext(val playlist: PlaylistInfo) : HomeAction
    data class ShufflePlaylist(val playlist: PlaylistInfo) : HomeAction
    data class QueuePlaylist(val playlist: PlaylistInfo) : HomeAction
}
