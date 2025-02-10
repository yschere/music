package com.example.music.ui.home

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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import com.example.music.model.FeaturedLibraryItemsFilterResult
import com.example.music.model.PlaylistInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.theme.MusicTheme
//import com.example.music.util.MenuItem
import com.example.music.util.fullWidthItem
import com.example.music.util.isCompact
import com.example.music.util.quantityStringResource
import com.example.music.util.radialGradientScrim
import com.example.music.util.verticalGradientScrim
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import org.apache.log4j.BasicConfigurator
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

private val logger = KotlinLogging.logger{}

/**
 * Composable for the Main Screen of the app. Contains windowSizeClass,
 * navigateToPlayer, and viewModel as parameters.
 */
@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    navigateToLibrary: () -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit, //TODO: PlayerSong support
    viewModel: HomeViewModel = hiltViewModel()
) {
    BasicConfigurator.configure()
    val homeScreenUiState by viewModel.state.collectAsStateWithLifecycle()
    val uiState = homeScreenUiState
    Box {
        logger.info { "Main Screen function start" }
        HomeScreenReady(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
            navigateToLibrary = navigateToLibrary,
            navigateToPlayer = navigateToPlayer,
            navigateToPlayerSong = navigateToPlayerSong,
            viewModel = viewModel,
        )

        if (uiState.errorMessage != null) {
            HomeScreenError(onRetry = viewModel::refresh)
        }
        logger.info { "Main Screen function end" }
    }
}

@Composable
private fun HomeScreenError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    logger.info { "Home Screen Error function start" }
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
private fun HomeScreenReady(
    uiState: HomeScreenUiState,
    windowSizeClass: WindowSizeClass,
    navigateToLibrary: () -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit, //TODO: PlayerSong support
    viewModel: HomeViewModel = hiltViewModel()
) {
    logger.info { "Home Screen Ready function start" }

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
                HomeScreen(
                    windowSizeClass = windowSizeClass,
                    isLoading = uiState.isLoading,
                    featuredLibraryItemsFilterResult = uiState.featuredLibraryItemsFilterResult,
                    librarySongs = uiState.playerSongs, //TODO: PlayerSong support
                    onHomeAction = viewModel::onHomeAction,
                    /*navigateToPlaylistDetails = {
                        navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, it.id.toString())
                        //navigator to supporting pane scaffold
                    },*/
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    navigateToLibrary = navigateToLibrary,
                    navigateToPlayer = navigateToPlayer,
                    navigateToPlayerSong = navigateToPlayerSong, //TODO: PlayerSong support
                    modifier = Modifier
                        .fillMaxSize()
                )
            },
            //TODO: when navigateTo___Details determined, need to update this. it's based on PodcastDetailsViewModel
            supportingPane = {
//                val albumId = navigator.currentDestination?.content
//                if (!albumId.isNullOrEmpty()) {
//                    val albumDetailsViewModel = hiltViewModel<AlbumDetailsViewModel, AlbumDetailsViewModel.AlbumDetailsViewModelFactory>(
//                            key = albumId
//                        ) { it.create(albumId.toLong()) }
//                    //TODO: change the podcastDetails section to handle playlist Details view
//                    //TODO: or to handle album/artist Details
//                    AlbumDetailsScreen(
//                        viewModel = albumDetailsViewModel,
//                        navigateToPlayer = navigateToPlayer,
//                        navigateBack = {
//                            if (navigator.canNavigateBack()) {
//                                navigator.navigateBack()
//                            }
//                        },
//                        showBackButton = navigator.isMainPaneHidden(),
//                    )
//                }
            },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

/**
 * Composable for Home Screen and its properties needed to render the
 * components of the page.
 */
@Composable
private fun HomeScreen(
    windowSizeClass: WindowSizeClass,
    isLoading: Boolean,
    featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterResult,
    //featuredPlaylists: PersistentList<PlaylistInfo>,
    //featuredSongs: PersistentList<SongInfo>,
    //library: LibraryInfo,
    librarySongs: List<PlayerSong>, //TODO: PlayerSong support
    onHomeAction: (HomeAction) -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit, //TODO: PlayerSong support
    modifier: Modifier = Modifier
) {
    logger.info { "Home Screen function start" }

    // Effect that changes the home category selection when there are no subscribed podcasts
    //TODO: repurpose this for RecentPlaylists, so that if there's no recent playlists as featured playlists, have a defaulted view
    LaunchedEffect(key1 = featuredLibraryItemsFilterResult.recentPlaylists) {
        if (featuredLibraryItemsFilterResult.recentPlaylists.isEmpty()) {
            onHomeAction(HomeAction.EmptyLibraryView(PlaylistInfo()))
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.song_added_to_your_queue) //TODO: update if need to
    //create scaffoldState here for drawerNav
    //val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    HomeScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            topBar = {
                HomeAppBar(
                    isExpanded = windowSizeClass.isCompact,
                    isSearchOn = false,
//                    drawerState = drawerState,
//                    //if include navMenu, would also have
//                    onNavigationIconClick = {
//                        coroutineScope.launch {
//                            drawerState.close()
//                        }
//                    },
                    navigateToLibrary = navigateToLibrary,
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
            //modifier = modifier,
            containerColor = Color.Transparent,
        ) { contentPadding ->
            // Main Content
            HomeContent(
                featuredLibraryItemsFilterResult = featuredLibraryItemsFilterResult,
                //featuredPlaylists = featuredLibraryItemsFilterResult.recentPlaylists.toPersistentList(),
                //featuredSongs = featuredLibraryItemsFilterResult.recentlyAddedSongs.toPersistentList(),
                librarySongs = librarySongs, //TODO: PlayerSong support
                modifier = modifier/*.statusBarsPadding()*/.padding(contentPadding), //this contentPadding comes from the Scaffold
                onHomeAction = { action ->
                    if (action is HomeAction.QueueSong) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(snackBarText)
                        }
                    }
                    onHomeAction(action)
                },
                navigateToPlaylistDetails = navigateToPlaylistDetails,
                navigateToPlayer = navigateToPlayer,
                navigateToPlayerSong = navigateToPlayerSong, //TODO: PlayerSong support
            )
        }
    }
}

/**
 * Composable for Home Screen's Background.
 */
@Composable
private fun HomeScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    logger.info { "Home Background function start" }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .radialGradientScrim(MaterialTheme.colorScheme.primary)
        )
        content()
    }
}

/**
 * Composable for Home Screen's Top App Bar.
 */
@Composable
private fun HomeAppBar(
    isSearchOn: Boolean,
//    drawerState: DrawerState,
    isExpanded: Boolean,
    navigateToLibrary: () -> Unit,
//    onNavigationIconClick: () -> Unit, //use this to capture navDrawer open/close action
    modifier: Modifier = Modifier,
) {
    logger.info { "Home App Bar function start" }

    var queryText by remember {
        mutableStateOf("")
    }
    Row(
        //horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
        //.background(Color.Transparent)
    ) {
        //if (!isSearchOn) {

        //not search time
        //IconButton(onClick = {}) { //
        IconButton(onClick = {}) {//onNavigationIconClick) {
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
        /*} else {
            // search time
            //back button
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back)
                )
            }

            //right align objects after this space
            Spacer(Modifier.weight(1f))

            SearchBar( //TODO: determine if this can be a shared item & if this can have visibility functionality
                inputField = {
                    SearchBarDefaults.InputField(
                        query = queryText,
                        onQueryChange = { queryText = it },
                        onSearch = {},
                        expanded = true,
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
//                        trailingIcon = {
//                            Icon(
//                                imageVector = Icons.Default.AccountCircle,
//                                contentDescription = stringResource(R.string.cd_account)
//                            )
//                        },
                        interactionSource = null,
                        modifier = if (isExpanded) Modifier.fillMaxWidth() else Modifier
                    )
                },
                expanded = false,
                onExpandedChange = {}
            ) {}
        }*/
    }
}

/* //Nav Menu Composable
@Composable
fun NavDrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Text(text = "Musicality", style = MaterialTheme.typography.displaySmall)
    }
}

data class MenuItem(
    val id: Long,
    val title: String,
    val contentDescription: String,
    val dest: Screen,
    val isClickable: Boolean
)

@Composable
fun NavDrawerBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    onItemClick: (MenuItem) -> Unit,
) {
    LazyColumn(modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{
                        onItemClick(item)
                    }.padding(16.dp)
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}*/

@Composable
private fun HomeContent(
    featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterResult,
    //featuredPlaylists: PersistentList<PlaylistInfo>,
    //featuredSongs: PersistentList<SongInfo>,
    //library: LibraryInfo,
    librarySongs: List<PlayerSong>, //TODO: PlayerSong support
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit, //TODO: PlayerSong support
) {
    // Main Content on Home screen
    logger.info { "Home Content function start" }
    val pLists = featuredLibraryItemsFilterResult.recentPlaylists.toPersistentList()
    val pagerState = rememberPagerState { pLists.size }
    LaunchedEffect(pagerState, pLists) {
        snapshotFlow { pagerState.currentPage }
            .collect {
//                val playlist = pLists.getOrNull(it)
//                playlist?.let { it1 -> HomeAction.LibraryPlaylistSelected(it1) }
//                    ?.let { it2 -> onHomeAction(it2) }
            }//crashes the app on Home screen redraw
    } //this section is called on every redraw for Home Screen
    logger.info { "Home Content - HomeContentGrid function call" }
    HomeContentGrid(
        pagerState = pagerState,
        featuredLibraryItemsFilterResult = featuredLibraryItemsFilterResult,
        //featuredPlaylists = featuredPlaylists,
        //featuredSongs = featuredSongs,
        //library = library,
        librarySongs = librarySongs, //TODO: PlayerSong support
        modifier = modifier,
        onHomeAction = onHomeAction,
        navigateToPlaylistDetails = navigateToPlaylistDetails,
        navigateToPlayer = navigateToPlayer,
        navigateToPlayerSong = navigateToPlayerSong, //TODO: PlayerSong support
    )
}

@Composable
private fun HomeContentGrid(
    pagerState: PagerState,
    featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterResult,
    //featuredPlaylists: PersistentList<PlaylistInfo>,
    //featuredSongs: PersistentList<SongInfo>,
    //library: LibraryInfo,
    librarySongs: List<PlayerSong>, //TODO: PlayerSong support
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit, //TODO: PlayerSong support
) {
    logger.info { "Home Content Grid function start" }
    LazyVerticalGrid(
        //columns = GridCells.Adaptive(362.dp),
        //columns = GridCells.Adaptive(250.dp),
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize()//.padding(horizontal = 4.dp)
    ) {
        logger.info { "Home Content Grid - layer vertical grid start" }
        logger.info { "featuredLibraryItemsFilterResult - recentPlaylists size: ${featuredLibraryItemsFilterResult.recentPlaylists.size}" }
        if (featuredLibraryItemsFilterResult.recentPlaylists.isNotEmpty()) {
            fullWidthItem {
                Text(
                    text = "Recent Playlists",
                    minLines = 1,
                    maxLines = 1,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
            fullWidthItem {
                //recent playlists as featured playlists would go here
                FeaturedPlaylistItem(
                    pagerState = pagerState,
                    items = featuredLibraryItemsFilterResult.recentPlaylists.toPersistentList(),
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        logger.info { "featuredLibraryItemsFilterResult - recentlyAddedSongs size: ${featuredLibraryItemsFilterResult.recentlyAddedSongs.size}" }
        if (featuredLibraryItemsFilterResult.recentlyAddedSongs.isNotEmpty()) { //featuredSongs as recent songs would go here and would need isNotEmpty check
            fullWidthItem {
                Text(
                    text = stringResource(R.string.recent_songs),
                    minLines = 1,
                    maxLines = 1,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
            /**
             * ORIGINAL VERSION: using featuredLibraryItemsFilterResult.recentlyAddedSongs which is List<SongInfo>
             */
            /*items(featuredLibraryItemsFilterResult.recentlyAddedSongs, key = {it.id}) { song ->
                //recently added songs as featured songs would go here, limit 5-10. More btn would take to fuller list of songs, limit 100
                SongListItem(
                    song = song,
                    album = getAlbumData(song.albumId!!),
                    onClick = navigateToPlayer,
                    onQueueSong = { },
                    modifier = Modifier.fillMaxWidth(),
                    isListEditable = false,
                    showArtistName = true,
                    showAlbumImage = true,
                    showAlbumTitle = true,
                    showDuration = true,
                )
            }*/

            /**
             * PLAYERSONG SUPPORT VERSION: using HomeScreen's uiState.librarySongs which is List<PlayerSong>
             */
            items(librarySongs) { song ->
                logger.info { "Home Content Grid - songs layout for song ${song.id}" }
                //recently added songs as featured songs would go here, limit 5-10. More btn would take to fuller list of songs, limit 100
                Box(Modifier.padding(horizontal = 12.dp, vertical = 0.dp)) {
                    SongListItem(
                        song = song,
                        onClick = navigateToPlayerSong,
                        onQueueSong = { },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        isListEditable = false,
                        showArtistName = true,
                        showAlbumImage = true,
                        showAlbumTitle = true,
                        showDuration = true,
                    )
                }
            }
        }
        logger.info { "Home Content Grid - lazy vertical grid end" }
    }
    logger.info { "Home Content Grid function end" }
}

@Composable
private fun FeaturedPlaylistItem(
    pagerState: PagerState,
    items: PersistentList<PlaylistInfo>,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    logger.info { "Featured Playlist Item function start" }
    Column(modifier = modifier) {
        FeaturedPlaylists(
            pagerState = pagerState,
            items = items,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private val FEATURED_PLAYLIST_IMAGE_SIZE_DP = 160.dp

@Composable
private fun FeaturedPlaylists(
    pagerState: PagerState,
    items: PersistentList<PlaylistInfo>,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    logger.info { "Featured Playlists function start" }
    // TODO: Using BoxWithConstraints is not quite performant since it requires 2 passes to compute
    // the content padding. This should be revisited once a carousel component is available.
    // Alternatively, version 1.7.0-alpha05 of Compose Foundation supports `snapPosition`
    // which solves this problem and avoids this calculation altogether. Once 1.7.0 is
    // stable, this implementation can be updated.
    BoxWithConstraints(
        modifier = modifier.background(Color.Transparent)
    ) {
        val horizontalPadding = (this.maxWidth - FEATURED_PLAYLIST_IMAGE_SIZE_DP) / 2
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(
                horizontal = horizontalPadding,
                vertical = 16.dp,
            ),
            pageSpacing = 24.dp,
            pageSize = PageSize.Fixed(FEATURED_PLAYLIST_IMAGE_SIZE_DP)
        ) { page ->
            val playlist = items[page]
            logger.info { "Home Content Grid - playlists layout for playlist ${playlist.id}" }
            FeaturedPlaylistCarouselItem(
                playlistImage = 1,//album.artwork!!,
                playlistTitle = playlist.name,
                //dateLastPlayed = album.dateLastPlayed?.let { lastUpdated(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        navigateToPlaylistDetails(playlist)
                    }
            )
        }
    }
}

@Composable
private fun FeaturedPlaylistCarouselItem(
    playlistTitle: String,
    //albumImage: String,
    playlistImage: Int,
    modifier: Modifier = Modifier,
) {
    logger.info { "Featured Playlist Carousel Item function start" }
    Column(modifier) {
        Box(
            Modifier
                .size(FEATURED_PLAYLIST_IMAGE_SIZE_DP)
                .align(Alignment.CenterHorizontally)
        ) {
            AlbumImage(
                albumImage = playlistImage,
                contentDescription = playlistTitle,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
            )
        }
        Text(
            text = playlistTitle,
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

private val CompactWindowSizeClass = WindowSizeClass.compute(360f, 780f)

//@DevicePreviews
@Preview
@Composable
private fun PreviewHome() {
    MusicTheme {
        HomeScreen(
            windowSizeClass = CompactWindowSizeClass,
            isLoading = false,
            featuredLibraryItemsFilterResult = FeaturedLibraryItemsFilterResult(
                recentPlaylists = PreviewPlaylists,
                recentlyAddedSongs = PreviewSongs
            ),
            //featuredPlaylists = PreviewPlaylists.toPersistentList(),
            //featuredSongs = PreviewSongs.toPersistentList(),
            //library = LibraryInfo(PreviewAlbumSongs),
            librarySongs = PreviewPlayerSongs,
            onHomeAction = {},
            navigateToLibrary = {},
            navigateToPlaylistDetails = {},
            navigateToPlayer = {},
            navigateToPlayerSong = {},
        )
    }
}

//@Preview
@Composable
private fun PreviewPodcastCard() {
    MusicTheme {
        FeaturedPlaylistCarouselItem(
            modifier = Modifier.size(128.dp),
            playlistTitle = "List of",
            playlistImage = 0,
        )
    }
}
