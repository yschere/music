package com.example.music.ui.artistdetails

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
import androidx.compose.foundation.pager.rememberPagerState
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
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.getAlbumsByArtist
import com.example.music.domain.testing.getSongsByArtist
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.player.MiniPlayerControlActions
import com.example.music.ui.shared.AlbumMoreOptionsBottomModal
import com.example.music.ui.shared.ArtistMoreOptionsBottomModal
import com.example.music.ui.shared.MiniPlayer
import com.example.music.ui.shared.DetailsSortSelectionBottomModal
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.FeaturedAlbumsCarousel
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.PlayShuffleButtons
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.LandscapePreview
import com.example.music.ui.tooling.SystemDarkPreview
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
        ArtistDetailsError(onRetry = viewModel::refresh)
    }
    Surface {
        if (uiState.isReady) {
            ArtistDetailsScreen(
                artist = uiState.artist,
                albums = uiState.albums.toPersistentList(),
                songs = uiState.songs,
                selectSong = uiState.selectSong,
                selectAlbum = uiState.selectAlbum,
                isActive = viewModel.isActive, // if playback is active
                isPlaying = viewModel.isPlaying,
                currentSong = viewModel.currentSong,

                onArtistAction = viewModel::onArtistAction,
                navigateBack = navigateBack,
                navigateToPlayer = navigateToPlayer,
                navigateToSearch = navigateToSearch,
                navigateToAlbumDetails = navigateToAlbumDetails,
                modifier = Modifier.fillMaxSize(),
                miniPlayerControlActions = MiniPlayerControlActions(
                    onPlayPress = viewModel::onPlay,
                    onPausePress = viewModel::onPause,
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
 * Loading Screen
 */
@Composable
private fun ArtistDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }
//full screen circular progress - loading screen

/**
 * Stateless version of Artist Details Screen
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailsScreen(
    artist: ArtistInfo,
    albums: PersistentList<AlbumInfo>,
    songs: List<SongInfo>,
    selectSong: SongInfo,
    selectAlbum: AlbumInfo,
    isActive: Boolean,
    isPlaying: Boolean,
    currentSong: SongInfo,

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
            appBarScrollBehavior.state.collapsedFraction > 0.8
        }
    }

    val sheetState = rememberModalBottomSheetState(false,)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var showAlbumMoreOptions by remember { mutableStateOf(false) }
    var showArtistMoreOptions by remember { mutableStateOf(false) }
    var showSongMoreOptions by remember { mutableStateOf( false ) }

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
                                text = artist.name,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.basicMarquee()
                            )
                        } else {
                            // if false, bar is expanded so use full header
                            ArtistDetailsHeaderTitle(artist)
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

                        // Artist More Options
                        IconButton(
                            onClick = {
                                showBottomSheet = true
                                showArtistMoreOptions = true
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
                /*ArtistDetailsTopAppBar(
                    navigateBack = navigateBack,
                    navigateToSearch = navigateToSearch,
                    onMoreOptionsClick = {
                        showBottomSheet = true
                        showArtistMoreOptions = true
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
            val albs = albums.toPersistentList()
            val pagerState = rememberPagerState { albs.size }
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = modifier.padding(contentPadding)
                    .fillMaxSize()
                    // does not have .padding(horizontal = 12.dp) to account for the albums carousel
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
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }

                    fullWidthItem {
                        FeaturedAlbumsCarousel(
                            pagerState = pagerState,
                            items = albums,
                            navigateToAlbumDetails = navigateToAlbumDetails,
                            onMoreOptionsClick = { album: AlbumInfo ->
                                Log.i(TAG, "Album More Options clicked: ${album.title}")
                                onArtistAction( ArtistAction.AlbumMoreOptionClicked(album) )
                                showBottomSheet = true
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
                                showBottomSheet = true
                                showSortSheet = true
                            },
                            onSelectClick = {
                                Log.i(TAG, "Multi Select btn clicked")
                            },
                            modifier = Modifier.padding(horizontal = 12.dp) // added for screen with carousel for additional horizontal padding
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
                            modifier = Modifier.padding(horizontal = 12.dp) // added for screen with carousel for additional horizontal padding
                        )
                    }

                    // Songs List
                    items(
                        items = songs
                    ) { song ->
                        Box(
                            Modifier.padding(horizontal = 12.dp, vertical = 0.dp) // added for screen with carousel for additional horizontal padding
                        ) {
                            SongListItem(
                                song = song,
                                onClick = {
                                    Log.i(TAG, "Song clicked: ${song.title}")
                                    onArtistAction(ArtistAction.PlaySong(song))
                                    navigateToPlayer()
                                },
                                onMoreOptionsClick = {
                                    Log.i(TAG, "Song More Option clicked: ${song.title}")
                                    onArtistAction(ArtistAction.SongMoreOptionClicked(song))
                                    showBottomSheet = true
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

            // ArtistDetails BottomSheet
            if (showBottomSheet) {
                Log.i(TAG, "ArtistDetails Content -> showBottomSheet is TRUE")
                // bottom sheet context - sort btn
                if (showSortSheet) {
                    Log.i(TAG, "ArtistDetails Content -> Song Sort Modal is TRUE")
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
                        context = "ArtistDetails",
                    )
                }

                // bottom sheet context - album more option btn
                else if (showAlbumMoreOptions) {
                    Log.i(TAG, "ArtistDetails Content -> Album More Options is TRUE")
                    AlbumMoreOptionsBottomModal(
                        onDismissRequest = {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        },
                        sheetState = sheetState,
                        album = selectAlbum,
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Play Album clicked :: ${selectAlbum.id}")
                                onArtistAction(ArtistAction.PlayAlbum(selectAlbum))
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
                                Log.i(TAG, "Album More Options Modal -> Play Album Next clicked :: ${selectAlbum.id}")
                                onArtistAction(ArtistAction.PlayAlbumNext(selectAlbum))
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
                                Log.i(TAG, "Album More Options Modal -> Shuffle Album clicked :: ${selectAlbum.id}")
                                onArtistAction(ArtistAction.ShuffleAlbum(selectAlbum))
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
                                Log.i(TAG, "Album More Options Modal -> Queue Album clicked :: ${selectAlbum.id}")
                                onArtistAction(ArtistAction.QueueAlbum(selectAlbum))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showAlbumMoreOptions = false
                                }
                            }
                        },
                        goToAlbum = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> GoToAlbum clicked :: ${selectAlbum.id}")
                                navigateToAlbumDetails(selectAlbum.id)
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
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
                                Log.i(TAG, "set showBottomSheet to FALSE; set AlbumMoreOptions to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showAlbumMoreOptions = false
                                }
                            }
                        },
                        context = "ArtistDetails",
                    )
                }

                // bottom sheet context - song more option btn
                else if (showSongMoreOptions) {
                    Log.i(TAG, "ArtistDetails Content -> Song More Options is TRUE")
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
                                onArtistAction(ArtistAction.PlaySong(selectSong))
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
                                onArtistAction(ArtistAction.PlaySongNext(selectSong))
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
                                Log.i(TAG, "Song More Options Modal -> QueueSong clicked :: ${selectSong.id}")
                                onArtistAction(ArtistAction.QueueSong(selectSong))
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
                        context = "ArtistDetails",
                    )
                }

                // bottom sheet context - artist more option btn
                else if (showArtistMoreOptions) {
                    Log.i(TAG, "ArtistDetails Content -> Artist More Options is TRUE")
                    ArtistMoreOptionsBottomModal(
                        onDismissRequest = {
                            showBottomSheet = false
                            showArtistMoreOptions = false
                        },
                        sheetState = sheetState,
                        artist = artist,
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> PlaySongs clicked")
                                onArtistAction(ArtistAction.PlaySongs(songs))
                                navigateToPlayer()
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showArtistMoreOptions = false
                                }
                            }
                        },
                        playNext = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> PlaySongsNext clicked")
                                onArtistAction(ArtistAction.PlaySongsNext(songs))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showArtistMoreOptions = false
                                }
                            }
                        },
                        shuffle = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> ShuffleSongs clicked")
                                onArtistAction(ArtistAction.ShuffleSongs(songs))
                                navigateToPlayer()
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showArtistMoreOptions = false
                                }
                            }
                        },
                        //addToPlaylist = {},
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> QueueSongs clicked")
                                onArtistAction(ArtistAction.QueueSongs(songs))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showBottomSheet to FALSE")
                                if(!sheetState.isVisible) {
                                    showBottomSheet = false
                                    showArtistMoreOptions = false
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
                                    showArtistMoreOptions = false
                                }
                            }
                        },
                        context = "ArtistDetails",
                    )
                }
            }
        }
    }
}

/**
 * Composable for Artist Details Screen's Top App Bar.
 */
@Composable
fun ArtistDetailsTopAppBar(
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

        // Artist More Options btn
        IconButton(onClick = onMoreOptionsClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
fun ArtistDetailsHeaderItem(
    artist: ArtistInfo,
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
                text = artist.name,
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
fun ArtistDetailsHeaderTitle(
    artist: ArtistInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = artist.name,
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
fun ArtistDetailsHeaderItemPreview() {
    ArtistDetailsHeaderItem(
        artist = PreviewArtists[0],
    )
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
            isActive = true,
            isPlaying = true,
            currentSong = PreviewSongs[0],

            onArtistAction = {},
            navigateBack = {},
            navigateToPlayer = {},
            navigateToSearch = {},
            navigateToAlbumDetails = {},
            miniPlayerControlActions = MiniPlayerControlActions(
                onPlayPress = {},
                onPausePress = {},
            ),
        )
    }
}
