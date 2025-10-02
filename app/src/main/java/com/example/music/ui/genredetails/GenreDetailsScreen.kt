package com.example.music.ui.genredetails

import android.util.Log
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.theme.SCREEN_PADDING
import com.example.music.designsys.theme.TOP_BAR_COLLAPSED_HEIGHT
import com.example.music.designsys.theme.TOP_BAR_EXPANDED_HEIGHT
import com.example.music.domain.testing.PreviewGenres
import com.example.music.domain.testing.getSongsInGenre
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.player.MiniPlayerControlActions
import com.example.music.ui.shared.MiniPlayer
import com.example.music.ui.shared.DetailsSortSelectionBottomModal
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.GenreActions
import com.example.music.ui.shared.GenreMoreOptionsBottomModal
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.PlayShuffleButtons
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongActions
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.ui.shared.BackNavBtn
import com.example.music.ui.shared.MoreOptionsBtn
import com.example.music.ui.shared.ScrollToTopFAB
import com.example.music.ui.shared.SearchBtn
import com.example.music.ui.shared.screenMargin
import com.example.music.util.fullWidthItem
import kotlinx.coroutines.launch

private const val TAG = "Genre Details Screen"

/**
 * Stateful version of Genre Details Screen
 */
@Composable
fun GenreDetailsScreen(
    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    viewModel: GenreDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        Log.e(TAG, "${uiState.errorMessage}")
        GenreDetailsError(onRetry = viewModel::refresh)
    }
    Surface(color = Color.Transparent) {
        if (uiState.isReady) {
            GenreDetailsScreen(
                genre = uiState.genre,
                songs = uiState.songs,
                selectSong = uiState.selectSong,
                currentSong = viewModel.currentSong,
                isActive = viewModel.isActive, // if playback is active
                isPlaying = viewModel.isPlaying,

                onGenreAction = viewModel::onGenreAction,
                navigateBack = navigateBack,
                navigateToPlayer = navigateToPlayer,
                navigateToSearch = navigateToSearch,
                navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToArtistDetails = navigateToArtistDetails,
                modifier = Modifier.fillMaxSize(),
                miniPlayerControlActions = MiniPlayerControlActions(
                    onPlay = viewModel::onPlay,
                    onPause = viewModel::onPause,
                )
            )
        } else {
            GenreDetailsLoadingScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Error Screen
 */
@Composable
private fun GenreDetailsError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) { Error(onRetry = onRetry, modifier = modifier) }

/**
 * Loading Screen with circular progress indicator in center
 */
@Composable
private fun GenreDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }

/**
 * Stateless version of Genre Details Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDetailsScreen(
    genre: GenreInfo,
    songs: List<SongInfo>,
    selectSong: SongInfo,
    currentSong: SongInfo,
    isActive: Boolean,
    isPlaying: Boolean,

    onGenreAction: (GenreAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    miniPlayerControlActions: MiniPlayerControlActions,
    modifier: Modifier = Modifier
) {
    Log.i(TAG, "GenreDetails Screen START\n" +
        "currentSong? ${currentSong.title}\n" +
        "isActive? $isActive")

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)

    val appBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val isCollapsed = remember {
        derivedStateOf { appBarScrollBehavior.state.collapsedFraction > 0.5 }
    }

    val listState = rememberLazyGridState()
    val displayButton = remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    val sheetState = rememberModalBottomSheetState(true)
    var showSortSheet by remember { mutableStateOf(false) }
    var showGenreMoreOptions by remember { mutableStateOf(false) }
    var showSongMoreOptions by remember { mutableStateOf(false) }

    ScreenBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = genre.name,
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
                        MoreOptionsBtn(onClick = { showGenreMoreOptions = true })
                    },
                    collapsedHeight = TOP_BAR_COLLAPSED_HEIGHT,
                    expandedHeight = TOP_BAR_EXPANDED_HEIGHT,
                    windowInsets = TopAppBarDefaults.windowInsets,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
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
            // GenreDetails Content
            Box(Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    state = listState,
                    modifier = modifier.padding(contentPadding)
                        .fillMaxSize()
                        .screenMargin()
                ) {
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
                        )
                    }

                    fullWidthItem {
                        PlayShuffleButtons(
                            onPlayClick = {
                                Log.i(TAG, "Play Songs btn clicked")
                                onGenreAction(GenreAction.PlaySongs(songs))
                                navigateToPlayer()
                            },
                            onShuffleClick = {
                                Log.i(TAG, "Shuffle Songs btn clicked")
                                onGenreAction(GenreAction.ShuffleSongs(songs))
                                navigateToPlayer()
                            },
                        )
                    }

                    items(items = songs) { song ->
                        SongListItem(
                            song = song,
                            onClick = {
                                Log.i(TAG, "Song clicked: ${song.title}")
                                onGenreAction(GenreAction.PlaySong(song))
                                navigateToPlayer()
                            },
                            onMoreOptionsClick = {
                                Log.i(TAG, "Song More Option clicked: ${song.title}")
                                onGenreAction(GenreAction.SongMoreOptionsClicked(song))
                                showSongMoreOptions = true
                            },
                            isListEditable = false,
                            showArtistName = true,
                            showAlbumImage = true,
                            showAlbumTitle = true,
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

            // GenreDetails BottomSheet
            if (showSortSheet) {
                Log.i(TAG, "GenreDetails Content -> Song Sort Modal is TRUE")
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
                    context = "GenreDetails",
                )
            }

            if (showGenreMoreOptions) {
                Log.i(TAG, "GenreDetails Content -> Genre More Options is TRUE")
                GenreMoreOptionsBottomModal(
                    onDismissRequest = { showGenreMoreOptions = false },
                    sheetState = sheetState,
                    genre = genre,
                    genreActions = GenreActions(
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Genre More Options Modal -> Play Songs clicked")
                                onGenreAction(GenreAction.PlaySongs(songs))
                                sheetState.hide()
                                navigateToPlayer()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showGenreMoreOptions to FALSE")
                                if(!sheetState.isVisible) showGenreMoreOptions = false
                            }
                        },
                        playNext = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Play Songs Next clicked")
                                onGenreAction(GenreAction.PlaySongsNext(songs))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showGenreMoreOptions to FALSE")
                                if(!sheetState.isVisible) showGenreMoreOptions = false
                            }
                        },
                        shuffle = {
                            coroutineScope.launch {
                                Log.i(TAG, "Genre More Options Modal -> Shuffle Songs clicked")
                                onGenreAction(GenreAction.ShuffleSongs(songs))
                                sheetState.hide()
                                navigateToPlayer()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showGenreMoreOptions to FALSE")
                                if(!sheetState.isVisible) showGenreMoreOptions = false
                            }
                        },
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Genre More Options Modal -> Queue Songs clicked")
                                onGenreAction(GenreAction.QueueSongs(songs))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showGenreMoreOptions to FALSE")
                                if(!sheetState.isVisible) showGenreMoreOptions = false
                            }
                        },
                    ),
                    onClose = {
                        coroutineScope.launch {
                            Log.i(TAG, "Hide sheet state")
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set showGenreMoreOptions to FALSE")
                            if(!sheetState.isVisible) showGenreMoreOptions = false
                        }
                    },
                    context = "GenreDetails",
                )
            }

            if (showSongMoreOptions) {
                Log.i(TAG, "GenreDetails Content -> Song More Options is TRUE")
                SongMoreOptionsBottomModal(
                    onDismissRequest = { showSongMoreOptions = false },
                    sheetState = sheetState,
                    song = selectSong,
                    songActions = SongActions(
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Play Song clicked :: ${selectSong.title}")
                                onGenreAction(GenreAction.PlaySong(selectSong))
                                navigateToPlayer()
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if(!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        playNext = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Play Song Next clicked :: ${selectSong.title}")
                                onGenreAction(GenreAction.PlaySongNext(selectSong))
                                navigateToPlayer()
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if(!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Queue Song clicked :: ${selectSong.title}")
                                onGenreAction(GenreAction.QueueSong(selectSong))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if(!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        goToArtist = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Go To Artist clicked :: ${selectSong.artistId}")
                                navigateToArtistDetails(selectSong.artistId)
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
                    context = "GenreDetails",
                )
            }
        }
    }
}

@Composable
fun GenreDetailsHeader(
    genre: GenreInfo,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = genre.name,
            maxLines = 2,
            minLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@SystemLightPreview
@Composable
fun GenreDetailsScreenPreview() {
    MusicTheme {
        GenreDetailsScreen(
            //Alternative
            //genre = PreviewGenres[0],
            //songs = getSongsInGenre(0),

            //JPop
            genre = PreviewGenres[3],
            songs = getSongsInGenre(3),
            selectSong = getSongsInGenre(3)[0],
            currentSong = PreviewSongs[0],
            isActive = true,
            isPlaying = true,

            onGenreAction = {},
            navigateBack = {},
            navigateToPlayer = {},
            navigateToSearch = {},
            navigateToAlbumDetails = {},
            navigateToArtistDetails = {},
            miniPlayerControlActions = MiniPlayerControlActions(
                onPlay = {},
                onPause = {},
            ),
        )
    }
}
