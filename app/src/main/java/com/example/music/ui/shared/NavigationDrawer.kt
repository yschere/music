package com.example.music.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.designsys.theme.NAV_DRAWER_CONTENT_PADDING
import com.example.music.designsys.theme.NAV_DRAWER_ITEMS_HEIGHT
import com.example.music.designsys.theme.NAV_DRAWER_MARGINS
import com.example.music.designsys.theme.NAV_DRAWER_WIDTH
import com.example.music.designsys.theme.SMALL_PADDING
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompLightPreview
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.util.quantityStringResource
import com.example.music.util.textHeightPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Composable for Navigation Drawer, used on Home Screen,
 * Library Screen, and Settings Screen.
 */
@Composable
fun NavDrawer(
    selectedLabel: String,
    totals: List<Int>,
    navigateToHome: () -> Unit,
    navigateToLibrary: () -> Unit,
    navigateToSettings: () -> Unit,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.primaryContainer,
                drawerContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    .fillMaxHeight()
                    .width(NAV_DRAWER_WIDTH)
            ) {
                Text(
                    text = "Musicality",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.fillMaxWidth().padding(vertical = NAV_DRAWER_MARGINS)
                )

                // Home Screen navigation item
                NavigationDrawerItem(
                    label = { Text(text = "Home Page") },
                    selected = (selectedLabel == "Home Page"),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = MaterialTheme.colorScheme.inverseSurface,
                    ),
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigateToHome()
                    },
                    modifier = Modifier.height(NAV_DRAWER_ITEMS_HEIGHT)
                )

                // Library Screen navigation item
                NavigationDrawerItem(
                    label = { Text(text = "Library") },
                    selected = (selectedLabel == "Library"),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = MaterialTheme.colorScheme.inverseSurface,
                    ),
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigateToLibrary()
                    },
                    modifier = Modifier.height(NAV_DRAWER_ITEMS_HEIGHT)
                )

                // Settings Screen navigation item
                NavigationDrawerItem(
                    label = { Text(text = "Settings") },
                    selected = (selectedLabel == "Settings"),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unselectedContainerColor = Color.Transparent,
                        unselectedTextColor = MaterialTheme.colorScheme.inverseSurface,
                    ),
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigateToSettings()
                    },
                    modifier = Modifier.height(NAV_DRAWER_ITEMS_HEIGHT)
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(horizontal = NAV_DRAWER_CONTENT_PADDING)
                )

                if(totals.isNotEmpty()) {
                    Text(
                        text = "In Your Library:",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = NAV_DRAWER_MARGINS)
                            .padding(top = NAV_DRAWER_CONTENT_PADDING, bottom = SMALL_PADDING)
                    )
                    Column (Modifier.padding(horizontal = NAV_DRAWER_MARGINS + NAV_DRAWER_CONTENT_PADDING)) {
                        Text(
                            text = quantityStringResource(id= R.plurals.songs, totals[0], totals[0]),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.textHeightPadding()
                        )
                        Text(
                            text = quantityStringResource(id= R.plurals.artists, totals[1], totals[1]),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.textHeightPadding()
                        )
                        Text(
                            text = quantityStringResource(id= R.plurals.albums, totals[2], totals[2]),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.textHeightPadding()
                        )
                        Text(
                            text = quantityStringResource(id= R.plurals.genres, totals[3], totals[3]),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.textHeightPadding()
                        )
                        Text(
                            text = quantityStringResource(id= R.plurals.playlists, totals[4], totals[4]),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.textHeightPadding()
                        )
                    }
                }
            }
        },
        scrimColor = MaterialTheme.colorScheme.surfaceBright.copy(alpha=0.7f)
    ){
        content()
    }
}

@CompLightPreview
@SystemDarkPreview
@Composable
fun PreviewNavDrawer() {
    MusicTheme {
        NavDrawer(
            selectedLabel = "Home Page",
            totals = listOf(6373, 990, 1427, 35, 9),
            navigateToHome = {},
            navigateToLibrary = {},
            navigateToSettings = {},
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            content = {},
        )
    }
}