package com.example.music.ui.library

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.allVerticalHingeBounds
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.occludingVerticalHingeBounds
import androidx.compose.material3.adaptive.separatingVerticalHingeBounds
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.music.R
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewComposers
import com.example.music.domain.testing.PreviewGenres
import com.example.music.domain.testing.PreviewPlayerSongs
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.model.AlbumInfo
import com.example.music.model.ArtistInfo
import com.example.music.model.ComposerInfo
import com.example.music.model.GenreInfo
import com.example.music.model.PlaylistInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.ui.library.album.albumItems
import com.example.music.ui.library.artist.artistItems
import com.example.music.ui.library.composer.composerItems
import com.example.music.ui.library.genre.genreItems
import com.example.music.ui.library.playlist.playlistItems
import com.example.music.ui.library.song.songItems
import com.example.music.ui.shared.NavDrawer
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.isCompact
import com.example.music.util.quantityStringResource
import com.example.music.util.radialGradientScrim
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isMainPaneHidden(): Boolean {
    return scaffoldValue[SupportingPaneScaffoldRole.Main] == PaneAdaptedValue.Hidden
}

/**
 * Copied from `calculatePaneScaffoldDirective()` in [PaneScaffoldDirective], with modifications to
 * only show 1 pane horizontally if either width or height size class is compact.
 */
//@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun calculateScaffoldDirective(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    verticalHingePolicy: HingePolicy = HingePolicy.AvoidSeparating
): PaneScaffoldDirective {
    val maxHorizontalPartitions: Int
    val verticalSpacerSize: Dp
    if (windowAdaptiveInfo.windowSizeClass.isCompact) {
        // Window width or height is compact. Limit to 1 pane horizontally.
        maxHorizontalPartitions = 1
        verticalSpacerSize = 0.dp
    } else {
        when (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> {
                maxHorizontalPartitions = 1
                verticalSpacerSize = 0.dp
            }

            WindowWidthSizeClass.MEDIUM -> {
                maxHorizontalPartitions = 1
                verticalSpacerSize = 0.dp
            }

            else -> {
                maxHorizontalPartitions = 2
                verticalSpacerSize = 24.dp
            }
        }
    }
    val maxVerticalPartitions: Int
    val horizontalSpacerSize: Dp

    if (windowAdaptiveInfo.windowPosture.isTabletop) {
        maxVerticalPartitions = 2
        horizontalSpacerSize = 24.dp
    } else {
        maxVerticalPartitions = 1
        horizontalSpacerSize = 0.dp
    }

    val defaultPanePreferredWidth = 360.dp

    return PaneScaffoldDirective(
        maxHorizontalPartitions,
        verticalSpacerSize,
        maxVerticalPartitions,
        horizontalSpacerSize,
        defaultPanePreferredWidth,
        getExcludedVerticalBounds(windowAdaptiveInfo.windowPosture, verticalHingePolicy)
    )
}

/**
 * Copied from `getExcludedVerticalBounds()` in [PaneScaffoldDirective] since it is private.
 */
//@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun getExcludedVerticalBounds(posture: Posture, hingePolicy: HingePolicy): List<Rect> {
    return when (hingePolicy) {
        HingePolicy.AvoidSeparating -> posture.separatingVerticalHingeBounds
        HingePolicy.AvoidOccluding -> posture.occludingVerticalHingeBounds
        HingePolicy.AlwaysAvoid -> posture.allVerticalHingeBounds
        else -> emptyList()
    }
}

/**
 * Composable for the Library Screen of the app. Contains windowSizeClass,
 * navigateToPlayer, and viewModel as parameters.
 */
@Composable
fun LibraryScreen(
    windowSizeClass: WindowSizeClass,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Box {
        LibraryScreenReady(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            navigateBack = navigateBack,
            navigateToHome = navigateToHome,
            navigateToLibrary = navigateToLibrary,
            navigateToSettings = navigateToSettings,
            navigateToAlbumDetails = navigateToAlbumDetails,
            navigateToArtistDetails = navigateToArtistDetails,
            navigateToComposerDetails = navigateToComposerDetails,
            navigateToGenreDetails = navigateToGenreDetails,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
            navigateToPlayer = navigateToPlayer,
            navigateToPlayerSong = navigateToPlayerSong,
            viewModel = viewModel,
        )

        if (uiState.errorMessage != null) {
            Text(text = uiState.errorMessage!!)
            LibraryScreenError(onRetry = viewModel::refresh)
        }
    }
}

/**
 * Error Screen
 */
@Composable
private fun LibraryScreenError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(id = R.string.an_error_has_occurred),
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(id = R.string.retry_label))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun LibraryScreenReady(
    uiState: LibraryScreenUiState,
    windowSizeClass: WindowSizeClass,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val navigator = rememberSupportingPaneScaffoldNavigator<String>(
        scaffoldDirective = calculateScaffoldDirective(currentWindowAdaptiveInfo())
    )
    BackHandler(enabled = navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    Surface {
        SupportingPaneScaffold(
            value = navigator.scaffoldValue,
            directive = navigator.scaffoldDirective,
            mainPane = {
                LibraryScreen(
                    //for now so I can see anything load in emulator, gonna put some preview values
                    windowSizeClass = windowSizeClass,
                    isLoading = uiState.isLoading,
                    libraryCategories = uiState.libraryCategories,
                    selectedLibraryCategory = uiState.selectedLibraryCategory,
                    //would probably load in the preferences data store around here to collect what sorting used to view screens
                    libraryAlbums = uiState.libraryAlbums,
                    libraryArtists = uiState.libraryArtists,
                    libraryComposers = uiState.libraryComposers,
                    libraryGenres = uiState.libraryGenres,
                    libraryPlaylists = uiState.libraryPlaylists,
                    libraryPlayerSongs = uiState.libraryPlayerSongs,
                    librarySongs = uiState.librarySongs,
                    totals = uiState.totals,
                    onLibraryAction = viewModel::onLibraryAction,
                    /*navigateToAlbumDetails = { //was navigateToPodcastDetails,
                       //  then navigateToPlaylistDetails but uiState
                       //  doesn't share playlistInfo, but it can
                       //  share genre, album, song infos
                       //  could make this navigateToAlbumDetails. would need an albumInfo that
                       //  can encapsulate the type of properties needed that Podcast has
                        navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, it.id.toString())
                    },*/
                    navigateBack = navigateBack,
                    navigateToHome = navigateToHome,
                    navigateToLibrary = navigateToLibrary,
                    navigateToSettings = navigateToSettings,
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    navigateToArtistDetails = navigateToArtistDetails,
                    navigateToComposerDetails = navigateToComposerDetails,
                    navigateToGenreDetails = navigateToGenreDetails,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    navigateToPlayer = navigateToPlayer,
                    navigateToPlayerSong = navigateToPlayerSong,
                    modifier = Modifier.fillMaxSize()
                )
            },
            supportingPane = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * Composable for Library Screen and its properties needed to render the
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
    libraryPlayerSongs: List<PlayerSong>, //TODO: PlayerSong support
    totals: List<Int>,
    onLibraryAction: (LibraryAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    modifier: Modifier = Modifier
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.song_added_to_your_queue) //TODO: update if need to
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val listState = rememberLazyListState()
    // The FAB is initially shown. Upon scrolling past the first item we hide the FAB by using a
    // remembered derived state to minimize unnecessary compositions.
    val fabVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    LibraryScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavDrawer(
            "Library",
            totals,
            navigateToHome,
            navigateToLibrary,
            navigateToSettings,
            drawerState,
        ) {
            Scaffold(
                topBar = {
                    LibraryAppBar(
                        isExpanded = windowSizeClass.isCompact,
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
                                .padding(horizontal = 16.dp)
                        )
                    }
                },
                /*floatingActionButton = {
                    FloatingActionButton(
                        modifier = Modifier/*.animateFloatingActionButton(
                            visible = fabVisible,
                            alignment = Alignment.BottomEnd
                        )*/,
                        onClick = { /* do something */ },
                    ) {
                        Icon(
                            Icons.Filled.KeyboardDoubleArrowUp,
                            contentDescription = "Jump to top of screen",
                            modifier = Modifier.clip(FloatingActionButtonDefaults.smallShape),
                        )
                    }
                },*/
                /*floatingActionButtonPosition = FabPosition.End,*/
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) { contentPadding ->
                // Main Content
                LibraryContent(
                    selectedLibraryCategory = selectedLibraryCategory,
                    libraryCategories = libraryCategories,
                    libraryAlbums = libraryAlbums,
                    libraryArtists = libraryArtists,
                    libraryComposers = libraryComposers,
                    libraryGenres = libraryGenres,
                    libraryPlaylists = libraryPlaylists,
                    libraryPlayerSongs = libraryPlayerSongs,
                    librarySongs = librarySongs,
                    modifier = Modifier.padding(contentPadding),
                    onLibraryAction = { action ->
                        if (action is LibraryAction.QueueSong) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(snackBarText)
                            }
                        }
                        onLibraryAction(action)
                    },
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    navigateToArtistDetails = navigateToArtistDetails,
                    navigateToComposerDetails = navigateToComposerDetails,
                    navigateToGenreDetails = navigateToGenreDetails,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    navigateToPlayer = navigateToPlayer,
                    navigateToPlayerSong = navigateToPlayerSong,
                )
            }
        }
    }
}

/**
 * Composable for Library Screen's Background.
 */
@Composable
private fun LibraryScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .radialGradientScrim(MaterialTheme.colorScheme.primary)//.copy(alpha = 0.9f))
        )
        content()
    }
}

/**
 * Composable for Library Screen's Top App Bar.
 */
@Composable
private fun LibraryAppBar(
    isExpanded: Boolean,
    onNavigationIconClick: () -> Unit, //use this to capture navDrawer open/close action
    modifier: Modifier = Modifier,
) {
    var queryText by remember {
        mutableStateOf("")
    }
    Row(
//        horizontalArrangement = Arrangement.End,
//        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        IconButton(onClick = onNavigationIconClick) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = stringResource(R.string.cd_more)
            )
        }

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        // search btn
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.cd_more)
            )
        }

        /*SearchBar( //TODO: determine if this can be a shared item & if this can have visibility functionality
            inputField = {
                SearchBarDefaults.InputField(
                    query = queryText,
                    onQueryChange = { queryText = it },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    enabled = true,
                    placeholder = {
                        Text(stringResource(id = R.string.cd_search))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
//                    trailingIcon = {
//                        Icon(
//                            imageVector = Icons.Default.AccountCircle,
//                            contentDescription = stringResource(R.string.cd_account)
//                        )
//                    },
                    interactionSource = null,
                    modifier = if (isExpanded) Modifier.fillMaxWidth() else Modifier
                )
            },
            expanded = false,
            onExpandedChange = {}
        ) {}*/
    }
}

private val FEATURED_ALBUM_IMAGE_SIZE_DP = 160.dp

/**
    Attempt to adjust the library categories so that they selected category determines the grid view
 */
@Composable
private fun LibraryContent(
    selectedLibraryCategory: LibraryCategory,
    libraryCategories: List<LibraryCategory>,
    libraryAlbums: List<AlbumInfo>,
    libraryArtists: List<ArtistInfo>,
    libraryComposers: List<ComposerInfo>,
    libraryGenres: List<GenreInfo>,
    libraryPlaylists: List<PlaylistInfo>,
    librarySongs: List<SongInfo>,
    libraryPlayerSongs: List<PlayerSong>,
    modifier: Modifier = Modifier,
    onLibraryAction: (LibraryAction) -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
) {
    if (selectedLibraryCategory in listOf(
            LibraryCategory.Artists,
            LibraryCategory.Composers,
            LibraryCategory.Genres,
            LibraryCategory.Songs,
        )
    ) {
        LazySingleColumnView(
            libraryCategories = libraryCategories,
            selectedLibraryCategory = selectedLibraryCategory,
            onLibraryAction = onLibraryAction,
            modifier = modifier,

            libraryArtists = libraryArtists,
            libraryComposers = libraryComposers,
            libraryGenres = libraryGenres,
            libraryPlayerSongs = libraryPlayerSongs,
            librarySongs = librarySongs,
            navigateToArtistDetails = navigateToArtistDetails,
            navigateToComposerDetails = navigateToComposerDetails,
            navigateToGenreDetails = navigateToGenreDetails,
            navigateToPlayer = navigateToPlayer,
            navigateToPlayerSong = navigateToPlayerSong,
        )
    }
    else { //selectedLibraryCategory in listOf(LibraryCategory.Playlists, LibraryCategory.Albums, LibraryCategory.Composers))
        LazyAdaptiveColumnView(
            libraryCategories = libraryCategories,
            selectedLibraryCategory = selectedLibraryCategory,
            onLibraryAction = onLibraryAction,
            modifier = modifier,

            libraryAlbums = libraryAlbums,
            libraryPlaylists = libraryPlaylists,
            navigateToAlbumDetails = navigateToAlbumDetails,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
//            navigateToPlayer = navigateToPlayer,
//            navigateToPlayerSong = navigateToPlayerSong
        )
    }
}

/**
 * Lazy Vertical Grid that has fixed column count of 1, renders grid like a lazy list of single row items
 */
@Composable
fun LazySingleColumnView(
    libraryCategories: List<LibraryCategory>,
    selectedLibraryCategory: LibraryCategory,
    onLibraryAction: (LibraryAction) -> Unit,
    modifier: Modifier = Modifier,
    libraryArtists: List<ArtistInfo>,
    libraryComposers: List<ComposerInfo>,
    libraryGenres: List<GenreInfo>,
    libraryPlayerSongs: List<PlayerSong>,
    librarySongs: List<SongInfo>,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.padding(horizontal = 12.dp),
    ) {
        fullWidthItem {
            LibraryCategoryTabs(
                libraryCategories = libraryCategories,
                selectedLibraryCategory = selectedLibraryCategory,
                showHorizontalLine = false,
                onLibraryCategorySelected = {
                    onLibraryAction(
                        LibraryAction.LibraryCategorySelected(
                            it
                        )
                    )
                },
                modifier = Modifier
            )
        }

        when (selectedLibraryCategory) { //composers, songs, artists
            LibraryCategory.Artists -> {
                artistItems(
                    //what would the artists screen need?
                    artists = libraryArtists,
                    navigateToArtistDetails = navigateToArtistDetails,
                )
            }

            LibraryCategory.Composers -> {
                composerItems(
                    //what would the composers screen need?
                    composers = libraryComposers,
                    navigateToComposerDetails = navigateToComposerDetails,
                )
            }

            LibraryCategory.Genres -> {
                genreItems(
                    //what would the genres screen need?
                    genres = libraryGenres,
                    navigateToGenreDetails = navigateToGenreDetails,
                )
            }

            LibraryCategory.Songs -> {
                songItems(
                    //what would the songs screen need?
                    songs = librarySongs,
                    playerSongs = libraryPlayerSongs,
                    navigateToPlayer = navigateToPlayer,
                    navigateToPlayerSong = navigateToPlayerSong,
                    //onQueueSong = { onLibraryAction(LibraryAction.QueueSong(it)) },
                )
            }

            else -> {
                //TODO not sure what to do here. shouldn't really get here since there's if else to catch selectedLibraryCategory value
            }
        }
    }
}

/**
 * Lazy Vertical Grid that has adaptive view sizing based on static dp size
 */
@Composable
fun LazyAdaptiveColumnView(
    libraryCategories: List<LibraryCategory>,
    selectedLibraryCategory: LibraryCategory,
    onLibraryAction: (LibraryAction) -> Unit,
    modifier: Modifier = Modifier,
    libraryAlbums: List<AlbumInfo>,
    libraryPlaylists: List<PlaylistInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
//    navigateToPlayer: (SongInfo) -> Unit,
//    navigateToPlayerSong: (PlayerSong) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(FEATURED_ALBUM_IMAGE_SIZE_DP),
        modifier = modifier.padding(horizontal = 12.dp),
    ) {
        fullWidthItem {
            LibraryCategoryTabs(
                libraryCategories = libraryCategories,
                selectedLibraryCategory = selectedLibraryCategory,
                showHorizontalLine = false,
                onLibraryCategorySelected = {
                    onLibraryAction(
                        LibraryAction.LibraryCategorySelected(
                            it
                        )
                    )
                },
                modifier = Modifier
            )
        }

        when(selectedLibraryCategory) {
            LibraryCategory.Albums -> {
                albumItems(
                    //what would the albums screen need?
                    albums = libraryAlbums,
                    navigateToAlbumDetails = navigateToAlbumDetails,
                )

            }

            LibraryCategory.Playlists -> {
                playlistItems(
                    //what would the playlist screen need?
                    playlists = libraryPlaylists,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    modifier = modifier//.align(Alignment.CenterHorizontally),
                )
            }

            else -> {
                //TODO not sure what to do here. shouldn't really get here since there's if else to catch selectedLibraryCategory value
            }
        }
    }
}




/* //original Library Content, could not get it to separate the lazy vertical grid into different grid cell layouts
@Composable
private fun LibraryContent(
    //showLibraryCategoryTabs: Boolean,
    selectedLibraryCategory: LibraryCategory,
    libraryCategories: List<LibraryCategory>,
    libraryAlbums: List<AlbumInfo>,
    libraryArtists: List<ArtistInfo>,
    libraryComposers: List<ComposerInfo>,
    libraryGenres: List<GenreInfo>,
    libraryPlaylists: List<PlaylistInfo>,
    librarySongs: List<SongInfo>,
    libraryPlayerSongs: List<PlayerSong>,
    modifier: Modifier = Modifier,
    onLibraryAction: (LibraryAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
) {
    // Main Content on Library screen
    //logger.info { "Library Content function start" }
//    val pagerState = rememberPagerState { librarySongs.size }
//    LaunchedEffect(pagerState, librarySongs.size) {
//        snapshotFlow { pagerState.currentPage }
//            .collect {
//                val song = librarySongsModel.songs.getOrNull(it)
//                song?.let { it1 -> LibraryAction.LibrarySongSelected(it1) }
//                    ?.let { it2 -> onLibraryAction(it2) }
//            }
//    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(FEATURED_ALBUM_IMAGE_SIZE_DP),
        modifier = modifier.padding(horizontal = 12.dp),
    ) {
        fullWidthItem {
            LibraryCategoryTabs(
                libraryCategories = libraryCategories,
                selectedLibraryCategory = selectedLibraryCategory,
                showHorizontalLine = false,
                onLibraryCategorySelected = { onLibraryAction(LibraryAction.LibraryCategorySelected(it)) },
                modifier = Modifier
            )
        }

        when (selectedLibraryCategory) {
            LibraryCategory.Playlists -> {
                playlistItems(
                    //what would the playlist screen need?
                    //library = librarySongs,
                    //playlistSortModel = libraryPlaylistsModel,
                    playlists = libraryPlaylists,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    modifier = modifier//.align(Alignment.CenterHorizontally),
                    //onQueueSong = { onLibraryAction(LibraryAction.QueueSong(it)) }
                )
            }

            LibraryCategory.Songs -> {
                songItems(
                    //what would the songs screen need?
                    //navigateToAlbumDetails = navigateToAlbumDetails,
                    //songSortModel = librarySongsModel,
                    songs = librarySongs,
                    playerSongs = libraryPlayerSongs,
                    navigateToPlayer = navigateToPlayer,
                    navigateToPlayerSong = navigateToPlayerSong,
                    //onGenreSelected = { onLibraryAction(LibraryAction.GenreSelected(it)) },
                    //onQueueSong = { onLibraryAction(LibraryAction.QueueSong(it)) },
                )
            }

            LibraryCategory.Artists -> {
                artistItems(
                    //what would the artists screen need?
                    artists = libraryArtists,
                    navigateToArtistDetails = navigateToArtistDetails,
//                    navigateToPlayer = navigateToPlayer,
                    //navigateToPlayerSong = navigateToPlayerSong,
                )
            }

            LibraryCategory.Albums -> {
                albumItems(
                    //what would the albums screen need?
                    albums = libraryAlbums,
                    navigateToAlbumDetails = navigateToAlbumDetails,
//                    navigateToPlayer = navigateToPlayer,
//                    navigateToPlayerSong = navigateToPlayerSong,
                )
            }

            LibraryCategory.Genres -> {
                genreItems(
                    //what would the genres screen need?
                    genres = libraryGenres,
                    navigateToGenreDetails = navigateToGenreDetails,
                )
            }

            LibraryCategory.Composers -> {
                composerItems(
                    //what would the composers screen need?
                    composers = libraryComposers,
                    navigateToComposerDetails = navigateToComposerDetails,
//                    navigateToAlbumDetails = navigateToAlbumDetails,
//                    navigateToPlayer = navigateToPlayer,
//                    navigateToPlayerSong = navigateToPlayerSong,
                )
            }
        }
    }
}*/

/*@Composable
private fun LibraryContentGrid(
    //showLibraryCategoryTabs: Boolean,
    //pagerState: PagerState,
    //featuredAlbums: PersistentList<AlbumInfo>,
    selectedLibraryCategory: LibraryCategory,
    libraryCategories: List<LibraryCategory>,
    //filterableGenresModel: FilterableGenresModel,
    //albumGenreFilterResult: AlbumGenreFilterResult,
    librarySongs: LibraryInfo,
    modifier: Modifier = Modifier,
    onLibraryAction: (LibraryAction) -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    //navigateToGenreDetails: (GenreInfo) -> Unit,
    //navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
) {
    LazyVerticalGrid(
        //columns = GridCells.Adaptive(362.dp),
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = true,
    ) {

//        if (showLibraryCategoryTabs) {
//            fullWidthItem {
//                Text(
//                    text = "Recently Added Songs",
//                    minLines = 1,
//                    maxLines = 1,
//                    style = MaterialTheme.typography.headlineSmall,
//                )
//            }
//            fullWidthItem {
//                //recently added songs as featured songs would go here, limit 5-10. More btn would take to fuller list of songs, limit 100
//
//            }
//        }

        //TODO take this as basis for Library tabbing between playlist, artists, albums, songs, genres, composers
        when (selectedLibraryCategory) {
            LibraryCategory.PlaylistView -> {
//                playlistItems(
//                    //what would the playlist screen need?
//                    library = library,
//                    navigateToPlayer = navigateToPlayer,
//                    onQueueSong = { onLibraryAction(LibraryAction.QueueSong(it)) }
//                )
            }

            LibraryCategory.SongView -> {
                songItems(
                    //what would the songs screen need?
                    //navigateToAlbumDetails = navigateToAlbumDetails,
                    library = librarySongs,
                    navigateToPlayer = navigateToPlayer,
                    //onGenreSelected = { onLibraryAction(LibraryAction.GenreSelected(it)) },
                    onQueueSong = { onLibraryAction(LibraryAction.QueueSong(it)) },
                )
            }

            LibraryCategory.ArtistView -> {
//                artistItems(
//                    //what would the artists screen need?
//                )
            }

            LibraryCategory.AlbumView -> {
//                albumItems(
//                    //what would the albums screen need?
//                )
            }

            LibraryCategory.GenreView -> {
//                genreItems(
//                    //what would the genres screen need?
//                )
            }

            LibraryCategory.ComposerView -> {
//                composerItems(
//                    //what would the composers screen need?
//                )
            }
        }
    }
}*/

/*@Composable
private fun LibraryCategoryTabs(
    libraryCategories: List<LibraryCategory>,
    selectedLibraryCategory: LibraryCategory,
    onLibraryCategorySelected: (LibraryCategory) -> Unit,
    showHorizontalLine: Boolean,
    modifier: Modifier = Modifier,
) {
    if (libraryCategories.isEmpty()) {
        return
    }

    val selectedIndex = libraryCategories.indexOfFirst { it == selectedLibraryCategory }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        LibraryCategoryTabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
        )
    }

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        indicator = indicator,
        modifier = modifier,
        divider = {
            if (showHorizontalLine) {
                HorizontalDivider()
            }
        }
    ) {
        libraryCategories.forEachIndexed { index, libraryCategory ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onLibraryCategorySelected(libraryCategory) },
                text = {
                    Text(
                        text = when (libraryCategory) {
                            LibraryCategory.PlaylistView -> stringResource(R.string.library_playlists)
                            LibraryCategory.SongView -> stringResource(R.string.library_songs)
                            LibraryCategory.ArtistView -> stringResource(R.string.library_artists)
                            LibraryCategory.AlbumView -> stringResource(R.string.library_albums)
                            LibraryCategory.GenreView -> stringResource(R.string.library_genres)
                            LibraryCategory.ComposerView -> stringResource(R.string.library_composers)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }
}*/

@Composable
private fun LibraryCategoryTabs(
    libraryCategories: List<LibraryCategory>,
    selectedLibraryCategory: LibraryCategory,
    onLibraryCategorySelected: (LibraryCategory) -> Unit,
    showHorizontalLine: Boolean,
    modifier: Modifier = Modifier,
) {
    if (libraryCategories.isEmpty()) {
        return
    }
    var selectedLibCatIndex by remember {mutableStateOf(0)}
    val selectedIndex = libraryCategories.indexOfFirst { it == selectedLibraryCategory }

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        LibraryCategoryTabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
        )
    }
    /*
    indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    color = Color.Black,
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .fillMaxWidth()
                )
            }
     */

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        indicator = indicator,
        modifier = Modifier,
        divider = {
            if (showHorizontalLine) {
                HorizontalDivider()
            }
        }
    ) {
        libraryCategories.forEachIndexed { index, libraryCategory ->
            Tab(
                selected = index == selectedIndex,
                //selectedContentColor = MaterialTheme.colorScheme.tertiary,
                onClick = { onLibraryCategorySelected(libraryCategory) },
                modifier = Modifier.padding(0.dp),
                content = {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = libraryCategory.name,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                /*text = {
                    Text(
                        text = when (libraryCategory) {
                            LibraryCategory.PlaylistView -> stringResource(R.string.library_playlists)
                            LibraryCategory.SongView -> stringResource(R.string.library_songs)
                            LibraryCategory.ArtistView -> stringResource(R.string.library_artists)
                            LibraryCategory.AlbumView -> stringResource(R.string.library_albums)
                            LibraryCategory.GenreView -> stringResource(R.string.library_genres)
                            LibraryCategory.ComposerView -> stringResource(R.string.library_composers)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }*/
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

/* //Featured Items Carousel, not used in this screen
@Composable
private fun FeaturedAlbums(
    pagerState: PagerState,
    items: PersistentList<AlbumInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: Using BoxWithConstraints is not quite performant since it requires 2 passes to compute
    // the content padding. This should be revisited once a carousel component is available.
    // Alternatively, version 1.7.0-alpha05 of Compose Foundation supports `snapPosition`
    // which solves this problem and avoids this calculation altogether. Once 1.7.0 is
    // stable, this implementation can be updated.
    BoxWithConstraints(
        modifier = modifier.background(Color.Transparent)
    ) {
        val horizontalPadding = (this.maxWidth - FEATURED_ALBUM_IMAGE_SIZE_DP) / 2
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(
                horizontal = horizontalPadding,
                vertical = 16.dp,
            ),
            pageSpacing = 24.dp,
            pageSize = PageSize.Fixed(FEATURED_ALBUM_IMAGE_SIZE_DP)
        ) { page ->
            val album = items[page]
            FeaturedAlbumCarouselItem(
                albumImage = 1,//album.artwork!!,
                albumTitle = album.title,
                //dateLastPlayed = album.dateLastPlayed?.let { lastUpdated(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        navigateToAlbumDetails(album)
                    }
            )
        }
    }
}

@Composable
private fun FeaturedAlbumCarouselItem(
    albumTitle: String,
    //albumImage: String,
    albumImage: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Box(
            Modifier
                .size(FEATURED_ALBUM_IMAGE_SIZE_DP)
                .align(Alignment.CenterHorizontally)
        ) {
            AlbumImage(
                albumImage = albumImage,
                contentDescription = albumTitle,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
            )
        }
        Text(
            text = albumTitle,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}*/

@Composable
private fun lastUpdated(updated: OffsetDateTime): String {
    val duration = Duration.between(updated.toLocalDateTime(), LocalDateTime.now())
    val days = duration.toDays().toInt()

    return when {
        days > 28 -> stringResource(R.string.updated_longer)
        days >= 7 -> {
            val weeks = days / 7
            quantityStringResource(R.plurals.updated_weeks_ago, weeks, weeks)
        }

        days > 0 -> quantityStringResource(R.plurals.updated_days_ago, days, days)
        else -> stringResource(R.string.updated_today)
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Preview
@Composable
private fun LibraryAppBarPreview() {
    MusicTheme {
        LibraryAppBar(
            isExpanded = false,
            onNavigationIconClick = {},
        )
    }
}

private val CompactWindowSizeClass = WindowSizeClass.compute(360f, 780f)

//@DevicePreviews
@Preview
@Composable
private fun PreviewLibrary() {
    MusicTheme {
        LibraryScreen(
            windowSizeClass = CompactWindowSizeClass,
            isLoading = false,
            libraryCategories = LibraryCategory.entries,
            selectedLibraryCategory = LibraryCategory.Playlists,
            libraryAlbums = PreviewAlbums,
            libraryArtists = PreviewArtists,
            libraryComposers = PreviewComposers,
            libraryGenres = PreviewGenres,
            libraryPlaylists = PreviewPlaylists,
            libraryPlayerSongs = PreviewPlayerSongs,
            librarySongs = PreviewSongs,
            totals = listOf(
                PreviewSongs.size,
                PreviewArtists.size,
                PreviewAlbums.size,
                PreviewPlaylists.size),
            onLibraryAction = {},
            navigateBack = {},
            navigateToHome = {},
            navigateToLibrary = {},
            navigateToSettings = {},
            navigateToAlbumDetails = {},
            navigateToArtistDetails = {},
            navigateToPlaylistDetails = {},
            navigateToGenreDetails = {},
            navigateToComposerDetails = {},
            navigateToPlayer = {},
            navigateToPlayerSong = {},
        )
    }
}
