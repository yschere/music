package com.example.music.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.usecases.FeaturedLibraryItemsUseCase
import com.example.music.domain.usecases.FeaturedLibraryItemsV2
import com.example.music.domain.usecases.FeaturedLibraryItemsFilterV2
import com.example.music.domain.usecases.GetSongAlbumDataUseCase
import com.example.music.domain.usecases.GetSongArtistDataUseCase
import com.example.music.domain.usecases.GetTotalCountsUseCase
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.player.SongPlayer
import com.example.music.domain.player.model.PlayerSong
import com.example.music.data.util.combine
import com.example.music.domain.player.model.toPlayerSong
import com.example.music.domain.usecases.GetTotalCountsV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.music.util.logger

//this is where all the components to create the HomeScreen view are stored/collected
@HiltViewModel
class HomeViewModel @Inject constructor(
    featuredLibraryItemsUseCase: FeaturedLibraryItemsUseCase,
    featuredLibraryItemsV2: FeaturedLibraryItemsV2,
    getTotalCountsUseCase: GetTotalCountsUseCase,
    getTotalCountsV2: GetTotalCountsV2,
    private val getArtistDataUseCase: GetSongArtistDataUseCase,
    private val getAlbumDataUseCase: GetSongAlbumDataUseCase,
    private val songPlayer: SongPlayer
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
        logger.info { "Home View Model - viewModelScope launch start" }
        viewModelScope.launch {
            // Holds the counts of songs, artists, albums, playlists in library for NavDrawer
            //val counts = getTotalCountsUseCase()
            val counts = getTotalCountsV2()

            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                refreshing,
                featuredLibraryItems,
            ) {
                refreshing,
                libraryItems,
                ->

                logger.info { "Home View Model - viewModelScope launch - combine start" }
                logger.info { "Home View Model - viewModelScope launch - combine - refreshing: $refreshing" }
                //logger.info { "Home View Model - viewModelScope launch - combine - libraryItemsPlaylists: ${libraryItems.recentPlaylists.size}" }
                logger.info { "Home View Model - viewModelScope launch - combine - libraryItemsAlbums: ${libraryItems.recentAlbums.size}" }
                logger.info { "Home View Model - viewModelScope launch - combine - libraryItemsSongs: ${libraryItems.recentlyAddedSongs.size}" }

                HomeScreenUiState(
                    isLoading = refreshing,
                    featuredLibraryItemsFilterResult = libraryItems,
                    playerSongs = libraryItems.recentlyAddedSongs.map { item ->
                        logger.info { "Song to PlayerSong: ID: ${item.id} - song: ${item.title}" }
                        // two options here for creating PlayerSong. I can either still do the original method of retrieving an ArtistInfo and AlbumInfo for the given songInfo
                        // OR
                        // since I am now able to access MediaStore data that is populating songInfo with much more info, like the artist name and album title, I could just use songInfo to construct PlayerSong
                        item.toPlayerSong()

                        // OR OR
                        // even more big brained: I just remove the PlayerSong support because SongInfo is more robust that it can support the UI more directly now

                        /* // ORIGINAL CODE FOR SONGINFO -> PLAYERSONG
                        val art = getArtistDataUseCase(item).first()
                        logger.info { "Song to PlayerSong: artist: ${art.name}" }
                        val alb = getAlbumDataUseCase(item).first()
                        logger.info { "Song to PlayerSong: album: ${alb.title}" }
                        PlayerSong(
                            item,
                            art,
                            alb,
                        )*/
                    },//TODO: PlayerSong support
                    totals = counts
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

        logger.info { "Home View Model - init end" }
    }

    fun refresh(force: Boolean = true) {
        logger.info { "Home View Model - refresh function start" }
        logger.info { "Home View Model - refreshing: ${refreshing.value}" }
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
        }
    }

    /*private fun onGenreSelected(genre: GenreInfo) {
        _selectedGenre.value = genre
    }

    private fun onHomeCategorySelected(homeCategory: HomeCategory) {
        selectedHomeCategory.value = homeCategory
    }

    private fun onLibraryAlbumSelected(album: AlbumInfo) {
        selectedLibraryAlbum.value = album
    }

    private fun onLibraryGenreSelected(genre: GenreInfo) {
        selectedLibraryGenre.value = genre
    }*/

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

    /*private fun onNavigationViewMenu() {
        //toggle NavMenu view here
    }*/

    private fun onEmptyPlaylistView() {
        //featuredPlaylists = null
    }

    private fun onQueueSong(song: PlayerSong) {
        songPlayer.addToQueue(song)
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
    data class LibraryAlbumSelected(val album: AlbumInfo) : HomeAction
    //data class LibraryPlaylistSelected(val playlist: PlaylistInfo) : HomeAction
    data class QueueSong(val song: PlayerSong) : HomeAction
}

@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterV2 = FeaturedLibraryItemsFilterV2(),
    //val featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterResult = FeaturedLibraryItemsFilterResult(),
    val playerSongs: List<PlayerSong> = emptyList(), //TODO: PlayerSong support
    val totals: List<Int> = emptyList()
    //val totals: List<Pair<String,Int>> = emptyList(),
)
