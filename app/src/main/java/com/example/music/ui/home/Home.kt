package com.example.music.ui.home

import android.util.Log
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.TopAppBarExpandedHeight
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.music.R
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.MARGIN_PADDING
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.player.MiniPlayerControlActions
import com.example.music.ui.shared.AlbumActions
import com.example.music.ui.shared.AlbumMoreOptionsBottomModal
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.FeaturedAlbumsCarousel
import com.example.music.ui.shared.FeaturedPlaylistsCarousel
import com.example.music.ui.shared.MiniPlayer
import com.example.music.ui.shared.NavDrawer
import com.example.music.ui.shared.NavDrawerBtn
import com.example.music.ui.shared.NavToMoreBtn
import com.example.music.ui.shared.PlaylistActions
import com.example.music.ui.shared.PlaylistMoreOptionsBottomModal
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SearchBtn
import com.example.music.ui.shared.SongActions
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.LandscapePreview
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import com.example.music.util.screenMargin
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
        if (uiState.errorMessage != null) {
            Log.e(TAG, "${uiState.errorMessage}")
            HomeScreenError(onRetry = viewModel::refresh)
        }
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
    }
}

/**
 * Error Screen
 */
@Composable
private fun HomeScreenError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) { Error(onRetry = onRetry, modifier = modifier) }

/**
 * Stateful version of Home Screen
 */
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
            //featuredAlbums = uiState.featuredAlbums,
            featuredPlaylists = uiState.featuredPlaylists,
            featuredSongs = uiState.featuredSongs,
            totals = uiState.totals,
            //selectAlbum = uiState.selectAlbum,
            selectPlaylist = uiState.selectPlaylist,
            selectSong = uiState.selectSong,
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
                onPlay = viewModel::onPlay,
                onPause = viewModel::onPause,
            ),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * Stateless version of Home Screen and its properties needed to render the
 * components of the page.
 */
@Composable
private fun HomeScreen(
    isLoading: Boolean,
    //featuredAlbums: List<AlbumInfo>,
    featuredPlaylists: List<PlaylistInfo>,
    featuredSongs: List<SongInfo>,
    totals: List<Int>,
    //selectAlbum: AlbumInfo,
    selectPlaylist: PlaylistInfo,
    selectSong: SongInfo,
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
        ScreenBackground(modifier = modifier) {
            Scaffold(
                topBar = {
                    HomeTopAppBar(
                        navigateToSearch = navigateToSearch,
                        onNavigationIconClick = {
                            coroutineScope.launch {
                                drawerState.apply { if (isClosed) open() else close() }
                            }
                        },
                    )
                    if (isLoading) {
                        LinearProgressIndicator(
                            Modifier.fillMaxWidth().screenMargin()
                        )
                    }
                },
                bottomBar = {
                    if (isActive) {
                        MiniPlayer(
                            song = currentSong,
                            isPlaying = isPlaying,
                            navigateToPlayer = navigateToPlayer,
                            onPlay = miniPlayerControlActions.onPlay,
                            onPause = miniPlayerControlActions.onPause,
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
                    //featuredAlbums = featuredAlbums,
                    featuredPlaylists = featuredPlaylists,
                    featuredSongs = featuredSongs,
                    //selectAlbum = selectAlbum,
                    selectPlaylist = selectPlaylist,
                    selectSong = selectSong,
                    modifier = modifier.padding(contentPadding),
                    onHomeAction = { action ->
                        if (action is HomeAction.QueueSong) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(snackBarText)
                            }
                        }
                        onHomeAction(action)
                    },
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
) {
    TopAppBar(
        title = {
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth().padding(CONTENT_PADDING)
            )
        },
        navigationIcon = { NavDrawerBtn(onClick = onNavigationIconClick) },
        actions = { SearchBtn(onClick = navigateToSearch) },
        expandedHeight = TopAppBarExpandedHeight,
        windowInsets = TopAppBarDefaults.windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        ),
        scrollBehavior = pinnedScrollBehavior(),
    )
}

/**
 * Composable for Home Screen Content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    coroutineScope: CoroutineScope,
    //featuredAlbums: List<AlbumInfo>,
    featuredPlaylists: List<PlaylistInfo>,
    featuredSongs: List<SongInfo>,
    //selectAlbum: AlbumInfo,
    selectPlaylist: PlaylistInfo,
    selectSong: SongInfo,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: () -> Unit,
) {
    Log.i(TAG, "HomeContent START\n" +
        //"Featured Albums: ${featuredAlbums.size}\n" +
        "Featured Playlists: ${featuredPlaylists.size}")

    //val albumPagerState = rememberPagerState { featuredAlbums.size }
    val playPagerState = rememberPagerState { featuredPlaylists.size }

    val sheetState = rememberModalBottomSheetState(true)
    //var showAlbumMoreOptions by remember { mutableStateOf(false) }
    var showPlaylistMoreOptions by remember { mutableStateOf(false) }
    var showSongMoreOptions by remember { mutableStateOf( false ) }

    HomeContentGrid(
        //albumPagerState = albumPagerState,
        playPagerState = playPagerState,
        //featuredAlbums = featuredAlbums,
        featuredPlaylists = featuredPlaylists,
        featuredSongs = featuredSongs,
        modifier = modifier,
        onHomeAction = onHomeAction,
        //onAlbumMoreOptionsClick = { showAlbumMoreOptions = true },
        onPlaylistMoreOptionsClick = { showPlaylistMoreOptions = true },
        onSongMoreOptionsClick = { showSongMoreOptions = true },
        //navigateToAlbumDetails = navigateToAlbumDetails,
        navigateToPlaylistDetails = navigateToPlaylistDetails,
        navigateToPlayer = navigateToPlayer,
    )

    /*if (showAlbumMoreOptions) {
        Log.i(TAG, "HomeContent -> Album More Options is TRUE")
        AlbumMoreOptionsBottomModal(
            onDismissRequest = { showAlbumMoreOptions = false },
            sheetState = sheetState,
            album = selectAlbum,
            albumActions = AlbumActions(
                play = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> PlayAlbum clicked :: ${selectAlbum.id}")
                        onHomeAction(HomeAction.PlayAlbum(selectAlbum))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) showAlbumMoreOptions = false
                    }
                },
                playNext = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> PlayAlbumNext clicked :: ${selectAlbum.id}")
                        onHomeAction(HomeAction.PlayAlbumNext(selectAlbum))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) showAlbumMoreOptions = false
                    }
                },
                shuffle = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> ShuffleAlbum clicked :: ${selectAlbum.id}")
                        onHomeAction(HomeAction.ShuffleAlbum(selectAlbum))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) showAlbumMoreOptions = false
                    }
                },
                addToQueue = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> QueueAlbum clicked :: ${selectAlbum.id}")
                        onHomeAction(HomeAction.QueueAlbum(selectAlbum))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) showAlbumMoreOptions = false
                    }
                },
                goToAlbumArtist = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> GoToAlbumArtist clicked :: ${selectAlbum.artistId ?: "null id"}")
                        sheetState.hide()
                        navigateToArtistDetails(selectAlbum.artistId ?: 0) // this isn't a good catch for when an album doesn't have an artist
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) showAlbumMoreOptions = false
                    }
                },
                goToAlbum = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> GoToAlbum clicked :: ${selectAlbum.id}")
                        sheetState.hide()
                        navigateToAlbumDetails(selectAlbum.id)
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                        if(!sheetState.isVisible) showAlbumMoreOptions = false
                    }
                },
            ),
            onClose = {
                coroutineScope.launch {
                    Log.i(TAG, "Hide sheet state")
                    sheetState.hide()
                }.invokeOnCompletion {
                    Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                    if(!sheetState.isVisible) showAlbumMoreOptions = false
                }
            },
            context = "Home",
        )
    }*/

    if (showPlaylistMoreOptions) {
        Log.i(TAG, "HomeContent -> Playlist More Options is TRUE")
        PlaylistMoreOptionsBottomModal(
            onDismissRequest = { showPlaylistMoreOptions = false },
            sheetState = sheetState,
            playlist = selectPlaylist,
            playlistActions = PlaylistActions(
                play = {
                    coroutineScope.launch {
                        Log.i(TAG, "Playlist More Options Modal -> PlayPlaylist clicked :: ${selectPlaylist.id}")
                        onHomeAction(HomeAction.PlayPlaylist(selectPlaylist))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showPlaylistMoreOptions to FALSE")
                        if(!sheetState.isVisible) showPlaylistMoreOptions = false
                    }
                },
                playNext = {
                    coroutineScope.launch {
                        Log.i(TAG, "Playlist More Options Modal -> PlayPlaylistNext clicked :: ${selectPlaylist.id}")
                        onHomeAction(HomeAction.PlayPlaylistNext(selectPlaylist))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showPlaylistMoreOptions to FALSE")
                        if(!sheetState.isVisible) showPlaylistMoreOptions = false
                    }
                },
                shuffle = {
                    coroutineScope.launch {
                        Log.i(TAG, "Playlist More Options Modal -> ShufflePlaylist clicked :: ${selectPlaylist.id}")
                        onHomeAction(HomeAction.ShufflePlaylist(selectPlaylist))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showPlaylistMoreOptions to FALSE")
                        if(!sheetState.isVisible) showPlaylistMoreOptions = false
                    }
                },
                addToQueue = {
                    coroutineScope.launch {
                        Log.i(TAG, "Playlist More Options Modal -> QueuePlaylist clicked :: ${selectPlaylist.id}")
                        onHomeAction(HomeAction.QueuePlaylist(selectPlaylist))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showPlaylistMoreOptions to FALSE")
                        if(!sheetState.isVisible) showPlaylistMoreOptions = false
                    }
                },
                goToPlaylist = {
                    coroutineScope.launch {
                        Log.i(TAG, "Playlist More Options Modal -> GoToPlaylist clicked :: ${selectPlaylist.id}")
                        sheetState.hide()
                        navigateToArtistDetails(selectPlaylist.id)
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showPlaylistMoreOptions to FALSE")
                        if(!sheetState.isVisible) showPlaylistMoreOptions = false
                    }
                },
            ),
            onClose = {
                coroutineScope.launch {
                    Log.i(TAG, "Hide sheet state")
                    sheetState.hide()
                }.invokeOnCompletion {
                    Log.i(TAG, "set showPlaylistMoreOptions to FALSE")
                    if(!sheetState.isVisible) showPlaylistMoreOptions = false
                }
            },
            context = "Home",
        )
    }

    if (showSongMoreOptions) {
        Log.i(TAG, "HomeContent -> Song More Options is TRUE")
        SongMoreOptionsBottomModal(
            onDismissRequest = { showSongMoreOptions = false },
            sheetState = sheetState,
            song = selectSong,
            songActions = SongActions(
                play = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> PlaySong clicked :: ${selectSong.id}")
                        onHomeAction(HomeAction.PlaySong(selectSong))
                        navigateToPlayer()
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) showSongMoreOptions = false
                    }
                },
                playNext = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> PlaySongNext clicked :: ${selectSong.id}")
                        onHomeAction(HomeAction.PlaySongNext(selectSong))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) showSongMoreOptions = false
                    }
                },
                addToQueue = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> QueueSong clicked :: ${selectSong.id}")
                        onHomeAction(HomeAction.QueueSong(selectSong))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) showSongMoreOptions = false
                    }
                },
                goToArtist = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> GoToArtist clicked :: ${selectSong.artistId}")
                        navigateToArtistDetails(selectSong.artistId)
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) showSongMoreOptions = false
                    }
                },
                goToAlbum = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> GoToAlbum clicked :: ${selectSong.albumId}")
                        navigateToAlbumDetails(selectSong.albumId)
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) showSongMoreOptions = false
                    }
                },
            ),
            onClose = {
                coroutineScope.launch {
                    Log.i(TAG, "Hide sheet state")
                    sheetState.hide()
                }.invokeOnCompletion {
                    Log.i(TAG, "set showBottomSheet to FALSE")
                    if(!sheetState.isVisible) showSongMoreOptions = false
                }
            },
            context = "Home"
        )
    }
}

@Composable
private fun HomeContentGrid(
    //albumPagerState: PagerState,
    playPagerState: PagerState,
    //featuredAlbums: List<AlbumInfo>,
    featuredPlaylists: List<PlaylistInfo>,
    featuredSongs: List<SongInfo>,
    modifier: Modifier = Modifier,
    onHomeAction: (HomeAction) -> Unit,
    //onAlbumMoreOptionsClick: () -> Unit,
    onPlaylistMoreOptionsClick: () -> Unit,
    onSongMoreOptionsClick: () -> Unit,
    //navigateToAlbumDetails: (Long) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize(),
    ) {
        /*if (featuredAlbums.isNotEmpty()) {
            fullWidthItem {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.recent_albums),
                        minLines = 1,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(MARGIN_PADDING)
                    )
                    Spacer(Modifier.weight(1f))
                    NavToMoreBtn(onClick = {})
                }
            }
            fullWidthItem {
                FeaturedAlbumsCarousel(
                    pagerState = albumPagerState,
                    items = featuredAlbums.toPersistentList(),
                    onClick = navigateToAlbumDetails,
                    onMoreOptionsClick = { album: AlbumInfo ->
                        onHomeAction( HomeAction.AlbumMoreOptionsClicked(album) )
                        onAlbumMoreOptionsClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }*/

        if (featuredPlaylists.isNotEmpty()) {
            fullWidthItem {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.recent_playlists),
                        minLines = 1,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(MARGIN_PADDING)
                    )
                    Spacer(Modifier.weight(1f))
                    NavToMoreBtn(onClick = {})
                }
            }
            fullWidthItem {
                FeaturedPlaylistsCarousel(
                    pagerState = playPagerState,
                    items = featuredPlaylists.toPersistentList(),
                    onClick = navigateToPlaylistDetails,
                    onMoreOptionsClick = { playlist: PlaylistInfo ->
                        onHomeAction( HomeAction.PlaylistMoreOptionsClicked(playlist) )
                        onPlaylistMoreOptionsClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (featuredSongs.isNotEmpty()) {
            fullWidthItem {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.recent_songs),
                        minLines = 1,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(MARGIN_PADDING)
                    )
                    Spacer(Modifier.weight(1f))
                    NavToMoreBtn(onClick = {})
                }
            }

            items(items = featuredSongs) { song ->
                Box(Modifier.screenMargin()) {
                    SongListItem(
                        song = song,
                        onClick = {
                            Log.i(TAG, "Song Clicked: ${song.title}")
                            onHomeAction(HomeAction.PlaySong(song))
                            navigateToPlayer()
                        },
                        onMoreOptionsClick = {
                            Log.i(TAG, "Song More Option Clicked: ${song.title}")
                            onHomeAction(HomeAction.SongMoreOptionsClicked(song))
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

@SystemLightPreview
@SystemDarkPreview
@LandscapePreview
@Composable
private fun PreviewHome() {
    MusicTheme {
        HomeScreen(
            //windowSizeClass = CompactWindowSizeClassLandscape,//CompactWindowSizeClass,
            isLoading = false,

            //featuredAlbums = PreviewAlbums,
            featuredPlaylists = PreviewPlaylists,
            featuredSongs = PreviewSongs,
            totals = listOf(6373, 990, 1427, 35, 9),
            //selectAlbum = PreviewAlbums[0],
            selectPlaylist = PreviewPlaylists[0],
            selectSong = PreviewSongs[0],
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
                onPlay = {},
                onPause = {},
            ),
            modifier = Modifier,
        )
    }
}
