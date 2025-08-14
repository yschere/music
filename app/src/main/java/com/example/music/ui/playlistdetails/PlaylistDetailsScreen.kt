package com.example.music.ui.playlistdetails

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.theme.Keyline1
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.getPlaylistSongs
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource

private const val TAG = "Playlist Details Screen"

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
 * Stateful version of Playlist Details Screen
 */
@Composable
fun PlaylistDetailsScreen(
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToSearch: () -> Unit,
    navigateBack: () -> Unit,
    //modifier: Modifier = Modifier,
    viewModel: PlaylistDetailsViewModel = hiltViewModel(),
) {
    //Log.i(TAG, "Initial Screen Call - Start")
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        Text(text = uiState.errorMessage!!)
        PlaylistDetailsError(onRetry = viewModel::refresh)
    }
    Surface {
        if (uiState.isReady) {
            PlaylistDetailsScreen(
                playlist = uiState.playlist,
                songs = uiState.songs,
                onQueueSong = viewModel::onQueueSong,
                navigateToPlayer = navigateToPlayer,
                navigateToSearch = navigateToSearch,
                navigateBack = navigateBack,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            PlaylistDetailsLoadingScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    /* -------ORIGINAL VERSION ---------
        //this version was based on the viewModel using sealed instance and @AssistedInject
        val state by viewModel.state.collectAsStateWithLifecycle()
        when (val s = state) {
            is PlaylistUiState.Loading -> {
                PlaylistDetailsLoadingScreen(
                    modifier = Modifier.fillMaxSize()
                )
            } // screen to show when ui state is in loading
            is PlaylistUiState.Ready -> {
                PlaylistDetailsScreen(
                    playlist = s.playlist,
                    songs = s.songs,
                    onQueueSong = viewModel::onQueueSong,
                    navigateToPlayer = navigateToPlayer,
                    navigateBack = navigateBack,
                    showBackButton = showBackButton,
                    modifier = modifier,
                )
            } // screen to show when ui state is ready to display
        }
    */
}

/**
 * Error Screen
 */
@Composable
private fun PlaylistDetailsError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
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
private fun PlaylistDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }
//full screen circular progress - loading screen

/**
 * Stateless Composable for Playlist Details Screen
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlaylistDetailsScreen(
    playlist: PlaylistInfo,
    songs: List<SongInfo>,
    onQueueSong: (SongInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToSearch: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) { //base level screen data / coroutine setter / screen component(s) caller
    //Log.i(TAG, "Draw Screen Start")

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)

    ScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        //base layer structure component
        Scaffold(
            contentWindowInsets = WindowInsets.systemBarsIgnoringVisibility,
                //can't quite remember what this one was for ...
                // was this meant to keep all the contents within the window insets?
            topBar = {
                PlaylistDetailsTopAppBar(
                    navigateToSearch = navigateToSearch,
                    navigateBack = navigateBack, //since using topAppBar here, separating navigateBack from the other navigate functions here
                )
            },
            bottomBar = {
                /* //should show BottomBarPlayer here if a queue session is running or service is running
                BottomBarPlayer(
                    song = PreviewSongs[5],
                    navigateToPlayer = { navigateToPlayer(PreviewSongs[5]) },
                )*/
            },
            snackbarHost = { // setting the snackbar hoststate to the scaffold
                SnackbarHost(hostState = snackbarHostState)
            },
            //modifier = modifier.fillMaxSize().systemBarsPadding(),
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background) //selects the appropriate color to be the content color for the container using background color
            //contentColor = MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
        ) { contentPadding -> //not sure why content padding into content function done in this way
            //logger.info { "Playlist Details Screen - Content function call" }
            PlaylistDetailsContent(
                playlist = playlist,
                songs = songs,
                /*onQueueSong = {
                    coroutineScope.launch { //use the onQueueSong btn onClick to trigger snackbar
                        snackbarHostState.showSnackbar(snackBarText)
                    }
                    onQueueSong(it)
                },*/
                navigateToPlayer = navigateToPlayer,
                modifier = Modifier.padding(contentPadding)//.padding(horizontal = 8.dp)
            )
        }
    }
    //logger.info{ "Playlist Details Screen function end" }
}

/**
 * Composable for Playlist Details Screen's Top App Bar.
 */
@Composable
private fun PlaylistDetailsTopAppBar(
    navigateToSearch: () -> Unit,
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
        IconButton( onClick = navigateBack ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.icon_back_nav),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        // search btn
        IconButton( onClick = navigateToSearch ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.icon_search),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        // more options btn // temporary placement till figure out if this should be part of header
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
 * Composable for Playlist Details Screen's Content.
 */
@Composable
private fun PlaylistDetailsContent(
    playlist: PlaylistInfo,
    songs: List<SongInfo>,
    //onQueueSong: (SongInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    modifier: Modifier = Modifier
) { //determines content of details screen
    //logger.info { "Playlist Details Content function start" }
    LazyVerticalGrid( //uses lazy vertical grid to store header and items list below it
        columns = GridCells.Adaptive(362.dp),
        modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        //logger.info { "Playlist Details Content - lazy vertical grid start" }
        // section 1: header item
        fullWidthItem {
            PlaylistDetailsHeader(
                playlist = playlist,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // section 2: songs list
        fullWidthItem {
            if (songs.isEmpty()) {
                PlaylistDetailsEmptyList(onClick = {})
            } else {
                Column {
                    SongCountAndAddSortSelectButtons( songs = songs, onAddClick = {}, onSelectClick = {}, onSortClick = {} )
                    PlayShuffleButtons(
                        onPlayClick = { /* probably send call to controller, or is it songPlayer? since that's in viewModel */ },
                        onShuffleClick = { /* probably send call to controller, or is it songPlayer? since that's in viewModel */ },
                    )
                }
            }
        }

        /**
         * ORIGINAL VERSION: using songs: List<SongInfo>
         */
        //items(songs, key = { it.id }) { song -> // for each song in list:
        items(songs) { song ->
            //Note: Because playlists are capable of having multiple copies of
            // the same song, its likely necessary going forward to change the
            // referencing of each song thru its playlist entry
            // for now, not using unique id, just outputting the list
            Box(Modifier.padding(horizontal = 4.dp, vertical = 0.dp)) {
                SongListItem(
                    //call the SongListItem function to display each one, include the data needed to display item in full,
                    //and should likely share context from where this call being made in case specific data needs to be shown / not shown
                    song = song,
                    onClick = navigateToPlayer,
                    onMoreOptionsClick = {},
                    //onQueueSong = onQueueSong,
                    modifier = Modifier.fillMaxWidth(),
                    isListEditable = false,
                    showArtistName = true,
                    showAlbumImage = true,
                    showAlbumTitle = true,
                    showTrackNumber = false,
                )
            }
        }
        //logger.info { "Playlist Details Content - lazy vertical grid end" }
    }
    //logger.info { "Playlist Details Content function end" }
}

/**
 * Default header based on jetcaster
 */
/*@Composable
private fun PlaylistDetailsHeaderItem(
    playlist: PlaylistInfo,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.padding(Keyline1)
    ) {
        val maxImageSize = this.maxWidth / 2
        val imageSize = min(maxImageSize, 148.dp)
        Column {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()//.background(MaterialTheme.colorScheme.secondary)
            ) {
                AlbumImage(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(MaterialTheme.shapes.large),
                        //.background(MaterialTheme.colorScheme.onPrimary),
                    //albumImage = album.artwork!!,//album.imageUrl or album.artwork when that is fixed
                    albumImage = 1,
                    contentDescription = playlist.name
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = playlist.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    PlaylistDetailsHeaderItemButtons(
                        onShuffleClick = {},
                        onPlayClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistDetailsHeaderItemButtons(
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier.padding(top = 16.dp)) {
        Button( // shuffle btn
            onClick = onShuffleClick,
            modifier = Modifier.semantics(mergeDescendants = true) { }
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = stringResource(R.string.icon_shuffle)
            )
        }

        Button( //play btn
            onClick = onPlayClick,
            modifier = Modifier.semantics(mergeDescendants = true) { }
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = stringResource(R.string.icon_play)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // more options btn
        IconButton(
            onClick = {  },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}*/

/**
 * Header with image on left, playlist name on right
 */
@Composable
private fun PlaylistDetailsHeader(
    playlist: PlaylistInfo,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.padding(Keyline1)
    ) {
        val maxImageSize = this.maxWidth / 2
        val imageSize = min( maxImageSize, 148.dp )
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,// or Bottom
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.bpicon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(MaterialTheme.shapes.large)
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = playlist.name,
                        maxLines = 2,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.basicMarquee()
                    )
                    //could put add songs btn and more options btn here
                }
            }
        }
    }
}

/**
 * Content section 1.3: song count and list sort icons
 */
@Composable
private fun SongCountAndAddSortSelectButtons(
    songs: List<SongInfo>,
    onAddClick: () -> Unit,
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
            modifier = Modifier
                .padding(8.dp)
                .weight(1f, true)
        )
        //Spacer(Modifier.weight(1f,true))

        // add icon
        IconButton(onClick = onAddClick) { // navigate to multiselect across all songs
            Icon(
                imageVector = Icons.Filled.Add,//want this to be sort icon // Icons.Default.Add,
                contentDescription = stringResource(R.string.icon_add_to_playlist),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        // sort icon
        IconButton(onClick = onSortClick) { // showBottomSheet = true
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,//want this to be sort icon
                contentDescription = stringResource(R.string.icon_sort),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        // multi-select icon
        IconButton(onClick = onSelectClick) { // navigate to multiselect across playlist songs
            Icon(
                imageVector = Icons.Filled.Checklist,//want this to be multi select icon
                contentDescription = stringResource(R.string.icon_multi_select),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/**
 * Content section 1.5: shuffle and play buttons
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
    ) {
        Text(
            text = "Add Songs to Playlist"
        )
        Button( //add btn
            onClick = onClick, // multi-select screen to pick songs to add to playlist
            modifier = Modifier.semantics(mergeDescendants = true) { }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.icon_add_to_playlist)
            )
        }
    }
}

//@CompLightPreview
//@CompDarkPreview
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

            onQueueSong = {},
            navigateToPlayer = {},
            navigateToSearch = {},
            navigateBack = {},
        )
    }
}
