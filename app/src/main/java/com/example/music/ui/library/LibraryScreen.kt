package com.example.music.ui.library

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.TopAppBarExpandedHeight
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.music.R
import com.example.music.designsys.theme.MARGIN_PADDING
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewComposers
import com.example.music.domain.testing.PreviewGenres
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.ui.library.album.albumItems
import com.example.music.ui.library.artist.artistItems
import com.example.music.ui.library.composer.composerItems
import com.example.music.ui.library.genre.genreItems
import com.example.music.ui.library.playlist.playlistItems
import com.example.music.ui.library.song.songItems
import com.example.music.ui.player.MiniPlayerControlActions
import com.example.music.ui.shared.AlbumMoreOptionsBottomModal
import com.example.music.ui.shared.ArtistMoreOptionsBottomModal
import com.example.music.ui.shared.CreatePlaylistBottomModal
import com.example.music.ui.shared.MiniPlayer
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.GenreMoreOptionsBottomModal
import com.example.music.ui.shared.LibrarySortSelectionBottomModal
import com.example.music.ui.shared.NavDrawer
import com.example.music.ui.shared.PlaylistMoreOptionsBottomModal
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompLightPreview
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.util.NavDrawerBtn
import com.example.music.util.ScrollToTopFAB
import com.example.music.util.SearchBtn
import com.example.music.util.fullWidthItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "Library Screen"

/**
 * Stateful Composable for the Library Screen of the app. Contains windowSizeClass,
 * navigateToPlayer, and viewModel as parameters.
 */
@Composable
fun LibraryScreen(
    windowSizeClass: WindowSizeClass,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToGenreDetails: (Long) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    Log.i(TAG, "Library Screen START")
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        Log.e(TAG, "${uiState.errorMessage}")
        LibraryScreenError(onRetry = viewModel::refresh)
    }
    Surface(color = Color.Transparent) {
        LibraryScreen(
            windowSizeClass = windowSizeClass,
            isLoading = uiState.isLoading,
            libraryCategories = uiState.libraryCategories,
            selectedLibraryCategory = uiState.selectedLibraryCategory,
            libraryAlbums = uiState.libraryAlbums,
            libraryArtists = uiState.libraryArtists,
            libraryComposers = uiState.libraryComposers,
            libraryGenres = uiState.libraryGenres,
            libraryPlaylists = uiState.libraryPlaylists,
            librarySongs = uiState.librarySongs,
            totals = uiState.totals,
            currentSong = viewModel.currentSong,
            isActive = viewModel.isActive, // if playback is active
            isPlaying = viewModel.isPlaying,

            onLibraryAction = viewModel::onLibraryAction,
            navigateToHome = navigateToHome,
            navigateToLibrary = navigateToLibrary,
            navigateToPlayer = navigateToPlayer,
            navigateToSearch = navigateToSearch,
            navigateToSettings = navigateToSettings,
            navigateToAlbumDetails = navigateToAlbumDetails,
            navigateToArtistDetails = navigateToArtistDetails,
            navigateToComposerDetails = navigateToComposerDetails,
            navigateToGenreDetails = navigateToGenreDetails,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
            modifier = Modifier.fillMaxSize(),
            miniPlayerControlActions = MiniPlayerControlActions(
                onPlayPress = viewModel::onPlay,
                onPausePress = viewModel::onPause,
            ),
        )
    }
}

/**
 * Error Screen
 */
@Composable
private fun LibraryScreenError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Error(
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Stateless Composable for Library Screen and its properties needed to render the
 * components of the page.
 */
@Composable
private fun LibraryScreen(
    windowSizeClass: WindowSizeClass,
    isLoading: Boolean,
    selectedLibraryCategory: LibraryCategory,
    libraryCategories: List<LibraryCategory>,
    libraryAlbums: List<AlbumInfo>,
    libraryArtists: List<ArtistInfo>,
    libraryComposers: List<ComposerInfo>,
    libraryGenres: List<GenreInfo>,
    libraryPlaylists: List<PlaylistInfo>,
    librarySongs: List<SongInfo>,
    totals: List<Int>,
    currentSong: SongInfo,
    isActive: Boolean,
    isPlaying: Boolean,

    onLibraryAction: (LibraryAction) -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToGenreDetails: (Long) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    miniPlayerControlActions: MiniPlayerControlActions,
    modifier: Modifier = Modifier
) {
    Log.i(TAG, "Library Screen START\n" +
            "currentSong? ${currentSong.title}\n" +
            "isActive? $isActive")

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue) //FixMe: update the snackBar selection to properly convey action taken
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    NavDrawer(
        "Library",
        totals,
        navigateToHome,
        navigateToLibrary,
        navigateToSettings,
        drawerState,
        coroutineScope,
    ) {
        ScreenBackground(
            modifier = modifier
        ) {
            Scaffold(
                topBar = {
                    LibraryTopAppBar(
                        navigateToSearch = navigateToSearch,
                        onNavigationIconClick = {
                            coroutineScope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                    )
                    if (isLoading) {
                        LinearProgressIndicator(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MARGIN_PADDING)
                        )
                    }
                },
                bottomBar = {
                    if (isActive){
                        MiniPlayer(
                            song = currentSong,
                            isPlaying = isPlaying,
                            navigateToPlayer = navigateToPlayer,
                            onPlayPress = miniPlayerControlActions.onPlayPress,
                            onPausePress = miniPlayerControlActions.onPausePress,
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        )
                    }
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                containerColor = Color.Transparent,
                contentColor = contentColorFor(MaterialTheme.colorScheme.background)
            ) { contentPadding ->
                // Main Content
                LibraryContent(
                    coroutineScope = coroutineScope,
                    selectedLibraryCategory = selectedLibraryCategory,
                    libraryCategories = libraryCategories,
                    libraryAlbums = libraryAlbums,
                    libraryArtists = libraryArtists,
                    libraryComposers = libraryComposers,
                    libraryGenres = libraryGenres,
                    libraryPlaylists = libraryPlaylists,
                    librarySongs = librarySongs,
                    isActive = isActive,

                    modifier = Modifier.padding(contentPadding),
                    onLibraryAction = { action ->
                        if (action is LibraryAction.QueueSong) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(snackBarText)
                            }
                        }
                        onLibraryAction(action)
                    },
                    navigateToPlayer = navigateToPlayer,
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    navigateToArtistDetails = navigateToArtistDetails,
                    navigateToComposerDetails = navigateToComposerDetails,
                    navigateToGenreDetails = navigateToGenreDetails,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                )
            }
        }
    }
}

/**
 * Composable for Library Screen's Top App Bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryTopAppBar(
    navigateToSearch: () -> Unit,
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = "Library",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MARGIN_PADDING, vertical = 8.dp)
            )
        },
        navigationIcon = { NavDrawerBtn(onClick = onNavigationIconClick) },
        actions = { SearchBtn(onClick = navigateToSearch) },
        expandedHeight = TopAppBarExpandedHeight,
        windowInsets = TopAppBarDefaults.windowInsets,
        colors = TopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        scrollBehavior = pinnedScrollBehavior(),
    )
}

/**
 * Composable for Library Screen Content
 * Adjusted the library categories so that the selected category determines the grid view
 * new version: want to make a singular vertical grid that has dynamic layout based on tab chosen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryContent(
    coroutineScope: CoroutineScope,

    selectedLibraryCategory: LibraryCategory,
    libraryCategories: List<LibraryCategory>,
    libraryAlbums: List<AlbumInfo>,
    libraryArtists: List<ArtistInfo>,
    libraryComposers: List<ComposerInfo>,
    libraryGenres: List<GenreInfo>,
    libraryPlaylists: List<PlaylistInfo>,
    librarySongs: List<SongInfo>,

    isActive: Boolean,
    modifier: Modifier = Modifier,
    onLibraryAction: (LibraryAction) -> Unit,

    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToGenreDetails: (Long) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: () -> Unit,
) {
    Log.i(TAG, "LibraryContent START")
    val listState = rememberLazyGridState()
    val displayButton = remember { derivedStateOf { listState.firstVisibleItemIndex > 1 } }

    val sheetState = rememberModalBottomSheetState(false,)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var showCreatePlaylist by remember { mutableStateOf(false) }

    var showAlbumMoreOptions by remember { mutableStateOf(false) }
    var showArtistMoreOptions by remember { mutableStateOf(false) }
    var showComposerMoreOptions by remember { mutableStateOf(false) }
    var showGenreMoreOptions by remember { mutableStateOf(false) }
    var showPlaylistMoreOptions by remember { mutableStateOf(false) }
    var showSongMoreOptions by remember { mutableStateOf(false) }

    var selectedAlbum by remember { mutableStateOf(AlbumInfo()) }
    var selectedArtist by remember { mutableStateOf(ArtistInfo()) }
    var selectedComposer by remember { mutableStateOf(ComposerInfo()) }
    var selectedGenre by remember { mutableStateOf(GenreInfo()) }
    var selectedPlaylist by remember { mutableStateOf(PlaylistInfo()) }
    var selectedSong by remember { mutableStateOf(SongInfo()) }

    val groupedAlbumItems = libraryAlbums.groupBy { it.title.first() }
    val groupedArtistItems = libraryArtists.groupBy { it.name.first() }
    val groupedComposerItems = libraryComposers.groupBy { it.name.first() }
    val groupedGenreItems = libraryGenres.groupBy { it.name.first() }
    val groupedPlaylistItems = libraryPlaylists.groupBy { it.name.first() }
    val groupedSongItems = librarySongs.groupBy { it.title.first() }

//    groupedArtistItems.forEach { (letter, artist) ->
//        Log.i(TAG, "Output for groupedArtists Letter: $letter, artist: $artist")
//    }

    // screen sizing parameters for generating LazyGrid columns
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val horizontalPadding = 12
    val itemSpacing = 8
    val availableWidth = screenWidth - (horizontalPadding * 2) //times 2 to account for padding on left and right sides
    val minCellWidth = 160 // same as FEATURED_ALBUM_IMAGE_SIZE_DP
    val columns = 1.coerceAtLeast((availableWidth / (minCellWidth + itemSpacing)))

    //using box so that scroll to top btn can appear above library content
    // effectively creating scroll up FAB
    Box(Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = listState,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding.dp)
        ) {
            fullWidthItem {
                LibraryCategoryTabs(
                    libraryCategories = libraryCategories,
                    selectedLibraryCategory = selectedLibraryCategory,
                    onLibraryCategorySelected = {
                        onLibraryAction(
                            LibraryAction.LibraryCategorySelected(
                                it
                            )
                        )
                    },
                )
            }

            when (selectedLibraryCategory) {
                /** single column section **/
                LibraryCategory.Artists -> {
                    /** practicing with sticky header **/
                    artistItems(
                        mappedArtists = groupedArtistItems,
                        artistCount = libraryArtists.size,
                        state = listState,
                        navigateToArtistDetails = { artist: ArtistInfo ->
                            Log.i(TAG, "Artist clicked: ${artist.name} :: ${artist.id}")
                            navigateToArtistDetails(artist.id)
                        },
                        onArtistMoreOptionsClick = { artist: ArtistInfo ->
                            Log.i(TAG, "Artist More Option Clicked: ${artist.name} :: ${artist.id}")
                            selectedArtist = artist
                            showBottomSheet = true
                            showArtistMoreOptions = true
                        },
                        onSortClick = {
                            Log.i(TAG, "Artist Sort btn clicked")
                            showBottomSheet = true
                            showSortSheet = true
                        },
                        onSelectClick = {
                            Log.i(TAG, "Artist Multi Select btn clicked")
                        }
                    )
                    /** original version, not using sticky headers **/
                    /*artistItems(
                        artists = libraryArtists,
                        navigateToArtistDetails = { artist: ArtistInfo ->
                            Log.i(TAG, "Artist clicked: ${artist.name}")
                            navigateToArtistDetails(artist.id)
                        },
                        onArtistMoreOptionsClick = { artist: ArtistInfo ->
                            Log.i(TAG, "Artist More Option Clicked: ${artist.name}")
                            selectedArtist = artist
                            showBottomSheet = true
                            showArtistMoreOptions = true
                        },
                        onSortClick = {
                            Log.i(TAG, "Artist Sort btn clicked")
                            showBottomSheet = true
                            showSortSheet = true
                        },
                        onSelectClick = {
                            Log.i(TAG, "Artist Multi Select btn clicked")
                        }
                    )*/
                }

                LibraryCategory.Composers -> {
                    composerItems(
                        composers = libraryComposers,
                        navigateToComposerDetails = { composer: ComposerInfo ->
                            Log.i(TAG, "Composer clicked: ${composer.name} :: ${composer.id}")
                            navigateToComposerDetails(composer)
                        },
                        onComposerMoreOptionsClick = { composer: ComposerInfo ->
                            Log.i(TAG, "Composer More Option clicked: ${composer.name} :: ${composer.id}")
                            selectedComposer = composer
                            showBottomSheet = true
                            showComposerMoreOptions = true
                        },
                        onSortClick = {
                            Log.i(TAG, "Composer Sort btn clicked")
                            showBottomSheet = true
                            showSortSheet = true
                        },
                        onSelectClick = {
                            Log.i(TAG, "Composer Multi Select btn clicked")
                        },
                    )
                }

                LibraryCategory.Genres -> {
                    genreItems(
                        genres = libraryGenres,
                        navigateToGenreDetails = { genre: GenreInfo ->
                            Log.i(TAG, "Genre clicked: ${genre.name} :: ${genre.id}")
                            navigateToGenreDetails(genre.id)
                        },
                        onGenreMoreOptionsClick = { genre: GenreInfo ->
                            Log.i(TAG, "Genre More Option Clicked: ${genre.name} :: ${genre.id}")
                            selectedGenre = genre
                            showBottomSheet = true
                            showGenreMoreOptions = true
                        },
                        onSortClick = {
                            Log.i(TAG, "Genre Sort btn clicked")
                            showBottomSheet = true
                            showSortSheet = true
                        },
                        onSelectClick = {
                            Log.i(TAG, "Genre Multi Select btn clicked")
                        },
                    )
                }

                LibraryCategory.Songs -> {
                    songItems(
                        songs = librarySongs,
                        navigateToPlayer = { song: SongInfo ->
                            Log.i(TAG, "Song clicked: ${song.title} :: ${song.id}")
                            onLibraryAction(LibraryAction.PlaySong(song))
                            navigateToPlayer()
                        },
                        onSongMoreOptionsClick = { song: SongInfo ->
                            Log.i(TAG, "Song More Option Clicked: ${song.title} :: ${song.id}")
                            selectedSong = song
                            showBottomSheet = true
                            showSongMoreOptions = true
                        },
                        onSortClick = {
                            Log.i(TAG, "Song Sort btn clicked")
                            showBottomSheet = true
                            showSortSheet = true
                        },
                        onSelectClick = {
                            Log.i(TAG, "Song Multi Select btn clicked")
                        },
                        onPlayClick = {
                            Log.i(TAG, "Play Songs btn clicked")
                            onLibraryAction(LibraryAction.PlaySongs(librarySongs))
                            navigateToPlayer()
                        },
                        onShuffleClick = {
                            Log.i(TAG, "Shuffle Songs btn clicked")
                            onLibraryAction(LibraryAction.ShuffleSongs(librarySongs))
                            navigateToPlayer()
                        },
                    )
                }

                /** adaptive columns section **/
                LibraryCategory.Albums -> {
                    albumItems(
                        albums = libraryAlbums,
                        navigateToAlbumDetails = { album: AlbumInfo ->
                            Log.i(TAG, "Album clicked: ${album.title} :: ${album.id}")
                            navigateToAlbumDetails(album.id)
                        },
                        onAlbumMoreOptionsClick = { album: AlbumInfo ->
                            Log.i(TAG, "Album More Option clicked: ${album.title} :: ${album.id}")
                            selectedAlbum = album
                            showBottomSheet = true
                            showAlbumMoreOptions = true
                        },
                        onSortClick = {
                            Log.i(TAG, "Album Sort btn clicked")
                            showBottomSheet = true
                            showSortSheet = true
                        },
                        onSelectClick = {
                            Log.i(TAG, "Album Multi Select btn clicked")
                        },
                    )
                }

                LibraryCategory.Playlists -> {
                    playlistItems(
                        playlists = libraryPlaylists,
                        navigateToPlaylistDetails = { playlist: PlaylistInfo ->
                            Log.i(TAG, "Playlist clicked: ${playlist.name} :: ${playlist.id}")
                            navigateToPlaylistDetails(playlist)
                        },
                        onPlaylistMoreOptionsClick = { playlist: PlaylistInfo ->
                            Log.i(TAG, "Playlist More Option clicked: ${playlist.name} :: ${playlist.id}")
                            selectedPlaylist = playlist
                            showBottomSheet = true
                            showPlaylistMoreOptions = true
                        },
                        onPlusClick = {
                            Log.i(TAG, "Create Playlist btn clicked")
                            showBottomSheet = true
                            showCreatePlaylist = true
                        },
                        onSortClick = {
                            Log.i(TAG, "Playlist Sort btn clicked")
                            showBottomSheet = true
                            showSortSheet = true
                        },
                        onSelectClick = {
                            Log.i(TAG, "Playlist Multi Select btn clicked")
                        },
                    )
                }
            }
        }

        ScrollToTopFAB(
            displayButton = displayButton,
            isActive = isActive,
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            }
        )
    }

    // Library BottomSheet
    if (showBottomSheet) {
        Log.i(TAG, "Library Content -> showBottomSheet is TRUE")
        // bottom sheet context - sort btn
        if (showSortSheet) {
            Log.i(TAG, "Library Content -> $selectedLibraryCategory Sort Modal is TRUE")
            LibrarySortSelectionBottomModal(
                onDismissRequest = {
                    showBottomSheet = false
                    showSortSheet = false
                },
                sheetState = sheetState,
                libraryCategory = selectedLibraryCategory,
                // need to show selection
                onClose = {
                    coroutineScope.launch {
                        Log.i(TAG, "Hide sheet state")
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showSortSheet = false
                        }
                    }
                },
                onApply = {
                    coroutineScope.launch {
                        Log.i(TAG, "Save sheet state - does nothing atm")
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showSortSheet = false
                        }
                    }
                },
            )
        }

        // bottom sheet context - album more option btn
        else if (showAlbumMoreOptions) {
            Log.i(TAG, "Library Content -> Album More Options is TRUE")
            AlbumMoreOptionsBottomModal(
                onDismissRequest = {
                    showBottomSheet = false
                    showAlbumMoreOptions = false
                },
                sheetState = sheetState,
                album = selectedAlbum,
                play = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> Play Album clicked :: ${selectedAlbum.id}")
                        onLibraryAction(LibraryAction.PlayAlbum(selectedAlbum))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                playNext = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> Play Album Next clicked :: ${selectedAlbum.id}")
                        onLibraryAction(LibraryAction.PlayAlbumNext(selectedAlbum))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                shuffle = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> Shuffle Album clicked :: ${selectedAlbum.id}")
                        onLibraryAction(LibraryAction.ShuffleAlbum(selectedAlbum))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                //addToPlaylist = {},
                addToQueue = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> Queue Album clicked :: ${selectedAlbum.id}")
                        onLibraryAction(LibraryAction.QueueAlbum(selectedAlbum))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                goToArtist = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> GoToArtist clicked :: ${selectedAlbum.albumArtistId ?: "null id"}")
                        navigateToArtistDetails(selectedAlbum.albumArtistId ?: 0L) // not a good check, would break if bottom modal didn't have null check too
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                goToAlbum = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> GoToAlbum clicked :: ${selectedAlbum.id}")
                        navigateToAlbumDetails(selectedAlbum.id)
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                onClose = {
                    coroutineScope.launch {
                        Log.i(TAG, "Hide sheet state")
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                context = "Library",
            )
        }

        // bottom sheet context - artist more option btn
        else if (showArtistMoreOptions) {
            Log.i(TAG, "Library Content -> Artist More Options is TRUE")
            ArtistMoreOptionsBottomModal(
                onDismissRequest = {
                    showBottomSheet = false
                    showArtistMoreOptions = false
                },
                sheetState = sheetState,
                artist = selectedArtist,
                play = {
                    coroutineScope.launch {
                        Log.i(TAG, "Artist More Options Modal -> Play Artist clicked :: ${selectedArtist.id}")
                        onLibraryAction(LibraryAction.PlayArtist(selectedArtist))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set ArtistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showArtistMoreOptions = false
                        }
                    }
                },
                playNext = {
                    coroutineScope.launch {
                        Log.i(TAG, "Artist More Options Modal -> Play Artist Next clicked :: ${selectedArtist.id}")
                        onLibraryAction(LibraryAction.PlayArtistNext(selectedArtist))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set ArtistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showArtistMoreOptions = false
                        }
                    }
                },
                shuffle = {
                    coroutineScope.launch {
                        Log.i(TAG, "Artist More Options Modal -> Shuffle Artist clicked :: ${selectedArtist.id}")
                        onLibraryAction(LibraryAction.ShuffleArtist(selectedArtist))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set ArtistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showArtistMoreOptions = false
                        }
                    }
                },
                //addToPlaylist = {},
                addToQueue = {
                    coroutineScope.launch {
                        Log.i(TAG, "Artist More Options Modal -> Queue Artist clicked :: ${selectedArtist.id}")
                        onLibraryAction(LibraryAction.QueueArtist(selectedArtist))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set ArtistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showArtistMoreOptions = false
                        }
                    }
                },
                goToArtist = {
                    coroutineScope.launch {
                        Log.i(TAG, "Artist More Options Modal -> GoToArtist clicked :: ${selectedArtist.id}")
                        navigateToArtistDetails(selectedArtist.id)
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set ArtistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showArtistMoreOptions = false
                        }
                    }
                },
                onClose = {
                    coroutineScope.launch {
                        Log.i(TAG, "Hide sheet state")
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set ArtistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showArtistMoreOptions = false
                        }
                    }
                },
                context = "Library",
            )
        }

        // bottom sheet context - genre more option btn
        else if (showGenreMoreOptions) {
            Log.i(TAG, "Library Content -> Genre More Options is TRUE")
            GenreMoreOptionsBottomModal(
                onDismissRequest = {
                    showBottomSheet = false
                    showGenreMoreOptions = false
                },
                sheetState = sheetState,
                genre = selectedGenre,
                play = {
                    coroutineScope.launch {
                        Log.i(TAG, "Genre More Options Modal -> Play Genre clicked :: ${selectedGenre.id}")
                        onLibraryAction(LibraryAction.PlayGenre(selectedGenre))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showGenreMoreOptions = false
                        }
                    }
                },
                /*playNext = {
                    coroutineScope.launch {
                        Log.i(TAG, "Artist More Options Modal -> Play Artist Next clicked")
                        onLibraryAction(LibraryAction.PlayArtistNext(selectedArtist))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set ArtistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showArtistMoreOptions = false
                        }
                    }
                },*/
                shuffle = {
                    coroutineScope.launch {
                        Log.i(TAG, "Genre More Options Modal -> Shuffle Genre clicked :: ${selectedGenre.id}")
                        onLibraryAction(LibraryAction.ShuffleGenre(selectedGenre))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showGenreMoreOptions = false
                        }
                    }
                },
                //addToPlaylist = {},
                /*addToQueue = {
                    coroutineScope.launch {
                        Log.i(TAG, "Artist More Options Modal -> Queue Artist clicked")
                        onLibraryAction(LibraryAction.QueueArtist(selectedArtist))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set ArtistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showArtistMoreOptions = false
                        }
                    }
                },*/
                goToGenre = {
                    coroutineScope.launch {
                        Log.i(TAG, "Genre More Options Modal -> GoToGenre clicked :: ${selectedGenre.id}")
                        navigateToGenreDetails(selectedGenre.id)
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showGenreMoreOptions = false
                        }
                    }
                },
                onClose = {
                    coroutineScope.launch {
                        Log.i(TAG, "Hide sheet state")
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showGenreMoreOptions = false
                        }
                    }
                },
                context = "Library",
            )
        }

        // bottom sheet context - genre more option btn
        else if (showPlaylistMoreOptions) {
            Log.i(TAG, "Library Content -> Playlist More Options is TRUE")
            PlaylistMoreOptionsBottomModal(
                onDismissRequest = {
                    showBottomSheet = false
                    showPlaylistMoreOptions = false
                },
                sheetState = sheetState,
                playlist = selectedPlaylist,
                play = {
                    coroutineScope.launch {
                        Log.i(TAG, "Playlist More Options Modal -> Play Playlist clicked :: ${selectedPlaylist.id}")
                        //onLibraryAction(LibraryAction.PlayPlaylist(selectedPlaylist))
                        navigateToPlayer()
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set PlaylistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showPlaylistMoreOptions = false
                        }
                    }
                },
                playNext = {
                    coroutineScope.launch {
                        Log.i(TAG, "Playlist More Options Modal -> Play Playlist Next clicked :: ${selectedPlaylist.id}")
                        //onLibraryAction(LibraryAction.PlayPlaylistNext(selectedPlaylist))
                        navigateToPlayer()
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set PlaylistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showPlaylistMoreOptions = false
                        }
                    }
                },
                shuffle = {
                    coroutineScope.launch {
                        Log.i(TAG, "Playlist More Options Modal -> Shuffle Playlist clicked :: ${selectedPlaylist.id}")
                        //onLibraryAction(LibraryAction.ShufflePlaylist(selectedPlaylist))
                        navigateToPlayer()
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set PlaylistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showPlaylistMoreOptions = false
                        }
                    }
                },
                addToQueue = {
                    coroutineScope.launch {
                        Log.i(TAG, "Playlist More Options Modal -> Queue Playlist clicked :: ${selectedPlaylist.id}")
                        //onLibraryAction(LibraryAction.QueuePlaylist(selectedPlaylist))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set PlaylistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showPlaylistMoreOptions = false
                        }
                    }
                },
                goToPlaylist = {
                    coroutineScope.launch {
                        Log.i(TAG, "Playlist More Options Modal -> GoToPlaylist clicked :: ${selectedPlaylist.id}")
                        navigateToPlaylistDetails(selectedPlaylist)
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set PlaylistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showPlaylistMoreOptions = false
                        }
                    }
                },
                onClose = {
                    coroutineScope.launch {
                        Log.i(TAG, "Hide sheet state")
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set PlaylistMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showPlaylistMoreOptions = false
                        }
                    }
                },
                context = "Library",
            )
        }

        // bottom sheet context - create playlist prompt
        else if (showCreatePlaylist) {
            Log.i(TAG, "Library Content -> Create Playlist is TRUE")
            CreatePlaylistBottomModal(
                onDismissRequest = {
                    showBottomSheet = false
                    showCreatePlaylist = false
                },
                sheetState = sheetState,
                onClose = {
                    coroutineScope.launch {
                        Log.i(TAG, "Hide sheet state")
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set showCreatePlaylist to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showCreatePlaylist = false
                        }
                    }
                },
                onCreate = {
                    coroutineScope.launch {
                        Log.i(TAG, "Create Playlist clicked; does nothing right now")
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set showCreatePlaylist to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showCreatePlaylist = false
                        }
                    }
                },
            )
        }

        // bottom sheet context - song more option btn
        else if (showSongMoreOptions) {
            Log.i(TAG, "Library Content -> Song More Options is TRUE")
            SongMoreOptionsBottomModal(
                onDismissRequest = {
                    showBottomSheet = false
                    showSongMoreOptions = false
                },
                sheetState = sheetState,
                song = selectedSong,
                play = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> PlaySong clicked :: ${selectedSong.id}")
                        onLibraryAction(LibraryAction.PlaySong(selectedSong))
                        navigateToPlayer()
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showSongMoreOptions = false
                        }
                    }
                },
                playNext = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> PlaySongNext clicked :: ${selectedSong.id}")
                        onLibraryAction(LibraryAction.PlaySongNext(selectedSong))
                        navigateToPlayer()
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showSongMoreOptions = false
                        }
                    }
                },
                //addToPlaylist = {},
                addToQueue = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> QueueSong clicked :: ${selectedSong.id}")
                        onLibraryAction(LibraryAction.QueueSong(selectedSong))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showSongMoreOptions = false
                        }
                    }
                },
                goToArtist = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> GoToArtist clicked :: ${selectedSong.artistId}")
                        navigateToArtistDetails(selectedSong.artistId)
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showSongMoreOptions = false
                        }
                    }
                },
                goToAlbum = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> GoToAlbum clicked :: ${selectedSong.albumId}")
                        navigateToAlbumDetails(selectedSong.albumId)
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showSongMoreOptions = false
                        }
                    }
                },
                onClose = {
                    coroutineScope.launch {
                        Log.i(TAG, "Hide sheet state")
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showSongMoreOptions = false
                        }
                    }
                },
                context = "Library",
            )
        }
    }
}

@Composable
private fun LibraryCategoryTabs(
    libraryCategories: List<LibraryCategory>,
    selectedLibraryCategory: LibraryCategory,
    onLibraryCategorySelected: (LibraryCategory) -> Unit,
) {
    if (libraryCategories.isEmpty()) {
        return
    }
    val selectedIndex = libraryCategories.indexOfFirst { it == selectedLibraryCategory }

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        LibraryCategoryTabIndicator(
            Modifier.tabIndicatorOffset(
                tabPositions[selectedIndex]
            ),
            MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        indicator = indicator,
        modifier = Modifier,
        divider = {}
    ) {
        libraryCategories.forEachIndexed { index, libraryCategory ->
            Tab(
                selected = index == selectedIndex,
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedContentColor = MaterialTheme.colorScheme.primary,
                onClick = { onLibraryCategorySelected(libraryCategory) },
                modifier = Modifier.clip(MaterialTheme.shapes.small),
                content = {
                    Text(
                        text = libraryCategory.name,
                        style =
                            if (index == selectedIndex) MaterialTheme.typography.titleLarge
                            else MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                },
            )
        }
    }
}

@Composable
private fun LibraryCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}

private val CompactWindowSizeClass = WindowSizeClass.compute(360f, 780f)

@SystemLightPreview
@Composable
private fun PreviewLibrary() {
    MusicTheme {
        LibraryScreen(
            windowSizeClass = CompactWindowSizeClass,
            isLoading = false,

            libraryCategories = LibraryCategory.entries,
            selectedLibraryCategory = LibraryCategory.Songs,
            libraryAlbums = PreviewAlbums,
            libraryArtists = PreviewArtists,
            libraryComposers = PreviewComposers,
            libraryGenres = PreviewGenres,
            libraryPlaylists = PreviewPlaylists,
            librarySongs = PreviewSongs,
            totals = listOf(
                PreviewSongs.size,
                PreviewArtists.size,
                PreviewAlbums.size,
                PreviewPlaylists.size),
            currentSong = PreviewSongs[0],
            isActive = true,
            isPlaying = true,

            onLibraryAction = {},
            navigateToHome = {},
            navigateToLibrary = {},
            navigateToPlayer = {},
            navigateToSearch = {},
            navigateToSettings = {},
            navigateToAlbumDetails = {},
            navigateToArtistDetails = {},
            navigateToComposerDetails = {},
            navigateToGenreDetails = {},
            navigateToPlaylistDetails = {},
            miniPlayerControlActions = MiniPlayerControlActions(
                onPlayPress = {},
                onPausePress = {},
            ),
        )
    }
}
