package com.example.music.ui.home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.music.R
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.FeaturedLibraryItemsFilterV2
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.player.MiniPlayerControlActions
import com.example.music.ui.shared.AlbumMoreOptionsBottomModal
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.FeaturedAlbumsCarousel
import com.example.music.ui.shared.MiniPlayer
import com.example.music.ui.shared.NavDrawer
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.util.NavDrawerBtn
import com.example.music.util.SearchBtn
import com.example.music.util.fullWidthItem
import com.example.music.util.isCompact
import com.example.music.util.quantityStringResource
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

private const val TAG = "Home Screen"

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
    Log.i(TAG, "Main Screen START")
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
    Error(
        onRetry = onRetry,
        modifier = modifier
    )
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
    Log.i(TAG, "Home Screen Ready START")

    Surface(color = Color.Transparent) {
        HomeScreen(
            isLoading = uiState.isLoading,
            featuredLibraryItemsFilterResult = uiState.featuredLibraryItemsFilterResult,
            totals = uiState.totals,
            selectSong = uiState.selectSong,
            selectAlbum = uiState.selectAlbum,
            currentSong = viewModel.currentSong,
            isActive = viewModel.isActive, // if playback is active
            isPlaying = viewModel.isPlaying,

            onHomeAction = viewModel::onHomeAction,
            navigateToHome = navigateToHome,
            navigateToLibrary = navigateToLibrary,
            navigateToPlayer = navigateToPlayer,
            navigateToSearch = navigateToSearch,
            navigateToSettings = navigateToSettings,
            navigateToAlbumDetails = navigateToAlbumDetails,
            navigateToArtistDetails = navigateToArtistDetails,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
            miniPlayerControlActions = MiniPlayerControlActions(
                onPlayPress = viewModel::onPlay,
                onPausePress = viewModel::onPause,
            ),
            modifier = Modifier.fillMaxSize(),
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
    isLoading: Boolean,
    featuredLibraryItemsFilterResult: FeaturedLibraryItemsFilterV2,
    totals: List<Int>,
    selectSong: SongInfo,
    selectAlbum: AlbumInfo,
    currentSong: SongInfo,
    isActive: Boolean,
    isPlaying: Boolean,

    onHomeAction: (HomeAction) -> Unit,

    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    miniPlayerControlActions: MiniPlayerControlActions,
    modifier: Modifier = Modifier
) {
    Log.i(TAG, "Home Screen START\n" +
        "currentSong? ${currentSong.title}\n" +
        "isActive? $isActive")
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
            modifier = modifier
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
                    if (isActive) {
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
            NavDrawerBtn(onClick = onNavigationIconClick)
        },
        actions = {
            SearchBtn(onClick = navigateToSearch)
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

    val sheetState = rememberModalBottomSheetState(false,)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAlbumMoreOptions by remember { mutableStateOf(false) }
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
                        Log.i(TAG, "Album More Options Modal -> PlaySongs clicked :: ${selectAlbum.id}")
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
                        Log.i(TAG, "Album More Options Modal -> PlaySongsNext clicked :: ${selectAlbum.id}")
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
                        Log.i(TAG, "Album More Options Modal -> ShuffleSongs clicked :: ${selectAlbum.id}")
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
                        Log.i(TAG, "Album More Options Modal -> QueueSongs clicked :: ${selectAlbum.id}")
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
                        Log.i(TAG, "Album More Options Modal -> GoToArtist clicked :: ${selectAlbum.albumArtistId ?: "null id"}")
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
                        Log.i(TAG, "Album More Options Modal -> GoToAlbum clicked :: ${selectAlbum.id}")
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
                        Log.i(TAG, "Song More Options Modal -> PlaySong clicked :: ${selectSong.id}")
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
                        Log.i(TAG, "Song More Options Modal -> PlaySongNext clicked :: ${selectSong.id}")
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
                        Log.i(TAG, "Song More Options Modal -> QueueSong clicked :: ${selectSong.id}")
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
                        Log.i(TAG, "Song More Options Modal -> GoToArtist clicked :: ${selectSong.artistId}")
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
                        Log.i(TAG, "Song More Options Modal -> GoToAlbum clicked :: ${selectSong.albumId}")
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
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = navigateToLibrary, //navigateToLibrary -> Playlists -> sortBy DateLastAccessed Desc
                        modifier = Modifier.padding(horizontal = 16.dp),
                        contentPadding = ButtonDefaults.TextButtonContentPadding,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.inversePrimary,
                        )
                    ) {
                        Text(
                            text = "More",
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
                    onClick = navigateToAlbumDetails,
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
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = navigateToLibrary, //navigateToLibrary -> Songs -> sortBy DateCreated Desc
                        modifier = Modifier.padding(horizontal = 16.dp),
                        contentPadding = ButtonDefaults.TextButtonContentPadding,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.inversePrimary,
                        )
                    ) {
                        Text(
                            text = "More",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            items(
                items = featuredLibraryItemsFilterResult.recentlyAddedSongs
            ) { song ->
                Box(
                    Modifier.padding(horizontal = 12.dp)
                ) {
                    SongListItem(
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
                        showArtistName = true,
                        showAlbumImage = true,
                        showAlbumTitle = true,
                        hasBackground = false,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
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
            currentSong = PreviewSongs[0],
            isActive = true,
            isPlaying = true,

            onHomeAction = {},
            navigateToHome = {},
            navigateToLibrary = {},
            navigateToPlayer = {},
            navigateToSearch = {},
            navigateToSettings = {},
            navigateToAlbumDetails = {},
            navigateToArtistDetails = {},
            navigateToPlaylistDetails = {},
            miniPlayerControlActions = MiniPlayerControlActions(
                onPlayPress = {},
                onPausePress = {},
            ),
            modifier = Modifier,
        )
    }
}
