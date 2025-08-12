package com.example.music.ui.library

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.domain.usecases.GetLibraryComposersUseCase
import com.example.music.domain.usecases.GetLibraryPlaylistsUseCase
import com.example.music.domain.usecases.GetAppPreferencesUseCase
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
//import com.example.music.domain.player.SongPlayer
import com.example.music.data.util.combine
import com.example.music.domain.usecases.GetLibraryAlbumsV2
import com.example.music.domain.usecases.GetLibraryArtistsV2
import com.example.music.domain.usecases.GetLibraryGenresV2
import com.example.music.domain.usecases.GetLibrarySongsV2
import com.example.music.domain.usecases.GetTotalCountsV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Library View Model"

/** Changelog:
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 5/3/2025 - Added AppPreferencesRepo back in to try sorting through it again.
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

@HiltViewModel
class LibraryViewModel @Inject constructor(
    getLibrarySongsV2: GetLibrarySongsV2,
    getLibraryPlaylistsUseCase: GetLibraryPlaylistsUseCase,
    getLibraryGenresV2: GetLibraryGenresV2,
    getLibraryComposersUseCase: GetLibraryComposersUseCase,
    getLibraryArtistsV2: GetLibraryArtistsV2,
    getLibraryAlbumsV2: GetLibraryAlbumsV2,
    getTotalCountsV2: GetTotalCountsV2,
    getAppPreferences: GetAppPreferencesUseCase,
    //private val songPlayer: SongPlayer,
) : ViewModel() {
    /* ------ Current running UI needs:  ------
        library needs to have categories: playlists, songs, artists, albums, genres, composers
        need to hold selected category, and the list of items to show with that category
        objects: SongSortModel, PlaylistSortModel, ArtistSortModel, AlbumSortModel, GenreSortModel
            each model contains list of each type's objects in library, and count of type's objects
        means of retrieving objects: GetLibrarySongsUseCase, GetLibraryPlaylistsUseCase,
            GetLibraryArtistsUseCase, GetLibraryAlbumsUseCase, GetLibraryGenresUseCase
     */

//    @Inject
//    lateinit var appPreferences: AppPreferencesRepo

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

    private val appPreferencesFlow = getAppPreferences()

    // Holds the sorting preferences saved in the data store
    //private val sortPrefs = getAppPreferences()
    private val showBottomSheet = MutableStateFlow(false)

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(LibraryScreenUiState())

    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<LibraryScreenUiState>
        get() = _state

    init {
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch start")
            val counts = getTotalCountsV2()

            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                libraryCategories,
                selectedLibraryCategory,
                refreshing,
                showBottomSheet,
//                appPreferences.appPreferencesFlow,
                appPreferencesFlow,
                //getLibraryAlbumsUseCase("title", true),//sortedAlbums,
                //getLibraryAlbumsV2("title", true),

                //getLibraryArtistsUseCase("name", true),//sortedArtists,
                //getLibraryArtistsV2("name", true),

                getLibraryComposersUseCase("name", true),//sortedComposers,

                //getLibraryGenresUseCase("name", true),//sortedGenres,
                //getLibraryGenresV2("name", true),

                getLibraryPlaylistsUseCase("name", true),//sortedPlaylists,

                //getLibrarySongsUseCase("title", true),//sortedSongs,
                //getLibrarySongsV2("title", true),
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
                showBottomSheet,
                appPreferences,
                //libraryAlbums,
                //libraryArtists,
                libraryComposers,
                //libraryGenres,
                libraryPlaylists,
                //librarySongs,
                ->

                Log.i(TAG, "LibraryScreenUiState:")
                Log.i(TAG, "isLoading: $refreshing")
                Log.i(TAG, "libraryCategories: $libraryCategories")
                Log.i(TAG, "selectedLibraryCategory: $libraryCategory")
                var libraryAlbums: List<AlbumInfo> = emptyList()
                var libraryArtists: List<ArtistInfo> = emptyList()
                //var libraryComposers: List<ComposerInfo> = emptyList()
                var libraryGenres: List<GenreInfo> = emptyList()
                //var libraryPlaylists: List<PlaylistInfo> = emptyList()
                var librarySongs: List<SongInfo> = emptyList()
                when (libraryCategory) {
                    LibraryCategory.Playlists -> {
                        //libraryPlaylists = getLibraryPlaylistsUseCase("name", true)//sortedPlaylists,
                        //libraryPlaylists = getLibraryPlaylistsUseCase(appPreferences.playlistSortOrder.name, appPreferences.isPlaylistAsc)
                    }
                    LibraryCategory.Songs -> {
                        //librarySongs = getLibrarySongsV2("TITLE", true)
                        //librarySongs = getLibrarySongsV2("TITLE", appPreferences.isSongAsc)
                        librarySongs = getLibrarySongsV2(appPreferences.songSortOrder.name, appPreferences.isSongAsc)
                    }
                    LibraryCategory.Artists -> {
                        //libraryArtists = getLibraryArtistsV2("ARTIST", true)
                        //libraryArtists = getLibraryArtistsV2("ARTIST", appPreferences.isArtistAsc)
                        libraryArtists = getLibraryArtistsV2(appPreferences.artistSortOrder.name, appPreferences.isArtistAsc)
                    }
                    LibraryCategory.Albums -> {
                        //libraryAlbums = getLibraryAlbumsV2("ALBUM", true)
                        //libraryAlbums = getLibraryAlbumsV2("ALBUM", appPreferences.isAlbumAsc)
                        libraryAlbums = getLibraryAlbumsV2(appPreferences.albumSortOrder.name, appPreferences.isAlbumAsc)
                    }
                    LibraryCategory.Genres -> {
                        //libraryGenres = getLibraryGenresV2("NAME", true)
                        //libraryGenres = getLibraryGenresV2("NAME", appPreferences.isGenreAsc)
                        libraryGenres = getLibraryGenresV2(appPreferences.genreSortOrder.name, appPreferences.isGenreAsc)
                    }
                    LibraryCategory.Composers -> {
                        //libraryComposers = getLibraryComposersUseCase(appPreferences.composerSortOrder.name, appPreferences.isComposerAsc)
                    }
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
                    totals = counts,
                    showBottomModal = showBottomSheet,
                )
            }.catch { throwable ->
                Log.i(TAG, "Error Caught: ${throwable.message}")
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
        Log.i(TAG, "Refresh call")
        viewModelScope.launch {
            runCatching {
                Log.i(TAG, "Refresh runCatching")
                refreshing.value = true
                //podcastsRepository.updatePodcasts(force)
            }.onFailure {
                Log.i(TAG, "$it ::: runCatching, not sure what is failing here tho")
            } // TODO: look at result of runCatching and show any errors

            Log.i(TAG, "refresh to be false -> sets screen to ready state")
            refreshing.value = false
        }
    }

    fun onLibraryAction(action: LibraryAction) {
        when (action) {
            is LibraryAction.LibraryCategorySelected -> onLibraryCategorySelected(action.libraryCategory)
            //maybe filtering / sorting selections added here?
            is LibraryAction.QueueSong -> onQueueSong(action.song)
            is LibraryAction.ShowModal -> onShowModal(action.libraryCategory, action.isModalOpen)
        }
    }

    private fun onLibraryCategorySelected(libraryCategory: LibraryCategory) {
        selectedLibraryCategory.value = libraryCategory
        refresh()
    }

    private fun onQueueSong(song: SongInfo) {
        //songPlayer.addToQueue(song)
    }

    private fun onShowModal(libraryCategory: LibraryCategory, isModalOpen: Boolean) {
        //what is the purpose of this function?
        // want to know which screen on library is being shown
        // want to have a way for contexted modal? how would this accomplish it tho
        showBottomSheet.value = isModalOpen
    }
}

enum class LibraryCategory {
    Playlists, Songs, Artists, Albums, Genres, Composers
}

@Immutable
sealed interface LibraryAction {
    data class LibraryCategorySelected(val libraryCategory: LibraryCategory) : LibraryAction
    data class QueueSong(val song: SongInfo) : LibraryAction
    data class ShowModal(val libraryCategory: LibraryCategory, val isModalOpen: Boolean) : LibraryAction
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
    val librarySongs: List<SongInfo> = emptyList(),
    val totals: List<Int> = emptyList(),
    val showBottomModal: Boolean = false,
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
