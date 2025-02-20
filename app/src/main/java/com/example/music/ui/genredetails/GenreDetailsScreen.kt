package com.example.music.ui.genredetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.theme.Keyline1
import com.example.music.domain.testing.PreviewGenres
import com.example.music.domain.testing.getSongsInGenre
import com.example.music.model.GenreInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.player.model.toPlayerSong
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource

/**
 * Stateful version of Genre Details Screen
 */
@Composable
fun GenreDetailsScreen(
//    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GenreDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    if (uiState.errorMessage != null) {
        GenreDetailsError(onRetry = viewModel::refresh)
    }
    if (uiState.isReady) {
        GenreDetailsScreen(
            genre = uiState.genre,
            //albums = uiState.albums.toPersistentList(),
            songs = uiState.songs,
            pSongs = uiState.pSongs,
            //onQueueSong = viewModel::onQueueSong,
            //navigateToAlbumDetails = navigateToAlbumDetails,
            navigateToPlayer = navigateToPlayer,
            navigateToPlayerSong = navigateToPlayerSong,
            navigateBack = navigateBack,
            modifier = modifier,
        )
    } else {
        GenreDetailsLoadingScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Error Screen
 */
@Composable
private fun GenreDetailsError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
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
private fun GenreDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }
//full screen circular progress - loading screen

/**
 * Stateless version of Genre Details Screen
 */
@Composable
fun GenreDetailsScreen(
    genre: GenreInfo,
    //albums: PersistentList<AlbumInfo>,
    songs: List<SongInfo>,
    pSongs: List<PlayerSong>,
    //onQueueSong: (PlayerSong) -> Unit,
    //navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit, //TODO: PlayerSong support
    navigateBack: () -> Unit,
    //showBackButton: Boolean,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.song_added_to_your_queue)

    ScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            topBar = {
                GenreDetailsTopAppBar(
                    navigateBack = navigateBack,
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            modifier = modifier.fillMaxSize().systemBarsPadding(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) { contentPadding ->
            GenreDetailsContent(
                genre = genre,
                //albums = albums,
                songs = songs,
                pSongs = pSongs,
                /*onQueueSong = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(snackBarText)
                    }
                    onQueueSong(it)
                },*/
                //navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToPlayer = navigateToPlayer,
                navigateToPlayerSong = navigateToPlayerSong,
                modifier = modifier.padding(contentPadding)
            )
        }
    }
}

/**
 * Composable for Genre Details Screen's Top App Bar.
 */
@Composable
fun GenreDetailsTopAppBar(
    navigateBack: () -> Unit,
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

@Composable
fun GenreDetailsContent(
    genre: GenreInfo,
    //albums: PersistentList<AlbumInfo>,
    songs: List<SongInfo>,
    pSongs: List<PlayerSong>,
    //onQueueSong: (PlayerSong) -> Unit,
    //navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit, //TODO: PlayerSong support
    modifier: Modifier = Modifier
) {

    /*
        ------- VERSION 2: albums and songs combined -------
        Goal: Header item to contain genre name,
        Use remainder of screen for two panes, first pane is albums list, second pane is songs list
        Albums list will have one, immutable sort order. Albums pane will have # albums as 'title'.
        Songs list will have sort and selection options. Songs pane will have # songs as 'title'.
        Future consideration: include shuffle and play btns between song pane 'title' and list
     */

    // --- Version 2: Iteration 2 Start ---
    /*val pagerState = rememberPagerState { albums.size }
    LaunchedEffect(pagerState, albums) {
        snapshotFlow { pagerState.currentPage }
            .collect {
//                val album = albums.getOrNull(it)
//                album?.let { it1 -> HomeAction.LibraryAlbumSelected(it1) }
//                    ?.let { it2 -> onHomeAction(it2) }
            }//crashes the app on Home screen redraw
    }*/

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize(),
    ) {
        //section 1: header item
        fullWidthItem {
            GenreDetailsHeaderItem(
                genre = genre,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        //section 2: albums list
        /*if (!albums.isEmpty()) {
            fullWidthItem {
                Text(
                    text = """\s[a-z]""".toRegex().replace(
                        quantityStringResource(R.plurals.albums, albums.size, albums.size)
                    ) {
                        it.value.uppercase()
                    },
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            fullWidthItem {
                FeaturedAlbumsCarousel(
                    pagerState = pagerState,
                    items = albums,
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }*/

        //section 3: songs list
        if (songs.isNotEmpty()) {
            fullWidthItem {
                Text(
                    text = """\s[a-z]""".toRegex().replace(
                        quantityStringResource(R.plurals.songs, songs.size, songs.size)
                    ) {
                        it.value.uppercase()
                    },
                    //text = quantityStringResource(R.plurals.songs, songs.size, songs.size),
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(pSongs) { song ->
                Box(Modifier.padding(horizontal = 12.dp, vertical = 0.dp)) {
                    SongListItem(
                        song = song,
                        onClick = navigateToPlayerSong,
                        //onQueueSong = { },
                        modifier = Modifier.fillMaxWidth(),
                        isListEditable = false,
                        showAlbumTitle = true,
                        showArtistName = true,
                        showAlbumImage = true,
                    )
                }
            }
        }
    }
}

@Composable
fun GenreDetailsHeaderItem(
    genre: GenreInfo,
    modifier: Modifier = Modifier
) {
    //TODO choose if want 1 image or multi image view for genre header
    // and for the 1 image, should it be the 1st album, or an image for externally of the genre?
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
//            AlbumImage(
//                modifier = Modifier
//                    //.size(widthConstraint, 200.dp)
//                    .fillMaxSize()
//                    .clip(MaterialTheme.shapes.large),
//                albumImage = R.drawable.bpicon,//album.artwork!!,//album.imageUrl or album.artwork when that is fixed
//                contentDescription = "genre Image"
//            )
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

//@Preview
@Composable
fun GenreDetailsHeaderItemPreview() {
    GenreDetailsHeaderItem(
        genre = PreviewGenres[0],
    )
}

@Preview
@Composable
fun GenreDetailsScreenPreview() {
    MusicTheme {
        GenreDetailsScreen(
            //Alternative
            //genre = PreviewGenres[0],
            //songs = getSongsInGenre(0),
            //pSongs = getSongsInGenre(0).map { it.toPlayerSong() },

            //JPop
            genre = PreviewGenres[3],
            songs = getSongsInGenre(3),
            pSongs = getSongsInGenre(3).map { it.toPlayerSong() },

            //navigateToAlbumDetails = {},
            navigateToPlayer = {},
            navigateToPlayerSong = {},
            navigateBack = {},
        )
    }
}
