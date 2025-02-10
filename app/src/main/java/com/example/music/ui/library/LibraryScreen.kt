package com.example.music.ui.library

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.domain.testing.PreviewPlayerSongs
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.model.AlbumInfo
import com.example.music.model.ArtistInfo
import com.example.music.model.FilterableGenresModel
import com.example.music.model.GenreInfo
import com.example.music.model.LibraryInfo
import com.example.music.model.PlaylistInfo
import com.example.music.model.PlaylistSortModel
import com.example.music.model.SongInfo
import com.example.music.model.SongSortModel
import com.example.music.player.model.PlayerSong
import com.example.music.ui.library.LibraryAction
import com.example.music.ui.library.LibraryScreenUiState
import com.example.music.ui.library.LibraryViewModel
import com.example.music.ui.library.playlist.playlistItems
import com.example.music.ui.library.song.songItems
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.isCompact
import com.example.music.util.quantityStringResource
import com.example.music.util.radialGradientScrim
import kotlinx.collections.immutable.PersistentList
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

//private val logger = KotlinLogging.logger{}

/**
 * Composable for the Main Screen of the app. Contains windowSizeClass,
 * navigateToPlayer, and viewModel as parameters.
 */
@Composable
fun LibraryScreen(
    windowSizeClass: WindowSizeClass,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    //navigateToGenreDetails: (GenreInfo) -> Unit,
    //navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    //BasicConfigurator.configure()
    val libraryScreenUiState by viewModel.state.collectAsStateWithLifecycle()
    val uiState = libraryScreenUiState

    Surface {
        LibraryScreenReady(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            navigateBack = navigateBack,
            navigateToHome = navigateToHome,
            navigateToAlbumDetails = navigateToAlbumDetails,
            navigateToArtistDetails = navigateToArtistDetails,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
            navigateToPlayer = navigateToPlayer,
            viewModel = viewModel,
        )

        if (uiState.errorMessage != null) {
            LibraryScreenError(onRetry = viewModel::refresh)
        }
    }
}

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
    //need prop for GetLibraryAlbumsUseCase
    //need prop for GetLibraryArtistsUseCase
    //need prop for GetLibraryComposersUseCase
    //need prop for GetLibraryGenresUseCase
    //need prop for GetLibrarySongsUseCase
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    //navigateToGenreDetails: (GenreInfo) -> Unit,
    //navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
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
                    //librarySongs = uiState.librarySongs,
                    librarySongsModel = uiState.librarySongsModel,
                    libraryPlaylistsModel = uiState.libraryPlaylistsModel,
                    libraryPlayerSongs = uiState.libraryPlayerSongs,
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
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    navigateToArtistDetails = navigateToArtistDetails,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    navigateToPlayer = navigateToPlayer,
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
    //librarySongs: LibraryInfo,
    libraryPlaylistsModel: PlaylistSortModel,
    libraryPlayerSongs: List<PlayerSong>,
    librarySongsModel: SongSortModel,
    onLibraryAction: (LibraryAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    //navigateToGenreDetails: (GenreInfo) -> Unit,
    //navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    modifier: Modifier = Modifier
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.song_added_to_your_queue) //TODO: update if need to

    LibraryScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            topBar = {
                LibraryAppBar(
                    isExpanded = windowSizeClass.isCompact,
                    navigateToHome = navigateToHome,
                    navigateToPlayer = navigateToPlayer,
                )
                if (isLoading) {
                    LinearProgressIndicator(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = Color.Transparent
        ) { contentPadding ->
            // Main Content
            val showLibraryCategoryTabs = libraryCategories.isNotEmpty() //TODO: repurpose this in Library
            LibraryContent(
                showLibraryCategoryTabs = showLibraryCategoryTabs,
                //featuredAlbums = featuredAlbums,
                selectedLibraryCategory = selectedLibraryCategory,
                libraryCategories = libraryCategories,
                //filterableGenresModel = filterableGenresModel,
                //albumGenreFilterResult = albumGenreFilterResult,
                libraryPlaylistsModel = libraryPlaylistsModel,
                libraryPlayerSongs = libraryPlayerSongs,
                librarySongsModel = librarySongsModel,
                modifier = Modifier.padding(contentPadding),
                onLibraryAction = { action ->
                    if (action is LibraryAction.QueueSong) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(snackBarText)
                        }
                    }
                    onLibraryAction(action)
                },
                navigateBack = navigateBack,
                navigateToHome = navigateToHome,
                navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToArtistDetails = navigateToArtistDetails,
                navigateToPlaylistDetails = navigateToPlaylistDetails,
                navigateToPlayer = navigateToPlayer,
            )
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryAppBar(
    isExpanded: Boolean,
    navigateToHome: () -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    var queryText by remember {
        mutableStateOf("")
    }
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        //IconButton(onClick = {}) { //
        IconButton(onClick = { /* TODO */ }) {
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

@Composable
private fun LibraryContent(
    showLibraryCategoryTabs: Boolean,
    //featuredAlbums: PersistentList<AlbumInfo>,
    selectedLibraryCategory: LibraryCategory,
    libraryCategories: List<LibraryCategory>,
    libraryPlaylistsModel: PlaylistSortModel,
    libraryPlayerSongs: List<PlayerSong>,
    librarySongsModel: SongSortModel,
    //librarySongs: LibraryInfo,
    modifier: Modifier = Modifier,
    onLibraryAction: (LibraryAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    //navigateToGenreDetails: (GenreInfo) -> Unit,
    //navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
) {
    // Main Content on Library screen
    //logger.info { "Library Content function start" }
    val pagerState = rememberPagerState { librarySongsModel.count }
    LaunchedEffect(pagerState, librarySongsModel.songs) {
        snapshotFlow { pagerState.currentPage }
            .collect {
//                val song = librarySongsModel.songs.getOrNull(it)
//                song?.let { it1 -> LibraryAction.LibrarySongSelected(it1) }
//                    ?.let { it2 -> onLibraryAction(it2) }
            }
    }


//    LibraryCategoryTabs(
//        libraryCategories = libraryCategories,
//        selectedLibraryCategory = selectedLibraryCategory,
//        showHorizontalLine = false,
//        onLibraryCategorySelected = { onLibraryAction(LibraryAction.LibraryCategorySelected(it)) },
//        modifier = Modifier
//    )

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
                    playlistSortModel = libraryPlaylistsModel,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    modifier = modifier//.align(Alignment.CenterHorizontally),
                    //onQueueSong = { onLibraryAction(LibraryAction.QueueSong(it)) }
                )
            }

            LibraryCategory.Songs -> {
                songItems(
                    //what would the songs screen need?
                    //navigateToAlbumDetails = navigateToAlbumDetails,
                    songSortModel = librarySongsModel,
                    playerSongs = libraryPlayerSongs,
                    navigateToPlayer = navigateToPlayer,
                    //onGenreSelected = { onLibraryAction(LibraryAction.GenreSelected(it)) },
                    onQueueSong = { onLibraryAction(LibraryAction.QueueSong(it)) },
                )
            }

            LibraryCategory.Artists -> {
//                artistItems(
//                    //what would the artists screen need?
//                )
            }

            LibraryCategory.Albums -> {
//                albumItems(
//                    //what would the albums screen need?
//                )
            }

            LibraryCategory.Genres -> {
//                genreItems(
//                    //what would the genres screen need?
//                )
            }

            LibraryCategory.Composers -> {
//                composerItems(
//                    //what would the composers screen need?
//                )
            }
        }
    }
}

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

//@Composable
//private fun FeaturedAlbumItem(
//    pagerState: PagerState,
//    items: PersistentList<AlbumInfo>,
//    navigateToAlbumDetails: (AlbumInfo) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    Column(modifier = modifier) {
//        //Spacer(Modifier.height(16.dp))
//
//        FeaturedAlbums(
//            pagerState = pagerState,
//            items = items,
//            navigateToAlbumDetails = navigateToAlbumDetails,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        //Spacer(Modifier.height(16.dp))
//    }
//}

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
}

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
@Preview
@Composable
private fun LibraryAppBarPreview() {
    MusicTheme {
        LibraryAppBar(
            isExpanded = false,
            navigateToHome = {},
            navigateToPlayer = {},
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
            librarySongsModel = SongSortModel(
                songs = PreviewSongs,
                count = PreviewSongs.size
            ),
            libraryPlaylistsModel = PlaylistSortModel(
                playlists = PreviewPlaylists,
                count = PreviewPlaylists.size
            ),
            libraryPlayerSongs = PreviewPlayerSongs,
            //librarySongs = LibraryInfo(PreviewAlbumSongs),
            onLibraryAction = {},
            navigateBack = {},
            navigateToHome = {},
            navigateToAlbumDetails = {},
            navigateToArtistDetails = {},
            navigateToPlaylistDetails = {},
            //navigateToGenreDetails = {},
            //navigateToComposerDetails = {},
            navigateToPlayer = {},
        )
    }
}

//@Preview
@Composable
private fun PreviewPodcastCard() {
    MusicTheme {
        FeaturedAlbumCarouselItem(
            modifier = Modifier.size(128.dp),
            albumTitle = "List of",
            albumImage = 0,
        )
    }
}
