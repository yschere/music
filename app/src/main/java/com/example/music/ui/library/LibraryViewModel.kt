package com.example.music.ui.library

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.music.data.repository.AlbumSortList
import com.example.music.data.repository.AppPreferencesRepo
import com.example.music.data.repository.ArtistSortList
import com.example.music.data.repository.ComposerSortList
import com.example.music.data.repository.GenreSortList
import com.example.music.data.repository.PlaylistSortList
import com.example.music.data.repository.SongSortList
import com.example.music.data.util.combine
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.usecases.GetAlbumDetails
import com.example.music.domain.usecases.GetAppPreferencesUseCase
import com.example.music.domain.usecases.GetArtistDetails
import com.example.music.domain.usecases.GetGenreDetails
import com.example.music.domain.usecases.GetLibraryAlbums
import com.example.music.domain.usecases.GetLibraryArtists
import com.example.music.domain.usecases.GetLibraryComposers
import com.example.music.domain.usecases.GetLibraryGenres
import com.example.music.domain.usecases.GetLibraryPlaylists
import com.example.music.domain.usecases.GetLibrarySongs
import com.example.music.domain.usecases.GetSongData
import com.example.music.domain.usecases.GetTotalCounts
import com.example.music.service.SongController
import com.example.music.ui.player.MiniPlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Library View Model"

data class LibraryScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val libraryCategories: List<LibraryCategory> = emptyList(),
    val selectedLibraryCategory: LibraryCategory = LibraryCategory.Playlists,
    val selectedSortPair: Pair<String, Boolean> = Pair("",false),
    val libraryAlbums: List<AlbumInfo> = emptyList(),
    val libraryArtists: List<ArtistInfo> = emptyList(),
    val libraryComposers: List<ComposerInfo> = emptyList(),
    val libraryGenres: List<GenreInfo> = emptyList(),
    val libraryPlaylists: List<PlaylistInfo> = emptyList(),
    val librarySongs: List<SongInfo> = emptyList(),
    val totals: List<Int> = emptyList(),
)

/**
 * ViewModel that handles the business logic and screen state of the Library screen
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    getAppPreferences: GetAppPreferencesUseCase,
    getLibraryAlbums: GetLibraryAlbums,
    getLibraryArtists: GetLibraryArtists,
    getLibraryComposers: GetLibraryComposers,
    getLibraryGenres: GetLibraryGenres,
    getLibraryPlaylists: GetLibraryPlaylists,
    getLibrarySongs: GetLibrarySongs,
    getTotalCounts: GetTotalCounts,

    private val getAlbumDetails: GetAlbumDetails,
    private val getArtistDetails: GetArtistDetails,
    private val getGenreDetails: GetGenreDetails,
    private val getSongData: GetSongData,
    private val songController: SongController
) : ViewModel(), MiniPlayerState {

    @Inject
    lateinit var appPreferences: AppPreferencesRepo

    // Holds the currently all available library categories
    private val libraryCategories = MutableStateFlow(LibraryCategory.entries)

    // Holds our currently selected category
    private val selectedLibraryCategory = MutableStateFlow(LibraryCategory.Playlists)
    private var selectedSortPair by mutableStateOf(Pair("", false))

    private val appPreferencesFlow = getAppPreferences()

    // bottom player section
    override var currentSong by mutableStateOf(SongInfo())
    private var _isActive by mutableStateOf(songController.isActive)
    var isActive
        get() = _isActive
        set(value) {
            _isActive = songController.isActive
            refresh(value)
        }

    override val player: Player?
        get() = songController.player
    private var _isPlaying by mutableStateOf(songController.isPlaying)
    override var isPlaying
        get() = _isPlaying
        set(value) {
            if (value) songController.play(true)
            else songController.pause()
        }

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
            val counts = getTotalCounts()
            Log.i(TAG, "SongController status:\n" +
                "isActive?: $isActive\n" +
                "player?: ${player?.playbackState}")

            combine(
                refreshing,
                libraryCategories,
                selectedLibraryCategory,
                appPreferencesFlow,
            ) {
                refreshing,
                libraryCategories,
                libraryCategory,
                appPreferences, ->
                Log.i(TAG, "LibraryScreenUiState combine START:\n" +
                    "isLoading: $refreshing\n" +
                    "libraryCategories: $libraryCategories\n" +
                    "selectedLibraryCategory: $libraryCategory")

                var libraryAlbums: List<AlbumInfo> = emptyList()
                var libraryArtists: List<ArtistInfo> = emptyList()
                var libraryComposers: List<ComposerInfo> = emptyList()
                var libraryGenres: List<GenreInfo> = emptyList()
                var libraryPlaylists: List<PlaylistInfo> = emptyList()
                var librarySongs: List<SongInfo> = emptyList()

                when (libraryCategory) {
                    LibraryCategory.Albums -> {
                        selectedSortPair = Pair(appPreferences.albumSortColumn, appPreferences.isAlbumAsc)
                        libraryAlbums = getLibraryAlbums(appPreferences.albumSortColumn, appPreferences.isAlbumAsc)
                    }
                    LibraryCategory.Artists -> {
                        selectedSortPair = Pair(appPreferences.artistSortColumn, appPreferences.isArtistAsc)
                        libraryArtists = getLibraryArtists(appPreferences.artistSortColumn, appPreferences.isArtistAsc)
                    }
                    LibraryCategory.Composers -> {
                        selectedSortPair = Pair(appPreferences.composerSortColumn, appPreferences.isComposerAsc)
                        libraryComposers = getLibraryComposers(selectedSortPair.first, selectedSortPair.second).first()
                    }
                    LibraryCategory.Genres -> {
                        selectedSortPair = Pair(appPreferences.genreSortColumn, appPreferences.isGenreAsc)
                        libraryGenres = getLibraryGenres(appPreferences.genreSortColumn, appPreferences.isGenreAsc)
                    }
                    LibraryCategory.Playlists -> {
                        selectedSortPair = Pair(appPreferences.playlistSortColumn, appPreferences.isPlaylistAsc)
                        libraryPlaylists = getLibraryPlaylists(selectedSortPair.first, selectedSortPair.second).first()
                    }
                    LibraryCategory.Songs -> {
                        selectedSortPair = Pair(appPreferences.songSortColumn, appPreferences.isSongAsc)
                        librarySongs = getLibrarySongs(appPreferences.songSortColumn, appPreferences.isSongAsc)
                    }
                }

                getSongControllerState()

                LibraryScreenUiState(
                    isLoading = refreshing,
                    libraryCategories = libraryCategories,
                    selectedLibraryCategory = libraryCategory,
                    selectedSortPair = selectedSortPair,
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

        viewModelScope.launch {
            songController.events.collect {
                Log.d(TAG, "get SongController Player Event(s)")

                // if events is empty, take these actions to generate the needed values for populating MiniPlayer
                if (it == null) {
                    Log.d(TAG, "init: running start up events to initialize MiniPlayer")
                    getSongControllerState()
                    onPlayerEvent(event = Player.EVENT_IS_LOADING_CHANGED)
                    onPlayerEvent(event = Player.EVENT_MEDIA_ITEM_TRANSITION)
                    onPlayerEvent(event = Player.EVENT_IS_PLAYING_CHANGED)
                    return@collect
                }
                // else, repeat the onPlayerEvent call to enact each event
                repeat(it.size()) { index ->
                    onPlayerEvent(it.get(index))
                }
            }
        }

        refresh(force = false)
        Log.i(TAG, "init END")
    }

    private fun onPlayerEvent(event: Int) {
        when (event) {
            // Event for checking if the SongController is loaded and ready to read
            Player.EVENT_IS_LOADING_CHANGED -> {
                val loaded = songController.loaded
                if (loaded.equals(true)) {
                    refreshing.value = false
                    isActive = songController.isActive
                }
                Log.d(TAG, "isLoading changed:\n" +
                    "isPlaying set to $isPlaying\n" +
                    "isActive set to $isActive")
            }

            // Event for checking if SongController is playing
            Player.EVENT_IS_PLAYING_CHANGED -> {
                _isPlaying = songController.isPlaying
                isActive = songController.isActive
                Log.d(TAG, "isPlaying changed:\n" +
                    "isPlaying set to $isPlaying\n" +
                    "isActive set to $isActive")
            }

            // Event for checking if the current media item has changed
            Player.EVENT_MEDIA_ITEM_TRANSITION -> {
                val mediaItem = songController.currentSong
                viewModelScope.launch {
                    var id = mediaItem?.mediaId
                    while (id == null) {
                        delay(100)
                        id = mediaItem?.mediaId
                    }
                    currentSong = getSongData(id.toLong())
                    Log.d(TAG, "Current Song set to ${currentSong.title}")
                    songController.logTrackNumber()
                }
            }

            Player.EVENT_TRACKS_CHANGED -> {
                songController.logTrackNumber()
            }
        }
    }

    private suspend fun getSongControllerState() {
        val id = songController.currentSong?.mediaId
        if (id != null) {
            currentSong = getSongData(id.toLong())
        }
        _isPlaying = songController.isPlaying
        isActive = songController.isActive
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

    fun onPlay() {
        Log.i(TAG,"Hit play btn")
        songController.play(true)
        _isPlaying = true
    }

    fun onPause() {
        Log.i(TAG, "Hit pause btn")
        songController.pause()
        _isPlaying = false
    }

    fun onLibraryAction(action: LibraryAction) {
        Log.i(TAG, "onLibraryAction - $action")
        when (action) {
            is LibraryAction.AppPreferencesUpdate -> onAppPreferencesUpdate(action.libraryCategory, action.newValue)
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

    private fun onAppPreferencesUpdate(libraryCategory: LibraryCategory, newValue: Pair<String, Boolean>) {
        viewModelScope.launch {
            when(libraryCategory) {
                LibraryCategory.Albums -> {
                    if (selectedSortPair.first != newValue.first && AlbumSortList.contains(newValue.first)) {
                        Log.i(TAG, "Updating Albums Sort Column -> ${newValue.first}")
                        appPreferences.updateAlbumSortColumn(newValue.first)
                    }
                    if (selectedSortPair.second != newValue.second) {
                        Log.i(TAG, "Updating Albums Asc/Desc -> ${newValue.second}")
                        appPreferences.updateAlbumAsc(newValue.second)
                    }
                }
                LibraryCategory.Artists -> {
                    if (selectedSortPair.first != newValue.first && ArtistSortList.contains(newValue.first)) {
                        Log.i(TAG, "Updating Artists Sort Column -> ${newValue.first}")
                        appPreferences.updateArtistSortColumn(newValue.first)
                    }
                    if (selectedSortPair.second != newValue.second) {
                        Log.i(TAG, "Updating Artists Asc/Desc -> ${newValue.second}")
                        appPreferences.updateArtistAsc(newValue.second)
                    }
                }
                LibraryCategory.Composers -> {
                    if (selectedSortPair.first != newValue.first && ComposerSortList.contains(newValue.first)) {
                        Log.i(TAG, "Updating Composers Sort Column -> ${newValue.first}")
                        appPreferences.updateComposerSortColumn(newValue.first)
                    }
                    if (selectedSortPair.second != newValue.second) {
                        Log.i(TAG, "Updating Composers Asc/Desc -> ${newValue.second}")
                        appPreferences.updateComposerAsc(newValue.second)
                    }
                }
                LibraryCategory.Genres -> {
                    if (selectedSortPair.first != newValue.first && GenreSortList.contains(newValue.first)) {
                        Log.i(TAG, "Updating Genres Sort Column -> ${newValue.first}")
                        appPreferences.updateGenreSortColumn(newValue.first)
                    }
                    if (selectedSortPair.second != newValue.second) {
                        Log.i(TAG, "Updating Genres Asc/Desc -> ${newValue.second}")
                        appPreferences.updateGenreAsc(newValue.second)
                    }
                }
                LibraryCategory.Playlists -> {
                    if (selectedSortPair.first != newValue.first && PlaylistSortList.contains(newValue.first)) {
                        Log.i(TAG, "Updating Playlists Sort Column -> ${newValue.first}")
                        appPreferences.updatePlaylistSortColumn(newValue.first)
                    }
                    if (selectedSortPair.second != newValue.second) {
                        Log.i(TAG, "Updating Playlists Asc/Desc -> ${newValue.second}")
                        appPreferences.updatePlaylistAsc(newValue.second)
                    }
                }
                LibraryCategory.Songs -> {
                    if (selectedSortPair.first != newValue.first && SongSortList.contains(newValue.first)) {
                        Log.i(TAG, "Updating Songs Sort Column -> ${newValue.first}")
                        appPreferences.updateSongSortColumn(newValue.first)
                    }
                    if (selectedSortPair.second != newValue.second) {
                        Log.i(TAG, "Updating Songs Asc/Desc -> ${newValue.second}")
                        appPreferences.updateSongAsc(newValue.second)
                    }
                }
            }
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
            val songs = getAlbumDetails(album.id).first().songs
            songController.play(songs)
        }
    }
    private fun onPlayAlbumNext(album: AlbumInfo) {
        Log.i(TAG, "onPlayAlbumNext -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetails(album.id).first().songs
            songController.addToQueueNext(songs)
        }
    }
    private fun onShuffleAlbum(album: AlbumInfo) {
        Log.i(TAG, "onShuffleAlbum -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetails(album.id).first().songs
            songController.shuffle(songs)
        }
    }
    private fun onQueueAlbum(album: AlbumInfo) {
        Log.i(TAG, "onQueueAlbum -> ${album.title}")
        viewModelScope.launch {
            val songs = getAlbumDetails(album.id).first().songs
            songController.addToQueue(songs)
        }
    }

    private fun onPlayArtist(artist: ArtistInfo) {
        Log.i(TAG, "onPlayArtist -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetails(artist.id).first().songs
            songController.play(songs)
        }
    }
    private fun onPlayArtistNext(artist: ArtistInfo) {
        Log.i(TAG, "onPlayArtistNext -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetails(artist.id).first().songs
            songController.addToQueueNext(songs)
        }
    }
    private fun onShuffleArtist(artist: ArtistInfo) {
        Log.i(TAG, "onShuffleArtist -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetails(artist.id).first().songs
            songController.shuffle(songs)
        }
    }
    private fun onQueueArtist(artist: ArtistInfo) {
        Log.i(TAG, "onQueueArtist -> ${artist.name}")
        viewModelScope.launch {
            val songs = getArtistDetails(artist.id).first().songs
            songController.addToQueue(songs)
        }
    }

    private fun onPlayGenre(genre: GenreInfo) {
        Log.i(TAG, "onPlayGenre -> ${genre.name}")
        viewModelScope.launch {
            val songs = getGenreDetails(genre.id).first().songs
            songController.play(songs)
        }
    }
    private fun onShuffleGenre(genre: GenreInfo) {
        Log.i(TAG, "onShuffleGenre -> ${genre.name}")
        viewModelScope.launch {
            val songs = getGenreDetails(genre.id).first().songs
            songController.shuffle(songs)
        }
    }
}

enum class LibraryCategory {
    Playlists, Songs, Artists, Albums, Genres, Composers
}

@Immutable
sealed interface LibraryAction {
    data class AppPreferencesUpdate(val libraryCategory: LibraryCategory, val newValue: Pair<String, Boolean>) : LibraryAction
    data class LibraryCategorySelected(val libraryCategory: LibraryCategory) : LibraryAction

    data class PlaySong(val song: SongInfo) : LibraryAction
    data class PlaySongNext(val song: SongInfo) : LibraryAction
    data class QueueSong(val song: SongInfo) : LibraryAction

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
