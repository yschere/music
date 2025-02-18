package com.example.music.ui.albumdetails

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
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.getArtistData
import com.example.music.domain.testing.getSongsInAlbum
import com.example.music.model.AlbumInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.player.model.toPlayerSong
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import com.example.music.util.radialGradientScrim

/**
 * Stateful version of Album Details Screen
 */
@Composable
fun AlbumDetailsScreen(
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    if (uiState.errorMessage != null) {
        AlbumDetailsError(onRetry = viewModel::refresh)
    }

    if (uiState.isReady) {
        AlbumDetailsScreen(
            album = uiState.album,
            songs = uiState.songs,
            pSongs = uiState.pSongs,
            navigateToPlayer = navigateToPlayer,
            navigateToPlayerSong = navigateToPlayerSong,
            navigateBack = navigateBack,
            showBackButton = showBackButton,
            modifier = modifier,
        )
    } else {
        AlbumDetailsLoadingScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Error Screen
 */
@Composable
private fun AlbumDetailsError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
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
@Composable
fun AlbumDetailsScreen(
    album: AlbumInfo,
    songs: List<SongInfo>,
    pSongs: List<PlayerSong>,
    //onQueueSong: (PlayerSong) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    navigateBack: () -> Unit,
    showBackButton: Boolean,
    modifier: Modifier = Modifier
) { //base level screen data / coroutine setter / screen component(s) caller

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.song_added_to_your_queue) //used to hold the little popup text that appears after an onClick event

    AlbumDetailsScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        //base layer structure component
        Scaffold(
            topBar = {
                AlbumDetailsTopAppBar(
                    navigateBack = navigateBack,
                    //modifier = Modifier.fillMaxWidth()
                )
            },
            snackbarHost = { // setting the snackbar hoststate to the scaffold
                SnackbarHost(hostState = snackbarHostState)
            },
            modifier = modifier.fillMaxSize().systemBarsPadding(), //says to use max size of screen?
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) { contentPadding -> //not sure why content padding into content function done in this way
            AlbumDetailsContent(
                album = album,
                songs = songs,
                pSongs = pSongs,
                navigateToPlayer = navigateToPlayer,
                navigateToPlayerSong = navigateToPlayerSong,
                modifier = Modifier.padding(contentPadding)//.padding(horizontal = 8.dp)
            )
        }
    }
}

/**
 * Composable for Album Details Screen's Background.
 */
@Composable
private fun AlbumDetailsScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .radialGradientScrim(MaterialTheme.colorScheme.primary)//.copy(alpha = 0.9f))
        )
        content()
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
    modifier: Modifier = Modifier
) {
    Row(
        Modifier
            .fillMaxWidth()
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
 * Composable for Album Details Screen's Content.
 */
@Composable
fun AlbumDetailsContent(
    album: AlbumInfo,
    songs: List<SongInfo>,
    pSongs: List<PlayerSong>,
    //onQueueSong: (PlayerSong) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    modifier: Modifier = Modifier
) { //determines content of details screen
    LazyVerticalGrid( //uses lazy vertical grid to store header and items list below it
        columns = GridCells.Adaptive(362.dp),
        modifier.fillMaxSize().padding(horizontal = 12.dp)
    ) {
        fullWidthItem {
            //section 1: header item
            // --- version 1 ---
//            AlbumDetailsHeaderItem( //header item uses album data to show album info
//                album = album,
//                modifier = Modifier.fillMaxWidth()
//            )

            // --- version 2 ---
            AlbumDetailsHeader(
                album = album,
                modifier = Modifier.fillMaxWidth()
            )
        }

        //section 2: songs list
        items(pSongs) { song -> // for each song in list:
            Box(Modifier.padding(horizontal = 4.dp, vertical = 0.dp)) {
                SongListItem(
                    song = song,
                    onClick = navigateToPlayerSong,
                    //onQueueSong = {},
                    modifier = Modifier.fillMaxWidth(), //TODO: uncomment when this is changed to be thru PlayerSong
                    isListEditable = false,
                    showAlbumImage = true,
                    showArtistName = true,
                    showAlbumTitle = false,
                )
            }
        }
    }
}

@Composable
fun AlbumDetailsHeaderItem(
    album: AlbumInfo,
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
                modifier = Modifier.fillMaxWidth()
            ) {
                AlbumImage(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(MaterialTheme.shapes.large),
                        //.background(MaterialTheme.colorScheme.onPrimary),
                    //albumImage = album.artwork!!,//album.imageUrl or album.artwork when that is fixed
                    albumImage = 1,
                    contentDescription = album.title
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = album.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    AlbumDetailsHeaderItemButtons(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumDetailsHeader(
    album: AlbumInfo,
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
                        text = album.title,
                        //color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = getArtistData(album.albumArtistId!!).name,
                        //color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = """\s[a-z]""".toRegex().replace(quantityStringResource(R.plurals.songs, album.songCount, album.songCount)) {
                            it.value.uppercase()
                        },
                        //text = quantityStringResource(R.plurals.songs, album.songCount, album.songCount),
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

@Composable
fun AlbumDetailsHeaderItemButtons(
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
fun AlbumDetailsHeaderLargeAlbumCover(
    album: AlbumInfo,
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
                contentDescription = album.title
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
                        text = album.title
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



//@Preview (name = "light mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
//@Preview (name = "dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumDetailsHeaderItemPreview() {
    MusicTheme {
        AlbumDetailsHeaderItem(
            album = PreviewAlbums[6],
        )
    }
}

//@Preview
//@Composable
//fun HeaderAlbumCoverPreview() {
//    MusicTheme {
//        AlbumDetailsHeaderLargeAlbumCover(
//            album = PreviewAlbums[0]
//        )
//    }
//}

@Preview
//@Preview (name = "light mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
//@Preview (name = "dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumDetailsScreenPreview() {
    MusicTheme {
        AlbumDetailsScreen(
//            album = PreviewAlbums[0],
//            songs = PreviewSongs,

            //Slow Rain
            album = PreviewAlbums[2],
            songs = getSongsInAlbum(281),
            pSongs = getSongsInAlbum(281).map { it.toPlayerSong() },

            //Kingdom Hearts Piano Collection
//            album = PreviewAlbums[6],
//            songs = getSongsInAlbum(307),

            navigateToPlayer = { },
            navigateToPlayerSong = { },
            navigateBack = { },
            showBackButton = true
        )
    }
}

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