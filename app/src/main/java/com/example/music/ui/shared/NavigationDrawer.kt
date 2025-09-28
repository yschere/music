package com.example.music.ui.shared

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompLightPreview
import com.example.music.ui.tooling.SystemDarkPreview
import com.example.music.util.quantityStringResource
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
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars).fillMaxHeight().width(200.dp)
            ) {
                Text(
                    text = "Musicality",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
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
                    }
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
                )

                HorizontalDivider(
                    color = Color.LightGray,
                    modifier = Modifier.padding(2.dp)
                )

                if(totals.isNotEmpty())
                {
                    Text(
                        text = "In Your Library:",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                    Text(
                        //text = quantityStringResource(id= R.plurals.songs, totals[0].second, totals[0]),
                        text = quantityStringResource(id= R.plurals.songs, totals[0], totals[0]),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                    )
                    Text(
                        //text = quantityStringResource(id= R.plurals.artists, totals[1].second, totals[1]),
                        text = quantityStringResource(id= R.plurals.artists, totals[1], totals[1]),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                    )
                    Text(
                        //text = quantityStringResource(id= R.plurals.albums, totals[2].second, totals[2]),
                        text = quantityStringResource(id= R.plurals.albums, totals[2], totals[2]),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                    )
                    Text(
                        //text = quantityStringResource(id= R.plurals.genres, totals[3].second, totals[3]),
                        //text = quantityStringResource(id= R.plurals.playlists, totals[3], totals[3]),
                        text = quantityStringResource(id= R.plurals.genres, totals[3], totals[3]),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                    )
                }
            }
        },
        //gesturesEnabled = false,
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
            totals = listOf(6922,298,30,9),
            navigateToHome = {},
            navigateToLibrary = {},
            navigateToSettings = {},
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            content = {},
        )
    }
}