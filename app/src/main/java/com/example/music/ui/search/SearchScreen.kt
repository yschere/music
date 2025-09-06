package com.example.music.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.music.R
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.SearchQueryFilterV2
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemLightPreview

/** Changelog:
 * 4/11/2025 - Removed SearchQueryViewModel from this file to become
 * a separate file. Moved both that file and this one to own package
 * under ui as ui.search
 *
 * 4/13/2025 - Finished revisions to screen to support MediaStore
 * querying for songs, artists, albums. Intended to be the screen
 * reachable by tapping on the Search Icon of other screens in app.
 * SearchBar is in the TopAppBar, and the contents of the screen
 * are the returned results of the search query.
 *
 * FixMe: further update so that the returned list items are standardized
 *  to match the rest of the app's list item views
 */

/**
 * Composable for the Search Screen of the app.
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

    Surface {
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
fun SearchScreenReady(
    uiState: SearchUiState,
    fieldState: SearchFieldState,
    queryText: String,

    navigateBack: () -> Unit,
    navigateToPlayer: () -> Unit,
    navigateToArtistDetails: (ArtistInfo) -> Unit,
    navigateToAlbumDetails: (AlbumInfo) -> Unit,
    viewModel: SearchQueryViewModel,
) {
    //val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    //val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)

    ScreenBackground(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background)
        ) { contentPadding ->

            SearchContent(
                navigateBack = navigateBack,
                uiState = uiState,
                searchFieldState = fieldState,
                queryText = queryText,

                // update the viewModel queryText
                onSearchInputChanged = { input -> viewModel.updateQuery(input) },

                // activate? thing to do when user click on field and make it active
                onSearchFieldClicked = { viewModel.changeFieldState() },

                // clear the query text and reset ui state // full screen reset
                onChevronClicked = { viewModel.resetUiState() },

                // clear the query text but still using field
                onClearInputClicked = { viewModel.clearQuery() },

                // send query
                onSendQuery = { viewModel.sendQuery() },

                // actions to do when user taps/clicks on results
                onSongClicked = { item ->
                    viewModel.onPlaySong(item)
                    navigateToPlayer()
                },
                onArtistClicked = { item -> navigateToArtistDetails(item) },
                onAlbumClicked = { item -> navigateToAlbumDetails(item) },
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
    onSearchInputChanged: (String) -> Unit,
    onSearchFieldClicked: () -> Unit,
    onChevronClicked: () -> Unit,
    onClearInputClicked: () -> Unit,
    onSendQuery: () -> Unit,

    onSongClicked: (SongInfo) -> Unit,
    onArtistClicked: (ArtistInfo) -> Unit,
    onAlbumClicked: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
    ) {
        SearchField(
            navigateBack,
            uiState,
            searchFieldState,
            queryText,
            onSearchInputChanged = onSearchInputChanged,
            onSearchFieldClicked = onSearchFieldClicked,
            onChevronClicked = onChevronClicked,
            onClearInputClicked = onClearInputClicked,
            onSendQuery = onSendQuery,
            onSongClicked = onSongClicked,
            onAlbumClicked = onAlbumClicked,
            onArtistClicked = onArtistClicked,
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
    onSearchInputChanged: (String) -> Unit,
    onSearchFieldClicked: () -> Unit,
    onChevronClicked: () -> Unit,
    onClearInputClicked: () -> Unit,
    onSendQuery: () -> Unit,
    onSongClicked: (SongInfo) -> Unit,
    onArtistClicked: (ArtistInfo) -> Unit,
    onAlbumClicked: (AlbumInfo) -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = queryText,
                onQueryChange = { onSearchInputChanged(it) },
                onSearch = {
                    onSendQuery()
                    isExpanded = true
                },
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                enabled = true,
                placeholder = {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.icon_search),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(stringResource(id = R.string.icon_search))
                    }
                },
                leadingIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.icon_back_nav),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                },
                trailingIcon = {
                    IconButton (
                        onClick = onChevronClicked
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                },
                interactionSource = null,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    //focusedTextColor =
                    //unfocusedTextColor =
                )
            )
        },
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        colors = SearchBarDefaults.colors(
            containerColor = Color.Transparent,
            dividerColor = Color.Black,
        ),
        modifier = Modifier
            .semantics { traversalIndex = 0f },
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
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp)
                    )
                }

                is SearchUiState.SearchResultsFound -> {
                    if (queryText != "" && uiState.results.songs.isNotEmpty()) {
                        Text(
                            text = "Songs",
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                        uiState.results.songs.forEach { result ->
                            ListItem(
                                headlineContent = { Text(result.title) },
                                modifier = Modifier
                                    .clickable {
                                        isExpanded = false
                                        onSongClicked(result)
                                    }
                                    .fillMaxWidth()
                            )
                        }
                    } else if (queryText != "" && uiState.results.songs.isEmpty()) {
                        Text(
                            text = "No Songs Found",
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }

                    if (queryText != "" && uiState.results.artists.isNotEmpty()) {
                        Text(
                            text = "Artists",
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                        uiState.results.artists.forEach { result ->
                            ListItem(
                                headlineContent = { Text(result.name) },
                                modifier = Modifier
                                    .clickable {
                                        isExpanded = false
                                        onArtistClicked(result)
                                    }
                                    .fillMaxWidth()
                            )
                        }
                    } else if (queryText != "" && uiState.results.artists.size == 0) {
                        Text(
                            text = "No Artists Found",
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }

                    if (queryText != "" && uiState.results.albums.isNotEmpty()) {
                        Text(
                            text = "Albums",
                            modifier = Modifier.padding(vertical = 4.dp),)
                        uiState.results.albums.forEach { result ->
                            ListItem(
                                headlineContent = { Text(result.title) },
                                modifier = Modifier
                                    .clickable {
                                        isExpanded = false
                                        onAlbumClicked(result)
                                    }
                                    .fillMaxWidth()
                            )
                        }
                    } else if (queryText != "" && uiState.results.albums.size == 0) {
                        Text(
                            text = "No Albums Found",
                            modifier = Modifier.padding(vertical = 4.dp),)
                    }
                }

                else -> {
                    Spacer(modifier = Modifier.fillMaxSize())
                }
            }

            // state logic
            // when ui state is idle, change nothing? keep as is
            // when ui state is loading, have loading bar indicator
            // when ui state is error, show error()
            // when ui state is no results, show no "" found
            // when ui state is results found, show list of results

            // when ui state is idle and field is active, show nothing
            // when ui state is results found, set field to inactive/idle
            // when ui state is loading, set field to idle
            // all other ui state, do nothing to field
        }
    }
}

@Composable
private fun SearchResultsList(
    items: SearchQueryFilterV2,
    onSongClicked: (SongInfo) -> Unit,
    onArtistClicked: (ArtistInfo) -> Unit,
    onAlbumClicked: (AlbumInfo) -> Unit,
) {
    LazyColumn {
        itemsIndexed(items = items.songs) { index, searchResult ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSongClicked.invoke(searchResult) }
            ) {
                Spacer(
                    modifier = Modifier.height(height = if(index == 0) 16.dp else 4.dp)
                )
                Text(
                    text = searchResult.title,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer( modifier = Modifier.height(4.dp) )
                Text(
                    text = searchResult.artistName,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12.sp
                )
                Spacer( modifier = Modifier.height(8.dp) )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(start = 16.dp)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.2f))
                )
                Spacer( modifier = Modifier.height(4.dp) )
            }
        }

        /*itemsIndexed(items = items.artists) { index, searchResult ->
            Column(
                modifier = Modifier.fillMaxWidth().clickable { onArtistClicked.invoke(searchResult) }
            ) {
                Spacer(
                    modifier = Modifier.height(height = if(index == 0) 16.dp else 4.dp)
                )
                Text(
                    text = searchResult.name,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer( modifier = Modifier.height(4.dp) )
                Text(
                    text = "${searchResult.albumCount} albums | ${searchResult.songCount} songs",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12.sp
                )
                Spacer( modifier = Modifier.height(8.dp) )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(start = 16.dp)
                        .background(MaterialTheme.colorScheme.background.copy(alpha=0.2f))
                )
                Spacer( modifier = Modifier.height(4.dp) )
            }
        }*/

        /*itemsIndexed(items = items.albums) { index, searchResult ->
            Column(
                modifier = Modifier.fillMaxWidth().clickable { onAlbumClicked.invoke(searchResult) }
            ) {
                Spacer(
                    modifier = Modifier.height(height = if(index == 0) 16.dp else 4.dp)
                )
                Text(
                    text = searchResult.title,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer( modifier = Modifier.height(4.dp) )
                Text(
                    text = "${searchResult.songCount} songs",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12.sp
                )
                Spacer( modifier = Modifier.height(8.dp) )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(start = 16.dp)
                        .background(MaterialTheme.colorScheme.background.copy(alpha=0.2f))
                )
                Spacer( modifier = Modifier.height(4.dp) )
            }
        }*/
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
