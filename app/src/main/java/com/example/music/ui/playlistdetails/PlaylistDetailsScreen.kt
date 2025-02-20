package com.example.music.ui.playlistdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.Keyline1
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.getPlaylistPlayerSongs
import com.example.music.domain.testing.getPlaylistSongs
import com.example.music.model.PlaylistInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource

/**
 * Stateful version of Playlist Details Screen
 */
@Composable
fun PlaylistDetailsScreen(
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaylistDetailsViewModel = hiltViewModel(),
) {
    //logger.info { "Playlist Details Screen - hilt view model function start" }
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        PlaylistDetailsError(onRetry = viewModel::refresh)
    }
    if (uiState.isReady) {
        PlaylistDetailsScreen(
            playlist = uiState.playlist,
            songs = uiState.songs,
            pSongs = uiState.pSongs, //TODO: PlayerSong support
            onQueueSong = viewModel::onQueueSong,
            navigateToPlayer = navigateToPlayer,
            navigateToPlayerSong = navigateToPlayerSong, //TODO: PlayerSong support
            navigateBack = navigateBack,
            modifier = modifier,
        )
    } else {
        PlaylistDetailsLoadingScreen(
            modifier = Modifier.fillMaxSize()
        )
    }

    /* -------ORIGINAL VERSION ---------
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
                pSongs = s.pSongs, //TODO: PlayerSong support
                onQueueSong = viewModel::onQueueSong,
                navigateToPlayer = navigateToPlayer,
                navigateToPlayerSong = navigateToPlayerSong, //TODO: PlayerSong support
                navigateBack = navigateBack,
                showBackButton = showBackButton,
                modifier = modifier,
            )
        } // screen to show when ui state is ready to display
    }*/
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

@Composable
private fun PlaylistDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }
//full screen circular progress - loading screen

/**
 * Stateless version of Playlist Details Screen
 */
@Composable
fun PlaylistDetailsScreen(
    playlist: PlaylistInfo,
    songs: List<SongInfo>,
    pSongs: List<PlayerSong>, //TODO: PlayerSong support
    onQueueSong: (PlayerSong) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit, //TODO: PlayerSong support
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) { //base level screen data / coroutine setter / screen component(s) caller
    //logger.info{ "Playlist Details Screen function start" }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.song_added_to_your_queue) //used to hold the little popup text that appears after an onClick event

    ScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        //base layer structure component
        Scaffold(
            topBar = {
                PlaylistDetailsTopAppBar(
                    navigateBack = navigateBack, //since using topAppBar here, separating navigateBack from the other navigate functions here
                )
            },
            snackbarHost = { // setting the snackbar hoststate to the scaffold
                SnackbarHost(hostState = snackbarHostState)
            },
            modifier = modifier.fillMaxSize().systemBarsPadding(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) { contentPadding -> //not sure why content padding into content function done in this way
            //logger.info { "Playlist Details Screen - Content function call" }
            PlaylistDetailsContent(
                playlist = playlist,
                songs = songs,
                pSongs = pSongs,//.toPersistentList(), //TODO: PlayerSong support
                /*onQueueSong = {
                    coroutineScope.launch { //use the onQueueSong btn onClick to trigger snackbar
                        snackbarHostState.showSnackbar(snackBarText)
                    }
                    onQueueSong(it)
                },*/
                //navigateToPlayer = navigateToPlayer,
                navigateToPlayerSong = navigateToPlayerSong, //TODO: PlayerSong support
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
fun PlaylistDetailsTopAppBar(
    navigateBack: () -> Unit,
    //should include album more options btn action here,
    //pretty sure that button also needs a context driven options set
    //modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        //back button
        IconButton(onClick = navigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.cd_back)
            )
        }

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        // search btn
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.cd_more)
            )
        }
    }
}

/**
 * Composable for Playlist Details Screen's Content.
 */
@Composable
fun PlaylistDetailsContent(
    playlist: PlaylistInfo,
    songs: List<SongInfo>,
    pSongs: List<PlayerSong>, //TODO: PlayerSong support
    //onQueueSong: (PlayerSong) -> Unit,
    //navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit, //TODO: PlayerSong support
    modifier: Modifier = Modifier
) { //determines content of details screen
    //logger.info { "Playlist Details Content function start" }
    LazyVerticalGrid( //uses lazy vertical grid to store header and items list below it
        columns = GridCells.Adaptive(362.dp),
        modifier.fillMaxSize().padding(horizontal = 12.dp)
    ) {
        //logger.info { "Playlist Details Content - lazy vertical grid start" }
        fullWidthItem {
            //section 1: header item
            PlaylistDetailsHeader(
                playlist = playlist,
                modifier = Modifier.fillMaxWidth()
            )
        }

        //section 2: songs list
        /**
         * ORIGINAL VERSION: using songs: List<SongInfo>
         */
        //items(songs, key = { it.id }) { song -> // for each song in list:
        /* items(songs) { song ->
            //TODO: because playlists are capable of having multiple copies of
            // the same song, its likely necessary going forward to change the
            // referencing of each song thru its playlist entry
            // for now, not using unique id, just outputting the list
            song.albumId?.let { getAlbumData(it) }?.let {
                SongListItem( //call the SongListItem function to display each one, include the data needed to display item in full,
                    //and should likely share context from where this call being made incase specific data needs to be shown / not shown
                    song = song,
                    //artist = artist,
                    album = it,
                    onClick = navigateToPlayer,
                    onQueueSong = onQueueSong,
                    modifier = Modifier.fillMaxWidth(),
                    isListEditable = false,
                    showArtistName = true,
                    showAlbumImage = true,
                    showAlbumTitle = true,
                )
            }
        } */

        /**
         * PLAYERSONG VERSION: using pSongs: List<PlayerSong>
         */
        items(pSongs) { song ->
            //logger.info { "Playlist Details Content - songs layout for song ${song.id}" }
            Box(Modifier.padding(horizontal = 4.dp, vertical = 0.dp)) {
                SongListItem(
                    song = song,
                    onClick = navigateToPlayerSong, //TODO: FOUND, spot where conversion to PlayerSong would need to be extensive
                    //onQueueSong = onQueueSong,
                    modifier = Modifier.fillMaxWidth(),
                    isListEditable = false,
                    showArtistName = true,
                    showAlbumImage = true,
                    showAlbumTitle = true,
                )
            }
        }
        //logger.info { "Playlist Details Content - lazy vertical grid end" }
    }
    //logger.info { "Playlist Details Content function end" }
}

@Composable
fun PlaylistDetailsHeaderItem(
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
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistDetailsHeader(
    playlist: PlaylistInfo,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        val maxImageSize = this.maxWidth / 2
        val imageSize = min( maxImageSize, 148.dp )
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
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
                        //color = MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = """\s[a-z]""".toRegex().replace(quantityStringResource(R.plurals.songs, playlist.songCount, playlist.songCount)) {
                            it.value.uppercase()
                        },
                        //text = quantityStringResource(R.plurals.songs, playlist.songCount, playlist.songCount),
                        //color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistDetailsHeaderItemButtons(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier.padding(top = 16.dp)) {
        Button( // shuffle btn
            onClick = onClick,
            modifier = Modifier.semantics(mergeDescendants = true) { }
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = null
            )
        }

        Button( //play btn
            onClick = onClick,
            modifier = Modifier.semantics(mergeDescendants = true) { }
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                //tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(R.string.cd_more)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlaylistDetailsHeaderLargeAlbumCover(
    playlist: PlaylistInfo,
    modifier: Modifier = Modifier,
) { //used to show album image as screen header
    Box(modifier = modifier.padding(Keyline1)) {
        Row(modifier.fillMaxWidth()) {
            AlbumImage(
                modifier = Modifier
                    //.size(this.maxWidth / 2)
                    .clip(MaterialTheme.shapes.large).background(MaterialTheme.colorScheme.onPrimary),
                //albumImage = album.artwork!!,//album.imageUrl or album.artwork when that is fixed
                albumImage = 1,
                contentDescription = playlist.name
            )
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets.systemBarsIgnoringVisibility,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.1f), // container's background color
                    titleContentColor = MaterialTheme.colorScheme.primary // title words color
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {},
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "backNavIcon")
                    }
                },
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = playlist.name
                    )
                }
            )
        },
        bottomBar = {},
//        contentColor = MaterialTheme.colorScheme.primary,
//        containerColor = MaterialTheme.colorScheme.background,

    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.bpicon),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

//@Preview (name = "light mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
//@Preview (name = "dark mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlaylistDetailsHeaderItemPreview() {
    MusicTheme {
        PlaylistDetailsHeaderItem(
            playlist = PreviewPlaylists[1],
        )
    }
}

@Preview
//@Preview (name = "light mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
//@Preview (name = "dark mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlaylistDetailsScreenPreview() {
    MusicTheme {
        PlaylistDetailsScreen(
            //hello
//            playlist = PreviewPlaylists[0],
//            songs = getPlaylistSongs(0),
//            pSongs= PreviewPlayerSongs, //TODO: PlayerSong support

            //ack
//            playlist = PreviewPlaylists[1],
//            songs = getPlaylistSongs(1),

            //give the goods
//            playlist = PreviewPlaylists[2],
//            songs = getPlaylistSongs(2),

            //TODO: PlayerSong support
            playlist = PreviewPlaylists[2],
            songs = getPlaylistSongs(2),
            pSongs = getPlaylistPlayerSongs(2),

            onQueueSong = { },
            navigateToPlayer = { },
            navigateToPlayerSong = { }, //TODO: PlayerSong support
            navigateBack = { },
        )
    }
}
