package com.example.music.ui.genredetails

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.theme.Keyline1
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.testing.PreviewGenres
import com.example.music.domain.testing.getSongsInGenre
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.SongInfo


import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource

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
 * Stateful version of Genre Details Screen
 */
@Composable
fun GenreDetailsScreen(
    //navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToSearch: () -> Unit,
    navigateBack: () -> Unit,
    //modifier: Modifier = Modifier,
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
                //albums = uiState.albums.toPersistentList(),
                songs = uiState.songs,
                //onQueueSong = viewModel::onQueueSong,
                //navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToPlayer = navigateToPlayer,
                navigateToSearch = navigateToSearch,
                navigateBack = navigateBack,
                modifier = Modifier.fillMaxSize(),
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
 * Stateless Composable for Genre Details Screen
 */
 @OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreDetailsScreen(
    genre: GenreInfo,
    //albums: PersistentList<AlbumInfo>,
    songs: List<SongInfo>,
    //onQueueSong: (SongInfo) -> Unit,
    //navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToSearch: () -> Unit,
    navigateBack: () -> Unit,
    //showBackButton: Boolean,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)

    ScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets.systemBarsIgnoringVisibility,
            topBar = {
                GenreDetailsTopAppBar(
                    navigateToSearch = navigateToSearch,
                    navigateBack = navigateBack,
                )
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
            //modifier = modifier.fillMaxSize().systemBarsPadding(),
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background) //selects the appropriate color to be the content color for the container using background color
            //contentColor = MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
        ) { contentPadding ->
            GenreDetailsContent(
                genre = genre,
                //albums = albums,
                songs = songs,
                /*onQueueSong = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(snackBarText)
                    }
                    onQueueSong(it)
                },*/
                //navigateToAlbumDetails = navigateToAlbumDetails,
                navigateToPlayer = navigateToPlayer,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}

/**
 * Composable for Genre Details Screen's Top App Bar.
 */
@Composable
fun GenreDetailsTopAppBar(
    navigateToSearch: () -> Unit,
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
        IconButton( onClick = navigateBack ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.icon_back_nav),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        // search btn //TODO: does this make more sense as a more options btn?
        IconButton( onClick = navigateToSearch ) {
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
 * Composable for Genre Details Screen's Content.
 */
@Composable
fun GenreDetailsContent(
    genre: GenreInfo,
    //albums: PersistentList<AlbumInfo>,
    songs: List<SongInfo>,
    //onQueueSong: (SongInfo) -> Unit,
    //navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    modifier: Modifier = Modifier
) {

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

        // section 2: albums list
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

        // section 3: songs list
        if (songs.isNotEmpty()) { // this is just to make sure the songs list loaded

            // songs header
            fullWidthItem {
                SongCountAndSortSelectButtons(
                    songs = songs,
                    onSelectClick = {},
                    onSortClick = {
                        //showBottomSheet = true
                        //showSortSheet = true
                    }
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
            /*AlbumImage(
                modifier = Modifier
                    .size(widthConstraint, 200.dp)
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large),
                albumImage = R.drawable.bpicon,//album.artwork!!,//album.imageUrl or album.artwork when that is fixed
                contentDescription = "genre Image"
            )*/
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
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

            //navigateToAlbumDetails = {},
            navigateToPlayer = {},
            navigateToSearch = {},
            navigateBack = {},
        )
    }
}
