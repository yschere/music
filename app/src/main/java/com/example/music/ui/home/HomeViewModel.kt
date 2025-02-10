package com.example.music.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.database.model.Artist
import com.example.music.data.repository.PlaylistRepo
import com.example.music.data.repository.SongRepo
import com.example.music.domain.FeaturedLibraryItemsUseCase
import com.example.music.domain.GetAlbumDataUseCase
import com.example.music.domain.GetArtistDataUseCase
import com.example.music.model.AlbumInfo
import com.example.music.model.ArtistInfo
import com.example.music.model.FeaturedLibraryItemsFilterResult
import com.example.music.model.PlaylistInfo
import com.example.music.player.SongPlayer
import com.example.music.player.model.PlayerSong
import com.example.music.player.model.toPlayerSong
import com.example.music.ui.Screen
import com.example.music.util.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import org.apache.log4j.BasicConfigurator
import javax.inject.Inject

private val logger = KotlinLogging.logger{}
//this is where all the components to create the HomeScreen view are stored/collected
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val featuredLibraryItemsUseCase: FeaturedLibraryItemsUseCase,
    private val getArtistDataUseCase: GetArtistDataUseCase,
    private val getAlbumDataUseCase: GetAlbumDataUseCase,
    private val songPlayer: SongPlayer
) : ViewModel() {
    /* ------ Current running UI needs:  ------
        objects: FeaturedLibraryItemsFilterResult, which contains
            Recent Playlists: list of most recently played playlists, limit passed as int 5
            Recently Added Songs: list of most recently added songs to library, limit passed as int 10
        means of retrieving object: FeaturedLibraryItemsUseCase
     */

    private val selectedLibraryPlaylist = MutableStateFlow<PlaylistInfo?>(null)

    private val featuredLibraryItems = featuredLibraryItemsUseCase() //returns Flow<FeaturedLibraryItemsFilterResult>
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
        BasicConfigurator.configure()
        logger.info { "Home View Model - viewModelScope launch start" }
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                //featuredPlaylists,
                //featuredSongs,
                refreshing,
                featuredLibraryItems,
            ) {
                //playlists,
                //songs,
                refreshing,
                libraryItems,
                ->

                logger.info { "Home View Model - viewModelScope launch - combine start" }
                logger.info { "Home View Model - viewModelScope launch - combine - refreshing: $refreshing" }
                logger.info { "Home View Model - viewModelScope launch - combine - libraryItemsPlaylists: ${libraryItems.recentPlaylists.size}" }
                logger.info { "Home View Model - viewModelScope launch - combine - libraryItemsSongs: ${libraryItems.recentlyAddedSongs.size}" }

                HomeScreenUiState(
                    isLoading = refreshing,
                    featuredLibraryItemsFilterResult = libraryItems,
                    //featuredPlaylists = playlists.map { it.asExternalModel() }.toPersistentList(),
                    //featuredSongs = songs.map { it.asExternalModel() }.toPersistentList(),
                    playerSongs = libraryItems.recentlyAddedSongs.map { item ->
                        logger.info { "Song to PlayerSong: ${item.id}" }
                        val art = getArtistDataUseCase(item).first() ?: ArtistInfo()
                        logger.info { " art: ${art.name}" }
                        val alb = getAlbumDataUseCase(item).first() ?: AlbumInfo()
                        logger.info { "alb: ${alb.title}" }
                        PlayerSong(
                            item,
                            art,
                            alb,
                        )
                    }//TODO: PlayerSong support
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
        BasicConfigurator.configure()
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
            is HomeAction.LibraryPlaylistSelected -> onLibraryPlaylistSelected(action.playlist)
            is HomeAction.EmptyLibraryView -> onEmptyPlaylistView()
            is HomeAction.QueueSong -> onQueueSong(action.song)
        }
    }
//
//    private fun onGenreSelected(genre: GenreInfo) {
//        _selectedGenre.value = genre
//    }
//
//    private fun onHomeCategorySelected(homeCategory: HomeCategory) {
//        selectedHomeCategory.value = homeCategory
//    }
//
//    private fun onLibraryAlbumSelected(album: AlbumInfo) {
//        selectedLibraryAlbum.value = album
//    }
//
//    private fun onLibraryGenreSelected(genre: GenreInfo) {
//        selectedLibraryGenre.value = genre
//    }
    private fun onLibraryPlaylistSelected(playlist: PlaylistInfo) {
    selectedLibraryPlaylist.value = playlist
    }

//    private fun onNavigationViewMenu() {
//        //toggle NavMenu view here
//    }

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
 * The second half of the Home Screen main pane generates tabs based on these home categories
 * Your Library shows latest songs in library (sorted list of songs by last played desc
 * Discover shows chips of genres in library (currently pulling form domainTesting/PreviewData.kt)
 *  And within the selected genre, shows list of albums within that genre (currently pulling from domainTesting/PreviewData.kt)
 */

@Immutable
sealed interface HomeAction {
    //data class ToggleNavMenu(): HomeAction,
    data class EmptyLibraryView(val playlist: PlaylistInfo) : HomeAction
    data class LibraryPlaylistSelected(val playlist: PlaylistInfo) : HomeAction
    data class QueueSong(val song: PlayerSong) : HomeAction
}

@Immutable
data class HomeScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterResult = FeaturedLibraryItemsFilterResult(),
    //val featuredPlaylists: PersistentList<PlaylistInfo> = persistentListOf(),
    //val featuredSongs: PersistentList<SongInfo> = persistentListOf(),
    //val featuredSongs: LibraryInfo = LibraryInfo(),
    val playerSongs: List<PlayerSong> = emptyList() //TODO: PlayerSong support
)
