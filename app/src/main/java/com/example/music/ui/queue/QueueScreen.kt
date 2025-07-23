package com.example.music.ui.queue

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.DisplayFeature
import com.example.music.R
import com.example.music.data.database.model.Song
import com.example.music.domain.model.SongInfo
import com.example.music.domain.player.model.PlayerSong
import com.example.music.ui.shared.ScreenBackground
import com.example.music.util.fullWidthItem

/**
 * StateFUL version of queue screen
 */
@Composable
fun QueueScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navigateBack: () -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    viewModel: QueueViewModel = hiltViewModel()
) {
    //val uiState = viewModel.state.collectAsStateWithLifecycle() //with lifecycle means it contains its context too

    //if (uiState.errorMessage != null) {
    //    Text(text = uiState.errorMessage!!)
    //    QueueScreenError(onRetry = viewModel::refresh)
    //}

    Surface {
        QueueScreen(
            windowSizeClass = windowSizeClass,
            //isLoading = uiState.isLoading,
            displayFeatures = displayFeatures,
            //onQueueAction = viewModel::onQueueAction,
            navigateBack = navigateBack,
            navigateToPlayer = navigateToPlayer,
            //viewModel = viewModel,
            //modifier = Modifier.fillMaxSize()
        )

    }
}

/**
 * Error Screen
 */
@Composable
private fun QueueScreenError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QueueScreen(
    windowSizeClass: WindowSizeClass,
    isLoading: Boolean,
    displayFeatures: List<DisplayFeature>,
    //onQueueAction: (QueueAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    modifier: Modifier = Modifier,
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
                QueueTopAppBar(navigateBack)
                if (isLoading) {
                    LinearProgressIndicator(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }},
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            //modifier = modifier.fillMaxSize().systemBarsPadding(),
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background) //selects the appropriate color to be the content color for the container using background color
            //contentColor = MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
        ) { contentPadding ->
            //Column {  }
            QueueContent(
                navigateToPlayer = navigateToPlayer,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}

@Composable
private fun QueueTopAppBar(
    navigateBack: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
    ) {

        //back button
        IconButton(onClick = navigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.icon_back_nav),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        // more options button
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
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
 * Composable for Queue Screen's Content
 */
@Composable
private fun QueueContent(
    //queueList probably needs to get passed in here
    //songs: List<PlayerSong>, //or queue: Queue
    //onQueueAction: (QueueAction) -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier.padding(horizontal = 12.dp),
    ) {
        fullWidthItem {
            Text(
                text = "Player Queue",
                //textAlign = Alignment.CenterHorizontally,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        /*items(songs){}*/
    }
}