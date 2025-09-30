package com.example.music.ui.search

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.music.R
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.SCREEN_PADDING
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.ui.shared.AlbumActions
import com.example.music.ui.shared.AlbumListItem
import com.example.music.ui.shared.AlbumMoreOptionsBottomModal
import com.example.music.ui.shared.ArtistActions
import com.example.music.ui.shared.ArtistListItem
import com.example.music.ui.shared.ArtistMoreOptionsBottomModal
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongActions
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.shared.SongMoreOptionsBottomModal
import com.example.music.util.BackNavBtn
import com.example.music.util.ClearFieldBtn
import com.example.music.util.NavToMoreBtn
import kotlinx.coroutines.launch

private const val TAG = "Search Screen"

/**
 * Stateful version of Search Screen.
 */
@Composable
fun SearchScreen(
    navigateBack: () -> Unit = {},
    navigateToPlayer: () -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    viewModel: SearchQueryViewModel = hiltViewModel(),
) {
    // contains the View / Ui state of the screen
    val uiState = viewModel.state.collectAsState().value

    Surface(color = Color.Transparent) {
        SearchScreenReady(
            uiState = uiState,
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToPlayer = navigateToPlayer,
            navigateToArtistDetails = navigateToArtistDetails,
            navigateToAlbumDetails = navigateToAlbumDetails,
            modifier = Modifier.fillMaxSize(),
        )
        if (uiState == SearchUiState.Error) {
            Log.e(TAG, "SearchUiState Error")
            SearchError(onRetry = {})
        }
    }
}

/**
 * Error Screen
 */
@Composable
private fun SearchError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Error(
        onRetry = onRetry,
        modifier = modifier
    )
}

@Composable
fun SearchScreenReady(
    uiState: SearchUiState,
    viewModel: SearchQueryViewModel,
    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchScreen(
        uiState = uiState,
        queryText = viewModel.queryText.collectAsState().value,
        selectSong = viewModel.selectedSong.collectAsState().value,
        selectArtist = viewModel.selectedArtist.collectAsState().value,
        selectAlbum = viewModel.selectedAlbum.collectAsState().value,

        onMoreOptionsAction = viewModel::onMoreOptionsAction,
        searchActions = SearchActions(
            updateQuery = viewModel::updateQuery,
            clearQuery = viewModel::clearQuery,
            sendQuery = viewModel::sendQuery,
        ),
        onSongMoreOptionsClick = viewModel::onSongMoreOptionsClick,
        onArtistMoreOptionsClick = viewModel::onArtistMoreOptionsClick,
        onAlbumMoreOptionsClick = viewModel::onAlbumMoreOptionsClick,
        onMoreResultsClick = viewModel::onMoreQuery,

        navigateBack = navigateBack,
        navigateToPlayer = navigateToPlayer,
        navigateToArtistDetails = navigateToArtistDetails,
        navigateToAlbumDetails = navigateToAlbumDetails,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    queryText: String,
    selectSong: SongInfo,
    selectArtist: ArtistInfo,
    selectAlbum: AlbumInfo,

    onMoreOptionsAction: (MoreOptionsAction) -> Unit,
    searchActions: SearchActions,
    onSongMoreOptionsClick: (SongInfo) -> Unit,
    onArtistMoreOptionsClick: (ArtistInfo) -> Unit,
    onAlbumMoreOptionsClick: (AlbumInfo) -> Unit,
    onMoreResultsClick: (String) -> Unit,

    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToArtistDetails: (Long) -> Unit,
    navigateToAlbumDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    //val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)

    val sheetState = rememberModalBottomSheetState(true)
    var showAlbumMoreOptions by remember { mutableStateOf(false) }
    var showArtistMoreOptions by remember { mutableStateOf(false) }
    var showSongMoreOptions by remember { mutableStateOf(false) }

    ScreenBackground(
        modifier = modifier
    ) {
        Scaffold(
            topBar = {},
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent,
            modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
            contentColor = contentColorFor(MaterialTheme.colorScheme.background)
        ) { contentPadding ->
            SearchContent(
                navigateBack = navigateBack,
                uiState = uiState,
                queryText = queryText,
                searchActions = searchActions,
                resultActions = ResultActions(
                    onSongClick = { song: SongInfo ->
                        Log.i(TAG, "Song clicked: ${song.title}")
                        onMoreOptionsAction(MoreOptionsAction.PlaySong(song))
                        navigateToPlayer()
                    },
                    onSongMoreOptionsClick = { song: SongInfo ->
                        Log.i(TAG, "Song More Options clicked: ${song.title}")
                        onSongMoreOptionsClick(song)
                        showSongMoreOptions = true
                    },
                    onArtistClick = { artist: ArtistInfo ->
                        Log.i(TAG, "Artist clicked: ${artist.name}")
                        navigateToArtistDetails(artist.id)
                    },
                    onArtistMoreOptionsClick = { artist: ArtistInfo ->
                        Log.i(TAG, "Artist More Options clicked: ${artist.name}")
                        onArtistMoreOptionsClick(artist)
                        showArtistMoreOptions = true
                    },
                    onAlbumClick = { album: AlbumInfo ->
                        Log.i(TAG, "Album clicked: ${album.title}")
                        navigateToAlbumDetails(album.id)
                    },
                    onAlbumMoreOptionsClick = { album: AlbumInfo ->
                        Log.i(TAG, "Album More Options clicked: ${album.title}")
                        onAlbumMoreOptionsClick(album)
                        showAlbumMoreOptions = true
                    },
                    onMoreResultsClick = onMoreResultsClick,
                ),
                modifier = Modifier.padding(contentPadding),
            )

            if (showAlbumMoreOptions) {
                Log.i(TAG, "Settings Content -> Album More Options is TRUE")
                AlbumMoreOptionsBottomModal(
                    onDismissRequest = { showAlbumMoreOptions = false },
                    sheetState = sheetState,
                    album = selectAlbum,
                    albumActions = AlbumActions(
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options -> Play Album clicked :: ${selectAlbum.id}")
                                onMoreOptionsAction(MoreOptionsAction.PlayAlbum(selectAlbum))
                                sheetState.hide()
                                navigateToPlayer()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                                if (!sheetState.isVisible) {
                                    showAlbumMoreOptions = false
                                }
                            }
                        },
                        playNext = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Play Album Next clicked :: ${selectAlbum.id}")
                                onMoreOptionsAction(MoreOptionsAction.PlayAlbumNext(selectAlbum))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                                if (!sheetState.isVisible) {
                                    showAlbumMoreOptions = false
                                }
                            }
                        },
                        shuffle = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Shuffle Album clicked :: ${selectAlbum.id}")
                                onMoreOptionsAction(MoreOptionsAction.ShuffleAlbum(selectAlbum))
                                sheetState.hide()
                                navigateToPlayer()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                                if (!sheetState.isVisible) {
                                    showAlbumMoreOptions = false
                                }
                            }
                        },
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Queue Album clicked :: ${selectAlbum.id}")
                                onMoreOptionsAction(MoreOptionsAction.QueueAlbum(selectAlbum))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                                if (!sheetState.isVisible) {
                                    showAlbumMoreOptions = false
                                }
                            }
                        },
                        goToAlbumArtist = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Go To Album Artist clicked :: ${selectAlbum.albumArtistId}")
                                navigateToArtistDetails(selectAlbum.albumArtistId ?: 0)
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                                if (!sheetState.isVisible) {
                                    showAlbumMoreOptions = false
                                }
                            }
                        },
                        goToAlbum = {
                            coroutineScope.launch {
                                Log.i(TAG, "Album More Options Modal -> Go To Album clicked :: ${selectAlbum.id}")
                                navigateToAlbumDetails(selectAlbum.id)
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                                if (!sheetState.isVisible) {
                                    showAlbumMoreOptions = false
                                }
                            }
                        },
                    ),
                    onClose = {
                        coroutineScope.launch {
                            Log.i(TAG, "Hide sheet state")
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set showAlbumMoreOptions to FALSE")
                            if (!sheetState.isVisible) showAlbumMoreOptions = false
                        }
                    },
                    context = "Search",
                )
            }

            if (showArtistMoreOptions) {
                Log.i(TAG, "Settings Content -> Artist More Options is TRUE")
                ArtistMoreOptionsBottomModal(
                    onDismissRequest = { showArtistMoreOptions = false },
                    sheetState = sheetState,
                    artist = selectArtist,
                    artistActions = ArtistActions(
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> Play Artist clicked :: ${selectArtist.name}")
                                onMoreOptionsAction(MoreOptionsAction.PlayArtist(selectArtist))
                                sheetState.hide()
                                navigateToPlayer()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showArtistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showArtistMoreOptions = false
                            }
                        },
                        playNext = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> Play Artist Next clicked :: ${selectArtist.name}")
                                onMoreOptionsAction(MoreOptionsAction.PlayArtistNext(selectArtist))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showArtistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showArtistMoreOptions = false
                            }
                        },
                        shuffle = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> Shuffle Artist clicked :: ${selectArtist.name}")
                                onMoreOptionsAction(MoreOptionsAction.ShuffleArtist(selectArtist))
                                sheetState.hide()
                                navigateToPlayer()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showArtistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showArtistMoreOptions = false
                            }
                        },
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> Queue Artist clicked :: ${selectArtist.name}")
                                onMoreOptionsAction(MoreOptionsAction.QueueArtist(selectArtist))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showArtistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showArtistMoreOptions = false
                            }
                        },
                        goToArtist = {
                            coroutineScope.launch {
                                Log.i(TAG, "Artist More Options Modal -> Play Artist clicked :: ${selectArtist.name}")
                                onMoreOptionsAction(MoreOptionsAction.PlayArtist(selectArtist))
                                sheetState.hide()
                                navigateToArtistDetails(selectArtist.id)
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showArtistMoreOptions to FALSE")
                                if(!sheetState.isVisible) showArtistMoreOptions = false
                            }
                        },
                    ),
                    onClose = {
                        coroutineScope.launch {
                            Log.i(TAG, "Hide sheet state")
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set showArtistMoreOptions to FALSE")
                            if (!sheetState.isVisible) showArtistMoreOptions = false
                        }
                    },
                    context = "Search",
                )
            }

            if (showSongMoreOptions) {
                Log.i(TAG, "Settings Content -> Song More Options is TRUE")
                SongMoreOptionsBottomModal(
                    onDismissRequest = { showSongMoreOptions = false },
                    sheetState = sheetState,
                    song = selectSong,
                    songActions = SongActions(
                        play = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Play Song clicked :: ${selectSong.title}")
                                onMoreOptionsAction(MoreOptionsAction.PlaySong(selectSong))
                                navigateToPlayer()
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if (!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        playNext = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Play Song Next clicked :: ${selectSong.title}")
                                onMoreOptionsAction(MoreOptionsAction.PlaySongNext(selectSong))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if (!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        addToQueue = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Queue Song clicked :: ${selectSong.title}")
                                onMoreOptionsAction(MoreOptionsAction.QueueSong(selectSong))
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if (!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        goToArtist = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Go To Artist clicked :: ${selectSong.artistId}")
                                navigateToArtistDetails(selectSong.artistId)
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if (!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                        goToAlbum = {
                            coroutineScope.launch {
                                Log.i(TAG, "Song More Options Modal -> Go To Album clicked :: ${selectSong.albumId}")
                                navigateToAlbumDetails(selectSong.albumId)
                                sheetState.hide()
                            }.invokeOnCompletion {
                                Log.i(TAG, "set showSongMoreOptions to FALSE")
                                if (!sheetState.isVisible) showSongMoreOptions = false
                            }
                        },
                    ),
                    onClose = {
                        coroutineScope.launch {
                            Log.i(TAG, "Hide sheet state")
                            sheetState.hide()
                        }.invokeOnCompletion {
                            Log.i(TAG, "set showSongMoreOptions to FALSE")
                            if (!sheetState.isVisible) showSongMoreOptions = false
                        }
                    },
                    context = "Search"
                )
            }
        }
    }
}

@Composable
fun SearchContent(
    navigateBack: () -> Unit,
    uiState: SearchUiState,
    queryText: String,
    searchActions: SearchActions,
    resultActions: ResultActions,
    modifier: Modifier = Modifier,
) {
    Log.i(TAG, "Search Content START")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        SearchField(
            navigateBack,
            uiState,
            queryText,
            searchActions,
            resultActions,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    navigateBack: () -> Unit,
    uiState: SearchUiState,
    queryText: String,
    searchActions: SearchActions,
    resultActions: ResultActions,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = queryText,
                onQueryChange = { searchActions.updateQuery(it) },
                onSearch = {
                    searchActions.sendQuery()
                    keyboardController?.hide()
                    isExpanded = true
                },
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                enabled = true,
                placeholder = { Text(stringResource(id = R.string.icon_search)) },
                leadingIcon = {
                    if (isExpanded) BackNavBtn(onClick = navigateBack)
                    else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.icon_search),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                },
                trailingIcon = {
                    if (isExpanded) ClearFieldBtn(onClick = searchActions.clearQuery)
                    else null
                },
                interactionSource = interactionSource,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        colors = SearchBarDefaults.colors(
            containerColor = Color.Transparent,
            dividerColor = Color.Black,
        ),
        modifier = Modifier.semantics { traversalIndex = 0f }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState == SearchUiState.Loading) {
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SCREEN_PADDING)
                )
            }
            when (uiState) {
                SearchUiState.NoResults -> {
                    Text(
                        text = "Nothing found,\nplease try a different search",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(SCREEN_PADDING)
                    )
                }

                is SearchUiState.SearchResultsFound -> {
                    if (queryText != "" && uiState.results.songs.isNotEmpty()) {
                        val songResult = uiState.results.songs

                        Spacer(Modifier.padding(CONTENT_PADDING))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Songs",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(SCREEN_PADDING),
                            )
                            Spacer(Modifier.weight(1f))
                            if (songResult.size > 20)
                                NavToMoreBtn(onClick = { resultActions.onMoreResultsClick("Songs") })
                        }

                        songResult.forEach { song ->
                            SongListItem(
                                song = song,
                                onClick = { resultActions.onSongClick(song) },
                                onMoreOptionsClick = { resultActions.onSongMoreOptionsClick(song) },
                                showArtistName = true,
                                showAlbumTitle = true,
                                showAlbumImage = true,
                            )
                        }
                    }
                    else if (queryText != "" && uiState.results.songs.isEmpty()) {
                        Spacer(Modifier.padding(CONTENT_PADDING))
                        Text(
                            text = "No Songs Found",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(SCREEN_PADDING),
                        )
                    }

                    if (queryText != "" && uiState.results.artists.isNotEmpty()) {
                        val artistResult = uiState.results.artists

                        Spacer(Modifier.padding(CONTENT_PADDING))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Artists",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(SCREEN_PADDING),
                            )
                            Spacer(Modifier.weight(1f))
                            if (artistResult.size > 20)
                                NavToMoreBtn(onClick = { resultActions.onMoreResultsClick("Artists") })
                        }
                        artistResult.forEach { artist ->
                            ArtistListItem(
                                artist = artist,
                                navigateToArtistDetails = { resultActions.onArtistClick(artist) },
                                onMoreOptionsClick = { resultActions.onArtistMoreOptionsClick(artist) },
                            )
                        }
                    }
                    else if (queryText != "" && uiState.results.artists.isEmpty()) {
                        Spacer(Modifier.padding(CONTENT_PADDING))
                        Text(
                            text = "No Artists Found",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(SCREEN_PADDING),
                        )
                    }

                    if (queryText != "" && uiState.results.albums.isNotEmpty()) {
                        val albumResult = uiState.results.albums

                        Spacer(Modifier.padding(CONTENT_PADDING))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Albums",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(SCREEN_PADDING),
                            )
                            Spacer(Modifier.weight(1f))
                            if (albumResult.size > 20)
                                NavToMoreBtn(onClick = { resultActions.onMoreResultsClick("Albums") })
                        }
                        albumResult.forEach { album ->
                            AlbumListItem(
                                album = album,
                                navigateToAlbumDetails = { resultActions.onAlbumClick(album) },
                                onMoreOptionsClick = { resultActions.onAlbumMoreOptionsClick(album) },
                                cardOrRow = false,
                            )
                        }
                    }
                    else if (queryText != "" && uiState.results.albums.isEmpty()) {
                        Spacer(Modifier.padding(CONTENT_PADDING))
                        Text(
                            text = "No Albums Found",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(SCREEN_PADDING),
                        )
                    }
                }

                else -> { Spacer(modifier = Modifier.fillMaxSize()) }
            }
        }
    }
}
