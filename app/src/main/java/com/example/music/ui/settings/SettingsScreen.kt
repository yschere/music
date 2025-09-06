package com.example.music.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.DisplayFeature
import com.example.music.R
import com.example.music.data.repository.ShuffleType
import com.example.music.designsys.theme.MusicShapes
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.PreviewArtists
import com.example.music.domain.testing.PreviewPlaylists
import com.example.music.domain.testing.PreviewSongs
import com.example.music.domain.model.SongInfo
import com.example.music.ui.shared.NavDrawer
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "Settings Screen"

/** Changelog:
 *
 * 7/22-23/2025 - Deleted SongPlayer from domain layer.
 */

/**
 * Stateful Composable for the Settings Screen
 */
@Composable
fun SettingsScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToPlayer: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    if (uiState.errorMessage != null) {
        Text(text = uiState.errorMessage!!)
        SettingsScreenError(onRetry = viewModel::refresh)
    }
    Surface {
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
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Error Screen
 */
@Composable
private fun SettingsScreenError(
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
 * Stateless Composable for Settings Screen and its properties needed to render the
 * components of the page.
 */
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
    navigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    NavDrawer(
        "Settings",
        totals,
        navigateToHome,
        navigateToLibrary,
        navigateToSettings,
        drawerState,
        coroutineScope,
    ) {
        ScreenBackground(
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Scaffold(
                //contentWindowInsets = WindowInsets.systemBarsIgnoringVisibility,
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                bottomBar = {
                    /* //should show BottomBarPlayer here if a queue session is running or service is running
                    BottomBarPlayer(
                        song = PreviewSongs[5],
                        navigateToPlayer = { navigateToPlayer(PreviewSongs[5]) },
                    )*/
                },
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                containerColor = Color.Transparent,
                contentColor = contentColorFor(MaterialTheme.colorScheme.background) //selects the appropriate color to be the content color for the container using background color
                //contentColor = MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
            ) { contentPadding ->
                Column {
                    SettingsTopAppBar(
                        navigateBack = navigateBack,
                    )
                    if (isLoading) {
                        LinearProgressIndicator(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }

                    SettingsContent(
                        coroutineScope = coroutineScope,
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
    navigateBack: () -> Unit, //use this to capture navDrawer open/close action
    //modifier: Modifier = Modifier,
) {
    //logger.info { "Settings App Bar function start" }
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
    }
}

//support class for setting up ThemeMode options with icons
data class OptionItem(val name: String, val icon: ImageVector, val contentDescription: String)//, val action: () -> Unit)

/**
 * Composable for Settings Screen's Content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    coroutineScope: CoroutineScope,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    onSettingsAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,

    //flags for individual settings
    isShuffleSettingEnabled: Boolean = true,
    isThemeModeEnabled: Boolean = true,
    isImportPlaylistEnabled: Boolean = true,
    isRefreshLibrarySettingEnabled: Boolean = true,
) {
    val sheetState = rememberModalBottomSheetState(true,)
    var showShuffleSheet by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }

    // Main Content on Settings screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        // ****** Settings Header ******
        Text(
            text = "Settings",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // ****** Settings Options ******
        // 1 set shuffle type
        // 2 set app theme
        // 3 import playlist
        // 4 refresh library
        // 5 export playlist?
        // 6 set color scheme
        // 7 audio effects / player visual effects
        // 8 show music on lock screen
        // 9 permissions
        // 10 equalizer


        // Select Shuffle Type
        if(isShuffleSettingEnabled) {
            SetShuffleTypeSetting(
                "Set Shuffle Type",
                "Set the type of queue shuffle when shuffle is on",//"Set ONCE for shuffle to occur on start of queue. Set ONLOOP for shuffle to occur every restart of queue.",
                onClick = { showShuffleSheet = true }
            )
        }
        HorizontalDivider(
            color = Color.LightGray
        )

        // Set Light/Dark Mode
        if(isThemeModeEnabled) {
            AppThemeSetting (
                "Theme Mode",
                "Set light mode, dark mode, or system default",
                onClick = { showThemeSheet = true },
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
                onSettingsAction = { action ->
                    /*if (action is SettingsAction.ImportPlaylist) {

                        coroutineScope.launch{
                            //not sure what to put here
                        }
                    }*/
                    onSettingsAction(action)
                },
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
                onSettingsAction = { action ->
                    /*if (action is SettingsAction.RefreshLibrary) {

                        coroutineScope.launch{
                            //not sure what to put here
                        }
                    }*/
                    onSettingsAction(action)
                },
            )
        }
    }

    if (showShuffleSheet) {
        ModalBottomSheet(
            onDismissRequest = {showShuffleSheet = false},
            sheetState = sheetState, // = rememberModalBottomSheetState(skipPartiallyExpanded = false,),
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background,
            scrimColor = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),// = MaterialTheme.colorScheme.scrim.copy(alpha=0.2f),
            properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
        ) {
            ShuffleRadioGroupSet(
                listOf(ShuffleType.ONCE, ShuffleType.ON_LOOP),
                onSettingsAction = {
                    onSettingsAction(
                        SettingsAction.ShuffleTypeSelected(
                            it
                        )
                    )
                }
            )
            Row {
                //cancel/exit btn
                Button(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                            showShuffleSheet = false
                        }
                    },
                    colors = buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,//.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    shape = MusicShapes.small,
                    modifier = Modifier//.fillMaxWidth()
                        .padding(10.dp).weight(0.5f)
                ) {
                    Text("CANCEL")
                }

                //apply btn
                Button(
                    onClick = {
                        //showBottomSheet = false
                        coroutineScope.launch {
                            sheetState.hide()
                            showShuffleSheet = false
                        }
                    },
                    colors = buttonColors(
                        //containerColor = MaterialTheme.colorScheme.primaryContainer,//.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.background,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    shape = MusicShapes.small,
                    modifier = Modifier//.fillMaxWidth()
                        .padding(10.dp).weight(0.5f)
                ) {
                    Text("APPLY")
                }
            }
        }
    }

    if (showThemeSheet) {
        ModalBottomSheet(
            onDismissRequest = {showThemeSheet = false},
            sheetState = sheetState, // = rememberModalBottomSheetState(skipPartiallyExpanded = false,),
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background,
            scrimColor = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f),
            properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
        ) {
            val themeOptions = listOf<OptionItem>(
                OptionItem("System default", Icons.Filled.Settings, "System default theme option"),
                OptionItem("Light", Icons.Filled.LightMode, "Light Mode theme option"),
                OptionItem("Dark", Icons.Filled.DarkMode, "Dark Mode theme option")
            )
            ThemeRadioGroupSet(
                themeOptions,
                //listOf("System default", "Light", "Dark"),
                onSettingsAction = {
                    onSettingsAction(
                        SettingsAction.ThemeModeSelected(
                            it
                        )
                    )
                }
            )
            Row {
                //cancel/exit btn
                Button(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                            showThemeSheet = false
                        }
                    },
                    colors = buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    shape = MusicShapes.small,
                    modifier = Modifier
                        .padding(10.dp).weight(0.5f)
                ) {
                    Text("CANCEL")
                }

                //apply btn
                Button(
                    onClick = {
                        //showBottomSheet = false
                        coroutineScope.launch {
                            sheetState.hide()
                            showThemeSheet = false
                        }
                    },
                    colors = buttonColors(
                        //containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.background,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    shape = MusicShapes.small,
                    modifier = Modifier
                        .padding(10.dp).weight(0.5f)
                ) {
                    Text("APPLY")
                }
            }
        }
    }
}

@Composable
fun SetShuffleTypeSetting(
    title: String,
    subtitle: String = "",
    onClick: () -> Unit,
) {
    Surface(
        color = Color.Transparent,
        onClick = onClick,// { showShuffleSheet = true } // transition screen to show list of options,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = title,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Text(
                    text = subtitle,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                //imageVector = Icons.Filled.ChevronRight,
                //tint = MaterialTheme.colorScheme.surfaceTint,
                contentDescription = title
            )

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
fun AppThemeSetting(
    title: String,
    subtitle: String = "",
    onClick: () -> Unit,
) {
    Surface(
        color = Color.Transparent,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = title,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier//.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Text(
                    text = subtitle,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                //imageVector = Icons.Filled.ChevronRight,
                //tint = MaterialTheme.colorScheme.surfaceTint,
                contentDescription = title
            )
        }
    }
}

@Composable
fun ShuffleRadioGroupSet(
    radioOptions: List<ShuffleType>,
    onSettingsAction: (ShuffleType) -> Unit,
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    // Note that Modifier. selectableGroup() is essential to ensure correct accessibility behavior
    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier.fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)
                            onSettingsAction(text)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                Text(
                    text = text.name,
                    color =
                        if (text == selectedOption)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ThemeRadioGroupSet(
    radioOptions: List<OptionItem>,
    onSettingsAction: (String) -> Unit,
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    // Note that Modifier. selectableGroup() is essential to ensure correct accessibility behavior
    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier.fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)
                            onSettingsAction(text.name)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                Icon(
                    imageVector = text.icon,
                    contentDescription = text.contentDescription,
                    tint =
                        if (text == selectedOption)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = text.name,
                    color =
                        if (text == selectedOption)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = title,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = subtitle,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = title,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = subtitle,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

//private val CompactWindowSizeClass = WindowSizeClass.compute(360f, 780f)

//@SystemLightPreview
@SystemDarkPreview
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
            )
        }
    }
}
