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
import com.example.music.domain.usecases.GetAlbumDetailsV2
import com.example.music.domain.usecases.GetSongDataV2
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

/** Changelog:
 * ---- TEST VERSION USING SAVEDSTATEHANDLE TO REPLICATE PLAYER SCREEN NAVIGATION
 * As of 2/10/2025, this version is in remote branch and working on
 * PlaylistDetailsScreen, PlaylistDetailsViewModel
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 7/22-23/2025 - Deleted SongPlayer from domain layer.
 */

private const val TAG = "Album Details View Model"

data class AlbumUiState (
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val album: AlbumInfo = AlbumInfo(),
    val artist: ArtistInfo = ArtistInfo(),
    val songs: List<SongInfo> = emptyList(),
    val selectSong: SongInfo = SongInfo(),
)

/**
 * ViewModel that handles the business logic and screen state of the Album Details screen
 */
@UnstableApi
@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    getAlbumDetailsV2: GetAlbumDetailsV2,

    private val getSongDataV2: GetSongDataV2,
    private val songController: SongController,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), MiniPlayerState {

    private val _albumId: String = savedStateHandle.get<String>(Screen.ARG_ALBUM_ID)!!
    private val albumId = _albumId.toLong()

    private val getAlbumDetailsData = getAlbumDetailsV2(albumId)
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val selectedSong = MutableStateFlow<SongInfo?>(null)

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
                "player?: ${player?.playbackState}\n")

            combine(
                refreshing,
                getAlbumDetailsData,
                selectedSong,
            ) {
                refreshing,
                albumDetailsFilterResult,
                selectSong ->
                Log.i(TAG, "AlbumUiState combine START\n" +
                    "albumDetailsFilterResult ID: ${albumDetailsFilterResult.album.id}\n" +
                    "albumDetailsFilterResult songs: ${albumDetailsFilterResult.songs.size}\n" +
                    "is SongController available: ${songController.isConnected()}\n" +
                    "isReady?: ${!refreshing}")

                getSongControllerState()

                AlbumUiState(
                    isReady = !refreshing,
                    album = albumDetailsFilterResult.album,
                    artist = albumDetailsFilterResult.artist,
                    songs = albumDetailsFilterResult.songs,
                    selectSong = selectSong ?: SongInfo(),
                )
            }.catch { throwable ->
                Log.i(TAG, "Error Caught: ${throwable.message}")
                emit(
                    AlbumUiState(
                        isReady = true,
                        errorMessage = throwable.message
                    )
                )
            }.collect{
                _state.value = it
            }
        }

        viewModelScope.launch {
            songController.events.collect {
                Log.d(TAG, "get SongController Player Event(s)")

                // if events is empty, take these actions to generate the needed values for populating the Player Screen
                if (it == null) {
                    Log.d(TAG, "init: running start up events to initialize AlbumDetailsVM")
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
                    "isPlaying set to $isPlaying" +
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
                    currentSong = getSongDataV2(id.toLong())
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
            currentSong = getSongDataV2(id.toLong())
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

    fun onPrevious() {
        Log.i(TAG, "Hit previous btn")
        songController.previous()
    }

    fun onNext() {
        Log.i(TAG, "Hit next btn")
        songController.next()
    }

    fun onAlbumAction(action: AlbumAction) {
        Log.i(TAG, "onAlbumAction - $action")
        when (action) {
            is AlbumAction.SongMoreOptionClicked -> onSongMoreOptionClick(action.song)

            is AlbumAction.PlaySong -> onPlaySong(action.song) // songMO-play
            is AlbumAction.PlaySongNext -> onPlaySongNext(action.song) // songMO-playNext
            //is AlbumAction.AddSongToPlaylist -> onAddToPlaylist(action.song) // songMO-addToPlaylist
            is AlbumAction.QueueSong -> onQueueSong(action.song) // songMO-addToQueue

            is AlbumAction.PlaySongs -> onPlaySongs(action.songs) // albumMO-play
            is AlbumAction.PlaySongsNext -> onPlaySongsNext(action.songs) // albumMO-playNext
            is AlbumAction.ShuffleSongs -> onShuffleSongs(action.songs) // albumMO-shuffle
            //is AlbumAction.AddAlbumToPlaylist -> onAddToPlaylist(action.songs) // albumMO-addToPlaylist
            is AlbumAction.QueueSongs -> onQueueSongs(action.songs) // albumMO-addToQueue
        }
    }

    private fun onSongMoreOptionClick(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionClick -> ${song.title}")
        selectedSong.value = song
    }

    private fun onPlaySong(song: SongInfo) {
        Log.i(TAG, "onPlaySong -> ${song.title}")
        songController.play(song)
    }
    private fun onPlaySongNext(song: SongInfo) {
        Log.i(TAG, "onQueueSongNext -> ${song.title}")
        songController.addToQueueNext(song)
    }
    private fun onQueueSong(song: SongInfo) {
        Log.i(TAG, "onQueueSong -> ${song.title}")
        songController.addToQueue(song)
    }

    private fun onPlaySongs(songs: List<SongInfo>) {
        Log.i(TAG, "onPlaySongs -> ${songs.size}")
        songController.play(songs)
        /* //what is the thing that would jump start this step process. would it go thru the viewModel??
        //step 1: regardless of shuffle being on or off, set shuffle to off
        //step 2: prepare the mediaPlayer with the new queue of items in order from playlist
        //step 3: set the player to play the first item in queue
        //step 4: navigateToPlayer(first item)
        //step 5: start playing
        coroutineScope.launch {
            sheetState.hide()
            showThemeSheet = false
        }*/
    }
    private fun onPlaySongsNext(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongsNext - ${songs.size}")
        songController.addToQueueNext(songs)
    }
    private fun onQueueSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongs -> ${songs.size}")
        songController.addToQueue(songs)
    }
    private fun onShuffleSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onShuffleSongs -> ${songs.size}")
        songController.shuffle(songs)
        /* //what is the thing that would jump start this step process
        //step 1: regardless of shuffle being on or off, set shuffle to on
        //step 2?: confirm the shuffle type
        //step 3: prepare the mediaPlayer with the new queue of items shuffled from playlist
        //step 4: set the player to play the first item in queue
        //step 5: navigateToPlayer(first item)
        //step 6: start playing
        //needs to take the songs in the playlist, shuffle the
        coroutineScope.launch {
            sheetState.hide()
            showThemeSheet = false
        }*/
    }
}

sealed interface AlbumAction {
    data class SongMoreOptionClicked(val song: SongInfo) : AlbumAction

    data class PlaySong(val song: SongInfo) : AlbumAction
    data class PlaySongNext(val song: SongInfo) : AlbumAction
    data class QueueSong(val song: SongInfo) : AlbumAction

    data class PlaySongs(val songs: List<SongInfo>) : AlbumAction
    data class PlaySongsNext(val songs: List<SongInfo>) : AlbumAction
    data class ShuffleSongs(val songs: List<SongInfo>) : AlbumAction
    data class QueueSongs(val songs: List<SongInfo>) : AlbumAction
}

/**
 * ---------ORIGINAL VERSION: ViewModel that handles the business logic and screen state of the Artist details screen.
 */
/*
sealed interface AlbumUiState {
    data object Loading : AlbumUiState
    data class Ready(
        val album: AlbumInfo,
        val songs: List<SongInfo>, //PersistentList<SongInfo> = persistentListOf(),
    ) : AlbumUiState
}

/**
 * ViewModel that handles the business logic and screen state of the Album details screen.
 */
@HiltViewModel(assistedFactory = AlbumDetailsViewModel.AlbumDetailsViewModelFactory::class)
class AlbumDetailsViewModel @AssistedInject constructor(
    private val songRepo: SongRepo,
    private val songPlayer: SongPlayer,
    private val albumRepo: AlbumRepo,
    @Assisted val albumId: Long,
    //albumId is an argument needed for the selected album details to view
) : ViewModel() {

    @AssistedFactory
    interface AlbumDetailsViewModelFactory {
        fun create(albumId: Long): AlbumDetailsViewModel
    }

    val state: StateFlow<AlbumUiState> =
        combine( //want to use this to store the information needed to correctly determine the album and songs to view
            albumRepo.getAlbumWithExtraInfo(albumId),
            songRepo.getSongsAndAlbumByAlbumId(albumId)
        ) { album, songsToAlbum ->
            val songs = songsToAlbum.map { it.song.asExternalModel() }
            AlbumUiState.Ready(
                album = album.album.asExternalModel(),
                songs = songs,//toPersistentList(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AlbumUiState.Loading
        )

    fun onQueueSong(playerSong: PlayerSong) {
        songPlayer.addToQueue(playerSong)
    }

}*/
