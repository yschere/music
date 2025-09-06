package com.example.music.ui.artistdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.Keyline1
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.getAlbumsByArtist
import com.example.music.domain.testing.getSongsByArtist
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo


import com.example.music.ui.shared.AlbumMoreOptionsBottomModal
import com.example.music.ui.shared.ArtistMoreOptionsBottomModal
import com.example.music.ui.shared.DetailsSortSelectionBottomModal
import com.example.music.ui.shared.FeaturedAlbumsCarousel
import com.example.music.ui.shared.FeaturedCarouselItem
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.ui.shared.formatStr
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.LandscapePreview
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope

/** Changelog:
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 4/13/2025 - Added navigateToSearch to Search Icon in TopAppBar
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

/**
 * Stateful version of Artist Details Screen
 */
@Composable
fun ArtistDetailsScreen(
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerV2: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    //modifier: Modifier = Modifier,
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
                onArtistAction = viewModel::onArtistAction,
                //onQueueSong = viewModel::onQueueSong,
                navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToPlayer = navigateToPlayer,
                navigateToPlayerV2 = navigateToPlayerV2,
                navigateToSearch = navigateToSearch,
                navigateBack = navigateBack,
                showBackButton = showBackButton,
                modifier = Modifier.fillMaxSize(),
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
    onArtistAction: (ArtistAction) -> Unit,
    //onQueueSong: (SongInfo) -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerV2: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier
) {
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

    var showBottomSheet by remember { mutableStateOf(false) } // if bottom modal needs to be opened
    var showSortSheet by remember { mutableStateOf(false) } // if bottom modal content is for sorting songs
    var showAlbumMoreOptions by remember { mutableStateOf(false) } // if bottom modal content is for album details more options
    var showArtistMoreOptions by remember { mutableStateOf(false) } // if bottom modal content is for album details more options
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
                            //AlbumDetailsHeaderLargeAlbumCover(album, modifier)
                            ArtistDetailsHeaderTitle(artist)
                        }
                    },
                    navigationIcon = {
                        //back button
                        IconButton( onClick = navigateBack ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.icon_back_nav),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    },
                    actions = {
                        // search btn
                        IconButton( onClick = navigateToSearch ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = stringResource(R.string.icon_search),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        // more options btn // temporary placement till figure out if this should be part of header
                        IconButton(
                            onClick = {
                                showBottomSheet = true
                                showArtistMoreOptions = true /* onMoreOptionsClick */
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

                //ArtistDetailsTopAppBar(
                    //navigateBack = navigateBack,
                //)
            },
            bottomBar = {
                /* //should show BottomBarPlayer here if a queue session is running or service is running
                BottomBarPlayer(
                    song = PreviewSongs[5],
                    navigateToPlayer = { navigateToPlayer(PreviewSongs[5]) },
                )*/
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            modifier = modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background) //selects the appropriate color to be the content color for the container using background color
            //contentColor = MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
        ) { contentPadding ->
            /* // VERSION 1 : call ArtistDetailsContent()
            ArtistDetailsContent(
                artist = artist,
                albums = albums,
                songs = songs,
                /*onQueueSong = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(snackBarText)
                    }
                    onQueueSong(it)
                },*/
                coroutineScope = coroutineScope,
                navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToPlayer = navigateToPlayer,
                modifier = Modifier.padding(contentPadding)
            )*/

            // VERSION 2 : place everything in details screen ()
            val albs = albums.toPersistentList()
            val pagerState = rememberPagerState { albs.size }
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = modifier.padding(contentPadding)
                    .fillMaxSize(),
                //does not have the initial .padding(horizontal = 12.dp) so that
                // Albums horizontal pager is not cut into
            ) {
                // section 1: header item
                // is within TopAppBar now

                //section 2: albums list
                if (albums.isNotEmpty()) {
                    fullWidthItem {
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
                        Column(modifier = modifier) {
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                            ) {
                                val horizontalPadding = (this.maxWidth - 160.dp) / 2
                                HorizontalPager(
                                    state = pagerState,
                                    contentPadding = PaddingValues(
                                        horizontal = horizontalPadding,
                                        vertical = 16.dp,
                                    ),
                                    pageSpacing = 24.dp,
                                    pageSize = PageSize.Fixed(160.dp)
                                ) { page ->
                                    val album = albums[page]
                                    FeaturedCarouselItem(
                                        itemImage = album.artworkUri,//album.artwork!!,
                                        itemTitle = album.title,
                                        itemSize = album.songCount,
                                        onMoreOptionsClick = {
                                            onArtistAction( ArtistAction.AlbumMoreOptionClicked(album) )
                                            //ArtistAction.AlbumMoreOptionClicked(album)
                                            showBottomSheet = true
                                            showAlbumMoreOptions = true
                                        },
                                        //onClick = AlbumMoreOptionsBottomModal(album),
                                        //dateLastPlayed = album.dateLastPlayed?.let { lastUpdated(it) },
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                navigateToAlbumDetails(album)
                                            }
                                    )
                                }
                            }
                        }
                        /*FeaturedAlbumsCarousel(
                            pagerState = pagerState,
                            items = albums,
                            navigateToAlbumDetails = navigateToAlbumDetails,
                            onMoreOptionsClick = {
                                onArtistAction( ArtistAction.AlbumMoreOptionClicked(it) )
                                ArtistAction.AlbumMoreOptionClicked(it)
                                showBottomSheet = true
                                showAlbumMoreOptions = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        )*/
                    }
                }

                //section 3: songs list
                if (songs.isNotEmpty()) {

                    // songs header
                    fullWidthItem {
                        SongCountAndSortSelectButtons(
                            songs = songs,
                            onSelectClick = {},
                            onSortClick = {
                                showBottomSheet = true
                                showSortSheet = true
                            },
                        )
                    }

                    fullWidthItem {
                        PlayShuffleButtons(
                            onPlayClick = { /* probably send call to controller, or is it songPlayer? since that's in viewModel */ },
                            onShuffleClick = { /* probably send call to controller, or is it songPlayer? since that's in viewModel */ },
                        )
                    }

                    // songs list
                    items(songs) { song ->
                        Box(Modifier.padding(horizontal = 12.dp, vertical = 0.dp)) {
                            SongListItem(
                                song = song,
                                onClick = navigateToPlayer,
                                onMoreOptionsClick = {
                                    onArtistAction( ArtistAction.SongMoreOptionClicked( song ) )
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
                    /*items(songs) { song ->
                        Box(modifier = modifier.padding(/*horizontal = 12.dp, */vertical = 0.dp)) {
                            Surface(
                                shape = MaterialTheme.shapes.large,
                                color = Color.Transparent,
                                //color = MaterialTheme.colorScheme.surfaceContainer,
                                onClick = { navigateToPlayer(song) },
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(/*horizontal = 12.dp, */vertical = 8.dp)
                                        .padding(start = 12.dp),
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
                                        onClick = {
                                            onArtistAction( ArtistAction.SongMoreOptionClicked( song ) )
                                            showBottomSheet = true
                                            showSongMoreOptions = true
                                        }, // pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
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
                    }*/
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
                        context = "ArtistDetails",
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
                        album = selectAlbum,
                        context = "ArtistDetails",
                        //navigateToArtistDetails = navigateToArtistDetails,
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
                        context = "ArtistDetails",
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

                else if (showArtistMoreOptions) {
                    ArtistMoreOptionsBottomModal(
                        onDismissRequest = {
                            showBottomSheet = false
                            showSongMoreOptions = false
                        },
                        coroutineScope = coroutineScope,
                        artist = artist,
                        context = "ArtistDetails",
                        navigateToArtistDetails = {},
                        navigateToPlayer = navigateToPlayer,
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
    modifier: Modifier = Modifier
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

        // search btn
        IconButton(onClick = {  }) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.icon_search),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        //more options btn // temporary placement till figure out if this should be part of header
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
 * Composable for Artist Details Screen's Content.
 */
@Composable
fun ArtistDetailsContent(
    artist: ArtistInfo,
    albums: PersistentList<AlbumInfo>,
    songs: List<SongInfo>,
    //onQueueSong: (SongInfo) -> Unit,
    coroutineScope: CoroutineScope,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    /*
        ------- VERSION 2: albums and songs combined in one grid -------
        Goal: Header item to contain artist name,
        Use remainder of screen for two panes, first pane is albums list, second pane is songs list
        Albums list will have one, immutable sort order. Albums pane will have # albums as 'title'.
        Songs list will have sort and selection options. Songs pane will have # songs as 'title'.
        Future consideration: include shuffle and play btns between song pane 'title' and list
     */

    val albs = albums.toPersistentList()
    val pagerState = rememberPagerState { albs.size }
    LaunchedEffect(pagerState, albs) {
        snapshotFlow { pagerState.currentPage }
            .collect {
                /*val album = albums.getOrNull(it)
                album?.let { it1 -> ArtistsDetailsAction.ArtistAlbumSelected(it1) }
                    ?.let { it2 -> onArtistsDetailsAction(it2) }*/
            }//crashes the app on screen redraw
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize(),
        //does not have the initial .padding(horizontal = 12.dp) so that
        // Albums horizontal pager is not cut into
    ) {
        //section 1: header item
        fullWidthItem {
            ArtistDetailsHeaderTitle ( artist )
            /*ArtistDetailsHeaderItem(
                artist = artist,
                modifier = Modifier
                    .fillMaxWidth()
            )*/
        }

        //section 2: albums list
        if (!albums.isEmpty()) {
            fullWidthItem {
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
                    onMoreOptionsClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        //section 3: songs list
        if (songs.isNotEmpty()) {

            // songs header
            fullWidthItem {
                SongCountAndSortSelectButtons(
                    songs = songs,
                    onSortClick = {},
                    onSelectClick = {},
                )
            }

            fullWidthItem {
                PlayShuffleButtons(
                    onPlayClick = { /* probably send call to controller, or is it songPlayer? since that's in viewModel */ },
                    onShuffleClick = { /* probably send call to controller, or is it songPlayer? since that's in viewModel */ },
                )
            }

            // songs list
            items(songs) { song ->
                Box(Modifier.padding(horizontal = 12.dp, vertical = 0.dp)) {
                    SongListItem(
                        song = song,
                        onClick = navigateToPlayer,
                        onMoreOptionsClick = {},
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
}

@Composable
fun ArtistDetailsHeaderItem(
    artist: ArtistInfo,
    modifier: Modifier = Modifier
) {
    // FUTURE THOUGHT: choose if want 1 image or multi image view for artist header
    // and for the 1 image, should it be the 1st album, or an image for externally of the artist?
    BoxWithConstraints(
        modifier = modifier.padding(Keyline1)
    ) {
        //val widthConstraint = this.maxWidth
        val maxImageSize = this.maxWidth / 2
        //val imageSize = min(maxImageSize, 148.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*AlbumImage(
                modifier = Modifier
                    //.size(widthConstraint, 200.dp)
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large),
                albumImage = R.drawable.bpicon,//album.artwork!!,//album.imageUrl or album.artwork when that is fixed
                contentDescription = "artist Image"
            )*/
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

/**
 * Content section 2.3: song count and list sort icons
 */
@Composable
private fun SongCountAndSortSelectButtons(
    songs: List<SongInfo>,
    onSortClick: () -> Unit,
    onSelectClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
    ) {
        Text(
            text = """\s[a-z]""".toRegex().replace(
                quantityStringResource(R.plurals.songs, songs.size, songs.size)
            ) {
                it.value.uppercase()
            },
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp).weight(1f, true) //try without padding?
        )

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
 * Content section 2.5: play and shuffle buttons
 */
@Composable
private fun PlayShuffleButtons(
    onPlayClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.padding(horizontal = 12.dp).padding(bottom = 8.dp)) {
        //Row(Modifier.padding(bottom = 8.dp)) { // original version for screens that don't have carousel / don't need to remove horizontal padding on lazyVerticalGrid
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
            selectSong = getSongsByArtist(113)[0],
            selectAlbum = getAlbumsByArtist(113)[0],
            onArtistAction = {},

            navigateToAlbumDetails = {},
            navigateToPlayer = {},
            navigateToPlayerV2 = {},
            navigateToSearch = {},
            navigateBack = {},
            showBackButton = true,
        )
    }
}
