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
import com.example.music.data.util.combine
import com.example.music.domain.usecases.GetAlbumDetailsV2
import com.example.music.domain.usecases.GetArtistDetailsV2
import com.example.music.domain.usecases.GetGenreDetailsV2
import com.example.music.domain.usecases.GetLibraryAlbumsV2
import com.example.music.domain.usecases.GetLibraryArtistsV2
import com.example.music.domain.usecases.GetLibraryGenresV2
import com.example.music.domain.usecases.GetLibrarySongsV2
import com.example.music.domain.usecases.GetTotalCountsV2
import com.example.music.service.SongController
import com.example.music.ui.albumdetails.AlbumAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

private const val TAG = "Library View Model"

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
)

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

    private val getAlbumDetailsV2: GetAlbumDetailsV2,
    private val getArtistDetailsV2: GetArtistDetailsV2,
    private val getGenreDetailsV2: GetGenreDetailsV2,
    private val songController: SongController
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

    /*// setup with values that retrieve sortOptions from preferences data store
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

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(LibraryScreenUiState())

    // Holds the view state if the UI is refreshing for new data
    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<LibraryScreenUiState>
        get() = _state

    init {
        Log.i(TAG, "init START")
        viewModelScope.launch {
            Log.i(TAG, "viewModelScope launch START")
            val counts = getTotalCountsV2()

            combine(
                refreshing,
                libraryCategories,
                selectedLibraryCategory,
                appPreferencesFlow,
                getLibraryComposersUseCase("name", true),//sortedComposers,
                getLibraryPlaylistsUseCase("name", true),//sortedPlaylists,
            ) {
                refreshing,
                libraryCategories,
                libraryCategory,
                appPreferences,
                libraryComposers,
                libraryPlaylists,
                ->
                Log.i(TAG, "LibraryScreenUiState combine START:\n" +
                    "isLoading: $refreshing\n" +
                    "libraryCategories: $libraryCategories\n" +
                    "selectedLibraryCategory: $libraryCategory\n")
                var libraryAlbums: List<AlbumInfo> = emptyList()
                var libraryArtists: List<ArtistInfo> = emptyList()
                var libraryGenres: List<GenreInfo> = emptyList()
                var librarySongs: List<SongInfo> = emptyList()
                when (libraryCategory) {
                    LibraryCategory.Playlists -> {}
                    LibraryCategory.Songs -> {
                        librarySongs = getLibrarySongsV2(appPreferences.songSortOrder.name, appPreferences.isSongAsc)
                    }
                    LibraryCategory.Artists -> {
                        libraryArtists = getLibraryArtistsV2(appPreferences.artistSortOrder.name, appPreferences.isArtistAsc)
                    }
                    LibraryCategory.Albums -> {
                        libraryAlbums = getLibraryAlbumsV2(appPreferences.albumSortOrder.name, appPreferences.isAlbumAsc)
                    }
                    LibraryCategory.Genres -> {
                        libraryGenres = getLibraryGenresV2(appPreferences.genreSortOrder.name, appPreferences.isGenreAsc)
                    }
                    LibraryCategory.Composers -> {}
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
                Log.i(TAG, "$it ::: runCatching failed (not sure what this means)")
            }

            Log.i(TAG, "refresh to be false -> sets screen to ready state")
            refreshing.value = false
        }
    }

    fun onLibraryAction(action: LibraryAction) {
        Log.i(TAG, "onLibraryAction - $action")
        when (action) {
            is LibraryAction.LibraryCategorySelected -> onLibraryCategorySelected(action.libraryCategory)

            is LibraryAction.PlaySong -> onPlaySong(action.song)
            is LibraryAction.PlaySongNext -> onPlaySongNext(action.song)
            is LibraryAction.QueueSong -> onQueueSong(action.song)

            is LibraryAction.PlaySongs -> onPlaySongs(action.songs)
            is LibraryAction.QueueSongs -> onQueueSongs(action.songs)
            is LibraryAction.ShuffleSongs -> onShuffleSongs(action.songs)

            is LibraryAction.PlayAlbum -> onPlayAlbum(action.album)
            is LibraryAction.PlayAlbumNext -> onPlayAlbumNext(action.album)
            is LibraryAction.ShuffleAlbum -> onShuffleAlbum(action.album)
            is LibraryAction.QueueAlbum -> onQueueAlbum(action.album)

            is LibraryAction.PlayArtist -> onPlayArtist(action.artist)
            is LibraryAction.PlayArtistNext -> onPlayArtistNext(action.artist)
            is LibraryAction.ShuffleArtist -> onShuffleArtist(action.artist)
            is LibraryAction.QueueArtist -> onQueueArtist(action.artist)

            is LibraryAction.PlayGenre -> onPlayGenre(action.genre)
            is LibraryAction.ShuffleGenre -> onShuffleGenre(action.genre)
        }
    }

    private fun onLibraryCategorySelected(libraryCategory: LibraryCategory) {
        selectedLibraryCategory.value = libraryCategory
        refresh()
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
        Log.i(TAG, "onQueueSong -> ${song.title}")
        songController.addToQueue(song)
    }

    private fun onPlaySongs(songs: List<SongInfo>) {
        Log.i(TAG, "onPlaySongs -> ${songs.size}")
        songController.play(songs)
    }
    private fun onShuffleSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onShuffleSongs -> ${songs.size}")
        songController.shuffle(songs)
    }
    private fun onQueueSongs(songs: List<SongInfo>) {
        Log.i(TAG, "onQueueSongs -> ${songs.size}")
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

    private fun onPlayArtist(artist: ArtistInfo) {
        Log.i(TAG, "onPlayArtist -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetailsV2(artist.id).first().songs
            songController.play(songs)
        }
    }
    private fun onPlayArtistNext(artist: ArtistInfo) {
        Log.i(TAG, "onPlayArtistNext -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetailsV2(artist.id).first().songs
            songController.addToQueueNext(songs)
        }
    }
    private fun onShuffleArtist(artist: ArtistInfo) {
        Log.i(TAG, "onShuffleArtist -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetailsV2(artist.id).first().songs
            songController.shuffle(songs)
        }
    }
    private fun onQueueArtist(artist: ArtistInfo) {
        Log.i(TAG, "onQueueArtist -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetailsV2(artist.id).first().songs
            songController.addToQueue(songs)
        }
    }

    private fun onPlayGenre(genre: GenreInfo) {
        Log.i(TAG, "onPlayGenre -> ${genre.name}")
        viewModelScope.launch {
            val songs = getGenreDetailsV2(genre.id).first().songs
            songController.play(songs)
        }
    }
    private fun onShuffleGenre(genre: GenreInfo) {
        Log.i(TAG, "onShuffleGenre -> ${genre.name}")
        viewModelScope.launch {
            val songs = getGenreDetailsV2(genre.id).first().songs
            songController.shuffle(songs)
        }
    }
}

enum class LibraryCategory {
    Playlists, Songs, Artists, Albums, Genres, Composers
}

@Immutable
sealed interface LibraryAction {
    data class LibraryCategorySelected(val libraryCategory: LibraryCategory) : LibraryAction

    data class PlaySong(val song: SongInfo) : LibraryAction // songMO-play
    data class PlaySongNext(val song: SongInfo) : LibraryAction // songMO-playNext
    data class QueueSong(val song: SongInfo) : LibraryAction // songMO-queue

    data class PlaySongs(val songs: List<SongInfo>) : LibraryAction
    data class QueueSongs(val songs: List<SongInfo>) : LibraryAction
    data class ShuffleSongs(val songs: List<SongInfo>) : LibraryAction

    data class PlayAlbum(val album: AlbumInfo) : LibraryAction
    data class PlayAlbumNext(val album: AlbumInfo) : LibraryAction
    data class ShuffleAlbum(val album: AlbumInfo) : LibraryAction
    data class QueueAlbum(val album: AlbumInfo) : LibraryAction

    data class PlayArtist(val artist: ArtistInfo) : LibraryAction
    data class PlayArtistNext(val artist: ArtistInfo) : LibraryAction
    data class ShuffleArtist(val artist: ArtistInfo) : LibraryAction
    data class QueueArtist(val artist: ArtistInfo) : LibraryAction

    data class PlayGenre(val genre: GenreInfo) : LibraryAction
    data class ShuffleGenre(val genre: GenreInfo) : LibraryAction
}

/* sealed interface LibraryScreenUiState {
    data object Loading : LibraryScreenUiState
    data class Ready(
        val libraryCategories: List<LibraryCategory> = emptyList(),
        val selectedLibraryCategory: LibraryCategory = LibraryCategory.PlaylistView,
        val librarySongsModel: SongAppModel = SongSortModel(),
        val libraryPlaylistsModel: PlaylistSortModel = PlaylistSortModel(),
    ) : LibraryScreenUiState
} */
