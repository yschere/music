package com.example.music.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.usecases.FeaturedLibraryItemsV2
import com.example.music.domain.model.FeaturedLibraryItemsFilterV2
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.PlaylistInfo
//import com.example.music.domain.player.SongPlayer
import com.example.music.data.util.combine
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.GetTotalCountsV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.music.util.logger

/** Changelog:
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/** logger tag for this class */
private const val TAG = "Home View Model"

//this is where all the components to create the HomeScreen view are stored/collected
@HiltViewModel
class HomeViewModel @Inject constructor(
    featuredLibraryItemsV2: FeaturedLibraryItemsV2,
    getTotalCountsV2: GetTotalCountsV2,
    //private val searchQueryV2: SearchQueryV2,
    //private val songPlayer: SongPlayer
) : ViewModel() {
    /* ------ Current running UI needs:  ------
        objects: FeaturedLibraryItemsFilterResult, which contains
            Recent Playlists: list of most recently played playlists, limit passed as int 5
            Recently Added Songs: list of most recently added songs to library, limit passed as int 10
        means of retrieving object: FeaturedLibraryItemsUseCase
     */

    // original
    //private val selectedLibraryPlaylist = MutableStateFlow<PlaylistInfo?>(null)

    // test version for using MediaStore, uses Album instead of playlist
    private val selectedLibraryAlbum = MutableStateFlow<AlbumInfo?>(null)

    // original
    //private val featuredLibraryItems = featuredLibraryItemsUseCase() //returns Flow<FeaturedLibraryItemsFilterResult>
        //.shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    // test version for using MediaStore, uses Album instead of playlist for now
    private val featuredLibraryItems = featuredLibraryItemsV2()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    //private val searchResults = MutableStateFlow<SearchQueryFilterV2?>(null)//searchQueryV2

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
        logger.info { "$TAG - viewModelScope launch start" }
        viewModelScope.launch {
            // Holds the counts of songs, artists, albums, playlists in library for NavDrawer
            //val counts = getTotalCountsUseCase()
            val counts = getTotalCountsV2()

            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                refreshing,
                featuredLibraryItems,
                ///searchResults,
            ) {
                refreshing,
                libraryItems,
                //searchResult,
                ->

                logger.info { "$TAG - viewModelScope launch - combine start" }
                logger.info { "$TAG - viewModelScope launch - combine - refreshing: $refreshing" }
                //logger.info { "$TAG - viewModelScope launch - combine - libraryItemsPlaylists: ${libraryItems.recentPlaylists.size}" }
                logger.info { "$TAG - viewModelScope launch - combine - libraryItemsAlbums: ${libraryItems.recentAlbums.size}" }
                logger.info { "$TAG - viewModelScope launch - combine - libraryItemsSongs: ${libraryItems.recentlyAddedSongs.size}" }

                HomeScreenUiState(
                    isLoading = refreshing,
                    featuredLibraryItemsFilterResult = libraryItems,
                    totals = counts,
                    //searchResults = searchResult ?: SearchQueryFilterV2(),
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

        logger.info { "$TAG - init end" }
    }

    fun refresh(force: Boolean = true) {
        logger.info { "$TAG - refresh function start" }
        logger.info { "$TAG - refreshing: ${refreshing.value}" }
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }

    //TODO: retro fit this for library
    fun onHomeAction(action: HomeAction) {
        when (action) {
            //is HomeAction.ToggleNavMenu -> onNavigationViewMenu()
            is HomeAction.LibraryAlbumSelected -> onLibraryAlbumSelected(action.album)
            //is HomeAction.LibraryPlaylistSelected -> onLibraryPlaylistSelected(action.playlist)
            is HomeAction.EmptyLibraryView -> onEmptyPlaylistView()
            is HomeAction.QueueSong -> onQueueSong(action.song)
            //is HomeAction.SendQuery -> onQuerySearch(action.query)
        }
    }

    private fun onMoreOptionsBtnClicked(item: Any) {

    }

    private fun onMoreBtnClicked(item: Any) {
        //showBottomSheet = true
    }

    //private fun onLibraryPlaylistSelected(playlist: PlaylistInfo) {
        //selectedLibraryPlaylist.value = playlist
    //}
    private fun onLibraryAlbumSelected(album: AlbumInfo) {
        selectedLibraryAlbum.value = album
    }

    private fun onEmptyPlaylistView() {
        //featuredPlaylists = null
    }

    private fun onQueueSong(song: SongInfo) {
        //songPlayer.addToQueue(song)
    }

//    private fun onQuerySearch(query: String) {
//        logger.info { query }
//        viewModelScope.launch {
//            searchResults.value = searchQueryV2(query)
//            // now that I have the songs, artists, albums ... how do i get them onto the UI?
//            //  or is this a case where I should have created a different 'screen' or 'activity' or 'fragment'
//            //  and have that handle the result -> UI conversion?
//        }
//    }

    // TODO create search query function for home search bar to call
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
    data class LibraryAlbumSelected(val album: AlbumInfo) : HomeAction
    //data class LibraryPlaylistSelected(val playlist: PlaylistInfo) : HomeAction
    data class QueueSong(val song: SongInfo) : HomeAction
    // TODO create search query class ... would this make it so the query string needs to be part of homeViewModel? maybe not?
    //data class SendQuery(val query: String) : HomeAction
}

@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterV2 = FeaturedLibraryItemsFilterV2(),
    val totals: List<Int> = emptyList(),
    //val searchResults: SearchQueryFilterV2 = SearchQueryFilterV2(),
)
