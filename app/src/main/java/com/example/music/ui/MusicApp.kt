package com.example.music.ui

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.window.layout.DisplayFeature
import com.example.music.R
import com.example.music.ui.albumdetails.AlbumDetailsScreen
import com.example.music.ui.artistdetails.ArtistDetailsScreen
import com.example.music.ui.composerdetails.ComposerDetailsScreen
import com.example.music.ui.genredetails.GenreDetailsScreen
import com.example.music.ui.home.MainScreen
import com.example.music.ui.library.LibraryScreen
import com.example.music.ui.player.PlayerScreen
import com.example.music.ui.playlistdetails.PlaylistDetailsScreen
import com.example.music.ui.search.SearchScreen
import com.example.music.ui.settings.SettingsScreen

/** Changelog:
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong. For MusicApp, this means
 * removing the navigateToPlayerSong(PlayerSong) navigation link
 *
 * 7/22-23/2025 - Removed PlayerSong completely
 */

private const val TAG = "Music App Navigation"

/**
 * Composable function for Music App state control and navigation
 */
@Composable
fun MusicApp(
    displayFeatures: List<DisplayFeature>,
    appState: MusicAppState = rememberMusicAppState()
) {
    Log.i(TAG, "navigation composable start")
    val adaptiveInfo = currentWindowAdaptiveInfo()
    /*val sizeClassText =
        "${adaptiveInfo.windowSizeClass.windowWidthSizeClass}\n" +
        "${adaptiveInfo.windowSizeClass.windowHeightSizeClass}"*/
    if (appState.isOnline) {
        NavHost(
            navController = appState.navController,
            startDestination = Screen.Home.route
        ) {
            // Home Navigation Router
            composable(Screen.Home.route) { backStackEntry ->
                MainScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass, //needed for screens meant to use full screen
                    //because windowSizeClass is getting from adaptiveInfo as well, there isn't a need to pass in displayFeatures (I"M ASSUMING THIS IS THE CASE, NOT CLEAR YET)
                    // could also be because the way MusicApp sets the starting destination as Home, the initiating scaffold directives are used to determine the rest of the features and bounds on the device
                    // seems like that could be the case because the navigator in HomeScreenReady gets set to rememberSupportingPaneScaffoldNavigator with scaffoldDirective that starts the calculation process for the features and bounds

                    navigateToHome = { appState.navigateToHome(backStackEntry) },
                    navigateToLibrary = { appState.navigateToLibrary(backStackEntry) },
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                    navigateToSettings = { appState.navigateToSettings(backStackEntry) },
                    navigateToAlbumDetails = { albumId: Long ->
                        appState.navigateToAlbumDetails(albumId, backStackEntry)
                    },
                    navigateToArtistDetails = { artistId: Long ->
                        appState.navigateToArtistDetails(artistId, backStackEntry)
                    },
                    navigateToPlaylistDetails = { playlist ->
                        appState.navigateToPlaylistDetails(playlist.id, backStackEntry)
                    },
                )
            }

            //Player Screen Navigation Router
            composable(Screen.Player.route) { backStackEntry ->
                PlayerScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass, //needed for screens meant to use full screen
                    displayFeatures = displayFeatures, //used to determine physical properties of display device to accommodate view accordingly
                    navigateBack = appState::navigateBack, //navigation back button
                    navigateToHome = { appState.navigateToHome(backStackEntry) },
                    navigateToLibrary = { appState.navigateToLibrary(backStackEntry) },
                )
            }

            //Library Screen Navigation Router
            composable(Screen.Library.route) { backStackEntry ->
                LibraryScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass, //needed for screens meant to use full screen
                    navigateBack = appState::navigateBack, //navigation back button
                    navigateToHome = { appState.navigateToHome(backStackEntry) },
                    navigateToLibrary = { appState.navigateToLibrary(backStackEntry) },
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                    navigateToSettings = { appState.navigateToSettings(backStackEntry) },
                    navigateToAlbumDetails = { album ->
                        appState.navigateToAlbumDetails(album.id, backStackEntry)
                    },
                    navigateToArtistDetails = { artist ->
                        appState.navigateToArtistDetails(artist.id, backStackEntry)
                    },
                    navigateToGenreDetails = { genre ->
                        appState.navigateToGenreDetails(genre.id, backStackEntry)
                    },
                    navigateToComposerDetails = { composer ->
                        appState.navigateToComposerDetails(composer.id, backStackEntry)
                    },
                    navigateToPlaylistDetails = { playlist ->
                        appState.navigateToPlaylistDetails(playlist.id, backStackEntry)
                    },
                )
            }

            //Search Screen Navigation Router
            composable(Screen.Search.route) { backStackEntry ->
                SearchScreen(
                    navigateBack = appState::navigateBack,
                    navigateToAlbumDetails = { album ->
                        appState.navigateToAlbumDetails(album.id, backStackEntry)
                    },
                    navigateToArtistDetails = { artist ->
                        appState.navigateToArtistDetails(artist.id, backStackEntry)
                    },
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                )
            }

            //Settings Screen Navigation Router
            composable(Screen.Settings.route) { backStackEntry ->
                SettingsScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass, //needed for screens meant to use full screen
                    displayFeatures = displayFeatures, //used to determine physical properties of display device to accommodate view accordingly
                    navigateBack = appState::navigateBack, //navigation back button
                    navigateToHome = { appState.navigateToHome(backStackEntry) },
                    navigateToLibrary = { appState.navigateToLibrary(backStackEntry) },
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSettings = { appState.navigateToSettings(backStackEntry) },
                )
            }

            // Selected Album Details Navigation Router
            composable(Screen.AlbumDetails.route) { backStackEntry ->
                AlbumDetailsScreen(
                    //windowSizeClass = adaptiveInfo.windowSizeClass, //needed for screens meant to use full screen
                    navigateBack = appState::navigateBack,
                    navigateToArtistDetails = { artistId ->
                        appState.navigateToArtistDetails(artistId, backStackEntry)
                    },
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                )
            }

            // Selected Artist Details Navigation Router
            composable(Screen.ArtistDetails.route) { backStackEntry ->
                ArtistDetailsScreen(
                    //keeping for now in case window class size becomes relevant
                    //windowSizeClass = adaptiveInfo.windowSizeClass,
                    navigateBack = appState::navigateBack,
                    navigateToAlbumDetails = { albumId ->
                        appState.navigateToAlbumDetails(albumId, backStackEntry)
                    },
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                )
            }

            // Selected Composer Details Navigation Router
            composable(Screen.ComposerDetails.route) { backStackEntry ->
                ComposerDetailsScreen(
                    //keeping for now in case window class size becomes relevant
                    //windowSizeClass = adaptiveInfo.windowSizeClass,

                    navigateBack = appState::navigateBack,
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                )
            }

            // Selected Genre Details Navigation Router
            composable(Screen.GenreDetails.route) { backStackEntry ->
                GenreDetailsScreen(
                    //keeping for now in case window class size becomes relevant
                    //windowSizeClass = adaptiveInfo.windowSizeClass,

                    navigateBack = appState::navigateBack,
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                )
            }

            // Selected Playlist Details Navigation Router
            composable(Screen.PlaylistDetails.route) { backStackEntry ->
                PlaylistDetailsScreen(
                    //keeping for now in case window class size becomes relevant
                    //windowSizeClass = adaptiveInfo.windowSizeClass,

                    navigateBack = appState::navigateBack,
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                )
            }
        }
    } else {
        OfflineDialog { appState.refreshOnline() }
    }
}

@Composable
fun OfflineDialog(onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.connection_error_title)) },
        text = { Text(text = stringResource(R.string.connection_error_message)) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.retry_label))
            }
        }
    )
}