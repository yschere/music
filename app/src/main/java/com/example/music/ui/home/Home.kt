package com.example.music.ui.home

import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.TopAppBarExpandedHeight
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
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
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.model.FeaturedLibraryItemsFilterV2
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.ui.shared.CustomDragHandle
import com.example.music.ui.shared.FeaturedAlbumsCarousel
import com.example.music.ui.shared.NavDrawer
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.shared.formatStr
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.util.fullWidthItem
import com.example.music.util.isCompact
import com.example.music.util.quantityStringResource
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

/** Changelog:
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 4/11/2025 - Connected search implementation in HomeViewModel and domain's SearchQueryV2 to
 * the HomeScreen in HomeTopAppBarV2
 *
 * 4/13/2025 - Further revised search implementation in app so that it is on a separate screen
 * SearchScreen / SearchQueryViewModel, and now tapping the Search Icon in the TopAppBar
 * will navigate to SearchScreen view. Removed HomeTopAppBarV2.
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

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
private fun getExcludedVerticalBounds(posture: Posture, hingePolicy: HingePolicy): List<Rect> {
    return when (hingePolicy) {
        HingePolicy.AvoidSeparating -> posture.separatingVerticalHingeBounds
        HingePolicy.AvoidOccluding -> posture.occludingVerticalHingeBounds
        HingePolicy.AlwaysAvoid -> posture.allVerticalHingeBounds
        else -> emptyList()
    }
}

/**
 * Composable for the Main Screen of the app. Contains windowSizeClass,
 * navigateToPlayer, and viewModel as parameters.
 */
@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    //navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Box {
        HomeScreenReady(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            navigateToHome = navigateToHome,
            navigateToAlbumDetails = navigateToAlbumDetails,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
            navigateToLibrary = navigateToLibrary,
            navigateToPlayer = navigateToPlayer,
            navigateToSearch = navigateToSearch,
            navigateToSettings = navigateToSettings,
            viewModel = viewModel,
        )

        if (uiState.errorMessage != null) {
            HomeScreenError(onRetry = viewModel::refresh)
        }
    }
}

/**
 * Error Screen
 */
@Composable
private fun HomeScreenError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
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
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
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
                HomeScreen(
                    windowSizeClass = windowSizeClass,
                    isLoading = uiState.isLoading,
                    featuredLibraryItemsFilterResult = uiState.featuredLibraryItemsFilterResult,
                    totals = uiState.totals,
                    selectedSong = uiState.selectSong,
                    onHomeAction = viewModel::onHomeAction,
                    /*navigateToPlaylistDetails = {
                        navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, it.id.toString())
                        //navigator to supporting pane scaffold
                    },*/
                    navigateToHome = navigateToHome,
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    navigateToLibrary = navigateToLibrary,
                    navigateToPlayer = navigateToPlayer,
                    navigateToSettings = navigateToSettings,
                    navigateToSearch = navigateToSearch,
                    modifier = Modifier.fillMaxSize()
                )
            },
            //FixMe: when navigateTo___Details determined, need to update this. it's based on PodcastDetailsViewModel
            supportingPane = {
                /*val playlistId = navigator.currentDestination?.content
                if (!playlistId.isNullOrEmpty()) {
                    val playlistDetailsViewModel = hiltViewModel<PlaylistDetailsViewModel, PlaylistDetailsViewModel.PlaylistDetailsViewModelFactory>(
                            key = playlistId
                        ) { it.create(playlistId.toLong()) }
                    PlaylistDetailsScreen(
                        viewModel = playlistDetailsViewModel,
                        navigateToPlayer = navigateToPlayer,
                        navigateToPlayerSong = navigateToPlayerSong,
                        navigateBack = {
                            if (navigator.canNavigateBack()) {
                                navigator.navigateBack()
                            }
                        },
                        //showBackButton = navigator.isMainPaneHidden(),
                    )
                }*/
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Composable for Home Screen and its properties needed to render the
 * components of the page.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    windowSizeClass: WindowSizeClass,
    isLoading: Boolean,
    featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterV2,
    totals: List<Int>,
    selectedSong: SongInfo,
    onHomeAction: (HomeAction) -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Effect that changes the home category selection when there are no subscribed podcasts
    //FixMe: repurpose this for RecentPlaylists, so that if there's no recent playlists as featured playlists, have a defaulted view
    LaunchedEffect(key1 = featuredLibraryItemsFilterResult.recentAlbums) {//featuredLibraryItemsFilterResult.recentPlaylists) {
        if (featuredLibraryItemsFilterResult.recentAlbums.isEmpty()) {//recentPlaylists.isEmpty()) {
            onHomeAction(HomeAction.EmptyLibraryView(PlaylistInfo()))
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue) //FixMe: update the snackBar selection to properly convey action taken
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    NavDrawer(
        "Home Page",
        totals,
        navigateToHome,
        navigateToLibrary,
        navigateToSettings,
        drawerState,
        coroutineScope,
    ) {
        ScreenBackground(
            modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Scaffold(
                topBar = {
                    HomeTopAppBar(
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
                                .padding(horizontal = 16.dp)
                        )
                    }
                },
                bottomBar = {
                    /* //should show BottomBarPlayer here if a queue session is running or service is running
                    BottomBarPlayer(
                        song = PreviewSongs[5],
                        navigateToPlayer = { navigateToPlayer(PreviewSongs[5]) },
                    )*/
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                containerColor = Color.Transparent,
                contentColor = contentColorFor(MaterialTheme.colorScheme.background) //selects the appropriate color to be the content color for the container using background color
                //contentColor = MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
            ) { contentPadding ->
                // Main Content
                HomeContent(
                    coroutineScope = coroutineScope,
                    featuredLibraryItemsFilterResult = featuredLibraryItemsFilterResult,
                    selectedSong = selectedSong,
                    modifier = modifier.padding(contentPadding), //this contentPadding comes from the Scaffold /*.statusBarsPadding()*/
                    onHomeAction = { action ->
                        if (action is HomeAction.QueueSong) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(snackBarText)
                            }
                        }
                        onHomeAction(action)
                    },
                    navigateToLibrary = navigateToLibrary,
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    navigateToPlayer = navigateToPlayer,
                )
            }
        }
    }
}

/**
 * Composable for Home Screen's Top App Bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    onNavigationIconClick: () -> Unit,
    navigateToSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = stringResource(R.string.icon_nav_drawer),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        },
        actions = {
            IconButton( onClick = navigateToSearch ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.icon_search),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    coroutineScope: CoroutineScope,
    featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterV2,
    //featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterResult,
    selectedSong: SongInfo,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: () -> Unit,
) {
    val pLists = featuredLibraryItemsFilterResult.recentAlbums.toPersistentList()
    val pagerState = rememberPagerState { pLists.size }
    LaunchedEffect(pagerState, pLists) {
        snapshotFlow { pagerState.currentPage }
            .collect {
            //this would be used to collect info for action that
            // will need the current context to be redrawn to display result
                //val playlist = pLists.getOrNull(it)
                //playlist?.let { it1 -> HomeAction.LibraryPlaylistSelected(it1) }
                    //?.let { it2 -> onHomeAction(it2) }
            }//crashes the app on Home screen redraw
    } //this section is called on every redraw for Home Screen

    val sheetState = rememberModalBottomSheetState(false,)
    var showBottomSheet by remember { mutableStateOf(false) }
    HomeContentGrid(
        //sheetState = sheetState,
        pagerState = pagerState,
        featuredLibraryItemsFilterResult = featuredLibraryItemsFilterResult,
        modifier = modifier,
        onHomeAction = onHomeAction,
        onMoreOptionsClick = { showBottomSheet = true },
        navigateToLibrary = navigateToLibrary,
        navigateToAlbumDetails = navigateToAlbumDetails,
        navigateToPlaylistDetails = navigateToPlaylistDetails,
        navigateToPlayer = navigateToPlayer,
    )

    if(showBottomSheet) {
        SongMoreOptionsBottomModal(
            onDismissRequest = { showBottomSheet = false },
            song = selectedSong,
            navigateToPlayer = navigateToPlayer, //FixMe
            sheetOnClick = {
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if(!sheetState.isVisible) { showBottomSheet = false }
                }
            }
        )
    }
}

@Composable
private fun HomeContentGrid(
    //sheetState: SheetState,
    pagerState: PagerState,
    featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterV2,
    //featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterResult,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    onMoreOptionsClick: (Any) -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(500.dp), //added so that sufficiently large screens will have multi columns
        //columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize(),//.padding(horizontal = 4.dp)
    ) {
        if (featuredLibraryItemsFilterResult.recentAlbums.isNotEmpty()) {//recentPlaylists.isNotEmpty()) {
            fullWidthItem {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recent_playlists),
                        minLines = 1,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,//headlineSmall,
                        modifier = Modifier.padding(16.dp)//.fillMaxWidth()
                    )

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = navigateToLibrary,// { /*onMoreOptionsClick*/ } //navigateToLibrary -> Playlists -> sortBy DateLastAccessed Desc
                        shape = MusicShapes.extraLarge,
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        contentPadding = ButtonDefaults.TextButtonContentPadding,

                    ) {
                        Text(
                            text = "More",
                            //color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            fullWidthItem {
                FeaturedAlbumsCarousel(
                //FeaturedPlaylistsCarousel(
                    pagerState = pagerState,
                    items = featuredLibraryItemsFilterResult.recentAlbums.toPersistentList(),//recentPlaylists.toPersistentList(),
                    //navigateToPlaylistDetails = navigateToPlaylistDetails,
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    onMoreOptionsClick = {},//onHomeAction( HomeAction.LibraryAlbumSelected ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (featuredLibraryItemsFilterResult.recentlyAddedSongs.isNotEmpty()) {
            fullWidthItem {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recent_songs),
                        minLines = 1,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,//headlineSmall,
                        modifier = Modifier.padding(16.dp)//.fillMaxWidth()
                    )

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = navigateToLibrary,// { /*onMoreOptionsClick*/ } //navigateToLibrary -> Songs -> sortBy DateCreated Desc
                        shape = MusicShapes.extraLarge,
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        contentPadding = ButtonDefaults.TextButtonContentPadding,

                        ) {
                        Text(
                            text = "More",
                            //color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            /**
             * ORIGINAL VERSION: using featuredLibraryItemsFilterResult.recentlyAddedSongs which is List<SongInfo>
             */
            items(featuredLibraryItemsFilterResult.recentlyAddedSongs) { song ->
                //recently added songs as featured songs would go here, limit 5-10. More btn would take to fuller list of songs, limit 100
                Box(Modifier.padding(horizontal = 12.dp, vertical = 0.dp)) {
                    HomeSongListItem(
                        song = song,
                        onClick = {
                            onHomeAction(HomeAction.SongClicked(song))
                            navigateToPlayer()
                        },
                        //onQueueSong = { },
                        onMoreOptionsClick = {
                            onHomeAction(HomeAction.SongMoreOptionClicked(song))
                            onMoreOptionsClick(song)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        //isListEditable = false,
                        //showArtistName = true,
                        //showAlbumImage = true,
                        //showAlbumTitle = true,
                        //showTrackNumber = false,
                    )
                }
            }
        }
    }
}

@Composable
fun HomeSongListItem(
    song: SongInfo,
    onClick: (SongInfo) -> Unit,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(4.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = Color.Transparent,
            //color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { onClick(song) },
        ) {
            HomeSongListItemRow(
                song = song,
                onMoreOptionsClick = onMoreOptionsClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun HomeSongListItemRow(
    song: SongInfo,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        AlbumImage(
            albumImage = song.artworkUri,
            contentDescription = song.title,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.small)
        )

        Column(modifier.weight(1f)) {
            Text(
                text = song.title,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
            )
            Row(
                modifier = modifier.padding(horizontal = 10.dp)
            ) {
                Text(
                    text = song.artistName,
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
                Text(
                    text = " • " + song.albumTitle,
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
                Text(
                    text = " • " + song.duration.formatStr(),
                    maxLines = 1,
                    minLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp)//, horizontal = 8.dp),
                )
            }
        }

        IconButton( //more options button
            //modifier = Modifier.padding(0.dp),
            onClick = onMoreOptionsClick, //pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
        ) {
            Icon( //more options icon
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
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
private val ExpandedWindowSizeClass = WindowSizeClass.compute(840f,900f)
private val CompactWindowSizeClassLandscape = WindowSizeClass.compute(780f,360f)
private val ExpandedWindowSizeClassLandscape = WindowSizeClass.compute(900f,840f)

//@SystemLightPreview
@SystemDarkPreview
//@LandscapePreview
@Composable
private fun PreviewHome() {
    MusicTheme {
        HomeScreen(
            windowSizeClass = CompactWindowSizeClassLandscape,//CompactWindowSizeClass,
            isLoading = false,
            /*featuredLibraryItemsFilterResult = FeaturedLibraryItemsFilterResult(
                recentPlaylists = PreviewPlaylists,
                recentlyAddedSongs = PreviewSongs
            ),*/
            featuredLibraryItemsFilterResult = FeaturedLibraryItemsFilterV2(
                recentAlbums = PreviewAlbums,
                recentlyAddedSongs = PreviewSongs
            ),
            totals = listOf(
                PreviewSongs.size,
                PreviewArtists.size,
                PreviewAlbums.size,
                PreviewPlaylists.size
            ),
            selectedSong = PreviewSongs[0],
            onHomeAction = {},
            navigateToHome = {},
            navigateToLibrary = {},
            navigateToSettings = {},
            navigateToSearch = {},
            navigateToAlbumDetails = {},
            navigateToPlaylistDetails = {},
            navigateToPlayer = {},
        )
    }
}
