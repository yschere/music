package com.example.music.ui.albumdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.TopAppBarExpandedHeight
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberBottomAppBarState
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.Keyline1
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.getArtistData
import com.example.music.domain.testing.getSongsInAlbum
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo

import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.ui.albumdetails.AlbumAction.SongMoreOptionClicked
import com.example.music.ui.shared.AlbumMoreOptionsBottomModal
import com.example.music.ui.shared.DetailsSortSelectionBottomModal
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.shared.formatStr
import com.example.music.ui.shared.gridVerticalScrollbar
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompDarkPreview
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.util.fullWidthItem
import com.example.music.util.logger
import com.example.music.util.quantityStringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Changelog:
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 4/3/2025 - Testing out using LargeTopAppBar within Scaffold to use it's nested
 * scroll behavior, and its collapsible app bar behavior. This could combine the
 * header and the top app bar of the screen.
 *
 * 4/4/2025 - Testing out scrollbar with lazy grid, placed in ui/shared/Scrollbar.kt
 *
 * 4/13/2025 - Added navigateToSearch to Search Icon in TopAppBar
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/**
 * Stateful version of Album Details Screen
 */
@Composable
fun AlbumDetailsScreen(
    navigateToPlayer: (SongInfo) -> Unit,
    //navigateToArtist: (ArtistInfo) -> Unit,
    navigateToSearch: () -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    //modifier: Modifier = Modifier,
    viewModel: AlbumDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        AlbumDetailsError(onRetry = viewModel::refresh)
    }
    Surface {
        if (uiState.isReady) {
            AlbumDetailsScreen(
                album = uiState.album,
                artist = uiState.artist,
                songs = uiState.songs,
                selectSong = uiState.selectSong,
                onAlbumAction = viewModel::onAlbumAction,
                navigateToPlayer = navigateToPlayer,
                navigateToSearch = navigateToSearch,
                navigateBack = navigateBack,
                showBackButton = showBackButton,
                modifier = Modifier.fillMaxSize(),
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

/**
 * Loading Screen
 */
@Composable
private fun AlbumDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }
//full screen circular progress - loading screen

/**
 * Stateless version of Album Details Screen
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailsScreen(
    album: AlbumInfo,
    artist: ArtistInfo,
    songs: List<SongInfo>,
    selectSong: SongInfo,
    //onQueueSong: (SongInfo) -> Unit,
    onAlbumAction: (AlbumAction) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToSearch: () -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier
) { //base level screen data / coroutine setter / screen component(s) caller

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

    var showBottomSheet by remember { mutableStateOf(false) } // if bottom modal needs to be opened
    var showSortSheet by remember { mutableStateOf(false) } // if bottom modal content is for sorting songs
    var showAlbumMoreOptions by remember { mutableStateOf(false) } // if bottom modal content is for album details more options
    var showSongMoreOptions by remember { mutableStateOf( false ) }

    ScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        //base layer structure component
        Scaffold(
            contentWindowInsets = WindowInsets.systemBarsIgnoringVisibility,
            topBar = {
                LargeTopAppBar(
                    title = {
                        // if true, bar is collapsed so use album title as title
                        if ( isCollapsed.value ) {
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
                    navigationIcon = {
                        //back button
                        IconButton(onClick = navigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.icon_back_nav),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    },
                    actions = {
                        // search btn //TODO: does this make more sense as a more options btn?
                        IconButton( onClick = navigateToSearch ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = stringResource(R.string.icon_search),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        // more options btn //TODO: temporary placement till figure out if this should be part of header
                        IconButton(
                            onClick = {
                                showBottomSheet = true
                                showAlbumMoreOptions = true /* onMoreOptionsClick */
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
                    expandedHeight = TopAppBarDefaults.LargeAppBarExpandedHeight + 76.dp,//200.dp, // for Header
                    //expandedHeight = TopAppBarDefaults.LargeAppBarExpandedHeight + 270.dp, // for HeaderLargeAlbumCover
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
//                AlbumDetailsTopAppBar(
//                    navigateBack, modifier
//                )
            },
            bottomBar = {
                /* //should show BottomBarPlayer here if a queue session is running or service is running
                BottomBarPlayer(
                    song = PreviewSongs[5],
                    navigateToPlayer = { navigateToPlayerSong(PreviewSongs[5]) },
                )*/
            },
            snackbarHost = { // setting the snackbar hoststate to the scaffold
                SnackbarHost(hostState = snackbarHostState)
            },
            modifier = modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background) //selects the appropriate color to be the content color for the container using background color
            //contentColor = MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
        ) { contentPadding ->
            // original content setter
            /*AlbumDetailsContent(
                album = album,
                artist = artist,
                songs = songs,
                coroutineScope = coroutineScope,
                navigateToPlayer = navigateToPlayer,
                modifier = Modifier.padding(contentPadding)//.padding(horizontal = 8.dp)
            )*/

            /** 4/4/2025 - attempting to put AlbumDetailsContent directly here to see if
             * doing so will make it possible to have full use of BottomModal for the TopAppBar
             * and the screen content at the same time
             *
             * 4/5/2025 - testing including song item creation directly into lazyVerticalGrid. This
             * should also help with bottom modal
             */

            Box(Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(362.dp),
                    state = listState,
                    modifier = Modifier.padding(contentPadding)
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    fullWidthItem {
                        SongCountAndSortSelectButtons(
                            songs = songs,
                            onSelectClick = {
                                // open song selection screen
                            },
                            onSortClick = {
                                showBottomSheet = true
                                showSortSheet = true
                            }
                        )
                    }

                    fullWidthItem {
                        PlayShuffleButtons(
                            onPlayClick = { onAlbumAction(AlbumAction.PlayAlbum(songs)) /* probably send call to controller, or is it songPlayer? since that's in viewModel */ },
                            onShuffleClick = { onAlbumAction(AlbumAction.ShuffleAlbum(songs)) /* probably send call to controller, or is it songPlayer? since that's in viewModel */ },
                        )
                    }

                    //section 2: songs list
                    items(songs) { song -> // for each song in list:
                        SongListItem(
                                song = song,
                                onClick = { navigateToPlayer(song) },
                                //onMoreOptionsClick = { song, context = "AlbumDetails", content = "SongInfo" }
                                //or
                                onMoreOptionsClick = {
                                    onAlbumAction(AlbumAction.SongMoreOptionClicked(song))
                                    showBottomSheet = true
                                    showSongMoreOptions = true
                                },
                                //onQueueSong = {},
                                modifier = Modifier.fillMaxWidth(), //TODO: uncomment when this is changed to be thru PlayerSong
                                isListEditable = false,
                                showAlbumImage = true,
                                showArtistName = true,
                                showAlbumTitle = false,
                                showTrackNumber = true,
                            )
                    }

                    //section 2: songs list VERSION 2: testing song list creation within content.
                    // Want to try different iterations of presenting song items
                    // things to pass to songListItem:
                        // surfaceOnClick: navigateToPlayer(song)
                        // item: song
                        // moreOptionsIconClick: needs to pop open bottom modal
                            // actions: play, playNext, addToPlaylist, addToQueue, goToArtist, editSongTags, deleteFromLibrary, viewSongDetails
                    /*items(songs) { song ->


                        Box(modifier = modifier.padding(4.dp)) {
                            Surface(
                                shape = MaterialTheme.shapes.large,
                                //color = Color.Transparent,
                                //color = MaterialTheme.colorScheme.surfaceContainer,
                                onClick = { navigateToPlayer(song) },
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    ),
                                ) {
                                    AlbumImage(
                                        albumImage = 1,
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
                                            modifier = Modifier.padding(
                                                vertical = 2.dp,
                                                horizontal = 10.dp
                                            )
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

                                    IconButton(
                                        //more options button
                                        //modifier = Modifier.padding(0.dp),
                                        onClick = {
                                            onAlbumAction(AlbumAction.SongMoreOptionClicked(song))
                                            //SongMoreOptionClicked(song)
                                            showBottomSheet = true
                                            showSongMoreOptions = true
                                        }, // pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
                                    ) {
                                        Icon(
                                            //more options icon
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = stringResource(R.string.icon_more),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        )
                                    }
                                }
                            }
                        }
                    }*/
                }
                AnimatedVisibility(
                    visible = displayButton.value,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 40.dp),
                    enter = slideInVertically(
                        // Start the slide from 40 (pixels) above where the content is supposed to go, to
                        // produce a parallax effect
                        initialOffsetY = { -40 }
                    ) + expandVertically(
                        expandFrom = Alignment. Top
                    ) + scaleIn(
                        // Animate scale from 0f to 1f using the top center as the pivot point.
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    ) + fadeIn(initialAlpha = 0.3f),
                    exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f),
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(0)
                            }
                        },
                        modifier = Modifier
                            .clip(MusicShapes.small)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardDoubleArrowUp,
                            contentDescription = stringResource(R.string.icon_scroll_to_top),
                            tint = MaterialTheme.colorScheme.background,
                        )
                    }
                }
            }
            if (showBottomSheet) {
                // need selection context - sort btn
                if (showSortSheet) {
                    DetailsSortSelectionBottomModal(
                        onDismissRequest = {
                            showBottomSheet = false
                            showSortSheet = false
                        },
                        coroutineScope = coroutineScope,
                        content = "SongInfo",
                        context = "AlbumDetails",
                        //itemInfo = album,
                    )
                }

                // need selection context - more option btn
                else if (showAlbumMoreOptions) {
                    AlbumMoreOptionsBottomModal(
                        onDismissRequest = {
                            showBottomSheet = false
                            showAlbumMoreOptions = false
                        },
                        coroutineScope = coroutineScope,
                        album = album,
                        context = "AlbumDetails",
                        //artist = artist, //I don't think I need to pass this if albumInfo has artistId and artistName
                        //AlbumDetails context
                        //navigateToAlbumDetails = navigateTo
                    )
                }

                else if (showSongMoreOptions) {
                    SongMoreOptionsBottomModal(
                        onDismissRequest = {
                            showBottomSheet = false
                            showSongMoreOptions = false
                        },
                        coroutineScope = coroutineScope,
                        song = selectSong,
                        context = "AlbumDetails",
                        //onQueueSong = { action ->
                        //                    if (action is AlbumAction.QueueSong) {
                        //                        coroutineScope.launch {
                        //                            snackbarHostState.showSnackbar(snackBarText)
                        //                        }
                        //                    }
                        //                    viewModel::onAlbumAction(action)
                        //                },
                        navigateToPlayer = navigateToPlayer,
                    )
                }
            }
        }
    }
}

/**
 * Composable for Album Details Screen's Top App Bar
 */
@Composable
fun AlbumDetailsTopAppBar(
    navigateBack: () -> Unit,
    //should include album more options btn action here,
    //pretty sure that button also needs a context driven options set
    //modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        //back button
        IconButton(onClick = navigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.icon_back_nav),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        // search btn //TODO: does this make more sense as a more options btn?
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.icon_search),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        //more options btn //TODO: temporary placement till figure out if this should be part of header
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,

            )
        }
    }
}

/**
 * Composable for Album Details Screen's Content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailsContent(
    album: AlbumInfo,
    artist: ArtistInfo,
    songs: List<SongInfo>,
    //onQueueSong: (SongInfo) -> Unit,
    coroutineScope: CoroutineScope,
    navigateToPlayer: (SongInfo) -> Unit,
    modifier: Modifier = Modifier
) { //determines content of details screen
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var showDetailSheet by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(362.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        /**
         * Version 2: revision of jetcaster.
         * Has album image on left;
         * album title, album artist, song count on right side.
         * Has song count, song sort btn, multi-select on separate row below.
         * Has shuffle and play btns on separate row below that.
         */
        // as of 4/3/2025, commented out while working with LargeTopAppBar
        /*fullWidthItem {
            AlbumDetailsHeader(
                album = album,
                modifier = Modifier.fillMaxWidth()
            )
        }*/

        /**
         * Version 3: based on music player / spotify player type.
         * Has album image centered at top;
         * Has album title, album artist, below image.
         * Has song count, song sort btn, multi-select on separate row below.
         */
        // as of 4/3/2025, commented out while working with LargeTopAppBar
        /*fullWidthItem {
            AlbumDetailsHeaderLargeAlbumCover(
                album = album,
                //modifier = Modifier.fillMaxWidth()
            )
        }*/

        fullWidthItem {
            SongCountAndSortSelectButtons(
                songs = songs,
                onSelectClick = {},
                onSortClick = {
                    showBottomSheet = true
                    showSortSheet = true
                }
            )
        }

        fullWidthItem {
            PlayShuffleButtons(
                onPlayClick = {},
                onShuffleClick = {},
            )
        }

        //section 2: songs list
        items(songs) { song -> // for each song in list:
            Box(Modifier.padding(horizontal = 4.dp, vertical = 0.dp)) {
                SongListItem(
                    song = song,
                    onClick = navigateToPlayer,
                    onMoreOptionsClick = {},
                    //onQueueSong = {},
                    modifier = Modifier.fillMaxWidth(), //TODO: uncomment when this is changed to be thru PlayerSong
                    isListEditable = false,
                    showAlbumImage = true,
                    showArtistName = true,
                    showAlbumTitle = false,
                    showTrackNumber = true,
                )
            }
        }

        fullWidthItem {
            if (showBottomSheet) {
                // need selection context - sort btn
                if (showSortSheet) {
                    DetailsSortSelectionBottomModal(
                        onDismissRequest = {
                            showBottomSheet = false
                            showSortSheet = false
                        },
                        coroutineScope = coroutineScope,
                        content = "PlayerSong",
                        context = "AlbumDetails",
                        //itemInfo = album,
                    )
                }

                // need selection context - more option btn
                if (showDetailSheet) {
                    AlbumMoreOptionsBottomModal(
                        onDismissRequest = { showBottomSheet = false },
                        coroutineScope = coroutineScope,
                        album = album,
                        context = "AlbumDetails",
                        //artist = artist,
                        //AlbumDetails context
                        //navigateToAlbumDetails = navigateTo
                    )
                }
            }
        }
    }
}

/**
 * Version 2: Header structure that has album image on left half, has album title, album artist name, song count on right side.
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
        val imageSize = min( maxImageSize, 148.dp )
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                /*AlbumImage(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(MaterialTheme.shapes.large),
                    albumImage = 1,
                    contentDescription = album.title
                )*/
                Image(
                    painter = painterResource(R.drawable.bpicon),
                    contentDescription = album.title,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(MaterialTheme.shapes.large)
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = album.title,
                        //color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = album.albumArtistName ?: "",
                        //color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

/**
 * Version 3: Header using large album image as first item on screen, centered horizontally.
 */
@Composable
fun AlbumDetailsHeaderLargeAlbumCover(
    album: AlbumInfo,
    modifier: Modifier = Modifier
) {
    /*
    what would it take to make the larger album image details screen?
    scaffold:
        header would be the album image scaled to the full width?
        details would be split into content header and content song list
            content header contains the album name, album artist, # songs, artist image(?), more options btn
            content list contains the song list items
                need that to share the context of it being from album details
                so it should show the song.albumTrackNumber, showArtistName = false, showDuration, showListEdit = false, showAlbum = false
     */
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()//.padding(16.dp)
    ) {
        val maxImageSize = this.maxWidth * 0.6f
        val imageSize = max( maxImageSize, 148.dp )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.bpicon),
                //contentScale = ContentScale.FillBounds, //transform content to size of container
                //contentScale = ContentScale.Crop, //crop in content to fill based on smaller dimension
                //contentScale = ContentScale.None, //keeps image as exact size within container
                //contentScale = ContentScale.Fit, //transform content to fit based on larger dimension
                //contentScale = ContentScale.Inside, //not sure what this is, result is same as fit
                //contentScale = ContentScale.FillWidth, //transform content to fit based on width
                //contentScale = ContentScale.FillHeight, //transform content to fit based on height
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
                //color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}


/**
 * Version 4: Album Details Top App Bar content using BoxWithConstraints
 */
/*@Composable
fun AlbumDetailsHeaderBox(
    album: AlbumInfo,
    modifier: Modifier = Modifier
) {
    val minBoxHeight = 64.dp //height of collapsed top bar
    /*
        // Goal: describe header when TopAppBar is in expanded state and when in collapsed state
            -in expanded state: show full header with album image, album title, album artist name
            -in collapsed state: show simplified header with album title as page title

            -want to use box with constraints to adjust layout and contents upon the change in constraint
     */
    BoxWithConstraints(
        contentAlignment = Alignment.TopStart,
        propagateMinConstraints = true,
        modifier = modifier.padding(16.dp)
    ) {
        //val boxWidth = maxWidth
        val boxHeight = maxHeight
        val smallHeight = 64.dp
        val maxImageSize = this.maxWidth * 0.5f
        val imageSize = min( maxImageSize, 148.dp )
        if (this.maxWidth == boxHeight) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    /*AlbumImage(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(MaterialTheme.shapes.large),
                    albumImage = 1,
                    contentDescription = album.title
                )*/
                    Image(
                        painter = painterResource(R.drawable.bpicon),
                        contentDescription = album.title,
                        modifier = Modifier
                            .size(imageSize)
                            .clip(MaterialTheme.shapes.large)
                    )
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = album.title,
                            //color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.basicMarquee()
                        )
                        Text(
                            text = album.albumArtistName ?: "",
                            //color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Visible,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        } else {
            Text(
                text = album.title,
                //color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}*/


/**
 * Content section 1.3: song count and list sort icons
 */
@Composable
private fun SongCountAndSortSelectButtons(
    songs: List<SongInfo>,
    onSortClick: () -> Unit,
    onSelectClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = """\s[a-z]""".toRegex()
                .replace(quantityStringResource(R.plurals.songs, songs.size, songs.size)) {
                    it.value.uppercase()
                },
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp).weight(1f, true)
        )
        //Spacer(Modifier.weight(1f,true))

        // sort icon
        IconButton(
            onClick = onSortClick,
            modifier = Modifier.semantics(mergeDescendants = true) { }
        ) { // showBottomSheet = true
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,//want this to be sort icon
                contentDescription = stringResource(R.string.icon_sort),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        // multi-select icon
        IconButton(
            onClick = onSelectClick,
            modifier = Modifier.semantics(mergeDescendants = true) { }
        ) {
            Icon(
                imageVector = Icons.Filled.Checklist,//want this to be multi select icon
                contentDescription = stringResource(R.string.icon_multi_select),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/**
 * Content section 1.5: play and shuffle buttons
 */
@Composable
private fun PlayShuffleButtons(
    onPlayClick: () -> Unit,
    onShuffleClick: () -> Unit,
    //modifier: Modifier = Modifier,
) {
    Row(Modifier.padding(bottom = 8.dp)) {
        // play btn
        Button(
            onClick = onPlayClick, //what is the thing that would jump start this step process. would it go thru the viewModel??
            //step 1: regardless of shuffle being on or off, set shuffle to off
            //step 2: prepare the mediaPlayer with the new queue of items in order from playlist
            //step 3: set the player to play the first item in queue
            //step 4: navigateToPlayer(first item)
            //step 5: start playing
            /*coroutineScope.launch {
                sheetState.hide()
                showThemeSheet = false
            }*/
            //did have colors set, colors = buttonColors( container -> primary, content -> background ) // coroutineScope.launch { sheetState.hide() showThemeSheet = false },
            shape = MusicShapes.small,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(0.5f)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.icon_play)
            )
            Text("PLAY")
        }

        // shuffle btn
        Button(
            onClick = onShuffleClick, //what is the thing that would jump start this step process
            //step 1: regardless of shuffle being on or off, set shuffle to on
            //step 2?: confirm the shuffle type
            //step 3: prepare the mediaPlayer with the new queue of items shuffled from playlist
            //step 4: set the player to play the first item in queue
            //step 5: navigateToPlayer(first item)
            //step 6: start playing
            //needs to take the songs in the playlist, shuffle the
            /*coroutineScope.launch {
                sheetState.hide()
                showThemeSheet = false
            }*/
            //did have colors set, colors = buttonColors( container -> primary, content -> background )
            shape = MusicShapes.small,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(0.5f)
        ) {
            Icon(
                imageVector = Icons.Filled.Shuffle,
                contentDescription = stringResource(R.string.icon_shuffle)
            )
            Text("SHUFFLE")
        }
    }
}

@Composable
fun AlbumDetailsSongListItem(
    song: SongInfo,
    onClick: (SongInfo) -> Unit,
    onMoreOptionsClick: () -> Unit,
    //onMoreOptionsClick: (Any) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(4.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            //color = Color.Transparent,
            //color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { onClick(song) },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                AlbumImage(
                    albumImage = 1,
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
                    //modifier = Modifier.padding(0.dp),
                    onClick = onMoreOptionsClick, //pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
                ) {
                    Icon( //more options icon
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.icon_more),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

//@CompLightPreview
//@CompDarkPreview
/*@Composable
fun AlbumDetailsHeaderItemPreview() {
    MusicTheme {
        AlbumDetailsHeader(
            album = PreviewAlbums[6],
            artist = getArtistData(PreviewAlbums[6].albumArtistId!!)
        )
    }
}*/

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
//            album = PreviewAlbums[0],
//            artist = PreviewArtists[0],
//            songs = PreviewSongs,

            //Slow Rain
            album = PreviewAlbums[2],
            artist = getArtistData(PreviewAlbums[2].albumArtistId!!),
            songs = getSongsInAlbum(PreviewAlbums[2].id),
            selectSong = SongInfo(),
            onAlbumAction = {},

            //Kingdom Hearts Piano Collection
//            album = PreviewAlbums[6],
//            artist = getArtistData(PreviewAlbums[6].albumArtistId!!),
//            songs = getSongsInAlbum(307),

            navigateToPlayer = {},
            navigateToSearch = {},
            navigateBack = {},
            showBackButton = true,
        )
    }
}


// AlbumDetailsTopAppBar on emulator: height 48dp, width 395dp, x 8dp, y 51dp
//      icon btn height&width 40dp
//      icon height&width 24dp
// SongListItem on emulator: box height 80dp, width 378dp, x 16dp
//      surface height 72dp, width 370dp, x 20dp
//      row height 56dp, width 345dp
//      image height&width 56dp, x 65dp