package com.example.music.ui.artistdetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.mediaresolver.model.Artist
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.GetAlbumDetailsV2
//import com.example.music.domain.player.SongPlayer
import com.example.music.domain.usecases.GetArtistDetailsV2
import com.example.music.service.SongController
import com.example.music.ui.Screen
import com.example.music.ui.albumdetails.AlbumAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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

private const val TAG = "Artist Details View Model"

data class ArtistUiState (
    val isReady: Boolean = false,
    val errorMessage: String? = null,
    val artist: ArtistInfo = ArtistInfo(),
    val albums: List<AlbumInfo> = emptyList(),
    val songs: List<SongInfo> = emptyList(),
    val selectAlbum: AlbumInfo = AlbumInfo(),
    val selectSong: SongInfo = SongInfo()
)

/**
 * ViewModel that handles the business logic and screen state of the Artist Details screen
 */
@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    getArtistDetailsV2: GetArtistDetailsV2,
    private val getAlbumDetailsV2: GetAlbumDetailsV2,
    private val songController: SongController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _artistId: String = savedStateHandle.get<String>(Screen.ARG_ARTIST_ID)!!
    private val artistId = _artistId.toLong()

    //private val getArtistDetailsData = getArtistDetailsUseCase(artistId)
    private val getArtistDetailsData = getArtistDetailsV2(artistId)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val selectedSong = MutableStateFlow<SongInfo?>(null)
    private val selectedAlbum = MutableStateFlow<AlbumInfo?>(null)

    private val _state = MutableStateFlow(ArtistUiState())

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<ArtistUiState>
        get() = _state

    init {
        Log.i(TAG, "init START --- artistId: $artistId")
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch START")
            combine(
                refreshing,
                getArtistDetailsData,
                selectedSong,
                selectedAlbum,
            ) {
                refreshing,
                artistDetailsFilterResult,
                selectSong,
                selectAlbum ->
                Log.i(TAG, "ArtistUiState combine START\n" +
                    "artistDetailsFilterResult ID: ${artistDetailsFilterResult.artist.id}\n" +
                    "artistDetailsFilterResult albums: ${artistDetailsFilterResult.albums.size}\n" +
                    "artistDetailsFilterResult songs: ${artistDetailsFilterResult.songs.size}\n" +
                    "is SongController available: ${songController.isConnected()}\n" +
                    "isReady?: ${!refreshing}")

                ArtistUiState(
                    isReady = !refreshing,
                    artist = artistDetailsFilterResult.artist,
                    albums = artistDetailsFilterResult.albums,
                    songs = artistDetailsFilterResult.songs,
                    selectSong = selectSong ?: SongInfo(),
                    selectAlbum = selectAlbum ?: AlbumInfo(),
                )
            }.catch { throwable ->
                emit(
                    ArtistUiState(
                        isReady = true,
                        errorMessage = throwable.message
                    )
                )
            }.collect{
                _state.value = it
            }
        }
        refresh(force = false)
        Log.i(TAG, "init END")
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

    fun onArtistAction(action: ArtistAction) {
        Log.i(TAG, "onArtistAction - $action")
        when (action) {
            is ArtistAction.AlbumMoreOptionClicked -> onAlbumMoreOptionClicked(action.album) // selects albumMO
            is ArtistAction.SongMoreOptionClicked -> onSongMoreOptionClicked(action.song) // selects songMO

            is ArtistAction.PlaySong -> onPlaySong(action.song) // songMO-play
            is ArtistAction.PlaySongNext -> onPlaySongNext(action.song) // songMo-playNext
            //is ArtistAction.AddSongToPlaylist -> songMO-addToPlaylist
            is ArtistAction.QueueSong -> onQueueSong(action.song) // songMO-addToQueue

            is ArtistAction.PlaySongs -> onPlaySongs(action.songs) // artistMO-play
            is ArtistAction.PlaySongsNext -> onPlaySongsNext(action.songs) // artistMO-playNext
            is ArtistAction.ShuffleSongs -> onShuffleSongs(action.songs) // artistMo-shuffle
            //is ArtistAction.AddArtistToPlaylist // artistMO-addToPlaylist
            is ArtistAction.QueueSongs -> onQueueSongs(action.songs) // artistMO-addToQueue

            is ArtistAction.PlayAlbum -> onPlayAlbum(action.album) // albumMO-play
            is ArtistAction.PlayAlbumNext -> onPlayAlbumNext(action.album) // albumMO-playNext
            is ArtistAction.ShuffleAlbum -> onShuffleAlbum(action.album) // albumMO-shuffle
            //is ArtistAction.AddAlbumToPlaylist // albumMO-addToPlaylist
            is ArtistAction.QueueAlbum -> onQueueAlbum(action.album) // albumMo-addToQueue
        }
    }

    private fun onAlbumMoreOptionClicked(album: AlbumInfo) {
        Log.i(TAG, "onAlbumMoreOptionClicked - ${album.title}")
        selectedAlbum.value = album
    }
    private fun onSongMoreOptionClicked(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionClick - ${song.title}")
        selectedSong.value = song
    }

    private fun onPlaySong(song: SongInfo) {
        Log.i(TAG, "onPlaySong - ${song.title}")
        songController.play(song)
    }
    private fun onPlaySongNext(song: SongInfo) {
        Log.i(TAG, "onPlaySongNext - ${song.title}")
        songController.addToQueueNext(song)
    }
    private fun onQueueSong(song: SongInfo) {
        Log.i(TAG, "onQueueSong - ${song.title}")
        songController.addToQueue(song)
    }

    private fun onPlaySongs(songs: List<SongInfo>) {
        Log.i(TAG, "onPlaySongs - ${songs.size}")
        songController.play(songs)
    }
    private fun onPlaySongsNext(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongsNext - ${songs.size}")
        songController.addToQueueNext(songs)
    }
    private fun onShuffleSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onShuffleSongs - ${songs.size}")
        songController.shuffle(songs)
    }
    private fun onQueueSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongs - ${songs.size}")
        songController.addToQueue(songs)
    }

    private fun onPlayAlbum(album: AlbumInfo) {
        Log.i(TAG, "onPlaySongs -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetailsV2(album.id).first().songs
            songController.play(songs)
        }
    }
    private fun onPlayAlbumNext(album: AlbumInfo) {
        Log.i(TAG, "onPlayAlbumNext -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetailsV2(album.id).first().songs
            songController.addToQueueNext(songs)
        }
    }
    private fun onShuffleAlbum(album: AlbumInfo) {
        Log.i(TAG, "onShuffleAlbum -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetailsV2(album.id).first().songs
            songController.shuffle(songs)
        }
    }
    private fun onQueueAlbum(album: AlbumInfo) {
        Log.i(TAG, "onQueueAlbum -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetailsV2(album.id).first().songs
            songController.addToQueue(songs)
        }
    }
}

sealed interface ArtistAction {
    data class AlbumMoreOptionClicked(val album: AlbumInfo) : ArtistAction
    data class SongMoreOptionClicked(val song: SongInfo) : ArtistAction

    data class PlaySong(val song: SongInfo) : ArtistAction
    data class PlaySongNext(val song: SongInfo) : ArtistAction
    data class QueueSong(val song: SongInfo) : ArtistAction

    data class PlaySongs(val songs: List<SongInfo>) : ArtistAction
    data class PlaySongsNext(val songs: List<SongInfo>) : ArtistAction
    data class ShuffleSongs(val songs: List<SongInfo>) : ArtistAction
    data class QueueSongs(val songs: List<SongInfo>) : ArtistAction

    data class PlayAlbum(val album: AlbumInfo) : ArtistAction
    data class PlayAlbumNext(val album: AlbumInfo) : ArtistAction
    data class ShuffleAlbum(val album: AlbumInfo) : ArtistAction
    data class QueueAlbum(val album: AlbumInfo) : ArtistAction
}

/**
 * ---------ORIGINAL VERSION: ViewModel that handles the business logic and screen state of the Artist details screen.
 */
/*
sealed interface ArtistUiState {
    data object Loading : ArtistUiState
    data class Ready(
        val artist: ArtistInfo,
        val albums: PersistentList<AlbumInfo> = persistentListOf(),
        val songs: PersistentList<SongInfo> = persistentListOf(),
    ) : ArtistUiState
}

/**
 * ViewModel that handles the business logic and screen state of the Artist details screen.
 */
@HiltViewModel(assistedFactory = ArtistDetailsViewModel.ArtistDetailsViewModelFactory::class)
class ArtistDetailsViewModel @AssistedInject constructor(
    private val songPlayer: SongPlayer,
    private val artistRepo: ArtistRepo,
    @Assisted private val artistId: Long,
    //artistId is an argument needed for the selected artist details to view
) : ViewModel() {

    @AssistedFactory
    interface ArtistDetailsViewModelFactory {
        fun create(artistId: Long): ArtistDetailsViewModel
    }

    val state: StateFlow<ArtistUiState> =
        combine(
            //should i be combining albums to artist here? as well as the songs?
            //if the details screen will contain both songs and albums, then yes i think
            //if the details screen will contain only albums, then only need to combine that
                //which, if it is just albums, how do i store the songs? and what do i do if someone selects to see songs list
            artistRepo.getArtistById(artistId),
            artistRepo.getAlbumsByArtistId(artistId),
            artistRepo.getSongsByArtistId(artistId)
        ) { artist, albums, songs ->
            ArtistUiState.Ready(
                artist = artist.asExternalModel(),
                albums = albums.map{it.asExternalModel()}.toPersistentList(),
                songs = songs.map{it.asExternalModel()}.toPersistentList(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArtistUiState.Loading
        )

    fun onAlbumSelect(album: AlbumInfo) {
        viewModelScope.launch{
            //Question: load albumDetails page for album selected here?
        }
    }

    fun onQueueSong(playerSong: PlayerSong) {
        songPlayer.addToQueue(playerSong)
    } //keeping this here for now, but would only be in use if songs list does appear on ArtistDetailsScreen
}*/