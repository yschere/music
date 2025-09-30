package com.example.music.ui.artistdetails

import android.util.Log
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.SCREEN_PADDING
import com.example.music.designsys.theme.TOP_BAR_COLLAPSED_HEIGHT
import com.example.music.designsys.theme.TOP_BAR_EXPANDED_HEIGHT
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.getAlbumsByArtist
import com.example.music.domain.testing.getSongsByArtist
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.player.MiniPlayerControlActions
import com.example.music.ui.shared.AlbumActions
import com.example.music.ui.shared.AlbumMoreOptionsBottomModal
import com.example.music.ui.shared.ArtistActions
import com.example.music.ui.shared.ArtistMoreOptionsBottomModal
import com.example.music.ui.shared.MiniPlayer
import com.example.music.ui.shared.DetailsSortSelectionBottomModal
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.FeaturedAlbumsCarousel
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.PlayShuffleButtons
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongActions
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.LandscapePreview
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.util.BackNavBtn
import com.example.music.util.MoreOptionsBtn
import com.example.music.util.ScrollToTopFAB
import com.example.music.util.SearchBtn
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

private const val TAG = "Artist Details Screen"

/**
 * Stateful version of Artist Details Screen
 */
@Composable
fun ArtistDetailsScreen(
    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    viewModel: ArtistDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        Log.e(TAG, "${uiState.errorMessage}")
        ArtistDetailsError(onRetry = viewModel::refresh)
    }
    Surface(color = Color.Transparent) {
        if (uiState.isReady) {
            ArtistDetailsScreen(
                artist = uiState.artist,
                albums = uiState.albums.toPersistentList(),
                songs = uiState.songs,
                selectSong = uiState.selectSong,
                selectAlbum = uiState.selectAlbum,
                currentSong = viewModel.currentSong,
                isActive = viewModel.isActive, // if playback is active
                isPlaying = viewModel.isPlaying,

                onArtistAction = viewModel::onArtistAction,
                navigateBack = navigateBack,
                navigateToPlayer = navigateToPlayer,
                navigateToSearch = navigateToSearch,
                navigateToAlbumDetails = navigateToAlbumDetails,
                modifier = Modifier.fillMaxSize(),
                miniPlayerControlActions = MiniPlayerControlActions(
                    onPlay = viewModel::onPlay,
                    onPause = viewModel::onPause,
                )
            )
        } else {
            ArtistDetailsLoadingScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Error Screen
 */
@Composable
private fun ArtistDetailsError(
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
private fun ArtistDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }

/**
 * Stateless version of Artist Details Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailsScreen(
    artist: ArtistInfo,
    albums: PersistentList<AlbumInfo>,
    songs: List<SongInfo>,
    selectSong: SongInfo,
    selectAlbum: AlbumInfo,
    currentSong: SongInfo,
    isActive: Boolean,
    isPlaying: Boolean,

    onArtistAction: (ArtistAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    miniPlayerControlActions: MiniPlayerControlActions,
    modifier: Modifier = Modifier
) {
    Log.i(TAG, "ArtistDetails Screen START\n" +
        "currentSong? ${currentSong.title}\n" +
        "isActive? $isActive")

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)

    val appBarScrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )
    val isCollapsed = remember {
        derivedStateOf {
            appBarScrollBehavior.state.collapsedFraction > 0.5
        }
    }

    val listState = rememberLazyGridState()
    val displayButton = remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    val sheetState = rememberModalBottomSheetState(true)
    var showSortSheet by remember { mutableStateOf(false) }
    var showAlbumMoreOptions by remember { mutableStateOf(false) }
    var showArtistMoreOptions by remember { mutableStateOf(false) }
    var showSongMoreOptions by remember { mutableStateOf( false ) }

    ScreenBackground(
        modifier = modifier
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = artist.name,
                            style = MaterialTheme.typography.headlineMedium,
                            overflow = TextOverflow.Clip,
                            modifier =
                                if (isCollapsed.value) Modifier.basicMarquee()
                                else Modifier,
                        )
                    },
                    navigationIcon = { BackNavBtn(onClick = navigateBack) },
                    actions = {
                        SearchBtn(onClick = navigateToSearch)
                        MoreOptionsBtn(onClick = { showArtistMoreOptions = true })
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
            val pagerState = rememberPagerState { albums.size }
            // ArtistDetails Content
            Box(Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    state = listState,
                    modifier = modifier.padding(contentPadding)
                        .fillMaxSize()
                        // does not have .padding(horizontal = SCREEN_PADDING) to account for the albums carousel
                ) {
                    // Albums Section
                    if (albums.isNotEmpty()) {
                        fullWidthItem {
                            // this item is only for listing count of albums, so not using sorting or selection here
                            Text(
                                text = """\s[a-z]""".toRegex().replace(
                                    quantityStringResource(R.plurals.albums, albums.size, albums.size)
                                ) {
                                    it.value.uppercase()
                                },
                                textAlign = TextAlign.Left,
                                style = MaterialTheme.typography.titleMedium,
                                // adding horizontal padding for screen with carousel
                                modifier = Modifier.padding(horizontal = SCREEN_PADDING, vertical = CONTENT_PADDING)
                            )
                        }

                        fullWidthItem {
                            FeaturedAlbumsCarousel(
                                pagerState = pagerState,
                                items = albums,
                                onClick = navigateToAlbumDetails,
                                onMoreOptionsClick = { album: AlbumInfo ->
                                    Log.i(TAG, "Album More Options clicked: ${album.title}")
                                    onArtistAction(ArtistAction.AlbumMoreOptionsClicked(album))
                                    showAlbumMoreOptions = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Song Section
                    if (songs.isNotEmpty()) {
                        fullWidthItem {
                            ItemCountAndSortSelectButtons(
                                id = R.plurals.songs,
                                itemCount = songs.size,
                                onSortClick = {
                                    Log.i(TAG, "Song Sort btn clicked")
                                    showSortSheet = true
                                },
                                onSelectClick = {
                                    Log.i(TAG, "Multi Select btn clicked")
                                },
                                // adding horizontal padding for screen with carousel
                                modifier = Modifier.padding(horizontal = SCREEN_PADDING)
                            )
                        }

                        fullWidthItem {
                            PlayShuffleButtons(
                                onPlayClick = {
                                    Log.i(TAG, "Play Songs btn clicked")
                                    onArtistAction(ArtistAction.PlaySongs(songs))
                                    navigateToPlayer()
                                },
                                onShuffleClick = {
                                    Log.i(TAG, "Shuffle Songs btn clicked")
                                    onArtistAction(ArtistAction.ShuffleSongs(songs))
                                    navigateToPlayer()
                                },
                                // adding horizontal padding for screen with carousel
                                modifier = Modifier.padding(horizontal = SCREEN_PADDING)
                            )
                        }

                        items(items = songs) { song ->
                            // adding horizontal padding for screen with carousel
                            Box(Modifier.padding(horizontal = SCREEN_PADDING)) {
                                SongListItem(
                                    song = song,
                                    onClick = {
                                        Log.i(TAG, "Song clicked: ${song.title}")
                                        onArtistAction(ArtistAction.PlaySong(song))
                                        navigateToPlayer()
                                    },
                                    onMoreOptionsClick = {
                                        Log.i(TAG, "Song More Option clicked: ${song.title}")
                                        onArtistAction(ArtistAction.SongMoreOptionsClicked(song))
                                        showSongMoreOptions = true
                                    },
                                    isListEditable = false,
                                    showAlbumImage = true,
                                    showArtistName = true,
                                    showAlbumTitle = true,
                                    showTrackNumber = false,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
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

            // ArtistDetails BottomSheet
            if (showSortSheet) {
                Log.i(TAG, "ArtistDetails Content -> Song Sort Modal is TRUE")
                DetailsSortSelectionBottomModal(
                    onDismissRequest = { showSortSheet = false },
                    sheetState = sheetState,
                    onClose = {
                        coroutineScope.launch {
                            Log.i(TAG, "Hide sheet state")
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set Song Sort to FALSE")
                            if(!sheetState.isVisible) showSortSheet = false
                        }
                    },
                    onApply = {
                        coroutineScope.launch {
                            Log.i(TAG, "Save sheet state - does nothing atm")
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set Song Sort to FALSE")
                            if(!sheetState.isVisible) showSortSheet = false
                        }
                    },
                    content = "SongInfo",
                    context = "ArtistDetails",
                )
            }

            if (showAlbumMoreOptions) {
                Log.i(TAG, "ArtistDetails Content -> Album More Options is TRUE")
                AlbumMoreOptionsBottomModal(
                    onDismissRequest = { showAlbumMoreOptions = false },
                    sheetState = sheetState,
                    album = selectAlbum,
                    albumActions = AlbumActions(
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Play Album clicked :: ${selectAlbum.id}")
                                onArtistAction(ArtistAction.PlayAlbum(selectAlbum))
                                sheetState.hide()
                                navigateToPlayer()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                                if(!sheetState.isVisible) showAlbumMoreOptions = false
                            }
                        },
                        playNext = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Play Album Next clicked :: ${selectAlbum.id}")
                                onArtistAction(ArtistAction.PlayAlbumNext(selectAlbum))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                                if(!sheetState.isVisible) showAlbumMoreOptions = false
                            }
                        },
                        shuffle = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Shuffle Album clicked :: ${selectAlbum.id}")
                                onArtistAction(ArtistAction.ShuffleAlbum(selectAlbum))
                                sheetState.hide()
                                navigateToPlayer()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                                if(!sheetState.isVisible) showAlbumMoreOptions = false
                            }
                        },
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Queue Album clicked :: ${selectAlbum.id}")
                                onArtistAction(ArtistAction.QueueAlbum(selectAlbum))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                                if(!sheetState.isVisible) showAlbumMoreOptions = false
                            }
                        },
                        goToAlbum = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Go To Album clicked :: ${selectAlbum.id}")
                                navigateToAlbumDetails(selectAlbum.id)
                                sheetState.hide()
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
                    context = "ArtistDetails",
                )
            }

            if (showSongMoreOptions) {
                Log.i(TAG, "ArtistDetails Content -> Song More Options is TRUE")
                SongMoreOptionsBottomModal(
                    onDismissRequest = { showSongMoreOptions = false },
                    sheetState = sheetState,
                    song = selectSong,
                    songActions = SongActions(
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Play Song clicked :: ${selectSong.id}")
                                onArtistAction(ArtistAction.PlaySong(selectSong))
                                navigateToPlayer()
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if(!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        playNext = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Play Song Next clicked :: ${selectSong.id}")
                                onArtistAction(ArtistAction.PlaySongNext(selectSong))
                                navigateToPlayer()
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if(!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Queue Song clicked :: ${selectSong.id}")
                                onArtistAction(ArtistAction.QueueSong(selectSong))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if(!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        goToAlbum = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Go To Album clicked :: ${selectSong.albumId}")
                                navigateToAlbumDetails(selectSong.albumId)
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if(!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                    ),
                    onClose = {
                        coroutineScope.launch {
                            Log.i(TAG, "Hide sheet state")
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set showSongMoreOptions to FALSE")
                            if(!sheetState.isVisible) showSongMoreOptions = false
                        }
                    },
                    context = "ArtistDetails",
                )
            }

            if (showArtistMoreOptions) {
                Log.i(TAG, "ArtistDetails Content -> Artist More Options is TRUE")
                ArtistMoreOptionsBottomModal(
                    onDismissRequest = { showArtistMoreOptions = false },
                    sheetState = sheetState,
                    artist = artist,
                    artistActions = ArtistActions(
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> Play Songs clicked")
                                onArtistAction(ArtistAction.PlaySongs(songs))
                                navigateToPlayer()
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showArtistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showArtistMoreOptions = false
                            }
                        },
                        playNext = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> Play Songs Next clicked")
                                onArtistAction(ArtistAction.PlaySongsNext(songs))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showArtistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showArtistMoreOptions = false
                            }
                        },
                        shuffle = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> Shuffle Songs clicked")
                                onArtistAction(ArtistAction.ShuffleSongs(songs))
                                navigateToPlayer()
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showArtistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showArtistMoreOptions = false
                            }
                        },
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> Queue Songs clicked")
                                onArtistAction(ArtistAction.QueueSongs(songs))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showArtistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showArtistMoreOptions = false
                            }
                        },
                    ),
                    onClose = {
                        coroutineScope.launch {
                            Log.i(TAG, "Hide sheet state")
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set showArtistMoreOptions to FALSE")
                            if(!sheetState.isVisible) showArtistMoreOptions = false
                        }
                    },
                    context = "ArtistDetails",
                )
            }
        }
    }
}

@Composable
fun ArtistDetailsHeader_Constraints(
    artist: ArtistInfo,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.sizeIn(maxHeight = this.maxHeight)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = artist.name,
                    maxLines = 2,
                    minLines = 1,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
fun ArtistDetailsHeader(
    artist: ArtistInfo,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = artist.name,
            maxLines = 2,
            minLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@SystemDarkPreview
@LandscapePreview
@Composable
fun ArtistDetailsScreenPreview() {
    MusicTheme {
        ArtistDetailsScreen(
            //Paramore
            //artist = PreviewArtists[1],
            //albums = getAlbumsByArtist(22).toPersistentList(),
            //songs = getSongsByArtist(22),

            //ACIDMAN
            artist = PreviewArtists[0],
            albums = getAlbumsByArtist(113).toPersistentList(),
            songs = getSongsByArtist(113),

            selectSong = getSongsByArtist(PreviewArtists[0].id)[0],
            selectAlbum = getAlbumsByArtist(113)[0],
            currentSong = PreviewSongs[0],
            isActive = true,
            isPlaying = true,

            onArtistAction = {},
            navigateBack = {},
            navigateToPlayer = {},
            navigateToSearch = {},
            navigateToAlbumDetails = {},
            miniPlayerControlActions = MiniPlayerControlActions(
                onPlay = {},
                onPause = {},
            ),
        )
    }
}
