package com.example.music.ui.home

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.database.model.Song
import com.example.music.domain.usecases.FeaturedLibraryItemsV2
import com.example.music.domain.model.FeaturedLibraryItemsFilterV2
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.PlaylistInfo
//import com.example.music.domain.player.SongPlayer
import com.example.music.data.util.combine
import com.example.music.domain.model.AlbumDetailsFilterResult
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.GetAlbumDetailsV2
import com.example.music.domain.usecases.GetTotalCountsV2
import com.example.music.service.SongController
import com.example.music.ui.albumdetails.AlbumAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Changelog:
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

private const val TAG = "Home View Model"

@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterV2 = FeaturedLibraryItemsFilterV2(),
    val totals: List<Int> = emptyList(),
    val selectSong: SongInfo = SongInfo(),
    val selectAlbum: AlbumInfo = AlbumInfo(),
)

/**
 * ViewModel that handles the business logic and screen state of the Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    featuredLibraryItemsV2: FeaturedLibraryItemsV2,
    getTotalCountsV2: GetTotalCountsV2,
    private val getAlbumDetailsV2: GetAlbumDetailsV2,
    private val songController: SongController
) : ViewModel() {
    /* ------ Current running UI needs:  ------
        objects: FeaturedLibraryItemsFilterResult, which contains
            Recent Playlists: list of most recently played playlists, limit passed as int 5
            Recently Added Songs: list of most recently added songs to library, limit passed as int 10
        means of retrieving object: FeaturedLibraryItemsUseCase
     */

    // test version for using MediaStore, uses Album instead of playlist for now
    private val featuredLibraryItems = featuredLibraryItemsV2()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    // Holds the song, album to show in more options modal
    private val selectedSong = MutableStateFlow<SongInfo?>(null)
    private val selectedAlbum = MutableStateFlow<AlbumInfo?>(null)

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(HomeScreenUiState())

    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)

    /* ------ Objects used in previous iterations:  ------
    private val _featuredLibraryItems = MutableStateFlow<FeaturedLibraryItemsFilterResult?>(null)

    private val featuredLibraryItems1 = MutableStateFlow(FeaturedLibraryItemsUseCase(songRepo, playlistRepo))
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val featuredLibraryItems3 = featuredLibraryItemsUseCase()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val featuredPlaylists = playlistRepo.sortPlaylistsByDateLastPlayedDesc(5)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val featuredSongs = songRepo.sortSongsByDateLastPlayedDesc(10)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    */

    val state: StateFlow<HomeScreenUiState>
        get() = _state

    init {
        Log.i(TAG, "init START")
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch START")
            val counts = getTotalCountsV2()

            combine(
                refreshing,
                featuredLibraryItems,
                selectedSong,
                selectedAlbum,
            ) {
                refreshing,
                libraryItems,
                selectSong,
                selectAlbum ->
                Log.i(TAG, "HomeUiState combine START\n" +
                    "refreshing: $refreshing\n" +
                    //"libraryItemsPlaylists: ${libraryItems.recentPlaylists.size}\n" +
                    "libraryItemsAlbums: ${libraryItems.recentAlbums.size}\n" +
                    "libraryItemsSongs: ${libraryItems.recentlyAddedSongs.size}\n" +
                    "is SongController available: ${songController.isConnected()}")

                HomeScreenUiState(
                    isLoading = refreshing,
                    featuredLibraryItemsFilterResult = libraryItems,
                    totals = counts,
                    selectSong = selectSong ?: SongInfo(),
                    selectAlbum = selectAlbum ?: AlbumInfo(),
                )
            }.catch { throwable ->
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

    fun onHomeAction(action: HomeAction) {
        Log.i(TAG, "onHomeAction - $action")
        when (action) {
            is HomeAction.EmptyLibraryView -> onEmptyPlaylistView()
            is HomeAction.AlbumMoreOptionClicked -> onAlbumMoreOptionClicked(action.album)
            //is HomeAction.LibraryPlaylistSelected -> onLibraryPlaylistSelected(action.playlist)
            is HomeAction.SongMoreOptionClicked -> onSongMoreOptionClick(action.song)

            is HomeAction.PlaySong -> onPlaySong(action.song) // songMO-play
            is HomeAction.PlaySongNext -> onQueueSongNext(action.song) // songMO-playNext
            //is HomeAction.AddSongToPlaylist -> onAddToPlaylist(action.song) // songMO-addToPlaylist
            is HomeAction.QueueSong -> onQueueSong(action.song) // songMO-addToQueue

            is HomeAction.PlaySongs -> onPlaySongs(action.album) // albumMO-play
            is HomeAction.PlaySongsNext -> onQueueSongsNext(action.album) // albumMO-playNext
            is HomeAction.ShuffleSongs -> onShuffleSongs(action.album) // albumMO-shuffle
            //is HomeAction.AddAlbumToPlaylist -> onAddToPlaylist(action.album) // albumMO-addToPlaylist
            is HomeAction.QueueSongs -> onQueueSongs(action.album) // albumMO-addToQueue
        }
    }

    private fun onEmptyPlaylistView() {
        //featuredPlaylists = null
    }

    private fun onAlbumMoreOptionClicked(album: AlbumInfo) {
        Log.i(TAG, "onAlbumMoreOptionClick -> ${album.title}")
        selectedAlbum.value = album
    }

    private fun onSongMoreOptionClick(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionClick -> ${song.title}")
        selectedSong.value = song
    }

    /*private fun onLibraryPlaylistSelected(playlist: PlaylistInfo) {
        selectedLibraryPlaylist.value = playlist
    }*/

    private fun onPlaySong(song: SongInfo) {
        Log.i(TAG, "onPlaySong -> ${song.title}")
        songController.play(song)
    }

    /*private fun onPlaySongs(songs: List<SongInfo>) {
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
    }*/
    private fun onPlaySongs(album: AlbumInfo) {
        Log.i(TAG, "onPlaySongs -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetailsV2(album.id).first().songs
            songController.play(songs)
        }
    }

    private fun onQueueSong(song: SongInfo) {
        Log.i(TAG, "onQueueSong -> ${song.title}")
        songController.addToQueue(song)
    }

    private fun onQueueSongNext(song: SongInfo) {
        Log.i(TAG, "onQueueSongNext -> ${song.title}")
        songController.addToQueueNext(song)
    }

    /*private fun onQueueSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongs -> ${songs.size}")
        songController.addToQueue(songs)
    }*/
    private fun onQueueSongs(album: AlbumInfo) {
        Log.i(TAG, "onQueueSongs -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetailsV2(album.id).first().songs
            songController.addToQueue(songs)
        }
    }

    /*private fun onQueueSongsNext(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongsNext - ${songs.size}")
        songController.addToQueueNext(songs)
    }*/
    private fun onQueueSongsNext(album: AlbumInfo) {
        Log.i(TAG, "onQueueSongsNext -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetailsV2(album.id).first().songs
            songController.addToQueueNext(songs)
        }
    }

    /*private fun onShuffleSongs(songs: List<SongInfo>) {
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
    }*/
    private fun onShuffleSongs(album: AlbumInfo) {
        Log.i(TAG, "onShuffleSongs -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetailsV2(album.id).first().songs
            songController.shuffle(songs)
        }
    }
}

/**
 * Enumerated list of Home Screen tab options as Home Screen Categories.
 * --DIFFERENT FROM PODCAST CATEGORY/GENRE--
 *  - The second half of the Home Screen main pane generates tabs based on these home categories
 *  - Your Library shows latest songs in library (sorted list of songs by last played desc
 *  - Discover shows chips of genres in library (currently pulling form domainTesting/PreviewData.kt)
 *  - Within the selected genre, shows list of albums within that genre (currently pulling from domainTesting/PreviewData.kt)
 */

@Immutable
sealed interface HomeAction {
    data class EmptyLibraryView(val playlist: PlaylistInfo) : HomeAction
    data class AlbumMoreOptionClicked(val album: AlbumInfo) : HomeAction
    data class SongMoreOptionClicked(val song: SongInfo) : HomeAction
    //data class LibraryPlaylistSelected(val playlist: PlaylistInfo) : HomeAction

    data class PlaySong(val song: SongInfo) : HomeAction
    //data class PlaySongs(val songs: List<SongInfo>) : HomeAction
    data class PlaySongs(val album: AlbumInfo) : HomeAction
    data class PlaySongNext(val song: SongInfo) : HomeAction
    //data class PlaySongsNext(val songs: List<SongInfo>) : HomeAction
    data class PlaySongsNext(val album: AlbumInfo) : HomeAction
    data class QueueSong(val song: SongInfo) : HomeAction
    //data class QueueSongs(val songs: List<SongInfo>) : HomeAction
    data class QueueSongs(val album: AlbumInfo) : HomeAction
    //data class ShuffleSongs(val songs: List<SongInfo>) : HomeAction
    data class ShuffleSongs(val album: AlbumInfo) : HomeAction
}
