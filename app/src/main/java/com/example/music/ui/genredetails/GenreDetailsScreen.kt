package com.example.music.ui.genredetails

import android.util.Log
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.testing.PreviewGenres
import com.example.music.domain.testing.getSongsInGenre
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.player.MiniPlayerControlActions
import com.example.music.ui.shared.MiniPlayer
import com.example.music.ui.shared.DetailsSortSelectionBottomModal
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.GenreMoreOptionsBottomModal
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.PlayShuffleButtons
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemLightPreview
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
        GenreDetailsError(onRetry = viewModel::refresh)
    }
    Surface {
        if (uiState.isReady) {
            GenreDetailsScreen(
                genre = uiState.genre,
                songs = uiState.songs,
                selectSong = uiState.selectSong,
                isActive = viewModel.isActive, // if playback is active
                isPlaying = viewModel.isPlaying,
                currentSong = viewModel.currentSong,

                onGenreAction = viewModel::onGenreAction,
                navigateBack = navigateBack,
                navigateToPlayer = navigateToPlayer,
                navigateToSearch = navigateToSearch,
                navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToArtistDetails = navigateToArtistDetails,
                modifier = Modifier.fillMaxSize(),
                miniPlayerControlActions = MiniPlayerControlActions(
                    onPlayPress = viewModel::onPlay,
                    onPausePress = viewModel::onPause,
                    onNext = viewModel::onNext,
                    onPrevious = viewModel::onPrevious
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
) {
    Error(
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Loading Screen
 */
@Composable
private fun GenreDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }
//full screen circular progress - loading screen

/**
 * Stateless Composable for Genre Details Screen
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GenreDetailsScreen(
    genre: GenreInfo,
    songs: List<SongInfo>,
    selectSong: SongInfo,
    isActive: Boolean,
    isPlaying: Boolean,
    currentSong: SongInfo,

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

    val appBarScrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )
    val isCollapsed = remember {
        derivedStateOf {
            appBarScrollBehavior.state.collapsedFraction > 0.8
        }
    }

    val sheetState = rememberModalBottomSheetState(false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var showGenreMoreOptions by remember { mutableStateOf(false) }
    var showSongMoreOptions by remember { mutableStateOf(false) }

    ScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets.systemBarsIgnoringVisibility,
            topBar = {
                LargeTopAppBar(
                    title = {
                        // if true, bar is collapsed so use album title as title
                        if ( isCollapsed.value ) {
                            Text(
                                text = genre.name,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.basicMarquee()
                            )
                        } else {
                            // if false, bar is expanded so use full header
                            GenreDetailsHeaderTitle(genre, modifier)
                        }
                    },
                    navigationIcon = {
                        // Back btn
                        IconButton(onClick = navigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.icon_back_nav),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    },
                    actions = {
                        // Search btn
                        IconButton(onClick = navigateToSearch) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = stringResource(R.string.icon_search),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }

                        // Genre More Options btn
                        IconButton(
                            onClick = {
                                showBottomSheet = true
                                showGenreMoreOptions = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.icon_more),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
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
                /*GenreDetailsTopAppBar(
                    navigateBack = navigateBack,
                    navigateToSearch = navigateToSearch,
                    onMoreOptionsClick = {
                        showBottomSheet = true
                        showGenreMoreOptions = true
                    }
                ) */
            },
            bottomBar = {
                if (isActive){
                    MiniPlayer(
                        song = currentSong,
                        isPlaying = isPlaying,
                        navigateToPlayer = navigateToPlayer,
                        onPlayPress = miniPlayerControlActions.onPlayPress,
                        onPausePress = miniPlayerControlActions.onPausePress,
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background) // MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
        ) { contentPadding ->
            // GenreDetails Content
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = modifier.padding(contentPadding)
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
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

                // Song List
                items(
                    items = songs
                ) { song ->
                    SongListItem(
                        song = song,
                        onClick = {
                            Log.i(TAG, "Song clicked ${song.title}")
                            onGenreAction(GenreAction.PlaySong(song))
                            navigateToPlayer()
                        },
                        onMoreOptionsClick = {
                            Log.i(TAG, "Song More Option clicked ${song.title}")
                            onGenreAction(GenreAction.SongMoreOptionClicked(song))
                            showBottomSheet = true
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

            // GenreDetails BottomSheet
            if (showBottomSheet) {
                Log.i(TAG, "GenreDetails Content -> showBottomSheet is TRUE")
                // bottom sheet context - sort btn
                if (showSortSheet) {
                    Log.i(TAG, "GenreDetails Content -> Song Sort Modal is TRUE")
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
                                Log.i(TAG, "set showBottomSheet to FALSE; set Song Sort to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showSortSheet = false
                                }
                            }
                        },
                        content = "SongInfo",
                        context = "GenreDetails",
                    )
                }

                // bottom sheet context - genre more option btn
                else if (showGenreMoreOptions) {
                    Log.i(TAG, "GenreDetails Content -> Genre More Options is TRUE")
                    GenreMoreOptionsBottomModal(
                        onDismissRequest = {
                            showBottomSheet = false
                            showGenreMoreOptions = false
                        },
                        sheetState = sheetState,
                        genre = genre,
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Genre More Options Modal -> Play Songs clicked")
                                onGenreAction(GenreAction.PlaySongs(songs))
                                sheetState.hide()
                                navigateToPlayer()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showGenreMoreOptions = false
                                }
                            }
                        },
                        playNext = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Play Songs Next clicked")
                                onGenreAction(GenreAction.PlaySongsNext(songs))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showGenreMoreOptions = false
                                }
                            }
                        },
                        shuffle = {
                            coroutineScope.launch {
                                Log.i(TAG, "Genre More Options Modal -> Shuffle Songs clicked")
                                onGenreAction(GenreAction.ShuffleSongs(songs))
                                sheetState.hide()
                                navigateToPlayer()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showGenreMoreOptions = false
                                }
                            }
                        },
                        //addToPlaylist = {},
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Genre More Options Modal -> Queue Songs clicked")
                                onGenreAction(GenreAction.QueueSongs(songs))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showGenreMoreOptions = false
                                }
                            }
                        },
                        onClose = {
                            coroutineScope.launch {
                                Log.i(TAG, "Hide sheet state")
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showGenreMoreOptions = false
                                }
                            }
                        },
                        context = "GenreDetails",
                    )
                }

                // bottom sheet context - song more option btn
                else if (showSongMoreOptions) {
                    Log.i(TAG, "GenreDetails Content -> Song More Options is TRUE")
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
                                onGenreAction(GenreAction.PlaySong(selectSong))
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
                                onGenreAction(GenreAction.PlaySongNext(selectSong))
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
                        //addToPlaylist = {},
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> QueueSong clicked")
                                onGenreAction(GenreAction.QueueSong(selectSong))
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
                                showBottomSheet = false
                                showSongMoreOptions = false
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
                        context = "GenreDetails",
                    )
                }
            }
        }
    }
}

/**
 * Composable for Genre Details Screen's Top App Bar.
 */
@Composable
fun GenreDetailsTopAppBar(
    navigateBack: () -> Unit,
    navigateToSearch: () -> Unit,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        // Back button
        IconButton(onClick = navigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.icon_back_nav),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        // Search btn
        IconButton(onClick = navigateToSearch) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.icon_search),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        // Genre More Options btn
        IconButton(onClick = onMoreOptionsClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/**
 * Composable for Genre Details Screen's Content.
 */
/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDetailsContent(
    coroutineScope: CoroutineScope,
    genre: GenreInfo,
    songs: List<SongInfo>,
    selectSong: SongInfo,
    onGenreAction: (GenreAction) -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.i(TAG, "GenreContent START")
    val sheetState = rememberModalBottomSheetState(false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var showGenreMoreOptions by remember { mutableStateOf(false) }
    var showSongMoreOptions by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize(),
        //does not have the initial .padding(horizontal = 12.dp) that Playlist Details
        // has because of the possible future where Albums shown in a horizontal pager
        // aka it mimics ArtistDetails
    ) {
        // section 1: header item
        fullWidthItem {
            GenreDetailsHeaderItem(
                genre = genre,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        // section 2: songs list
        if (songs.isNotEmpty()) {
            // songs header
            fullWidthItem {
                SongCountAndSortSelectButtons(
                    songs = songs,
                    onSelectClick = {
                        Log.i(TAG, "Multi Select btn clicked")
                    },
                    onSortClick = {
                        Log.i(TAG, "Song Sort btn clicked")
                        showBottomSheet = true
                        showSortSheet = true
                    }
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

            // songs list
            items(songs) { song ->
                Box(Modifier.padding(horizontal = 12.dp, vertical = 0.dp)) {
                    SongListItem(
                        song = song,
                        onClick = {
                            Log.i(TAG, "Song clicked ${song.title}")
                            onGenreAction(GenreAction.PlaySong(song))
                            navigateToPlayer()
                        },
                        onMoreOptionsClick = {
                            Log.i(TAG, "Song More Option clicked ${song.title}")
                            onGenreAction(GenreAction.SongMoreOptionClicked(song))
                            showBottomSheet = true
                            showSongMoreOptions = true
                        },
                        //onQueueSong = { },
                        isListEditable = false,
                        showArtistName = true,
                        showAlbumImage = true,
                        showAlbumTitle = true,
                        showTrackNumber = false,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }

    // GenreDetails BottomSheet
    if (showBottomSheet) {
        Log.i(TAG, "GenreDetails Content -> showBottomSheet is TRUE")
        // bottom sheet context - sort btn
        if (showSortSheet) {
            Log.i(TAG, "GenreDetails Content -> Song Sort Modal is TRUE")
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
                        Log.i(TAG, "set showBottomSheet to FALSE")
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
                        Log.i(TAG, "set showBottomSheet to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showSortSheet = false
                        }
                    }
                },
                content = "SongInfo",
                context = "GenreDetails",
            )
        }

        // bottom sheet context - genre more option btn
        else if (showGenreMoreOptions) {
            Log.i(TAG, "GenreDetails Content -> Genre More Options is TRUE")
            GenreMoreOptionsBottomModal(
                onDismissRequest = {
                    showBottomSheet = false
                    showGenreMoreOptions = false
                },
                sheetState = sheetState,
                genre = genre,
                play = {
                    coroutineScope.launch {
                        Log.i(TAG, "Genre More Options Modal -> Play Songs clicked")
                        onGenreAction(GenreAction.PlaySongs(songs))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showGenreMoreOptions = false
                        }
                    }
                },
                playNext = {
                    coroutineScope.launch {
                        Log.i(TAG, "Album More Options Modal -> Play Songs Next clicked")
                        onGenreAction(GenreAction.PlaySongsNext(songs))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showGenreMoreOptions = false
                        }
                    }
                },
                shuffle = {
                    coroutineScope.launch {
                        Log.i(TAG, "Genre More Options Modal -> Shuffle Songs clicked")
                        onGenreAction(GenreAction.ShuffleSongs(songs))
                        sheetState.hide()
                        navigateToPlayer()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showGenreMoreOptions = false
                        }
                    }
                },
                //addToPlaylist = {},
                addToQueue = {
                    coroutineScope.launch {
                        Log.i(TAG, "Genre More Options Modal -> Queue Songs clicked")
                        onGenreAction(GenreAction.QueueSongs(songs))
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showGenreMoreOptions = false
                        }
                    }
                },
                onClose = {
                    coroutineScope.launch {
                        Log.i(TAG, "Hide sheet state")
                        sheetState.hide()
                    }.invokeOnCompletion {
                        Log.i(TAG, "set showBottomSheet to FALSE; set GenreMoreOptions to FALSE")
                        if(!sheetState.isVisible) {
                            showBottomSheet = false
                            showGenreMoreOptions = false
                        }
                    }
                },
                context = "GenreDetails",
            )
        }

        // bottom sheet context - song more option btn
        else if (showSongMoreOptions) {
            Log.i(TAG, "GenreDetails Content -> Song More Options is TRUE")
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
                        onGenreAction(GenreAction.PlaySong(selectSong))
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
                        onGenreAction(GenreAction.PlaySongNext(selectSong))
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
                //addToPlaylist = {},
                addToQueue = {
                    coroutineScope.launch {
                        Log.i(TAG, "Song More Options Modal -> QueueSong clicked")
                        onGenreAction(GenreAction.QueueSong(selectSong))
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
                        showBottomSheet = false
                        showSongMoreOptions = false
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
                context = "GenreDetails",
            )
        }
    }
}*/

@Composable
fun GenreDetailsHeaderItem(
    genre: GenreInfo,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.padding(Keyline1)
    ) {
        //val widthConstraint = this.maxWidth
        val maxImageSize = this.maxWidth / 2
        //val imageSize = min(maxImageSize, 148.dp)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = genre.name,
                maxLines = 2,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                //color = MaterialTheme.colorScheme.primaryContainer,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}


@Composable
fun GenreDetailsHeaderTitle(
    genre: GenreInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = genre.name,
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
fun GenreDetailsHeaderItemPreview() {
    GenreDetailsHeaderItem(
        genre = PreviewGenres[0],
    )
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
            isActive = true,
            isPlaying = true,
            currentSong = PreviewSongs[0],

            onGenreAction = {},
            navigateToPlayer = {},
            navigateToSearch = {},
            navigateToAlbumDetails = {},
            navigateToArtistDetails = {},
            navigateBack = {},
            miniPlayerControlActions = MiniPlayerControlActions(
                onPlayPress = {},
                onPausePress = {},
                onNext = {},
                onPrevious = {},
            ),
        )
    }
}
