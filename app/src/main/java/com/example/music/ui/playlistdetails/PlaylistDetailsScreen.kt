package com.example.music.ui.playlistdetails

import android.net.Uri
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.ITEM_IMAGE_CARD_SIZE
import com.example.music.designsys.theme.TOP_BAR_IMAGE_EXPANDED_HEIGHT
import com.example.music.designsys.theme.SCREEN_PADDING
import com.example.music.designsys.theme.TOP_BAR_COLLAPSED_HEIGHT
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.domain.testing.getPlaylistSongs
import com.example.music.ui.player.MiniPlayerControlActions
import com.example.music.ui.shared.AddToPlaylistFAB
import com.example.music.ui.shared.BackNavBtn
import com.example.music.ui.shared.DetailsSortSelectionBottomModal
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.ItemCountAndPlusSortSelectButtons
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.MiniPlayer
import com.example.music.ui.shared.MoreOptionsBtn
import com.example.music.ui.shared.PlayShuffleButtons
import com.example.music.ui.shared.PlaylistActions
import com.example.music.ui.shared.PlaylistMoreOptionsBottomModal
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.ScrollToTopFAB
import com.example.music.ui.shared.SearchBtn
import com.example.music.ui.shared.SongActions
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompDarkPreview
import com.example.music.ui.tooling.CompLightPreview
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.util.frontTextPadding
import com.example.music.util.fullWidthItem
import com.example.music.util.listItemIconMod
import com.example.music.util.screenMargin
import kotlinx.coroutines.launch

private const val TAG = "Playlist Details Screen"

/**
 * Stateful version of Playlist Details Screen
 */
@Composable
fun PlaylistDetailsScreen(
    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    viewModel: PlaylistDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        Log.e(TAG, "${uiState.errorMessage}")
        PlaylistDetailsError(onRetry = viewModel::refresh)
    }
    Surface(color = Color.Transparent) {
        if (uiState.isReady) {
            PlaylistDetailsScreen(
                playlist = uiState.playlist,
                songs = uiState.songs,
                selectSong = uiState.selectSong,
                currentSong = viewModel.currentSong,
                isActive = viewModel.isActive, // if playback is active
                isPlaying = viewModel.isPlaying,

                onPlaylistAction = viewModel::onPlaylistAction,
                navigateBack = navigateBack,
                navigateToPlayer = navigateToPlayer,
                navigateToSearch = navigateToSearch,
                navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToArtistDetails = navigateToArtistDetails,
                modifier = Modifier.fillMaxSize(),
                miniPlayerControlActions = MiniPlayerControlActions(
                    onPlay = viewModel::onPlay,
                    onPause = viewModel::onPause,
                ),
            )
        } else {
            PlaylistDetailsLoadingScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Error Screen
 */
@Composable
private fun PlaylistDetailsError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) { Error(onRetry = onRetry, modifier = modifier) }

/**
 * Loading Screen with circular progress indicator in center
 */
@Composable
private fun PlaylistDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }

/**
 * Stateless Composable for Playlist Details Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistDetailsScreen(
    playlist: PlaylistInfo,
    songs: List<SongInfo>,
    selectSong: SongInfo,
    currentSong: SongInfo,
    isActive: Boolean,
    isPlaying: Boolean,

    onPlaylistAction: (PlaylistAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    miniPlayerControlActions: MiniPlayerControlActions,
    modifier: Modifier = Modifier,
) {
    Log.i(TAG, "Playlist Details Screen START\n" +
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

    val sheetState = rememberModalBottomSheetState(true)
    var showSortSheet by remember { mutableStateOf(false) }
    var showPlaylistMoreOptions by remember { mutableStateOf(false) }
    var showSongMoreOptions by remember { mutableStateOf( false ) }

    ScreenBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        // if true, bar is collapsed so use album title as title
                        if ( isCollapsed.value ) {
                            Text(
                                text = playlist.name,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.basicMarquee()
                            )
                        } else {
                            // if false, bar is expanded so use full header
                            //PlaylistDetailsHeaderLargeCover(playlist, modifier)
                            PlaylistDetailsHeader(playlist, modifier)
                        }
                    },
                    navigationIcon = { BackNavBtn(onClick = navigateBack) },
                    actions = {
                        SearchBtn(onClick = navigateToSearch)
                        MoreOptionsBtn(onClick = { showPlaylistMoreOptions = true })
                    },
                    collapsedHeight = TOP_BAR_COLLAPSED_HEIGHT,
                    expandedHeight = TOP_BAR_IMAGE_EXPANDED_HEIGHT,
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
            // PlaylistDetails Content
            Box(Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    state = listState,
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize()
                        .screenMargin()
                ) {
                    if (songs.isEmpty()) {
                        fullWidthItem {
                            PlaylistDetailsEmptyList(
                                onClick = {
                                    Log.i(TAG, "Empty list -> Add to Playlist btn clicked")
                                    // want to navigate to multi-select view of all songs
                                    // maybe have it use sort and filter options?
                                    // have search as well that will also have selection enabled
                                }
                            )
                        }
                    }
                    else {
                        fullWidthItem {
                            ItemCountAndPlusSortSelectButtons(
                                id = R.plurals.songs,
                                itemCount = songs.size,
                                createOrAdd = false, // add to playlist
                                onPlusClick = {
                                    Log.i(TAG, "Add Songs to Playlist btn clicked")
                                }, // want this to nav to multi-select view that shows "add songs to playlist"
                                onSortClick = {
                                    Log.i(TAG, "Song Sort btn clicked")
                                    showSortSheet = true
                                },
                                onSelectClick = {
                                    Log.i(TAG, "Multi-Select btn clicked")
                                    // want to navigate to multi-select view of songs in playlist
                                    // maybe have it use sort and filter options?
                                    // have search as well that will also have selection enabled
                                    // but this search will be just the songs in playlist?
                                },
                            )
                        }
                        fullWidthItem {
                            PlayShuffleButtons(
                                onPlayClick = {
                                    Log.i(TAG, "Play Songs btn clicked")
                                    onPlaylistAction(PlaylistAction.PlaySongs(songs))
                                    navigateToPlayer()
                                },
                                onShuffleClick = {
                                    Log.i(TAG, "Shuffle Songs btn clicked")
                                    onPlaylistAction(PlaylistAction.ShuffleSongs(songs))
                                    navigateToPlayer()
                                },
                            )
                        }
                        items(items = songs) { song ->
                            SongListItem(
                                song = song,
                                onClick = {
                                    Log.i(TAG, "Song clicked: ${song.title}")
                                    onPlaylistAction(PlaylistAction.PlaySong(song))
                                    navigateToPlayer()
                                },
                                onMoreOptionsClick = {
                                    Log.i(TAG, "Song More Option clicked: ${song.title}")
                                    onPlaylistAction(PlaylistAction.SongMoreOptionsClicked(song))
                                    showSongMoreOptions = true
                                },
                                isListEditable = false,
                                showAlbumImage = true,
                                showArtistName = true,
                                showAlbumTitle = true,
                                showTrackNumber = false,
                                modifier = Modifier.fillMaxWidth(),
                            )
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

            // PlaylistDetails BottomSheet
            if (showSortSheet) {
                Log.i(TAG, "PlaylistDetails Content -> Song Sort Modal is TRUE")
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
                    context = "PlaylistDetails"
                )
            }

            if (showPlaylistMoreOptions) {
                Log.i(TAG, "PlaylistDetails Content -> Playlist More Options is TRUE")
                PlaylistMoreOptionsBottomModal(
                    onDismissRequest = { showPlaylistMoreOptions = false },
                    sheetState = sheetState,
                    playlist = playlist,
                    playlistActions = PlaylistActions(
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Playlist More Options Modal -> Play Songs clicked")
                                onPlaylistAction(PlaylistAction.PlaySongs(songs))
                                navigateToPlayer()
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set PlaylistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showPlaylistMoreOptions = false
                            }
                        },
                        playNext = {
                            coroutineScope.launch {
                                Log.i(TAG, "Playlist More Options Modal -> Play Songs Next clicked")
                                onPlaylistAction(PlaylistAction.PlaySongsNext(songs))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set PlaylistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showPlaylistMoreOptions = false
                            }
                        },
                        shuffle = {
                            coroutineScope.launch {
                                Log.i(TAG, "Playlist More Options Modal -> Shuffle Songs clicked")
                                onPlaylistAction(PlaylistAction.ShuffleSongs(songs))
                                navigateToPlayer()
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set PlaylistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showPlaylistMoreOptions = false
                            }
                        },
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Playlist More Options Modal -> Queue Songs clicked")
                                onPlaylistAction(PlaylistAction.QueueSongs(songs))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set PlaylistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showPlaylistMoreOptions = false
                            }
                        },
                    ),
                    onClose = {
                        coroutineScope.launch {
                            Log.i(TAG, "Hide sheet state")
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set PlaylistMoreOptions to FALSE")
                            if(!sheetState.isVisible) showPlaylistMoreOptions = false
                        }
                    },
                    context = "PlaylistDetails"
                )
            }

            if (showSongMoreOptions) {
                Log.i(TAG, "PlaylistDetails Content -> Song More Options is TRUE")
                SongMoreOptionsBottomModal(
                    onDismissRequest = { showSongMoreOptions = false },
                    sheetState = sheetState,
                    song = selectSong,
                    songActions = SongActions(
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Play Song clicked :: ${selectSong.title}")
                                onPlaylistAction(PlaylistAction.PlaySong(selectSong))
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
                                onPlaylistAction(PlaylistAction.PlaySongNext(selectSong))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if(!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Queue Song clicked :: ${selectSong.title}")
                                onPlaylistAction(PlaylistAction.QueueSong(selectSong))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "showSongMoreOptions to FALSE")
                                if(!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        goToArtist = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Go To Artist clicked :: ${selectSong.artistId}")
                                navigateToArtistDetails(selectSong.artistId)
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set SongMoreOptions to FALSE")
                                if(!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        goToAlbum = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Go To Album clicked :: ${selectSong.albumId}")
                                navigateToAlbumDetails(selectSong.albumId)
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set SongMoreOptions to FALSE")
                                if(!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                    ),
                    onClose = {
                        coroutineScope.launch {
                            Log.i(TAG, "Hide sheet state")
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set SongMoreOptions to FALSE")
                            if(!sheetState.isVisible) showSongMoreOptions = false
                        }
                    },
                    context = "PlaylistDetails"
                )
            }
        }
    }
}

/**
 * Composable for Playlist Details Screen's header.
 * Has playlist image on the left side, playlist name on the right side.
 */
@Composable
private fun PlaylistDetailsHeader(
    playlist: PlaylistInfo,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val imageSize = min(this.maxWidth / 2, ITEM_IMAGE_CARD_SIZE)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(end = SCREEN_PADDING),
        ) {
            if (playlist.playlistImage.isEmpty()) {
                AlbumImage(
                    albumImage = Uri.parse(""),
                    contentDescription = playlist.name,
                    modifier = Modifier.listItemIconMod(imageSize, MaterialTheme.shapes.medium),
                )
            } else {
                PlaylistDetailsThumbnails(playlist.playlistImage, imageSize / 2)
            }
            Text(
                text = playlist.name,
                maxLines = 2,
                overflow = TextOverflow.Visible,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.frontTextPadding().align(Alignment.CenterVertically)
            )
        }
    }
}

/**
 * Composable for Playlist Details Screen to display the playlist image as a set of the first few songs of the playlist
 */
@Composable
fun PlaylistDetailsThumbnails(
    playlistImage: List<Uri>,
    imageSize: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clip(MaterialTheme.shapes.medium)) {
        Column {
            Row {
                AlbumImage(
                    albumImage = playlistImage[0],
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                )
                AlbumImage(
                    albumImage = playlistImage[1],
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                    )
            }
            Row {
                AlbumImage(
                    albumImage = playlistImage[2],
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                )
                AlbumImage(
                    albumImage = playlistImage[3],
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                )
            }
        }
    }
}

/**
 * Content section that appears when the playlist has 0 songs.
 * Shows prompt that gives user option to add songs to playlist.
 * Tapping the btn should send user to multi-select songs screen.
 */
@Composable
private fun PlaylistDetailsEmptyList(
    onClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
            .padding(SCREEN_PADDING)
    ) {
        Text(text = "Add Songs to Playlist")
        AddToPlaylistFAB(onClick = onClick)
    }
}

@CompLightPreview
@CompDarkPreview
@Composable
fun PlaylistDetailsHeaderItemPreview() {
    MusicTheme {
        PlaylistDetailsHeader(
            playlist = PreviewPlaylists[1],
        )
    }
}

@SystemLightPreview
@SystemDarkPreview
@Composable
fun PlaylistDetailsScreenPreview() {
    MusicTheme {
        PlaylistDetailsScreen(
            //hello
            //playlist = PreviewPlaylists[0],
            //songs = getPlaylistSongs(0),

            //ack
            //playlist = PreviewPlaylists[1],
            //songs = getPlaylistSongs(1),

            //give the goods
            playlist = PreviewPlaylists[2],
            songs = getPlaylistSongs(2),

            selectSong = getPlaylistSongs(2)[1],
            currentSong = PreviewSongs[9],
            isActive = true,
            isPlaying = false,

            onPlaylistAction = {},
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
