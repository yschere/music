package com.example.music.ui.genredetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.designsys.theme.Keyline1
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewGenres
import com.example.music.domain.testing.getSongsInGenre
import com.example.music.domain.testing.getAlbumsByArtist
import com.example.music.domain.testing.getSongsByArtist
import com.example.music.model.AlbumInfo
import com.example.music.model.ArtistInfo
import com.example.music.model.GenreInfo
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.player.model.toPlayerSong
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.quantityStringResource
import com.example.music.util.radialGradientScrim
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

/**
 * Stateful version of Genre Details Screen
 */
@Composable
fun GenreDetailsScreen(
//    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    navigateBack: () -> Unit,
    //showBackButton: Boolean,
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
            //boxOrRow = boxOrRow, // TODO change how this works when doing real call and not preview
            //onQueueSong = viewModel::onQueueSong,
            //navigateToAlbumDetails = navigateToAlbumDetails,
            navigateToPlayer = navigateToPlayer,
            navigateToPlayerSong = navigateToPlayerSong,
            navigateBack = navigateBack,
            //showBackButton = showBackButton,
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

    GenreDetailsScreenBackground(
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
 * Composable for Genre Details Screen's Background.
 */
@Composable
private fun GenreDetailsScreenBackground(
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
        ------- VERSION 1: box/row albums -------
        set TRUE -- for box version of album list item, use grid system
        set FALSE -- for row version of album list item, use 1 column lazy vertical grid
    */
    /*if (boxOrRow) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            //contentPadding = PaddingValues(12.dp),
            //modifier.fillMaxSize()
        ) {
            fullWidthItem {
                GenreDetailsHeaderItem(
                    genre = genre,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(albums, key = { it.id }) { album ->
                AlbumListItem( //TODO create new AlbumListItem object in shared
                    album = album,
                    onClick = {},
                    //onClick = navigateToAlbumDetails,
                    //onQueueSong = onQueueSong,
                    boxOrRow = boxOrRow,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

    } else {
        //want this for row version for now
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier.fillMaxSize()
        ) {
            fullWidthItem {
                GenreDetailsHeaderItem(
                    genre = genre,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(albums, key = { it.id }) { album ->
                AlbumListItem( //TODO create new AlbumListItem object in shared
                    album = album,
                    onClick = {},
                    //onClick = navigateToAlbumDetails,
                    //onQueueSong = onQueueSong,
                    boxOrRow = boxOrRow,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }*/

    /*
        ------- VERSION 2: albums and songs combined -------
        Goal: Header item to contain genre name,
        Use remainder of screen for two panes, first pane is albums list, second pane is songs list
        Albums list will have one, immutable sort order. Albums pane will have # albums as 'title'.
        Songs list will have sort and selection options. Songs pane will have # songs as 'title'.
        Future consideration: include shuffle and play btns between song pane 'title' and list
     */

    // --- Version 2: Iteration 2 Start ---
//    val pagerState = rememberPagerState { albums.size }
//    LaunchedEffect(pagerState, albums) {
//        snapshotFlow { pagerState.currentPage }
//            .collect {
//                val playlist = pLists.getOrNull(it)
//                playlist?.let { it1 -> HomeAction.LibraryPlaylistSelected(it1) }
//                    ?.let { it2 -> onHomeAction(it2) }
            //}//crashes the app on Home screen redraw
    //}

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize(),//.padding(horizontal = 4.dp)
    ) {
        fullWidthItem {
            //section 1: header item
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
                    text = quantityStringResource(R.plurals.albums, albums.size, albums.size),
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            fullWidthItem {
                FeaturedAlbums(
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
                    text = """\s[a-z]""".toRegex().replace(quantityStringResource(R.plurals.songs, songs.size, songs.size)) {
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
    // --- Version 2: Iteration 2 End --

    /* -------- Version 2: Iteration 1 --------
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        //section 1: header item
        GenreDetailsHeaderItem(
            genre = genre,
            modifier = Modifier.fillMaxWidth()
        )

        //section 2: albums list
        Text(
            text = if (albums.size == 1) "${albums.size} album" else "${albums.size} albums",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(8.dp)
        )
        BoxWithConstraints {
//            val constraints = if (minWidth < 600.dp) { //portait view
//                AlbumSizeHelper(margin = 160.dp)
//            } else { //landscape view
//                AlbumSizeHelper(margin = 32.dp)
//            }
            Constraints(0, maxWidth.value.toInt(), 0, 500)
            LazyHorizontalGrid(
                //rows = GridCells.Adaptive(minSize = 250.dp),
                //rows = GridCells.FixedSize(250.dp),
                rows = GridCells.Fixed(1),
                //verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier.sizeIn(50.dp, 50.dp, maxWidth, 190.dp)
            ) {
                items(albums, key = { it.id }) { album ->
                    AlbumListItem(
                        album = album,
                        onClick = {},
                        boxOrRow = boxOrRow,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
//        LazyHorizontalGrid(
//            //rows = GridCells.Adaptive(minSize = 250.dp),
//            rows = GridCells.FixedSize(250.dp),
//            //rows = GridCells.Fixed(1),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            //modifier.fillMaxSize()
//        ) {
//            items(albums, key = { it.id }) { album ->
//                AlbumListItem(
//                    album = album,
//                    onClick = {},
//                    boxOrRow = boxOrRow,
//                    modifier = Modifier.fillMaxWidth(),
//                )
//            }
//        }

        //section 3: songs list
        Text(
            text = if (songs.size == 1) "${songs.size} song" else "${songs.size} songs",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier.fillMaxSize()
        ) {
            items(songs, key = { it.id }) { song ->
                SongListItem(
                    song = song,
                    album = albums[0],//TODO change this so it collects the song's corresponding album from albums
                    onClick = {},
                    isListEditable = false,
                    showAlbumTitle = true,
                    showAlbumImage = true,
                    showGenreName = true,
                    onQueueSong = { },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    } */
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

private val FEATURED_ALBUM_IMAGE_SIZE_DP = 160.dp

@Composable
private fun FeaturedAlbums(
    pagerState: PagerState,
    items: PersistentList<AlbumInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FeaturedAlbumsCarousel(
            pagerState = pagerState,
            items = items,
            navigateToAlbumDetails = navigateToAlbumDetails,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FeaturedAlbumsCarousel(
    pagerState: PagerState,
    items: PersistentList<AlbumInfo>,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.background(Color.Transparent)
    ) {
        val horizontalPadding = (this.maxWidth - FEATURED_ALBUM_IMAGE_SIZE_DP) / 2
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(
                horizontal = horizontalPadding,
                vertical = 16.dp,
            ),
            pageSpacing = 24.dp,
            pageSize = PageSize.Fixed(FEATURED_ALBUM_IMAGE_SIZE_DP)
        ) { page ->
            val album = items[page]
            FeaturedAlbumsCarouselItem(
                albumImage = 1,//album.artwork!!,
                albumName = album.title,
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

@Composable
private fun FeaturedAlbumsCarouselItem(
    albumName: String,
    //albumImage: String,
    albumImage: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Box(
            Modifier
                .size(FEATURED_ALBUM_IMAGE_SIZE_DP)
                .align(Alignment.CenterHorizontally)
        ) {
            AlbumImage(
                albumImage = albumImage,
                contentDescription = albumName,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
            )
        }
        Text(
            text = albumName,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
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
//            genre = PreviewArtists[0],
//            albums = getAlbumsByArtist(0).toPersistentList(),
//            songs = getSongsByArtist(0),
//            pSongs = getSongsByArtist(0).map { it.toPlayerSong() },

            //Paramore
//            genre = PreviewArtists[1],
//            albums = getAlbumsByArtist(22).toPersistentList(),
//            songs = getSongsByArtist(22),
//            pSongs = getSongsByArtist(22).map { it.toPlayerSong() },

            //ACIDMAN
            genre = PreviewGenres[0],
            //albums = getAlbumsByArtist(113).toPersistentList(),
            songs = getSongsByArtist(113),
            pSongs = getSongsByArtist(113).map { it.toPlayerSong() },

            //navigateToAlbumDetails = {},
            navigateToPlayer = {},
            navigateToPlayerSong = {},
            navigateBack = {},
        )
    }
}
