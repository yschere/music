package com.example.music.ui.home

import android.util.Log
import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import com.example.music.ui.shared.AlbumMoreOptionsBottomModal
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

private const val TAG = "Home Screen"

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
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Box {
        HomeScreenReady(
            uiState = uiState,
            windowSizeClass = windowSizeClass,
            navigateToHome = navigateToHome,
            navigateToLibrary = navigateToLibrary,
            navigateToPlayer = navigateToPlayer,
            navigateToSearch = navigateToSearch,
            navigateToSettings = navigateToSettings,
            navigateToAlbumDetails = navigateToAlbumDetails,
            navigateToArtistDetails = navigateToArtistDetails,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
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
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
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
                    isLoading = uiState.isLoading,
                    featuredLibraryItemsFilterResult = uiState.featuredLibraryItemsFilterResult,
                    totals = uiState.totals,
                    selectSong = uiState.selectSong,
                    selectAlbum = uiState.selectAlbum,
                    onHomeAction = viewModel::onHomeAction,
                    navigateToHome = navigateToHome,
                    navigateToLibrary = navigateToLibrary,
                    navigateToPlayer = navigateToPlayer,
                    navigateToSearch = navigateToSearch,
                    navigateToSettings = navigateToSettings,
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    navigateToArtistDetails = navigateToArtistDetails,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    modifier = Modifier.fillMaxSize(),
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun HomeScreen(
    isLoading: Boolean,
    featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterV2,
    totals: List<Int>,
    selectSong: SongInfo,
    selectAlbum: AlbumInfo,
    onHomeAction: (HomeAction) -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
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
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                containerColor = Color.Transparent,
                contentColor = contentColorFor(MaterialTheme.colorScheme.background)  //contentColor = MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
            ) { contentPadding ->
                // Main Content
                HomeContent(
                    coroutineScope = coroutineScope,
                    featuredLibraryItemsFilterResult = featuredLibraryItemsFilterResult,
                    selectSong = selectSong,
                    selectAlbum = selectAlbum,
                    modifier = modifier.padding(contentPadding),
                    onHomeAction = { action ->
                        if (action is HomeAction.QueueSong) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(snackBarText)
                            }
                        }
                        onHomeAction(action)
                    },
                    navigateToLibrary = navigateToLibrary,
                    navigateToPlayer = navigateToPlayer,
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    navigateToArtistDetails = navigateToArtistDetails,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
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
    selectSong: SongInfo,
    selectAlbum: AlbumInfo,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: () -> Unit,
) {
    Log.i(TAG, "HomeContent START")
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
    var showAlbumMoreOptions by remember { mutableStateOf(false) } // if bottom modal content is for album details more options
    var showSongMoreOptions by remember { mutableStateOf( false ) }

    HomeContentGrid(
        pagerState = pagerState,
        featuredLibraryItemsFilterResult = featuredLibraryItemsFilterResult,
        modifier = modifier,
        onHomeAction = onHomeAction,
        onAlbumMoreOptionsClick = {
            showBottomSheet = true
            showAlbumMoreOptions = true
        },
        onSongMoreOptionsClick = {
            showBottomSheet = true
            showSongMoreOptions = true
        },
        navigateToLibrary = navigateToLibrary,
        navigateToAlbumDetails = navigateToAlbumDetails,
        navigateToPlaylistDetails = navigateToPlaylistDetails,
        navigateToPlayer = navigateToPlayer,
    )

    if(showBottomSheet) {
        Log.i(TAG, "HomeContent -> showBottomSheet is TRUE")
        if(showAlbumMoreOptions) {
            Log.i(TAG, "HomeContent -> Album More Options is TRUE")
            AlbumMoreOptionsBottomModal(
                onDismissRequest = {
                    showBottomSheet = false
                    showAlbumMoreOptions = false
                },
                sheetState = sheetState,
                album = selectAlbum,
                play = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> PlaySongs clicked")
                        onHomeAction(HomeAction.PlaySongs(selectAlbum))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                playNext = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> PlaySongsNext clicked")
                        onHomeAction(HomeAction.PlaySongsNext(selectAlbum))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                shuffle = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> ShuffleSongs clicked")
                        onHomeAction(HomeAction.ShuffleSongs(selectAlbum))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                //addToPlaylist = {},
                addToQueue = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> QueueSongs clicked")
                        onHomeAction(HomeAction.QueueSongs(selectAlbum))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                goToArtist = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> GoToArtist clicked")
                        sheetState.hide()
                        navigateToArtistDetails(selectAlbum.albumArtistId ?: 0) // this isn't a good catch for when an album doesn't have an album artist
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                goToAlbum = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> GoToAlbum clicked")
                        sheetState.hide()
                        navigateToAlbumDetails(selectAlbum.id)
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
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
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        }
                    }
                },
                context = "Home",
            )
        }
        else if (showSongMoreOptions) {
            Log.i(TAG, "HomeContent -> Song More Options is TRUE")
            SongMoreOptionsBottomModal(
                onDismissRequest = {
                    showBottomSheet = false
                    showSongMoreOptions = false
                },
                sheetState = sheetState,
                song = selectSong,
                play = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> PlaySong clicked")
                        onHomeAction(HomeAction.PlaySong(selectSong))
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
                        Log.i(TAG, "Song More Options Modal -> PlaySongNext clicked")
                        onHomeAction(HomeAction.PlaySongNext(selectSong))
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
                        Log.i(TAG, "Song More Options Modal -> QueueSong clicked")
                        onHomeAction(HomeAction.QueueSong(selectSong))
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
                        Log.i(TAG, "Song More Options Modal -> GoToArtist clicked")
                        navigateToArtistDetails(selectSong.artistId)
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
                        Log.i(TAG, "Song More Options Modal -> GoToAlbum clicked")
                        navigateToAlbumDetails(selectSong.albumId)
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
            )
        }
    }
}

@Composable
private fun HomeContentGrid(
    pagerState: PagerState,
    featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterV2,
    //featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterResult,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    onAlbumMoreOptionsClick: () -> Unit,
    onSongMoreOptionsClick: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(500.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        if (featuredLibraryItemsFilterResult.recentAlbums.isNotEmpty()) {
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
                    onMoreOptionsClick = { album: AlbumInfo ->
                        onHomeAction( HomeAction.AlbumMoreOptionClicked(album) )
                        onAlbumMoreOptionsClick()
                    },
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
            items(featuredLibraryItemsFilterResult.recentlyAddedSongs) { song ->
                Box(Modifier.padding(horizontal = 12.dp, vertical = 0.dp)) {
                    HomeSongListItem(
                        song = song,
                        onClick = {
                            Log.i(TAG, "Song Clicked: ${song.title}")
                            onHomeAction(HomeAction.PlaySong(song))
                            navigateToPlayer()
                        },
                        onMoreOptionsClick = {
                            Log.i(TAG, "Song More Option Clicked: ${song.title}")
                            onHomeAction(HomeAction.SongMoreOptionClicked(song))
                            onSongMoreOptionsClick()
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
fun HomeSongListItem(
    song: SongInfo,
    onClick: () -> Unit,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(4.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = Color.Transparent, //MaterialTheme.colorScheme.surfaceContainer,
            onClick = onClick,
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
            onClick = onMoreOptionsClick,
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
            //windowSizeClass = CompactWindowSizeClassLandscape,//CompactWindowSizeClass,
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
            selectSong = PreviewSongs[0],
            selectAlbum = PreviewAlbums[0],
            onHomeAction = {},
            navigateToHome = {},
            navigateToLibrary = {},
            navigateToPlayer = {},
            navigateToSearch = {},
            navigateToSettings = {},
            navigateToAlbumDetails = {},
            navigateToArtistDetails = {},
            navigateToPlaylistDetails = {},
            modifier = Modifier,
        )
    }
}
