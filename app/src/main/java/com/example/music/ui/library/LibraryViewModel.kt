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
    val selectSong: SongInfo = SongInfo(),
    val selectAlbum: AlbumInfo = AlbumInfo(),
    val selectArtist: ArtistInfo = ArtistInfo()
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

    // Holds the sorting preferences saved in the data store
    //private val sortPrefs = getAppPreferences()

    // Holds the selected items values to show in more options modals
    private val selectedSong = MutableStateFlow<SongInfo?>(null)
    private val selectedAlbum = MutableStateFlow<AlbumInfo?>(null)
    private val selectedArtist = MutableStateFlow<ArtistInfo?>(null)

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
                selectedSong,
                selectedAlbum,
                selectedArtist,
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
                refreshing,
                libraryCategories,
                libraryCategory,
                appPreferences,
                //libraryAlbums,
                //libraryArtists,
                libraryComposers,
                //libraryGenres,
                libraryPlaylists,
                //librarySongs,
                selectSong,
                selectAlbum,
                selectArtist
                ->

                Log.i(TAG, "LibraryScreenUiState combine START:\n" +
                    "isLoading: $refreshing\n" +
                    "libraryCategories: $libraryCategories\n" +
                    "selectedLibraryCategory: $libraryCategory\n")
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
                    selectSong = selectSong ?: SongInfo(),
                    selectAlbum = selectAlbum ?: AlbumInfo(),
                    selectArtist = selectArtist ?: ArtistInfo()
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
            is LibraryAction.AlbumMoreOptionsClicked -> onAlbumMoreOptionsClicked(action.album)
            is LibraryAction.ArtistMoreOptionsClicked -> onArtistMoreOptionsClicked(action.artist)
            is LibraryAction.SongMoreOptionsClicked -> onSongMoreOptionsClicked(action.song)

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
        }
    }

    private fun onLibraryCategorySelected(libraryCategory: LibraryCategory) {
        selectedLibraryCategory.value = libraryCategory
        refresh()
    }

    private fun onAlbumMoreOptionsClicked(album: AlbumInfo) {
        Log.i(TAG, "onAlbumMoreOptionClick -> ${album.title}")
        selectedAlbum.value = album
    }
    private fun onArtistMoreOptionsClicked(artist: ArtistInfo) {
        Log.i(TAG, "onArtistMoreOptionClick -> ${artist.name}")
        selectedArtist.value = artist
    }
    private fun onSongMoreOptionsClicked(song: SongInfo) {
        Log.i(TAG, "onSongMoreOptionClick -> ${song.title}")
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
}

enum class LibraryCategory {
    Playlists, Songs, Artists, Albums, Genres, Composers
}

@Immutable
sealed interface LibraryAction {
    data class LibraryCategorySelected(val libraryCategory: LibraryCategory) : LibraryAction
    data class AlbumMoreOptionsClicked(val album: AlbumInfo) : LibraryAction
    data class ArtistMoreOptionsClicked(val artist: ArtistInfo) : LibraryAction
    data class SongMoreOptionsClicked(val song: SongInfo) : LibraryAction

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
