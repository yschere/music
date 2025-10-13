package com.example.music.ui.settings

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.TopAppBarExpandedHeight
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.DisplayFeature
import com.example.music.R
import com.example.music.data.repository.ShuffleType
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.LIST_ITEM_HEIGHT
import com.example.music.designsys.theme.MODAL_CONTENT_PADDING
import com.example.music.designsys.theme.ROW_ITEM_HEIGHT
import com.example.music.designsys.theme.SMALL_PADDING
import com.example.music.ui.shared.ActionItem
import com.example.music.ui.shared.Actions
import com.example.music.ui.shared.Error
import com.example.music.ui.shared.NavDrawer
import com.example.music.ui.shared.ScreenBackground
import com.example.music.ui.shared.SettingsBottomModal
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.ui.shared.NavDrawerBtn
import com.example.music.util.frontTextPadding
import com.example.music.util.modalHeaderPadding
import com.example.music.util.screenMargin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "Settings Screen"

/**
 * Stateful Composable for the Settings Screen
 */
@Composable
fun SettingsScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    Log.i(TAG, "Settings Screen START")
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Surface(color = Color.Transparent) {
        if (uiState.errorMessage != null) {
            Log.e(TAG, "${uiState.errorMessage}")
            SettingsScreenError(onRetry = viewModel::refresh)
        }

        SettingsScreen(
            windowSizeClass = windowSizeClass,
            isLoading = uiState.isLoading,
            displayFeatures = displayFeatures,
            totals = uiState.totals,
            onSettingsAction = viewModel::onSettingsAction,
            navigateToHome = navigateToHome,
            navigateToLibrary = navigateToLibrary,
            navigateToSettings = navigateToSettings,
            modifier = Modifier.fillMaxSize(),
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
) { Error(onRetry = onRetry, modifier = modifier) }

/**
 * Stateless version of Settings Screen and its properties needed to render the
 * components of the page.
 */
@Composable
private fun SettingsScreen(
    windowSizeClass: WindowSizeClass,
    isLoading: Boolean,
    displayFeatures: List<DisplayFeature>,
    totals: List<Int>,

    onSettingsAction: (SettingsAction) -> Unit,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Log.i(TAG, "Settings Screen START")

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarText = stringResource(id = R.string.sbt_song_added_to_your_queue)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    NavDrawer(
        selectedLabel = "Settings",
        totals = totals,
        navigateToHome = navigateToHome,
        navigateToLibrary = navigateToLibrary,
        navigateToSettings = navigateToSettings,
        drawerState = drawerState,
        coroutineScope = coroutineScope,
    ) {
        ScreenBackground(modifier = modifier) {
            Scaffold(
                topBar = {
                    SettingsTopAppBar(
                        onNavigationIconClick = {
                            coroutineScope.launch {
                                drawerState.apply { if (isClosed) open() else close() }
                            }
                        },
                    )
                    if (isLoading) {
                        LinearProgressIndicator(
                            Modifier.fillMaxWidth().screenMargin()
                        )
                    }
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                containerColor = Color.Transparent,
                contentColor = contentColorFor(MaterialTheme.colorScheme.background)
            ) { contentPadding ->
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

/**
 * Composable for Settings Screen's Top App Bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(
    onNavigationIconClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth().padding(CONTENT_PADDING)
            )
        },
        navigationIcon = { NavDrawerBtn(onClick = onNavigationIconClick) },
        actions = {},
        expandedHeight = TopAppBarExpandedHeight,
        windowInsets = TopAppBarDefaults.windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        ),
        scrollBehavior = pinnedScrollBehavior(),
    )
}

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
    Log.i(TAG, "SettingsContent START")

    val sheetState = rememberModalBottomSheetState(true)
    var showShuffleSheet by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }

    // Main Content on Settings screen
    Column(modifier = modifier.fillMaxSize()) {
        /* // ****** Settings Options ******
        // 1 set shuffle type
        // 2 set app theme
        // 3 import playlist
        // 4 refresh library
        // 5 export playlist?
        // 6 set color scheme
        // 7 audio effects / player visual effects
        // 8 show music on lock screen
        // 9 permissions
        // 10 equalizer */

        // Select Shuffle Type
        if(isShuffleSettingEnabled) {
            SettingRowItemWithModal(
                "Set Shuffle Type",
                "Set the type of queue shuffle when shuffle is on",
                //"Set ONCE for shuffle to occur on start of queue.
                // Set ONLOOP for shuffle to occur every restart of queue.",
                onClick = { showShuffleSheet = true }
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

        // Set Light/Dark Mode
        if (isThemeModeEnabled) {
            SettingRowItemWithModal(
                "Theme Mode",
                "Set light mode, dark mode, or system default",
                onClick = {
                    showThemeSheet = true
                },
            )
        }

        // Import Playlist from device
        if (isImportPlaylistEnabled) {
            SettingRowItem(
                "Import Playlist",
                "Tap to search device for playlists to import",
                onClick = {
                    onSettingsAction(SettingsAction.ImportPlaylist)
                },
            )
        }

        //Refresh Library
        if (isRefreshLibrarySettingEnabled) {
            SettingRowItem(
                "Refresh App Library",
                "Tap to update your music library",
                onClick = {
                    onSettingsAction(SettingsAction.RefreshLibrary)
                },
            )
        }
    }

    if (showShuffleSheet) {
        SettingsBottomModal(
            onDismissRequest = { showShuffleSheet = false },
            sheetState = sheetState,
            onClose = {
                coroutineScope.launch {
                    Log.i(TAG, "Hide sheet state")
                    sheetState.hide()
                }.invokeOnCompletion {
                    Log.i(TAG, "set showShuffleSheet to FALSE")
                    if(!sheetState.isVisible) showShuffleSheet = false
                }
            },
            onApply = {
                coroutineScope.launch {
                    Log.i(TAG, "Hide sheet state")
                    sheetState.hide()
                }.invokeOnCompletion {
                    Log.i(TAG, "set showShuffleSheet to FALSE")
                    if(!sheetState.isVisible) showShuffleSheet = false
                }
            },
        ) {
            ShuffleModalContent(
                onShuffleApply = {
                    Log.i(TAG, "Shuffle selected: ${it.name}")
                    onSettingsAction( SettingsAction.ShuffleTypeSelected( it ) )
                }
            )
        }
    }

    if (showThemeSheet) {
        SettingsBottomModal(
            onDismissRequest = { showThemeSheet = false },
            sheetState = sheetState,
            onClose = {
                coroutineScope.launch {
                    Log.i(TAG, "Hide sheet state")
                    sheetState.hide()
                }.invokeOnCompletion {
                    Log.i(TAG, "set showThemeSheet to FALSE")
                    if(!sheetState.isVisible) showThemeSheet = false
                }
            },
            onApply = {
                coroutineScope.launch {
                    Log.i(TAG, "Apply clicked: does nothing right now")
                    sheetState.hide()
                }.invokeOnCompletion {
                    Log.i(TAG, "set showThemeSheet to FALSE")
                    if(!sheetState.isVisible) showThemeSheet = false
                }
            },
        ) {
            ThemeModalContent(
                onThemeApply = {
                    Log.i(TAG, "Theme selected: $it")
                    onSettingsAction(SettingsAction.ThemeModeSelected(it))
                }
            )
        }
    }
}

@Composable
private fun SettingRowItem(
    title: String,
    subtitle: String = "",
    onClick: () -> Unit = {},
) {
    Surface(
        color = Color.Transparent,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(ROW_ITEM_HEIGHT)
                .screenMargin()
        ) {
            Column {
                Text(
                    text = title,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                )
                Text(
                    text = subtitle,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun SettingRowItemWithModal(
    title: String,
    subtitle: String = "",
    onClick: () -> Unit = {}
) {
    Surface(
        color = Color.Transparent,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .height(ROW_ITEM_HEIGHT)
                .screenMargin()
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
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = title
            )
        }
    }
}

@Composable
private fun ShuffleModalContent(
    onShuffleApply: (ShuffleType) -> Unit,
) {
    Column(modifier = Modifier) {
        Text(
            text = "Set Shuffle Type:",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
                .modalHeaderPadding()
        )
        ShuffleRadioGroupSet(
            radioOptions = listOf(ShuffleType.ONCE, ShuffleType.ON_LOOP),
            onSettingsAction = onShuffleApply,
        )
    }
}

@Composable
private fun ThemeModalContent(
    onThemeApply: (String) -> Unit,
) {
    val themeOptions = arrayListOf(
        Actions.ThemeDefault,
        Actions.ThemeLight,
        Actions.ThemeDark,
    )
    Column(modifier = Modifier) {
        Text(
            text = "Set Theme Mode:",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
                .modalHeaderPadding()
        )
        ThemeRadioGroupSet(
            radioOptions = themeOptions,
            onSettingsAction = onThemeApply
        )
    }
}

@Composable
private fun ShuffleRadioGroupSet(
    radioOptions: List<ShuffleType>,
    onSettingsAction: (ShuffleType) -> Unit,
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    // Note that Modifier. selectableGroup() is essential to ensure correct accessibility behavior
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.selectableGroup()
    ) {
        radioOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .height(LIST_ITEM_HEIGHT)
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = {
                            onOptionSelected(option)
                            onSettingsAction(option)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = MODAL_CONTENT_PADDING)
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    modifier = Modifier.padding(SMALL_PADDING),
                    onClick = null, // null recommended for accessibility with screenreaders
                )
                Text(
                    text = option.name,
                    color =
                        if (option == selectedOption) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.frontTextPadding(),
                )
            }
        }
    }
}

@Composable
private fun ThemeRadioGroupSet(
    radioOptions: List<ActionItem>,
    onSettingsAction: (String) -> Unit,
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    // Note that Modifier. selectableGroup() is essential to ensure correct accessibility behavior
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.selectableGroup()
    ) {
        radioOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .height(LIST_ITEM_HEIGHT)
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = {
                            onOptionSelected(option)
                            onSettingsAction(option.name)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = MODAL_CONTENT_PADDING)
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    modifier = Modifier.padding(SMALL_PADDING),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.contentDescription.toString(),
                    tint =
                        if (option == selectedOption) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(SMALL_PADDING),
                )
                Text(
                    text = option.name,
                    color =
                        if (option == selectedOption) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.frontTextPadding(),
                )
            }
        }
    }
}

//private val CompactWindowSizeClass = WindowSizeClass.compute(360f, 780f)

//@SystemLightPreview
//@SystemDarkPreview
@Composable
private fun PreviewSettings() {
    MusicTheme {
        BoxWithConstraints {
            SettingsScreen(
                windowSizeClass = WindowSizeClass.compute(maxWidth.value, maxHeight.value),
                isLoading = false,
                displayFeatures = emptyList(),
                totals = listOf(6373, 990, 1427, 35, 9),
                onSettingsAction = {},
                navigateToHome = {},
                navigateToLibrary = {},
                navigateToSettings = {},
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SystemDarkPreview
@Composable
private fun SettingsModalPreview() {
    MusicTheme {
        SettingsBottomModal(
            onDismissRequest = {},
            sheetState = SheetState(
                initialValue = SheetValue.Expanded,
                skipPartiallyExpanded = true,
                density = Density(1f,1f)
            ),
            onClose = {},
            onApply = {}
        ) {
//            ShuffleModalContent {  }
            ThemeModalContent {  }
        }
    }
}