package com.example.music.ui.library

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.AppPreferences
import com.example.music.domain.GetLibraryAlbumsUseCase
import com.example.music.domain.GetLibraryArtistsUseCase
import com.example.music.domain.GetLibraryComposersUseCase
import com.example.music.domain.GetLibraryGenresUseCase
import com.example.music.domain.GetLibraryPlaylistsUseCase
import com.example.music.domain.GetLibrarySongsUseCase
import com.example.music.domain.GetAppPreferencesUseCase
import com.example.music.domain.GetTotalCountsUseCase
import com.example.music.model.AlbumInfo
import com.example.music.model.ArtistInfo
import com.example.music.model.ComposerInfo
import com.example.music.model.GenreInfo
import com.example.music.model.PlaylistInfo
import com.example.music.model.SongInfo
import com.example.music.player.SongPlayer
import com.example.music.player.model.PlayerSong
import com.example.music.util.combine
import com.example.music.util.logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class LibraryViewModel @Inject constructor(
    getLibrarySongsUseCase: GetLibrarySongsUseCase,
    getLibraryPlaylistsUseCase: GetLibraryPlaylistsUseCase,
    getLibraryGenresUseCase: GetLibraryGenresUseCase,
    getLibraryComposersUseCase: GetLibraryComposersUseCase,
    getLibraryArtistsUseCase: GetLibraryArtistsUseCase,
    getLibraryAlbumsUseCase: GetLibraryAlbumsUseCase,
    getTotalCountsUseCase: GetTotalCountsUseCase,
    getAppPreferences: GetAppPreferencesUseCase, //checks AppPreferencesDataStore
    private val songPlayer: SongPlayer,
) : ViewModel() {
    /* ------ Current running UI needs:  ------
        library needs to have categories: playlists, songs, artists, albums, genres, composers
        need to hold selected category, and the list of items to show with that category
        objects: SongSortModel, PlaylistSortModel, ArtistSortModel, AlbumSortModel, GenreSortModel
            each model contains list of each type's objects in library, and count of type's objects
        means of retrieving objects: GetLibrarySongsUseCase, GetLibraryPlaylistsUseCase,
            GetLibraryArtistsUseCase, GetLibraryAlbumsUseCase, GetLibraryGenresUseCase
     */

    // Holds the currently all available library categories
    private val libraryCategories = MutableStateFlow(LibraryCategory.entries)

    // Holds our currently selected category
    private val selectedLibraryCategory = MutableStateFlow(LibraryCategory.Playlists)

    /*//TODO: set up with values that retrieve sortOptions from preferences data store
    private val sortedSongs = getLibrarySongsUseCase("title", true)
        //.stateIn(viewModelScope)//, SharingStarted.WhileSubscribed())
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val sortedPlaylists = getLibraryPlaylistsUseCase("name", true)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val sortedAlbums = getLibraryAlbumsUseCase("title", true)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val sortedArtists = getLibraryArtistsUseCase("name", true)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val sortedComposers = getLibraryComposersUseCase("name", true)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val sortedGenres = getLibraryGenresUseCase("name", true)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())*/


    /* ------ Objects used in previous iterations:  ------
    private val songs = songRepo.getAllSongs()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val playlists1 = MutableStateFlow(GetLibraryPlaylistsUseCase(playlistRepo))

    private val playlists = playlistRepo.getAllPlaylists()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val playlistSortModel = playlists1.value
    */

    // Holds the sorting preferences saved in the data store
    //private val sortPrefs = getAppPreferences()

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(LibraryScreenUiState())

    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<LibraryScreenUiState>
        get() = _state

    init {
        logger.info { "Library View Model - viewModelScope launch start" }
        viewModelScope.launch {
            val counts = getTotalCountsUseCase()

            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                libraryCategories,
                selectedLibraryCategory,
                refreshing,
                getLibraryAlbumsUseCase("title", true),//sortedAlbums,
                getLibraryArtistsUseCase("name", true),//sortedArtists,
                getLibraryComposersUseCase("name", true),//sortedComposers,
                getLibraryGenresUseCase("name", true),//sortedGenres,
                getLibraryPlaylistsUseCase("name", true),//sortedPlaylists,
                getLibrarySongsUseCase("title", true),//sortedSongs,
            ) {

            /*combine(
                libraryCategories,
                selectedLibraryCategory,
                refreshing,
                sortPrefs.transform<AppPreferences,List<AlbumInfo>> { values ->
                    getLibraryAlbumsUseCase(values.albumSortOrder.name, values.isAlbumAsc)//sortedAlbums,
                },
                sortPrefs.transform<AppPreferences,List<ArtistInfo>> { values ->
                    getLibraryArtistsUseCase(values.artistSortOrder.name, values.isArtistAsc)//sortedArtists,
                },
                sortPrefs.transform<AppPreferences,List<ComposerInfo>> { values ->
                    getLibraryComposersUseCase(values.composerSortOrder.name, values.isComposerAsc)//sortedComposers,
                },
                sortPrefs.transform<AppPreferences,List<GenreInfo>> { values ->
                    getLibraryGenresUseCase(values.genreSortOrder.name, values.isGenreAsc)//sortedGenres,
                },
                sortPrefs.transform<AppPreferences,List<PlaylistInfo>> { values ->
                    getLibraryPlaylistsUseCase(values.playlistSortOrder.name, values.isPlaylistAsc)//sortedPlaylists,
                },
                sortPrefs.transform<AppPreferences,List<SongInfo>> { values ->
                    getLibrarySongsUseCase(values.songSortOrder.name, values.isSongAsc)//sortedSongs,
                },
            ){*/
                libraryCategories,
                libraryCategory,
                refreshing,
                libraryAlbums,
                libraryArtists,
                libraryComposers,
                libraryGenres,
                libraryPlaylists,
                librarySongs,
                ->

                logger.info { "Library View Model - LibraryScreenUiState:"}
                logger.info { "Library View Model - isLoading: $refreshing"}
                logger.info { "Library View Model - libraryCategories: $libraryCategories"}
                logger.info { "Library View Model - selectedLibraryCategory: $libraryCategory"}

                val libraryPlayerSongs = librarySongs.map { item->
                    PlayerSong(
                        item,
                        libraryArtists.find { it.id == item.artistId }?: ArtistInfo(),
                        libraryAlbums.find { it.id == item.albumId }?: AlbumInfo(),
                    )
                }
                LibraryScreenUiState(
                    isLoading = refreshing,
                    libraryCategories = libraryCategories,
                    selectedLibraryCategory = libraryCategory,
                    libraryAlbums = libraryAlbums,
                    libraryArtists = libraryArtists,
                    libraryComposers = libraryComposers,
                    libraryGenres = libraryGenres,
                    libraryPlaylists = libraryPlaylists,
                    librarySongs = librarySongs,
                    libraryPlayerSongs = libraryPlayerSongs,
                    totals = counts
                )
            }.catch { throwable ->
                logger.info { "Library View Model - Error Caught: ${throwable.message}"}
                emit(
                    LibraryScreenUiState(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                )
            }.collect {
                _state.value = it
            }
        }

        refresh(force = false)
    }

    fun refresh(force: Boolean = true) {
        logger.info { "Library View Model - Refresh call" }
        viewModelScope.launch {
            runCatching {
                logger.info { "Library View Model - Refresh runCatching" }
                refreshing.value = true
                //podcastsRepository.updatePodcasts(force)
            }.onFailure {
                logger.info { "$it ::: runCatching, not sure what is failing here tho" }
            } // TODO: look at result of runCatching and show any errors

            logger.info { "Library View Model - refresh to be false -> sets Library to ready state" }
            refreshing.value = false
        }
    }

    //TODO: retro fit this for library
    fun onLibraryAction(action: LibraryAction) {
        when (action) {
            is LibraryAction.LibraryCategorySelected -> onLibraryCategorySelected(action.libraryCategory)
            //maybe filtering / sorting selections added here?
            is LibraryAction.QueueSong -> onQueueSong(action.song)
        }
    }

    private fun onLibraryCategorySelected(libraryCategory: LibraryCategory) {
        selectedLibraryCategory.value = libraryCategory
    }

    private fun onQueueSong(song: PlayerSong) {
        songPlayer.addToQueue(song)
    }

}

enum class LibraryCategory {
    Playlists, Songs, Artists, Albums, Genres, Composers
}

@Immutable
sealed interface LibraryAction {
    data class LibraryCategorySelected(val libraryCategory: LibraryCategory) : LibraryAction
    data class QueueSong(val song: PlayerSong) : LibraryAction
}

data class LibraryScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val libraryCategories: List<LibraryCategory> = emptyList(),
    val selectedLibraryCategory: LibraryCategory = LibraryCategory.Playlists,
    val libraryAlbums: List<AlbumInfo> = emptyList(),
    val libraryArtists: List<ArtistInfo> = emptyList(),
    val libraryComposers: List<ComposerInfo> = emptyList(),
    val libraryGenres: List<GenreInfo> = emptyList(),
    val libraryPlaylists: List<PlaylistInfo> = emptyList(),
    val libraryPlayerSongs: List<PlayerSong> = emptyList(),//TODO: PlayerSong support
    val librarySongs: List<SongInfo> = emptyList(),
    val totals: List<Int> = emptyList()
)
/* sealed interface LibraryScreenUiState {
    data object Loading : LibraryScreenUiState
    data class Ready(
        val libraryCategories: List<LibraryCategory> = emptyList(),
        val selectedLibraryCategory: LibraryCategory = LibraryCategory.PlaylistView,
        val librarySongsModel: SongAppModel = SongSortModel(),
        val libraryPlaylistsModel: PlaylistSortModel = PlaylistSortModel(),
    ) : LibraryScreenUiState
} */
