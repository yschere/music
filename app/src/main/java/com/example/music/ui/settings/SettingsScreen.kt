package com.example.music.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.DisplayFeature
import com.example.music.R
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.model.SongInfo
import com.example.music.player.model.PlayerSong
import com.example.music.ui.shared.NavDrawer
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.theme.MusicTheme
import com.example.music.util.isCompact
import kotlinx.coroutines.launch

/**
 * Stateful version of Settings Screen
 */
@Composable
fun SettingsScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Box {
        SettingsScreen(
            windowSizeClass = windowSizeClass,
            isLoading = uiState.isLoading,
            displayFeatures = displayFeatures,
            totals = uiState.totals,
            onSettingsAction = viewModel::onSettingsAction,
            navigateBack = navigateBack,
            navigateToHome = navigateToHome,
            navigateToLibrary = navigateToLibrary,
            navigateToSettings = navigateToSettings,
            navigateToPlayer = navigateToPlayer,
            navigateToPlayerSong = navigateToPlayerSong,
            modifier = Modifier.fillMaxSize()
        )

        if (uiState.errorMessage != null) {
            SettingsScreenError(onRetry = viewModel::refresh)
        }
    }
}

@Composable
private fun SettingsScreenError(onRetry: () -> Unit, modifier: Modifier = Modifier) {

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
private fun SettingsScreen(
    windowSizeClass: WindowSizeClass,
    isLoading: Boolean,
    displayFeatures: List<DisplayFeature>,
    totals: List<Int>,
    onSettingsAction: (SettingsAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToPlayer: (SongInfo) -> Unit,
    navigateToPlayerSong: (PlayerSong) -> Unit,
    modifier: Modifier = Modifier,
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.song_added_to_your_queue)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ScreenBackground(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavDrawer(
            "Settings",
            totals,
            navigateToHome,
            navigateToLibrary,
            navigateToSettings,
            drawerState,
            coroutineScope,
        ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                modifier = modifier.fillMaxSize().systemBarsPadding(),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) { contentPadding ->
                Column {
                    SettingsTopAppBar(
                        isExpanded = windowSizeClass.isCompact,
                        isSearchOn = false,
                        isLoading = isLoading,
                        onNavigationIconClick = {
                            coroutineScope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }
                    )

                    SettingsContent(
                        windowSizeClass = windowSizeClass,
                        displayFeatures = displayFeatures,
                        //settings data store
                        onSettingsAction = onSettingsAction,
                        modifier = modifier.padding(contentPadding),
                    )
                }
            }
        }
    }
}

/**
 * Composable for Settings Screen's Top App Bar.
 */
@Composable
private fun SettingsTopAppBar(
    isSearchOn: Boolean,
    isExpanded: Boolean,
    isLoading: Boolean,
    onNavigationIconClick: () -> Unit, //use this to capture navDrawer open/close action
    modifier: Modifier = Modifier,
) {
    //logger.info { "Settings App Bar function start" }

    var queryText by remember {
        mutableStateOf("")
    }
    Row(
        //horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
        //.background(Color.Transparent)
    ) {
        //if (!isSearchOn) {

        //not search time
        IconButton(onClick = onNavigationIconClick) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = stringResource(R.string.cd_more)
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
    if (isLoading) {
        LinearProgressIndicator(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        )
    }
}

/**
 * Composable for Settings Screen's Content.
 */
@Composable
private fun SettingsContent(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    onSettingsAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,

    //flags for individual settings
    isShuffleSettingEnabled: Boolean = true,
    isImportPlaylistEnabled: Boolean = true,
    isRefreshLibrarySettingEnabled: Boolean = true,
) {
    // Main Content on Settings screen
    Column(
        modifier = modifier
            .fillMaxSize()
//            .verticalGradientScrim(
//                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
//                //color = MaterialTheme.colorScheme.secondary,
//                startYPercentage = 1f,
//                endYPercentage = 0f
//            )
            .systemBarsPadding()//why is this getting called again when it was passed into the column around PlayerContentRegular?
            .padding(horizontal = 8.dp)
//        horizontalAlignment = Alignment.Start,
//        modifier = Modifier.fillMaxSize()//.padding(horizontal = 4.dp)
    ) {
        // Settings Content here

        // Settings header
        Text(
            text = "Settings",
            //textAlign = Alignment.CenterHorizontally,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Settings Options

        // Select Shuffle Type
        if(isShuffleSettingEnabled) {
            SetShuffleTypeSetting(
                "Set Shuffle Type",
                "Set the type of queue shuffle when shuffle is on",//"Set ONCE for shuffle to occur on start of queue. Set ONLOOP for shuffle to occur every restart of queue.",
                onSettingsAction,
            )
        }

        HorizontalDivider(
            color = Color.LightGray
        )

        // Import Playlist from device
        if(isImportPlaylistEnabled) {
            ImportPlaylistSetting (
                "Import Playlist",
                "Tap to search device for playlists to import",
                onSettingsAction,
            )
        }

        HorizontalDivider(
            color = Color.LightGray
        )

        //Refresh Library
        if(isRefreshLibrarySettingEnabled) {
            RefreshLibrarySetting(
                "Refresh App Library",
                "Tap to update your music library",
                onSettingsAction,
            )
        }
    }
}

@Composable
fun SetShuffleTypeSetting(
    title: String,
    subtitle: String = "",
    onSettingsAction: (SettingsAction) -> Unit,
) {
    Surface(
        color = Color.Transparent,
        onClick = { /* transition screen to show list of options */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = title,
                    textAlign = TextAlign.Left,
                    //color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Text(
                    text = subtitle,
                    textAlign = TextAlign.Left,
                    //color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Spacer(Modifier.weight(1f))
//            IconButton(onClick = {/* will be same as surface onClick*/ }) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                tint = MaterialTheme.colorScheme.surfaceTint,
                contentDescription = "Set Shuffle Type Option"
            )
//            }
            /*
            if I want this to be done in the google suggested way,
            would need this to be a radio button selection on a new page/fragment
            so it would need to be supported with SupportScaffoldPane
            title: Set Shuffle Type
            subtitle: When Shuffle is turned on
            radio option 1: Shuffle Once
                option subtitle: Set ONCE for shuffle to occur on start of queue, order will not change after queue restarts.
            radio option 2: Shuffle On Loop
                option subtitle: Set ONLOOP for shuffle to occur every restart of queue.
         */
        }
    }
}

@Composable
fun ImportPlaylistSetting(
    title: String,
    subtitle: String = "",
    onSettingsAction: (SettingsAction) -> Unit,
) {
    Surface(
        color = Color.Transparent,
        onClick = { onSettingsAction(SettingsAction.ImportPlaylist) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = title,
                textAlign = TextAlign.Left,
                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = subtitle,
                textAlign = TextAlign.Left,
                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
fun RefreshLibrarySetting(
    title: String,
    subtitle: String = "",
    onSettingsAction: (SettingsAction) -> Unit,
) {
    Surface(
        color = Color.Transparent,
        onClick = { onSettingsAction(SettingsAction.RefreshLibrary) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = title,
                textAlign = TextAlign.Left,
                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = subtitle,
                textAlign = TextAlign.Left,
                //color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

//private val CompactWindowSizeClass = WindowSizeClass.compute(360f, 780f)

@Preview
@Composable
private fun PreviewSettings() {
    MusicTheme {
        BoxWithConstraints {
            SettingsScreen(
                //uiState = {},
                windowSizeClass = WindowSizeClass.compute(maxWidth.value, maxHeight.value),
                isLoading = false,
                displayFeatures = emptyList(),
                totals = listOf(
                    PreviewSongs.size,
                    PreviewArtists.size,
                    PreviewAlbums.size,
                    PreviewPlaylists.size),
                onSettingsAction = {},
                navigateBack = {},
                navigateToHome = {},
                navigateToLibrary = {},
                navigateToSettings = {},
                navigateToPlayer = {},
                navigateToPlayerSong = {},
            )
        }
    }
}
