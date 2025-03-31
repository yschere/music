package com.example.music.ui.albumdetails

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
import androidx.compose.material.icons.filled.Checklist
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
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.getArtistData
import com.example.music.domain.testing.getSongsInAlbum
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.player.model.PlayerSong
import com.example.music.domain.player.model.toPlayerSong
import com.example.music.ui.shared.AlbumMoreOptionsBottomModal
import com.example.music.ui.shared.DetailsSortSelectionBottomModal
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import kotlinx.coroutines.CoroutineScope

/**
 * Stateful version of Album Details Screen
 */
@Composable
fun AlbumDetailsScreen(
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
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
                pSongs = uiState.pSongs,
                navigateToPlayer = navigateToPlayer,
                navigateToPlayerSong = navigateToPlayerSong,
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
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlbumDetailsScreen(
    album: AlbumInfo,
    artist: ArtistInfo,
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
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue) //use this to hold the little popup text that appears after an onClick event

    ScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        //base layer structure component
        Scaffold(
            contentWindowInsets = WindowInsets.systemBarsIgnoringVisibility,
            topBar = {
                AlbumDetailsTopAppBar(
                    navigateBack = navigateBack,
                    //modifier = Modifier.fillMaxWidth()
                )
            },
            bottomBar = {
                /* //should show BottomBarPlayer here if a queue session is running or service is running
                BottomBarPlayer(
                    song = PreviewPlayerSongs[5],
                    navigateToPlayerSong = { navigateToPlayerSong(PreviewPlayerSongs[5]) },
                )*/
            },
            snackbarHost = { // setting the snackbar hoststate to the scaffold
                SnackbarHost(hostState = snackbarHostState)
            },
            //modifier = modifier.fillMaxSize().systemBarsPadding(), //says to use max size of screen?
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background) //selects the appropriate color to be the content color for the container using background color
            //contentColor = MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
        ) { contentPadding -> //not sure why content padding into content function done in this way
            AlbumDetailsContent(
                album = album,
                artist = artist,
                songs = songs,
                pSongs = pSongs,
                coroutineScope = coroutineScope,
                navigateToPlayer = navigateToPlayer,
                navigateToPlayerSong = navigateToPlayerSong,
                modifier = Modifier.padding(contentPadding)//.padding(horizontal = 8.dp)
            )
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
    pSongs: List<PlayerSong>,
    //onQueueSong: (PlayerSong) -> Unit,
    coroutineScope: CoroutineScope,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    modifier: Modifier = Modifier
) { //determines content of details screen
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var showDetailSheet by remember { mutableStateOf(false) }

    LazyVerticalGrid( //uses lazy vertical grid to store header and items list below it
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
        fullWidthItem {
            AlbumDetailsHeader(
                album = album,
                artist = artist,
                modifier = Modifier.fillMaxWidth()
            )
        }

        /**
         * Version 3: based on music player / spotify player type.
         * Has album image centered at top;
         * Has album title, album artist, below image.
         * Has song count, song sort btn, multi-select on separate row below.
         */
        fullWidthItem {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //AlbumDetailsHeaderLargeAlbumCover( album = album, artist = artist, modifier = Modifier.fillMaxWidth() )
            }
        }

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
            ShufflePlayButtons(
                onShuffleClick = {},
                onPlayClick = {}
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
                        artist = artist,
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
    artist: ArtistInfo,
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
                        text = artist.name,
                        //color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.titleMedium
                    )
                    /*Text(
                        text = """\s[a-z]""".toRegex().replace(quantityStringResource(R.plurals.songs, album.songCount, album.songCount)) {
                            it.value.uppercase()
                        },
                        //text = quantityStringResource(R.plurals.songs, album.songCount, album.songCount),
                        //color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.titleMedium
                    )*/
                }
            }
        }
    }
}

/**
 * Version 3: Header using large album image as first item on screen, centered horizontally.
 * This is a variant I wanted to try so it has the
 * scaffold with it that started the content hierarchy.
 */
@Composable
fun AlbumDetailsHeaderLargeAlbumCover(
    album: AlbumInfo,
    artist: ArtistInfo,
    modifier: Modifier = Modifier,
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.padding(Keyline1).size(250.dp)) {
            Image(
                painter = painterResource(R.drawable.bpicon),
                contentDescription = album.title,
                //contentScale = ContentScale.FillBounds, //transform content to size of container
                //contentScale = ContentScale.Crop, //crop in content to fill based on smaller dimension
                //contentScale = ContentScale.None, //keeps image as exact size within container
                //contentScale = ContentScale.Fit, //transform content to fit based on larger dimension
                //contentScale = ContentScale.Inside, //not sure what this is, result is same as fit
                //contentScale = ContentScale.FillWidth, //transform content to fit based on width
                //contentScale = ContentScale.FillHeight, //transform content to fit based on height
                modifier = modifier
                    //.fillMaxSize()
                    .clip(MaterialTheme.shapes.large)
                    //.background(MaterialTheme.colorScheme.onPrimary),
            )
        }

        Text(
            text = album.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.basicMarquee()
        )
        Text(
            text = artist.name,//getArtistData(album.albumArtistId!!).name, //should probably change this to get data normally
            maxLines = 1,
            overflow = TextOverflow.Visible,
            style = MaterialTheme.typography.titleMedium
        )
        /*Text(
            text = """\s[a-z]""".toRegex().replace(quantityStringResource(R.plurals.songs, album.songCount, album.songCount)) {
                it.value.uppercase()
            },
            //text = quantityStringResource(R.plurals.songs, album.songCount, album.songCount),
            //color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            style = MaterialTheme.typography.titleMedium
        )*/
    }
}

// section 1.3: song count and list sort icons
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

// section 1.5: shuffle and play buttons
@Composable
private fun ShufflePlayButtons(
    onShuffleClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    Row(Modifier.padding(bottom = 8.dp)) {
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
    }
}

//@CompLightPreview
//@CompDarkPreview
@Composable
fun AlbumDetailsHeaderItemPreview() {
    MusicTheme {
        AlbumDetailsHeader(
            album = PreviewAlbums[6],
            artist = getArtistData(PreviewAlbums[6].albumArtistId!!)
        )
    }
}

/*@Preview
@Composable
fun HeaderAlbumCoverPreview() {
    MusicTheme {
        AlbumDetailsHeaderLargeAlbumCover(
            album = PreviewAlbums[0]
        )
    }
}*/

//@SystemLightPreview
@SystemDarkPreview
//@LandscapePreview
@Composable
fun AlbumDetailsScreenPreview() {
    MusicTheme {
        AlbumDetailsScreen(
            //album = PreviewAlbums[0],
            //songs = PreviewSongs,

            //Slow Rain
            album = PreviewAlbums[2],
            artist = getArtistData(PreviewAlbums[2].albumArtistId!!),
            songs = getSongsInAlbum(281),
            pSongs = getSongsInAlbum(281).map { it.toPlayerSong() },

            //Kingdom Hearts Piano Collection
            //album = PreviewAlbums[6],
            //songs = getSongsInAlbum(307),

            navigateToPlayer = { },
            navigateToPlayerSong = { },
            navigateBack = { },
            showBackButton = true
        )
    }
}
