package com.example.music.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.music.R
import com.example.music.domain.usecases.SearchQueryUseCase
import com.example.music.domain.model.AlbumInfo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.SearchQueryFilterResult
import com.example.music.domain.player.model.PlayerSong
import com.example.music.ui.library.artist.ArtistListItem
import com.example.music.ui.library.composer.ComposerListItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    viewModel: SearchQueryViewModel,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    Surface(Modifier.fillMaxSize()) {
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue) //TODO: update if need to
        ScreenBackground( modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars) ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                containerColor = Color.Transparent,
                contentColor = contentColorFor(MaterialTheme.colorScheme.background) //selects the appropriate color to be the content color for the container using background color
                //contentColor = MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
            ) { contentPadding ->
                Column {
                    SearchTopAppBar(
                        //uiState = uiState,
                        toggleSearch = viewModel::toggleSearch,
                        sendQuery = viewModel::sendQuery,
                        coroutineScope = coroutineScope,
                        isSearchOn = true,
                        isExpanded = false,
                        navigateBack = { },
                        modifier = Modifier.padding(contentPadding),
                    )
                }
            }
        }
    }
}


@Composable
fun SearchQuery(
    searchQueryFilterResult: SearchQueryFilterResult,

    ) {
    LazyColumn {
        items(searchQueryFilterResult.pSongs) { song ->
            SongListItem(
                song,
                {},
                false,
                true,
                true,
                true,
                false,
            )
        }
        //HorizontalDivider()
        items(searchQueryFilterResult.artists) { artist ->
            ArtistListItem(
                artist = artist,
                {},
            )
        }

        items(searchQueryFilterResult.albums) { album ->
            AlbumListItem(
                album = album,
                {},
                false,
            )
        }

        items(searchQueryFilterResult.composers) { composer ->
            ComposerListItem(
                composer = composer,
                {},
            )
        }
    }
}

/**
 * Composable for Home Screen's Top App Bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopAppBar(
    //getSearchQueryUseCase: SearchQueryUseCase,
    toggleSearch: () -> Unit,
    sendQuery: (String) -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    isSearchOn: Boolean,
    isExpanded: Boolean,
    navigateBack: () -> Unit, //use this to capture navDrawer open/close action
    modifier: Modifier = Modifier,
) {
    //logger.info { "Home App Bar function start" }
    var queryText by remember {
        mutableStateOf("")
    }
    var searchResults: SearchQueryFilterResult = SearchQueryFilterResult()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
            // search time
            //back button
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.icon_back_nav),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            //right align objects after this space
            Spacer(Modifier.weight(1f))

            DockedSearchBar( //TODO: determine if this can be a shared item & if this can have visibility functionality
                inputField = {
                    SearchBarDefaults.InputField(
                        query = queryText,
                        onQueryChange = { queryText = it },
                        onSearch = {
                            coroutineScope.launch {
                                val results = sendQuery(queryText)

                            }
                        },
                        expanded = true,
                        onExpandedChange = {},
                        enabled = true,
                        placeholder = {
                            Text(stringResource(id = R.string.icon_search))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,//Icons.Default.Search,
                                contentDescription = stringResource(R.string.icon_back_nav)//null
                            )
                        },
                        /*trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = stringResource(R.string.cd_account)
                            )
                        },*/
                        interactionSource = null,
                        modifier = if (isExpanded) Modifier.fillMaxWidth() else Modifier
                    )
                },
                expanded = false,
                onExpandedChange = {}
            ) {
                SearchQuery(searchResults)


            }
    }
}

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
                searchResults.pSongs,
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
}

sealed interface SearchUiState{
    data object Loading: SearchUiState
    data class Ready(
        val songsResult: List<PlayerSong> = emptyList(),
        val artistsResult: List<ArtistInfo> = emptyList(),
        val albumsResult: List<AlbumInfo> = emptyList(),
        val composersResult: List<ComposerInfo> = emptyList(),
    ) : SearchUiState
}
