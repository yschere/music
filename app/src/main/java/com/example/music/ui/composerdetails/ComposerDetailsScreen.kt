package com.example.music.ui.composerdetails

import android.util.Log
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.R
import com.example.music.designsys.theme.Keyline1
import com.example.music.domain.testing.PreviewComposers
import com.example.music.domain.testing.getSongsByComposer
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.SongInfo
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.ItemCountAndSortSelectButtons
import com.example.music.ui.shared.Loading
import com.example.music.ui.shared.MiniPlayer
import com.example.music.ui.shared.PlayShuffleButtons
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SongListItem
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.ui.tooling.SystemLightPreview
import com.example.music.util.BackNavBtn
import com.example.music.util.MoreOptionsBtn
import com.example.music.util.SearchBtn
import com.example.music.util.fullWidthItem

private const val TAG = "Composer Details Screen"

/**
 * Stateful version of Composer Details Screen
 */
@Composable
fun ComposerDetailsScreen(
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateBack: () -> Unit = {},
    viewModel: ComposerDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        Text(text = uiState.errorMessage!!)
        ComposerDetailsError(onRetry = viewModel::refresh)
    }
    Surface {
        if (uiState.isReady) {
            ComposerDetailsScreen(
                composer = uiState.composer,
                songs = uiState.songs,
                navigateToPlayer = navigateToPlayer,
                navigateToSearch = navigateToSearch,
                navigateBack = navigateBack,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            ComposerDetailsLoadingScreen(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Error Screen
 */
@Composable
private fun ComposerDetailsError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Error(
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Loading Screen with circular progress indicator in center
 */
@Composable
private fun ComposerDetailsLoadingScreen(
    modifier: Modifier = Modifier
) { Loading(modifier = modifier) }

/**
 * Stateless Composable for Composer Details Screen
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComposerDetailsScreen(
    composer: ComposerInfo,
    songs: List<SongInfo>,
    navigateToPlayer: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateBack: () -> Unit,
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
                ComposerDetailsTopAppBar(
                    navigateToSearch = navigateToSearch,
                    navigateBack = navigateBack,
                )
            },
            bottomBar = {},
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background)
        ) { contentPadding ->
            ComposerDetailsContent(
                composer = composer,
                songs = songs,
                navigateToPlayer = navigateToPlayer,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}

/**
 * Composable for Composer Details Screen's Top App Bar.
 */
@Composable
fun ComposerDetailsTopAppBar(
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
        BackNavBtn(onClick = navigateBack)

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        // Search btn
        SearchBtn(onClick = navigateToSearch)

        // Composer More Options btn
        MoreOptionsBtn(onClick = {})
    }
}

/**
 * Composable for Composer Details Screen's Content.
 */
@Composable
fun ComposerDetailsContent(
    composer: ComposerInfo,
    songs: List<SongInfo>,
    navigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        // Header Item
        fullWidthItem {
            ComposerDetailsHeaderItem(
                composer = composer,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        // Song List
        if (songs.isNotEmpty()) {
            fullWidthItem {
                ItemCountAndSortSelectButtons(
                    id = R.plurals.composers,
                    itemCount = songs.size,
                    onSortClick = {},
                    onSelectClick = {}
                )
            }

            fullWidthItem {
                PlayShuffleButtons(
                    onPlayClick = {
                        Log.i(TAG, "Play Songs btn clicked")
                        //onComposerAction(ComposerAction.PlaySongs(songs))
                        //navigateToPlayer()
                    },
                    onShuffleClick = {
                        Log.i(TAG, "Shuffle Songs btn clicked")
                        //onComposerAction(ComposerAction.ShuffleSongs(songs))
                        //navigateToPlayer()
                    },
                )
            }

            items(
                items = songs
            ) { song ->
                SongListItem(
                    song = song,
                    onClick = {
                        Log.i(TAG, "Song clicked: ${song.title}")
                        //onComposerAction(ComposerAction.PlaySong(song))
                        //navigateToPlayer()
                    },
                    onMoreOptionsClick = {
                        Log.i(TAG, "Song More Option clicked: ${song.title}")
                        //onComposerAction(ComposerAction.SongMoreOptionClicked(song))
                        //showBottomSheet = true
                        //showSongMoreOptions = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isListEditable = false,
                    showAlbumTitle = true,
                    showArtistName = true,
                    showAlbumImage = true,
                    showTrackNumber = false,
                )
            }
        }
    }
}

@Composable
fun ComposerDetailsHeaderItem(
    composer: ComposerInfo,
    modifier: Modifier = Modifier
) {
    //FUTURE THOUGHT: choose if want 1 image or multi image view for composer header
    // and for the 1 image, should it be the 1st album, or an image for externally of the composer?
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
            Text(
                text = composer.name,
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
fun ComposerDetailsHeaderItemPreview() {
    ComposerDetailsHeaderItem(
        composer = PreviewComposers[0],
    )
}

@SystemLightPreview
@SystemDarkPreview
@Composable
fun ComposerDetailsScreenPreview() {
    MusicTheme {
        ComposerDetailsScreen(
            //composer = PreviewComposers[0],
            //songs = getSongsByComposer(291),

            //Paramore
            //composer = PreviewComposers[3],
            //songs = getSongsByComposer(410),

            //Tatsuya Kitani
            composer = PreviewComposers[1],
            songs = getSongsByComposer(PreviewComposers[1].id),

            navigateToPlayer = {},
            navigateToSearch = {},
            navigateBack = {},
        )
    }
}
