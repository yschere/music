package com.example.music.ui.library

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.database.model.SongToAlbum
import com.example.music.domain.GetAlbumDataUseCase
import com.example.music.domain.GetArtistDataUseCase
import com.example.music.domain.GetLibraryPlaylistsUseCase
import com.example.music.domain.GetLibrarySongsUseCase
import com.example.music.model.LibraryInfo
import com.example.music.model.PlaylistSortModel
import com.example.music.model.SongSortModel
import com.example.music.model.asAlbumToSongInfo
import com.example.music.player.SongPlayer
import com.example.music.player.model.PlayerSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/* sealed interface LibraryScreenUiState {
    data object Loading : LibraryScreenUiState
    data class Ready(
        val libraryCategories: List<LibraryCategory> = emptyList(),
        val selectedLibraryCategory: LibraryCategory = LibraryCategory.PlaylistView,
        val librarySongsModel: SongSortModel = SongSortModel(),
        val libraryPlaylistsModel: PlaylistSortModel = PlaylistSortModel(),
    ) : LibraryScreenUiState
} */

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getLibrarySongsUseCase: GetLibrarySongsUseCase,
    private val getLibraryPlaylistsUseCase: GetLibraryPlaylistsUseCase,
    private val getAlbumDataUseCase: GetAlbumDataUseCase, //using this to be able to get AlbumInfo from SongInfo
    private val getArtistDataUseCase: GetArtistDataUseCase,
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

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(LibraryScreenUiState())

    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)

    private val sortedSongs = getLibrarySongsUseCase("title", true) //TODO: set up with values that retrieve sortOptions from preferences data store
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    private val sortedPlaylists = getLibraryPlaylistsUseCase("name", true) //TODO: set up with values that retrieve sortOptions from preferences data store
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    /* ------ Objects used in previous iterations:  ------
    private val songs = songRepo.getAllSongs()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val playlists1 = MutableStateFlow(GetLibraryPlaylistsUseCase(playlistRepo))

    private val playlists = playlistRepo.getAllPlaylists()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val playlistSortModel = playlists1.value
    */

    val state: StateFlow<LibraryScreenUiState>
        get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            com.example.music.util.combine(
                libraryCategories,
                selectedLibraryCategory,
                refreshing,
                sortedSongs,
                sortedPlaylists,
                sortedSongs.map { items ->
                    items.songs.map { item ->
                        PlayerSong(
                            item,
                            getArtistDataUseCase(item).first(),
                            getAlbumDataUseCase(item).first(),
                        )

                    }
                }
//                songs.flatMapLatest { songAlbums ->
//                    songRepo.getSongsAndAlbumsByAlbumIds(songAlbums.map { song -> song.albumId!! })
//                },
//                playlists.flatMapLatest { playlist ->
//                    playlistRepo.getPlaylistsByIds(playlist.map { p -> p.id })
//                },
//                playlists1,
                //playlistSortModel,
            ) {
                libraryCategories,
                libraryCategory,
                refreshing,
                librarySongs,
                libraryPlaylists,
                sortedPlayerSongs ->

//                val libraryPlaylists = playlists.map { p ->
//                    p.asExternalModel()
//                }
                //val playlistSortModel = playlists1.invoke("name", true)

                //libraryPlaylists = playlistSortModel.

                LibraryScreenUiState(
                    isLoading = refreshing,
                    libraryCategories = libraryCategories,
                    selectedLibraryCategory = libraryCategory,
                    librarySongsModel = librarySongs,
                    //librarySongs = librarySongs.asLibrary(), //og version using AlbumToSong to transform into libraryInfo
                    //librarySongs = librarySongs.songs.map { it.toPlayerSong() }, //version that would transform librarySongs to List<PlayerSong>
                    libraryPlaylistsModel = libraryPlaylists,
                    libraryPlayerSongs = sortedPlayerSongs,
                )
            }.catch { throwable ->
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
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                //podcastsRepository.updatePodcasts(force)
            }
            // TODO: look at result of runCatching and show any errors

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

//TODO: what does this one need to be if the List<SongInfo> cannot be SongInfo?
// Do I have to make something else?
// Can SongPlayerData or PlaylistToSongInfo be used to compensate?
// Turned it into SongToAlbum so it can contain both song and album data
private fun List<SongToAlbum>.asLibrary(): LibraryInfo =
    LibraryInfo(
        songs = this.map { it.asAlbumToSongInfo() }
    )


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
    val librarySongsModel: SongSortModel = SongSortModel(),
    val libraryPlaylistsModel: PlaylistSortModel = PlaylistSortModel(),
    val libraryPlayerSongs: List<PlayerSong> = emptyList(),//TODO: PlayerSong support
    //val libraryPlaylists: List<PlaylistInfo> = emptyList(),
    //val librarySongs: LibraryInfo = LibraryInfo(),
    //val featuredArtists: List<ArtistInfo> = emptyList(),
    //val featuredAlbums: List<AlbumInfo> = emptyList(),
    //val featuredGenres: List<GenreInfo> = emptyList(),
    //val featuredComposers: List<ComposerInfo> = emptyList(),
)
