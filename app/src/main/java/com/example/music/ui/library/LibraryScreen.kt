package com.example.music.ui.library

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.example.music.R
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewComposers
import com.example.music.domain.testing.PreviewGenres
import com.example.music.domain.testing.PreviewPlayerSongs
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.GenreInfo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.player.model.PlayerSong
import com.example.music.ui.library.album.albumItems
import com.example.music.ui.library.artist.artistItems
import com.example.music.ui.library.composer.composerItems
import com.example.music.ui.library.genre.genreItems
import com.example.music.ui.library.playlist.playlistItems
import com.example.music.ui.library.song.songItems
import com.example.music.ui.shared.NavDrawer
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.fullWidthItem
import com.example.music.util.isCompact
import com.example.music.util.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Changelog:
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 *
 * 4/13/2025 - Added navigateToSearch to Search Icon in TopAppBar
 */

/**
 * Stateful Composable for the Library Screen of the app. Contains windowSizeClass,
 * navigateToPlayer, and viewModel as parameters.
 */
@Composable
fun LibraryScreen(
    windowSizeClass: WindowSizeClass,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    //navigateToPlayerSong: (PlayerSong) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        Text(text = uiState.errorMessage!!)
        LibraryScreenError(onRetry = viewModel::refresh)
    }
    Surface {
        LibraryScreen(
            //for now so I can see anything load in emulator, gonna put some preview values
            windowSizeClass = windowSizeClass,
            isLoading = uiState.isLoading,
            libraryCategories = uiState.libraryCategories,
            selectedLibraryCategory = uiState.selectedLibraryCategory,
            //would probably load in the preferences data store around here to collect what sorting used to view screens
            libraryAlbums = uiState.libraryAlbums,
            libraryArtists = uiState.libraryArtists,
            libraryComposers = uiState.libraryComposers,
            libraryGenres = uiState.libraryGenres,
            libraryPlaylists = uiState.libraryPlaylists,
            //libraryPlayerSongs = uiState.libraryPlayerSongs,
            librarySongs = uiState.librarySongs,
            totals = uiState.totals,
            onLibraryAction = viewModel::onLibraryAction,
            /*navigateToAlbumDetails = { //was navigateToPodcastDetails,
               //  then navigateToPlaylistDetails but uiState
               //  doesn't share playlistInfo, but it can
               //  share genre, album, song infos
               //  could make this navigateToAlbumDetails. would need an albumInfo that
               //  can encapsulate the type of properties needed that Podcast has
                navigator.navigateTo(SupportingPaneScaffoldRole.Supporting, it.id.toString())
            },*/
            navigateToHome = navigateToHome,
            navigateToLibrary = navigateToLibrary,
            navigateToSettings = navigateToSettings,
            navigateToAlbumDetails = navigateToAlbumDetails,
            navigateToArtistDetails = navigateToArtistDetails,
            navigateToComposerDetails = navigateToComposerDetails,
            navigateToGenreDetails = navigateToGenreDetails,
            navigateToPlaylistDetails = navigateToPlaylistDetails,
            navigateToPlayer = navigateToPlayer,
            //navigateToPlayerSong = navigateToPlayerSong,
            navigateToSearch = navigateToSearch,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Error Screen
 */
@Composable
private fun LibraryScreenError(
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
 * Stateless Composable for Library Screen and its properties needed to render the
 * components of the page.
 */
@Composable
private fun LibraryScreen(
    windowSizeClass: WindowSizeClass,
    isLoading: Boolean,
    selectedLibraryCategory: LibraryCategory,
    libraryCategories: List<LibraryCategory>,
    libraryAlbums: List<AlbumInfo>,
    libraryArtists: List<ArtistInfo>,
    libraryComposers: List<ComposerInfo>,
    libraryGenres: List<GenreInfo>,
    libraryPlaylists: List<PlaylistInfo>,
    librarySongs: List<SongInfo>,
    //libraryPlayerSongs: List<PlayerSong>, //TODO: PlayerSong support
    totals: List<Int>,
    //totals: List<Pair<String,Int>>,
    onLibraryAction: (LibraryAction) -> Unit,
    //navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    //navigateToPlayerSong: (PlayerSong) -> Unit,
    navigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue) //TODO: update if need to
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    //moved to using listState and visible within LibraryContent()
    //val listState = rememberLazyListState()
    // The FAB is initially shown. Upon scrolling past the first item we hide the FAB by using a
    // remembered derived state to minimize unnecessary compositions.
    //val fabVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    //library screen seeing if navDrawer acts differently if placed before ScreenBackground
    NavDrawer(
        "Library",
        totals,
        navigateToHome,
        navigateToLibrary,
        navigateToSettings,
        drawerState,
        coroutineScope,
    ) {
        ScreenBackground(
            modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Scaffold(
                topBar = {
                    LibraryTopAppBar(
                        navigateToSearch = navigateToSearch,
                        onNavigationIconClick = {
                            coroutineScope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                    )
                    if (isLoading) {
                        LinearProgressIndicator(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                },
                bottomBar = {
                    /* //should show BottomBarPlayer here if a queue session is running or service is running
                    BottomBarPlayer(
                        song = PreviewPlayerSongs[5],
                        navigateToPlayerSong = { navigateToPlayerSong(PreviewPlayerSongs[5]) },
                    )*/
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                containerColor = Color.Transparent,
                contentColor = contentColorFor(MaterialTheme.colorScheme.background) //selects the appropriate color to be the content color for the container using background color
            ) { contentPadding ->
                // Main Content
                LibraryContent(
                    coroutineScope = coroutineScope,
                    selectedLibraryCategory = selectedLibraryCategory,
                    libraryCategories = libraryCategories,
                    libraryAlbums = libraryAlbums,
                    libraryArtists = libraryArtists,
                    libraryComposers = libraryComposers,
                    libraryGenres = libraryGenres,
                    libraryPlaylists = libraryPlaylists,
                    //libraryPlayerSongs = libraryPlayerSongs,
                    librarySongs = librarySongs,
                    modifier = Modifier.padding(contentPadding),
                    onLibraryAction = { action ->
                        if (action is LibraryAction.QueueSong) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(snackBarText)
                            }
                        }
                        onLibraryAction(action)
                    },
                    navigateToAlbumDetails = navigateToAlbumDetails,
                    navigateToArtistDetails = navigateToArtistDetails,
                    navigateToComposerDetails = navigateToComposerDetails,
                    navigateToGenreDetails = navigateToGenreDetails,
                    navigateToPlaylistDetails = navigateToPlaylistDetails,
                    navigateToPlayer = navigateToPlayer,
                    //navigateToPlayerSong = navigateToPlayerSong,
                )
            }
        }
    }
}

/**
 * Composable for Library Screen's Top App Bar.
 */
@Composable
private fun LibraryTopAppBar(
    navigateToSearch: () -> Unit,
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        // nav drawer btn
        IconButton( onClick = onNavigationIconClick ) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = stringResource(R.string.icon_nav_drawer),
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
    }
}

private val FEATURED_ALBUM_IMAGE_SIZE_DP = 160.dp

/**
 * Composable for Library Screen Content
 * Adjusted the library categories so that the selected category determines the grid view
 * new version: want to make a singular vertical grid that has dynamic layout based on tab chosen
 */
@Composable
private fun LibraryContent(
    coroutineScope: CoroutineScope,

    selectedLibraryCategory: LibraryCategory,
    libraryCategories: List<LibraryCategory>,
    libraryAlbums: List<AlbumInfo>,
    libraryArtists: List<ArtistInfo>,
    libraryComposers: List<ComposerInfo>,
    libraryGenres: List<GenreInfo>,
    libraryPlaylists: List<PlaylistInfo>,
    librarySongs: List<SongInfo>,
    //libraryPlayerSongs: List<PlayerSong>,

    modifier: Modifier = Modifier,
    onLibraryAction: (LibraryAction) -> Unit,

    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToPlaylistDetails: (PlaylistInfo) -> Unit,
    navigateToGenreDetails: (GenreInfo) -> Unit,
    navigateToComposerDetails: (ComposerInfo) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    //navigateToPlayerSong: (PlayerSong) -> Unit,
) {
    val listState = rememberLazyGridState()
    val displayButton = remember { derivedStateOf { listState.firstVisibleItemIndex > 1 } }
    //derived state is useful when the evaluated expression's value changes are less often than the item's own value changing
    //     so that recomposition only occurs when the evaluated result changes.
    //     the emitted state/value doesn't have to be updated with every change in the given item
    //     ALSO good for form validation -> updating a button/field error or disable state when an input is valid/invalid
    //     if there's something that could be used in viewModel/nonComposable, it can still be with derivedStateOf, just won't need remember

    //so to make use of derived state for changing the sticky headers,
    //     would need way to select those out of the groupedBy maps and have those items be the deciders

    //if i want to make sticky headers, seems like that would need to be
    //     defined before the items set creation since it will
    //     be dependent on the sort order before using groupBy
    //     might be fine to use as init version, but if there's a defined
    //     sort order later, need to reference that instead

    val groupedAlbumItems = libraryAlbums.groupBy { it.title.first() }
    val groupedArtistItems = libraryArtists.groupBy { it.name.first() }
    val groupedComposerItems = libraryComposers.groupBy { it.name.first() }
    val groupedGenreItems = libraryGenres.groupBy { it.name.first() }
    val groupedPlaylistItems = libraryPlaylists.groupBy { it.name.first() }
    val groupedSongItems = librarySongs.groupBy { it.title.first() }

    groupedArtistItems.forEach { (letter, artist) ->
        logger.info { "Output for groupedArtists Letter: $letter, artist: $artist" }
    }
    //if artists, composers, genres, songs -> column fixed to 1 -> LazyVerticalGrid(columns = GridCells.Fixed(1), modifier = Modifier.padding(horizontal = 12.dp))
    //if playlists, albums -> column fixed, but to whatever the max amount of columns can be shown

    // can this section be done on a span basis? so that if i set the column to be some number X,
    // i can then make the adaptive ones be span1 and the other be spanX?
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val horizontalPadding = 12
    val itemSpacing = 8
    val availableWidth = screenWidth - (horizontalPadding * 2) //times 2 to account for padding on left and right sides
    val minCellWidth = 160 // same as FEATURED_ALBUM_IMAGE_SIZE_DP
    val columns = 1.coerceAtLeast((availableWidth / (minCellWidth + itemSpacing)))

    //using box so that scroll to top btn can appear above library content
    // effectively creating scroll up FAB
    Box(Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = listState,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding.dp)
        ) {
            fullWidthItem {
                LibraryCategoryTabs(
                    libraryCategories = libraryCategories,
                    selectedLibraryCategory = selectedLibraryCategory,
                    onLibraryCategorySelected = { //selectedLibraryCategory.value = it
                        onLibraryAction(
                            LibraryAction.LibraryCategorySelected(
                                it
                            )
                        )
                    },
                )
            }

            when (selectedLibraryCategory) {
                /** single column section **/
                LibraryCategory.Artists -> {
                    /** practicing with sticky header **/
                    artistItems(
                        mappedArtists = groupedArtistItems,
                        artistCount = libraryArtists.size,
                        coroutineScope = coroutineScope,
                        state = listState,
                        navigateToArtistDetails = navigateToArtistDetails,
                    )

                    /** original version **/
                    /*artistItems(
                        //what would the artists screen need?
                        artists = libraryArtists,
                        coroutineScope = coroutineScope,
                        navigateToArtistDetails = navigateToArtistDetails,
                    )*/
                }

                LibraryCategory.Composers -> {
                    composerItems(
                        //what would the composers screen need?
                        composers = libraryComposers,
                        coroutineScope = coroutineScope,
                        navigateToComposerDetails = navigateToComposerDetails,
                    )
                }

                LibraryCategory.Genres -> {
                    genreItems(
                        //what would the genres screen need?
                        genres = libraryGenres,
                        coroutineScope = coroutineScope,
                        navigateToGenreDetails = navigateToGenreDetails,
                    )
                }

                LibraryCategory.Songs -> {
                    songItems(
                        //what would the songs screen need?
                        songs = librarySongs,
                        coroutineScope = coroutineScope,
                        navigateToPlayer = navigateToPlayer,
                        //onQueueSong = { onLibraryAction(LibraryAction.QueueSong(it)) },
                    )
                }

                /** adaptive columns section **/
                LibraryCategory.Albums -> {
                    albumItems(
                        //what would the albums screen need?
                        albums = libraryAlbums,
                        coroutineScope = coroutineScope,
                        navigateToAlbumDetails = navigateToAlbumDetails,
                    )
                }

                LibraryCategory.Playlists -> {
                    playlistItems(
                        //what would the playlist screen need?
                        playlists = libraryPlaylists,
                        navigateToPlaylistDetails = navigateToPlaylistDetails,
                        coroutineScope = coroutineScope,
                        modifier = modifier//.align(Alignment.CenterHorizontally),
                    )
                }
            }
        }

        /**
         * Scroll to top btn for Library Content that appears
         * after scrolling down beyond first few items
         */
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
}

@Composable
private fun LibraryCategoryTabs(
    libraryCategories: List<LibraryCategory>,
    selectedLibraryCategory: LibraryCategory,
    onLibraryCategorySelected: (LibraryCategory) -> Unit,
    //modifier: Modifier = Modifier,
) {
    if (libraryCategories.isEmpty()) {
        return
    }
    val selectedIndex = libraryCategories.indexOfFirst { it == selectedLibraryCategory }

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        LibraryCategoryTabIndicator(
            Modifier.tabIndicatorOffset(
                tabPositions[selectedIndex]
            ),
            MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        indicator = indicator,
        modifier = Modifier,
        divider = {}
    ) {
        libraryCategories.forEachIndexed { index, libraryCategory ->
            Tab(
                selected = index == selectedIndex,
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedContentColor = MaterialTheme.colorScheme.primary,
                onClick = { onLibraryCategorySelected(libraryCategory) },
                modifier = Modifier.padding(0.dp),
                content = {
                    Text(
                        text = libraryCategory.name,
                        style =
                            if (index == selectedIndex)
                                MaterialTheme.typography.titleLarge
                            else MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                },
            )
        }
    }
}

@Composable
private fun LibraryCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}

/*@Composable
private fun lastUpdated(updated: OffsetDateTime): String {
    val duration = Duration.between(updated.toLocalDateTime(), LocalDateTime.now())
    val days = duration.toDays().toInt()

    return when {
        days > 28 -> stringResource(R.string.updated_longer)
        days >= 7 -> {
            val weeks = days / 7
            quantityStringResource(R.plurals.updated_weeks_ago, weeks, weeks)
        }

        days > 0 -> quantityStringResource(R.plurals.updated_days_ago, days, days)
        else -> stringResource(R.string.updated_today)
    }
}*/

@Preview
@Composable
private fun LibraryTopAppBarPreview() {
    MusicTheme {
        LibraryTopAppBar(
            navigateToSearch = {},
            onNavigationIconClick = {},
        )
    }
}

private val CompactWindowSizeClass = WindowSizeClass.compute(360f, 780f)

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewLibrary() {
    MusicTheme {
        LibraryScreen(
            windowSizeClass = CompactWindowSizeClass,
            isLoading = false,
            libraryCategories = LibraryCategory.entries,
            selectedLibraryCategory = LibraryCategory.Songs,
            libraryAlbums = PreviewAlbums,
            libraryArtists = PreviewArtists,
            libraryComposers = PreviewComposers,
            libraryGenres = PreviewGenres,
            libraryPlaylists = PreviewPlaylists,
            //libraryPlayerSongs = PreviewPlayerSongs,
            librarySongs = PreviewSongs,
            totals = listOf(
                PreviewSongs.size,
                PreviewArtists.size,
                PreviewAlbums.size,
                PreviewPlaylists.size),
            onLibraryAction = {},
            //navigateBack = {},
            navigateToHome = {},
            navigateToLibrary = {},
            navigateToSettings = {},
            navigateToAlbumDetails = {},
            navigateToArtistDetails = {},
            navigateToPlaylistDetails = {},
            navigateToGenreDetails = {},
            navigateToComposerDetails = {},
            navigateToPlayer = {},
            //navigateToPlayerSong = {},
            navigateToSearch = {},
        )
    }
}
