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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.music.R
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.ui.shared.AlbumListItem
import com.example.music.ui.shared.ArtistListItem
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem

private const val TAG = "Search Screen"

/**
 * Stateful version of Search Screen.
 */
@Composable
fun SearchScreen(
    navigateBack: () -> Unit = {},
    navigateToPlayer: () -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    viewModel: SearchQueryViewModel = hiltViewModel(),
) {
    // contains the View / Ui state of the screen
    val uiState = viewModel.state.collectAsState().value

    // contains the SearchBar Field state
    val fieldState = viewModel.searchFieldState.collectAsState().value

    // contains the state of the string text in the SearchBar
    val queryText = viewModel.queryText.collectAsState().value

    Surface(color = Color.Transparent) {
        SearchScreenReady(
            uiState = uiState,
            fieldState = fieldState,
            queryText = queryText,
            navigateBack = navigateBack,
            navigateToPlayer = navigateToPlayer,
            navigateToArtistDetails = navigateToArtistDetails,
            navigateToAlbumDetails = navigateToAlbumDetails,
            viewModel = viewModel,
        )
        if (uiState == SearchUiState.Error) {
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
    fieldState: SearchFieldState,
    queryText: String,

    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    viewModel: SearchQueryViewModel,
    modifier: Modifier = Modifier,
) {
    //val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    //val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)

    ScreenBackground(
        modifier = modifier
    ) {
        Scaffold(
            topBar = {},
            contentWindowInsets = WindowInsets.systemBars,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background)
        ) { contentPadding ->
            SearchContent(
                navigateBack = navigateBack,
                uiState = uiState,
                searchFieldState = fieldState,
                queryText = queryText,
                searchActions = SearchActions(
                    updateQuery = viewModel::updateQuery, // onSearchInputChanged
                    changeFieldState = viewModel::changeFieldState, // onSearchFieldClicked
                    resetUiState = viewModel::resetUiState, // onChevronClicked
                    clearQuery = viewModel::clearQuery, // onClearInputClicked
                    sendQuery = viewModel::sendQuery, // onSendQuery
                ),
                resultActions = ResultActions(
                    onSongClicked = { item ->
                        Log.i(TAG, "Song clicked: ${item.title}")
                        viewModel.onPlaySong(item)
                        navigateToPlayer()
                    },
                    /*onSongMoreOptionsClicked = { item: SongInfo ->
                        Log.i(TAG, "Song More Options clicked: ${item.title}")
                        onSearchAction(SearchAction.SongMoreOptionClicked(item))
                        showBottomSheet = true
                        showSongMoreOptions = true
                        //play, playNext, add to queue, add to playlist, go to artist, go to album
                    },*/
                    onArtistClicked = { item ->
                        Log.i(TAG, "Artist clicked: ${item.name}")
                        navigateToArtistDetails(item)
                    },
                    /*onArtistMoreOptionsClicked = { item: ArtistInfo ->
                        Log.i(TAG, "Artist More Options clicked: ${item.name}")
                        onSearchAction(SearchAction.ArtistMoreOptionClicked(item))
                        showBottomSheet = true
                        showArtistMoreOptions = true
                        //play, playNext, shuffle, add to queue, add to playlist, go to artist
                    },*/
                    onAlbumClicked = { item ->
                        Log.i(TAG, "Album clicked: ${item.title}")
                        navigateToAlbumDetails(item)
                    },
                    /*onAlbumMoreOptionsClicked = { item: AlbumInfo ->
                        Log.i(TAG, "Album More Options clicked: ${item.title}")
                        onSearchAction(SearchAction.AlbumMoreOptionClicked(item))
                        showBottomSheet = true
                        showAlbumMoreOptions = true
                        //play, playnext, shuffle, add to queue, add to playlist, go to album
                    },*/
                ),
                modifier = Modifier.padding(contentPadding),
            )
        }
    }
}

@Composable
fun SearchContent(
    navigateBack: () -> Unit,
    uiState: SearchUiState,
    searchFieldState: SearchFieldState,
    queryText: String,
    searchActions: SearchActions,
    resultActions: ResultActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 12.dp)
    ) {
        SearchField(
            navigateBack,
            uiState,
            searchFieldState,
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
    searchFieldState: SearchFieldState,
    queryText: String,
    searchActions: SearchActions,
    resultActions: ResultActions,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = queryText,
                onQueryChange = { searchActions.updateQuery(it) },
                onSearch = {
                    searchActions.sendQuery()
                    isExpanded = true
                },
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                enabled = true,
                placeholder = { Text(stringResource(id = R.string.icon_search)) },
                leadingIcon = {
                    if (isExpanded) {
                        IconButton(onClick = navigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.icon_back_nav),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.icon_search),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    /*IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.icon_back_nav),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }*/
                },
                trailingIcon = {
                    IconButton (
                        onClick = searchActions.resetUiState
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
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
                        .padding(horizontal = 16.dp)
                )
            }
            when (uiState) {
                SearchUiState.NoResults -> {
                    Text(
                        text = "Nothing found,\nplease try a different search",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    )
                }

                is SearchUiState.SearchResultsFound -> {
                    if (queryText != "" && uiState.results.songs.isNotEmpty()) {
                        Spacer(Modifier.padding(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Songs",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(16.dp),
                            )
                            Spacer(Modifier.weight(1f))
                            Button(
                                onClick = {},
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentPadding = ButtonDefaults.TextButtonContentPadding,
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = MaterialTheme.colorScheme.inversePrimary,
                                )
                            ) {
                                Text(
                                    text = "More",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        uiState.results.songs.forEach { song ->
                            SongListItem(
                                song = song,
                                onClick = { resultActions.onSongClicked(song) },
                                onMoreOptionsClick = {
                                    //onSongMoreOptionClicked(song)
                                },
                                showArtistName = true,
                                showAlbumTitle = true,
                                showAlbumImage = true,
                            )
                        }
                    } else if (queryText != "" && uiState.results.songs.isEmpty()) {
                        Spacer(Modifier.padding(8.dp))
                        Text(
                            text = "No Songs Found",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    }

                    if (queryText != "" && uiState.results.artists.isNotEmpty()) {
                        Spacer(Modifier.padding(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Artists",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(16.dp),
                            )
                            Spacer(Modifier.weight(1f))
                            Button(
                                onClick = {},
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentPadding = ButtonDefaults.TextButtonContentPadding,
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = MaterialTheme.colorScheme.inversePrimary,
                                )
                            ) {
                                Text(
                                    text = "More",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        uiState.results.artists.forEach { artist ->
                            ArtistListItem(
                                artist = artist,
                                navigateToArtistDetails = { resultActions.onArtistClicked(artist) },
                                onMoreOptionsClick = {
                                    //onArtistMoreOptionsClicked(artist)
                                },
                            )
                        }
                    } else if (queryText != "" && uiState.results.artists.isEmpty()) {
                        Spacer(Modifier.padding(8.dp))
                        Text(
                            text = "No Artists Found",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    }

                    if (queryText != "" && uiState.results.albums.isNotEmpty()) {
                        Spacer(Modifier.padding(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Albums",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(16.dp),
                            )
                            Spacer(Modifier.weight(1f))
                            Button(
                                onClick = {},
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentPadding = ButtonDefaults.TextButtonContentPadding,
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = MaterialTheme.colorScheme.inversePrimary,
                                )
                            ) {
                                Text(
                                    text = "More",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        uiState.results.albums.forEach { album ->
                            AlbumListItem(
                                album = album,
                                navigateToAlbumDetails = { resultActions.onAlbumClicked(album) },
                                onMoreOptionsClick = {
                                    //onAlbumMoreOptionsClicked(album)
                                },
                                cardOrRow = false,
                            )
                        }
                    } else if (queryText != "" && uiState.results.albums.isEmpty()) {
                        Spacer(Modifier.padding(8.dp))
                        Text(
                            text = "No Albums Found",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }

                else -> { Spacer(modifier = Modifier.fillMaxSize()) }
            }

            /* // state logic
            // when ui state is idle, change nothing? keep as is
            // when ui state is loading, have loading bar indicator
            // when ui state is error, show error()
            // when ui state is no results, show no "" found
            // when ui state is results found, show list of results

            // when ui state is idle and field is active, show nothing
            // when ui state is results found, set field to inactive/idle
            // when ui state is loading, set field to idle
            // all other ui state, do nothing to field
             */
        }
    }
}

/*
@HiltViewModel(assistedFactory = SearchQueryViewModel.Factory::class)
class SearchQueryViewModel @AssistedInject constructor(
    private val getSearchQueryUseCase: SearchQueryUseCase,
    @Assisted private val query: String = "",
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(query: String): SearchQueryViewModel
    }

    private val refreshing = MutableStateFlow(false)

    val state: StateFlow<SearchUiState> =
        combine(
            refreshing,
            getSearchQueryUseCase(query)
        ) {
            refreshing,
            searchResults, ->
            SearchUiState.Ready(
                searchResults.songs,
                searchResults.artists,
                searchResults.albums,
                searchResults.composers,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SearchUiState.Loading
        )

    fun toggleSearch() {
        viewModelScope.launch {
            //this.getSearchQueryUseCase()
        }
    }

    fun sendQuery(query: String): Flow<SearchQueryFilterResult> {
        return getSearchQueryUseCase(query)
    }
}*/

/*
sealed interface SearchUiState{
    data object Loading: SearchUiState
    data class Ready(
        val songsResult: List<SongInfo> = emptyList(),
        val artistsResult: List<ArtistInfo> = emptyList(),
        val albumsResult: List<AlbumInfo> = emptyList(),
        val composersResult: List<ComposerInfo> = emptyList(),
    ) : SearchUiState
}
*/
