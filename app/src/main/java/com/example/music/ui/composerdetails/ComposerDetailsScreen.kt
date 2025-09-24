package com.example.music.ui.composerdetails

import android.util.Log
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.theme.Keyline1
import com.example.music.domain.testing.PreviewComposers
import com.example.music.domain.testing.getSongsByComposer
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.player.MiniPlayerControlActions
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.MiniPlayer
import com.example.music.ui.shared.PlayShuffleButtons
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.util.BackNavBtn
import com.example.music.util.MoreOptionsBtn
import com.example.music.util.ScrollToTopFAB
import com.example.music.util.SearchBtn
import com.example.music.util.fullWidthItem
import kotlinx.coroutines.launch

private const val TAG = "Composer Details Screen"

/**
 * Stateful version of Composer Details Screen
 */
@Composable
fun ComposerDetailsScreen(
    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    viewModel: ComposerDetailsViewModel = hiltViewModel(),
) {
    Log.i(TAG, "Composer Details Screen START")
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        ComposerDetailsError(onRetry = viewModel::refresh)
    }
    Surface(color = Color.Transparent) {
        if (uiState.isReady) {
            ComposerDetailsScreen(
                composer = uiState.composer,
                songs = uiState.songs,
                currentSong = viewModel.currentSong,
                isActive = viewModel.isActive, // if playback is active
                isPlaying = viewModel.isPlaying,

                onComposerAction = viewModel::onComposerAction,
                navigateBack = navigateBack,
                navigateToPlayer = navigateToPlayer,
                navigateToSearch = navigateToSearch,
                navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToArtistDetails = navigateToArtistDetails,
                modifier = Modifier.fillMaxSize(),
                miniPlayerControlActions = MiniPlayerControlActions(
                    onPlayPress = viewModel::onPlay,
                    onPausePress = viewModel::onPause,
                )
            )
        } else {
            ComposerDetailsLoadingScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Error Screen
 */
@Composable
private fun ComposerDetailsError(
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
private fun ComposerDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }

/**
 * Stateless Composable for Composer Details Screen
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ComposerDetailsScreen(
    composer: ComposerInfo,
    songs: List<SongInfo>,
    currentSong: SongInfo,
    isActive: Boolean,
    isPlaying: Boolean,

    onComposerAction: (ComposerAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    miniPlayerControlActions: MiniPlayerControlActions,
    modifier: Modifier = Modifier
) {
    Log.i(TAG, "ComposerDetails Screen START\n" +
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
            appBarScrollBehavior.state.collapsedFraction > 0.8
        }
    }

    val listState = rememberLazyGridState()
    val displayButton = remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    val sheetState = rememberModalBottomSheetState(false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var showComposerMoreOptions by remember { mutableStateOf(false) }
    var showSongMoreOptions by remember { mutableStateOf(false) }

    ScreenBackground(
        modifier = modifier
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        if ( isCollapsed.value ) {
                            Text(
                                text = composer.name,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.basicMarquee()
                            )
                        } else {
                            ComposerDetailsHeaderTitle(composer, modifier)
                        }
                    },
                    navigationIcon = {
                        // Back btn
                        BackNavBtn(onClick = navigateBack)
                    },
                    actions = {
                        // Search btn
                        SearchBtn(onClick = navigateToSearch)

                        // Composer More Options
                        MoreOptionsBtn(
                            onClick = {
                                showBottomSheet = true
                                showComposerMoreOptions = true
                            }
                        )
                    },
                    collapsedHeight = 48.dp,//TopAppBarDefaults.LargeAppBarCollapsedHeight, // is 64.dp
                    expandedHeight = 120.dp,//80.dp,//TopAppBarDefaults.LargeAppBarExpandedHeight,//200.dp, // for Header
                    windowInsets = TopAppBarDefaults.windowInsets,
                    colors = TopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
                        onPlayPress = miniPlayerControlActions.onPlayPress,
                        onPausePress = miniPlayerControlActions.onPausePress,
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background)
        ) { contentPadding ->
            // ComposerDetails Content
            Box(Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = modifier.padding(contentPadding)
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
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
                            }
                        )
                    }

                    fullWidthItem {
                        PlayShuffleButtons(
                            onPlayClick = {
                                Log.i(TAG, "Play Songs btn clicked")
                                //onComposerAction(ComposerAction.PlaySongs(songs))
                                //navigateToPlayer()
                            },
                            onShuffleClick = {
                                Log.i(TAG, "Shuffle Songs btn clicked")
                                //onComposerAction(ComposerAction.ShuffleSongs(songs))
                                //navigateToPlayer()
                            },
                        )
                    }

                    items(items = songs) { song ->
                        SongListItem(
                            song = song,
                            onClick = {
                                Log.i(TAG, "Song clicked: ${song.title}")
                                //onComposerAction(ComposerAction.PlaySong(song))
                                //navigateToPlayer()
                            },
                            onMoreOptionsClick = {
                                Log.i(TAG, "Song More Option clicked: ${song.title}")
                                //onComposerAction(ComposerAction.SongMoreOptionClicked(song))
                                //showBottomSheet = true
                                //showSongMoreOptions = true
                            },
                            isListEditable = false,
                            showAlbumTitle = true,
                            showArtistName = true,
                            showAlbumImage = true,
                            showTrackNumber = false,
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
            /*ComposerDetailsContent(
                composer = composer,
                songs = songs,
                navigateToPlayer = navigateToPlayer,
                modifier = Modifier.padding(contentPadding)
            )*/

            // ComposerDetails BottomSheet content would go here
        }
    }
}

/**
 * Composable for Composer Details Screen's Content.
 */
@Composable
fun ComposerDetailsContent(
    composer: ComposerInfo,
    songs: List<SongInfo>,
    navigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        // Header Item
        fullWidthItem {
            ComposerDetailsHeaderTitle(
                composer = composer,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        // Song List
        if (songs.isNotEmpty()) {
            fullWidthItem {
                ItemCountAndSortSelectButtons(
                    id = R.plurals.composers,
                    itemCount = songs.size,
                    onSortClick = {},
                    onSelectClick = {}
                )
            }

            fullWidthItem {
                PlayShuffleButtons(
                    onPlayClick = {
                        Log.i(TAG, "Play Songs btn clicked")
                        //onComposerAction(ComposerAction.PlaySongs(songs))
                        //navigateToPlayer()
                    },
                    onShuffleClick = {
                        Log.i(TAG, "Shuffle Songs btn clicked")
                        //onComposerAction(ComposerAction.ShuffleSongs(songs))
                        //navigateToPlayer()
                    },
                )
            }

            items(
                items = songs
            ) { song ->
                SongListItem(
                    song = song,
                    onClick = {
                        Log.i(TAG, "Song clicked: ${song.title}")
                        //onComposerAction(ComposerAction.PlaySong(song))
                        //navigateToPlayer()
                    },
                    onMoreOptionsClick = {
                        Log.i(TAG, "Song More Option clicked: ${song.title}")
                        //onComposerAction(ComposerAction.SongMoreOptionClicked(song))
                        //showBottomSheet = true
                        //showSongMoreOptions = true
                    },
                    isListEditable = false,
                    showAlbumTitle = true,
                    showArtistName = true,
                    showAlbumImage = true,
                    showTrackNumber = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun ComposerDetailsHeaderTitle(
    composer: ComposerInfo,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = composer.name,
            maxLines = 2,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            //color = MaterialTheme.colorScheme.primaryContainer,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

//@Preview
@Composable
fun ComposerDetailsHeaderItemPreview() {
    ComposerDetailsHeaderTitle(
        composer = PreviewComposers[0],
    )
}

@SystemLightPreview
@SystemDarkPreview
@Composable
fun ComposerDetailsScreenPreview() {
    MusicTheme {
        ComposerDetailsScreen(
            //composer = PreviewComposers[0],
            //songs = getSongsByComposer(291),

            //Paramore
            //composer = PreviewComposers[3],
            //songs = getSongsByComposer(410),

            //Tatsuya Kitani
            composer = PreviewComposers[1],
            songs = getSongsByComposer(PreviewComposers[1].id),
            currentSong = PreviewSongs[0],
            isActive = true,
            isPlaying = true,

            onComposerAction = {},
            navigateBack = {},
            navigateToPlayer = {},
            navigateToSearch = {},
            navigateToAlbumDetails = {},
            navigateToArtistDetails = {},
            miniPlayerControlActions = MiniPlayerControlActions(
                onPlayPress = {},
                onPausePress = {},
            ),
        )
    }
}
