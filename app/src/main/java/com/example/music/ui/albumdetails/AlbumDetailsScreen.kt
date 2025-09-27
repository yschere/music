package com.example.music.ui.albumdetails

import android.util.Log
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.DEFAULT_PADDING
import com.example.music.designsys.theme.ITEM_IMAGE_CARD_SIZE
import com.example.music.designsys.theme.MARGIN_PADDING
import com.example.music.designsys.theme.TOP_BAR_COLLAPSED_HEIGHT
import com.example.music.designsys.theme.TOP_BAR_EXPANDED_HEIGHT
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.getSongsInAlbum
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.player.MiniPlayerControlActions
import com.example.music.ui.shared.AlbumActions
import com.example.music.ui.shared.AlbumMoreOptionsBottomModal
import com.example.music.ui.shared.MiniPlayer
import com.example.music.ui.shared.DetailsSortSelectionBottomModal
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.PlayShuffleButtons
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongActions
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompDarkPreview
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.util.BackNavBtn
import com.example.music.util.MoreOptionsBtn
import com.example.music.util.ScrollToTopFAB
import com.example.music.util.SearchBtn
import com.example.music.util.fullWidthItem
import kotlinx.coroutines.launch

private const val TAG = "Album Details Screen"

/**
 * Stateful version of Album Details Screen
 */
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun AlbumDetailsScreen(
    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    viewModel: AlbumDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        AlbumDetailsError(onRetry = viewModel::refresh)
    }
    Surface(color = Color.Transparent) {
        if (uiState.isReady) {
            AlbumDetailsScreen(
                album = uiState.album,
                songs = uiState.songs,
                selectSong = uiState.selectSong,
                currentSong = viewModel.currentSong,
                isActive = viewModel.isActive, // if playback is active
                isPlaying = viewModel.isPlaying,

                onAlbumAction = viewModel::onAlbumAction,
                navigateBack = navigateBack,
                navigateToPlayer = navigateToPlayer,
                navigateToSearch = navigateToSearch,
                navigateToArtistDetails = navigateToArtistDetails,
                modifier = Modifier.fillMaxSize(),
                miniPlayerControlActions = MiniPlayerControlActions(
                    onPlay = viewModel::onPlay,
                    onPause = viewModel::onPause,
                )
            )
        } else {
            AlbumDetailsLoadingScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Error Screen
 */
@Composable
private fun AlbumDetailsError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Error(
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Loading Screen with circular progress indicator in center
 */
@Composable
private fun AlbumDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }

/**
 * Stateless version of Album Details Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailsScreen(
    album: AlbumInfo,
    songs: List<SongInfo>,
    selectSong: SongInfo,
    currentSong: SongInfo,
    isActive: Boolean,
    isPlaying: Boolean,

    onAlbumAction: (AlbumAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    miniPlayerControlActions: MiniPlayerControlActions,
    modifier: Modifier = Modifier
) {
    Log.i(TAG, "AlbumDetails Screen START\n" +
        "currentSong? ${currentSong.title}\n" +
        "isActive? $isActive")

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue) //use this to hold the little popup text that appears after an onClick event

    val appBarScrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )
    val isCollapsed = remember {
        derivedStateOf {
            appBarScrollBehavior.state.collapsedFraction > 0.8
        }
    }

    val listState = rememberLazyGridState()
    val displayButton = remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    val sheetState = rememberModalBottomSheetState(false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var showAlbumMoreOptions by remember { mutableStateOf(false) }
    var showSongMoreOptions by remember { mutableStateOf( false ) }

    ScreenBackground(
        modifier = modifier
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        // if true, bar is collapsed so use album title as title
                        if (isCollapsed.value) {
                            Text(
                                text = album.title,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.basicMarquee()
                            )
                        } else {
                            // if false, bar is expanded so use full header
                            //AlbumDetailsHeaderLargeAlbumCover(album, modifier)
                            AlbumDetailsHeader(album, modifier)
                        }
                    },
                    navigationIcon = { BackNavBtn(onClick = navigateBack) },
                    actions = {
                        SearchBtn(onClick = navigateToSearch)
                        MoreOptionsBtn(
                            onClick = {
                                showBottomSheet = true
                                showAlbumMoreOptions = true
                            }
                        )
                    },
                    collapsedHeight = TOP_BAR_COLLAPSED_HEIGHT,
                    expandedHeight = TOP_BAR_EXPANDED_HEIGHT,
                    windowInsets = TopAppBarDefaults.windowInsets,
                    colors = TopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        titleContentColor = contentColorFor(MaterialTheme.colorScheme.background),
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    scrollBehavior = appBarScrollBehavior,
                )
            },
            bottomBar = {
                if (isActive){
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
            modifier = modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background)
        ) { contentPadding ->
            // AlbumDetails Content
            Box(Modifier.fillMaxSize()) {
                // Songs List Content
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(362.dp),
                    state = listState,
                    modifier = Modifier.padding(contentPadding)
                        .fillMaxSize()
                        .padding(horizontal = DEFAULT_PADDING)
                ) {
                    fullWidthItem {
                        ItemCountAndSortSelectButtons(
                            id = R.plurals.songs,
                            itemCount = songs.size,
                            onSortClick = {
                                Log.i(TAG, "Song Sort btn clicked")
                                showBottomSheet = true
                                showSortSheet = true
                            },
                            onSelectClick = {
                                Log.i(TAG, "Multi Select btn clicked")
                            },
                        )
                    }

                    fullWidthItem {
                        PlayShuffleButtons(
                            onPlayClick = {
                                Log.i(TAG, "Play Album btn clicked")
                                onAlbumAction(AlbumAction.PlaySongs(songs))
                                navigateToPlayer()
                            },
                            onShuffleClick = {
                                Log.i(TAG, "Shuffle Album btn clicked")
                                onAlbumAction(AlbumAction.ShuffleSongs(songs))
                                navigateToPlayer()
                            },
                        )
                    }

                    items(items = songs) { song ->
                        SongListItem(
                            song = song,
                            onClick = {
                                Log.i(TAG, "Song clicked: ${song.title}")
                                onAlbumAction(AlbumAction.PlaySong(song))
                                navigateToPlayer()
                            },
                            onMoreOptionsClick = {
                                Log.i(TAG, "Song More Option clicked: ${song.title}")
                                onAlbumAction(AlbumAction.SongMoreOptionsClicked(song))
                                showBottomSheet = true
                                showSongMoreOptions = true
                            },
                            isListEditable = false,
                            showAlbumImage = false,
                            showArtistName = true,
                            showAlbumTitle = false,
                            showTrackNumber = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                ScrollToTopFAB(
                    displayButton = displayButton,
                    isActive = isActive,
                    onClick = {
                        coroutineScope.launch {
                            Log.i(TAG, "Scroll to Top btn clicked")
                            listState.animateScrollToItem(0)
                        }
                    }
                )
            }

            // AlbumDetails BottomSheet
            if (showBottomSheet) {
                Log.i(TAG, "AlbumDetails Content -> showBottomSheet is TRUE")
                // bottom sheet context - sort btn
                if (showSortSheet) {
                    Log.i(TAG, "AlbumDetails Content -> Song Sort Modal is TRUE")
                    DetailsSortSelectionBottomModal(
                        onDismissRequest = {
                            showBottomSheet = false
                            showSortSheet = false
                        },
                        sheetState = sheetState,
                        // need to show selection
                        onClose = {
                            coroutineScope.launch {
                                Log.i(TAG, "Hide sheet state")
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE; set Song Sort to FALSE")
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
                                Log.i(TAG, "set showBottomSheet to FALSE; set DetailSort to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showSortSheet = false
                                }
                            }
                        },
                        content = "SongInfo",
                        context = "AlbumDetails",
                    )
                }

                // bottom sheet context - album more option btn
                else if (showAlbumMoreOptions) {
                    Log.i(TAG, "AlbumDetails Content -> Album More Options is TRUE")
                    AlbumMoreOptionsBottomModal(
                        onDismissRequest = {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        },
                        sheetState = sheetState,
                        album = album,
                        albumActions = AlbumActions(
                            play = {
                                coroutineScope.launch {
                                    Log.i(TAG, "Album More Options Modal -> PlaySongs clicked")
                                    onAlbumAction(AlbumAction.PlaySongs(songs))
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
                                    Log.i(TAG, "Album More Options Modal -> PlaySongsNext clicked")
                                    onAlbumAction(AlbumAction.PlaySongsNext(songs))
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
                                    Log.i(TAG, "Album More Options Modal -> ShuffleSongs clicked")
                                    onAlbumAction(AlbumAction.ShuffleSongs(songs))
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
                                    Log.i(TAG, "Album More Options Modal -> QueueSongs clicked")
                                    onAlbumAction(AlbumAction.QueueSongs(songs))
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
                                    if(!sheetState.isVisible) {
                                        showBottomSheet = false
                                        showAlbumMoreOptions = false
                                    }
                                }
                            },
                            goToAlbumArtist = {
                                coroutineScope.launch {
                                    Log.i(TAG, "Album More Options Modal -> GoToArtist clicked :: ${album.albumArtistId ?: "null id"}")
                                    navigateToArtistDetails(album.albumArtistId ?: 0L) // not a good check for if this is null
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
                                    if(!sheetState.isVisible) {
                                        showBottomSheet = false
                                        showAlbumMoreOptions = false
                                    }
                                }
                            },
                        ),
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
                        context = "AlbumDetails",
                    )
                }

                // bottom sheet context - song more option btn
                else if (showSongMoreOptions) {
                    Log.i(TAG, "AlbumDetails Content -> Song More Options is TRUE")
                    SongMoreOptionsBottomModal(
                        onDismissRequest = {
                            showBottomSheet = false
                            showSongMoreOptions = false
                        },
                        sheetState = sheetState,
                        song = selectSong,
                        songActions = SongActions(
                            play = {
                                coroutineScope.launch {
                                    Log.i(TAG, "Song More Options Modal -> PlaySong clicked :: ${selectSong.id}")
                                    onAlbumAction(AlbumAction.PlaySong(selectSong))
                                    sheetState.hide()
                                    navigateToPlayer()
                                }.invokeOnCompletion {
                                    Log.i(TAG, "set showBottomSheet to FALSE; set SongMoreOptions to FALSE")
                                    if(!sheetState.isVisible) {
                                        showBottomSheet = false
                                        showSongMoreOptions = false
                                    }
                                }
                            },
                            playNext = {
                                coroutineScope.launch {
                                    Log.i(TAG, "Song More Options Modal -> PlaySongNext clicked :: ${selectSong.id}")
                                    onAlbumAction(AlbumAction.PlaySongNext(selectSong))
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    Log.i(TAG, "set showBottomSheet to FALSE; set SongMoreOptions to FALSE")
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
                                    onAlbumAction(AlbumAction.QueueSong(selectSong))
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    Log.i(TAG, "set showBottomSheet to FALSE; set SongMoreOptions to FALSE")
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
                                    Log.i(TAG, "set showBottomSheet to FALSE; set SongMoreOptions to FALSE")
                                    if(!sheetState.isVisible) {
                                        showBottomSheet = false
                                        showSongMoreOptions = false
                                    }
                                }
                            },
                        ),
                        onClose = {
                            coroutineScope.launch {
                                Log.i(TAG, "Hide sheet state")
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE; set SongMoreOptions to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showSongMoreOptions = false
                                }
                            }
                        },
                        context = "AlbumDetails",
                    )
                }
            }
        }
    }
}

/**
 * Composable for Album Details Screen's header.
 * Has album image on the left side, album title and album artist on the right side.
 */
@Composable
fun AlbumDetailsHeader(
    album: AlbumInfo,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.padding(16.dp)
    ) {
        val maxImageSize = this.maxWidth / 2
        val imageSize = min(maxImageSize, ITEM_IMAGE_CARD_SIZE)
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                AlbumImage(
                    albumImage = album.artworkUri,
                    contentDescription = album.title,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(MaterialTheme.shapes.large),
                )
                Column(
                    modifier = Modifier.padding(start = MARGIN_PADDING),
                ) {
                    Text(
                        text = album.title,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = album.albumArtistName ?: "",
                        maxLines = 2,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

/**
 * Composable for Album Details Screen's header.
 * This version has album image enlarged and centered at the top, 
 * album title and album artist below the album image.
 */
@Composable
fun AlbumDetailsHeaderLargeAlbumCover(
    album: AlbumInfo,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        val maxImageSize = this.maxWidth * 0.6f
        val imageSize = max(maxImageSize, ITEM_IMAGE_CARD_SIZE)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlbumImage(
                albumImage = album.artworkUri,
                contentDescription = album.title,
                modifier = Modifier
                    .size(imageSize)
                    .clip(MaterialTheme.shapes.large)
            )

            Text(
                text = album.title,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.basicMarquee().padding(vertical = 8.dp)
            )
            Text(
                text = album.albumArtistName ?: "",
                maxLines = 1,
                overflow = TextOverflow.Visible,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}


//@CompDarkPreview
@Composable
fun HeaderAlbumCoverPreview() {
    MusicTheme {
        AlbumDetailsHeaderLargeAlbumCover(
            album = PreviewAlbums[0],
        )
    }
}

//@SystemLightPreview
@SystemDarkPreview
//@LandscapePreview
@Composable
fun AlbumDetailsScreenPreview() {
    MusicTheme {
        AlbumDetailsScreen(
            //album = PreviewAlbums[0],
            //songs = PreviewSongs,

            //Slow Rain
            album = PreviewAlbums[2],
            songs = getSongsInAlbum(PreviewAlbums[2].id),

            //Kingdom Hearts Piano Collection
            //album = PreviewAlbums[6],
            //songs = getSongsInAlbum(307),

            selectSong = getSongsInAlbum(PreviewAlbums[2].id)[0],
            currentSong = PreviewSongs[0],
            isActive = true,
            isPlaying = true,

            onAlbumAction = {},
            navigateBack = {},
            navigateToPlayer = {},
            navigateToSearch = {},
            navigateToArtistDetails = {},
            miniPlayerControlActions = MiniPlayerControlActions(
                onPlay = {},
                onPause = {},
            ),
        )
    }
}
