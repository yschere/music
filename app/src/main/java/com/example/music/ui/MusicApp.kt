package com.example.music.ui

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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

private const val TAG = "Music App Nav Routes"

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
                    navigateToAlbumDetails = { albumId ->
                        Log.i(TAG, "id: $albumId")
                        appState.navigateToAlbumDetails(albumId, backStackEntry)
                    },
                    navigateToArtistDetails = { artistId ->
                        Log.i(TAG, "id: $artistId")
                        appState.navigateToArtistDetails(artistId, backStackEntry)
                    },
                    navigateToPlaylistDetails = { playlist ->
                        appState.navigateToPlaylistDetails(playlist.id, backStackEntry)
                    },
                )
            }

            // Library Screen Navigation Router
            composable(Screen.Library.route) { backStackEntry ->
                LibraryScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass,
                    navigateBack = appState::navigateBack,
                    navigateToHome = { appState.navigateToHome(backStackEntry) },
                    navigateToLibrary = { appState.navigateToLibrary(backStackEntry) },
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                    navigateToSettings = { appState.navigateToSettings(backStackEntry) },
                    navigateToAlbumDetails = { albumId ->
                        Log.i(TAG, "id: $albumId")
                        appState.navigateToAlbumDetails(albumId, backStackEntry)
                    },
                    navigateToArtistDetails = { artistId ->
                        Log.i(TAG, "id: $artistId")
                        appState.navigateToArtistDetails(artistId, backStackEntry)
                    },
                    navigateToGenreDetails = { genreId ->
                        Log.i(TAG, "id: $genreId")
                        appState.navigateToGenreDetails(genreId, backStackEntry)
                    },
                    navigateToComposerDetails = { composer ->
                        appState.navigateToComposerDetails(composer.id, backStackEntry)
                    },
                    navigateToPlaylistDetails = { playlist ->
                        appState.navigateToPlaylistDetails(playlist.id, backStackEntry)
                    },
                )
            }

            // Player Screen Navigation Router
            composable(Screen.Player.route) { backStackEntry ->
                PlayerScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass,
                    displayFeatures = displayFeatures,
                    navigateBack = appState::navigateBack,
                    navigateToAlbumDetails = { albumId ->
                        Log.i(TAG, "id: $albumId")
                        appState.navigateToAlbumDetails(albumId, backStackEntry)
                    },
                    navigateToArtistDetails = { artistId ->
                        Log.i(TAG, "id: $artistId")
                        appState.navigateToArtistDetails(artistId, backStackEntry)
                    }
                )
            }

            // Search Screen Navigation Router
            composable(Screen.Search.route) { backStackEntry ->
                SearchScreen(
                    navigateBack = appState::navigateBack,
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToAlbumDetails = { albumId ->
                        Log.i(TAG, "id: $albumId")
                        appState.navigateToAlbumDetails(albumId, backStackEntry)
                    },
                    navigateToArtistDetails = { artistId ->
                        Log.i(TAG, "id: $artistId")
                        appState.navigateToArtistDetails(artistId, backStackEntry)
                    },
                )
            }

            // Settings Screen Navigation Router
            composable(Screen.Settings.route) { backStackEntry ->
                SettingsScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass,
                    displayFeatures = displayFeatures,
                    navigateToHome = { appState.navigateToHome(backStackEntry) },
                    navigateToLibrary = { appState.navigateToLibrary(backStackEntry) },
                    navigateToSettings = { appState.navigateToSettings(backStackEntry) },
                )
            }

            // Selected Album Details Navigation Router
            composable(Screen.AlbumDetails.route) { backStackEntry ->
                AlbumDetailsScreen(
                    navigateBack = appState::navigateBack,
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                    navigateToArtistDetails = { artistId ->
                        Log.i(TAG, "id: $artistId")
                        appState.navigateToArtistDetails(artistId, backStackEntry)
                    },
                )
            }

            // Selected Artist Details Navigation Router
            composable(Screen.ArtistDetails.route) { backStackEntry ->
                ArtistDetailsScreen(
                    navigateBack = appState::navigateBack,
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                    navigateToAlbumDetails = { albumId ->
                        Log.i(TAG, "id: $albumId")
                        appState.navigateToAlbumDetails(albumId, backStackEntry)
                    },
                )
            }

            // Selected Composer Details Navigation Router
            composable(Screen.ComposerDetails.route) { backStackEntry ->
                ComposerDetailsScreen(
                    navigateBack = appState::navigateBack,
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                    navigateToAlbumDetails = { albumId ->
                        Log.i(TAG, "id: $albumId")
                        appState.navigateToAlbumDetails(albumId, backStackEntry)
                    },
                    navigateToArtistDetails = { artistId ->
                        Log.i(TAG, "id: $artistId")
                        appState.navigateToArtistDetails(artistId, backStackEntry)
                    }
                )
            }

            // Selected Genre Details Navigation Router
            composable(Screen.GenreDetails.route) { backStackEntry ->
                GenreDetailsScreen(
                    navigateBack = appState::navigateBack,
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                    navigateToAlbumDetails = { albumId ->
                        Log.i(TAG, "id: $albumId")
                        appState.navigateToAlbumDetails(albumId, backStackEntry)
                    },
                    navigateToArtistDetails = { artistId ->
                        Log.i(TAG, "id: $artistId")
                        appState.navigateToArtistDetails(artistId, backStackEntry)
                    }
                )
            }

            // Selected Playlist Details Navigation Router
            composable(Screen.PlaylistDetails.route) { backStackEntry ->
                PlaylistDetailsScreen(
                    navigateBack = appState::navigateBack,
                    navigateToPlayer = { appState.navigateToPlayer(backStackEntry) },
                    navigateToSearch = { appState.navigateToSearch(backStackEntry) },
                    navigateToAlbumDetails = { albumId ->
                        Log.i(TAG, "id: $albumId")
                        appState.navigateToAlbumDetails(albumId, backStackEntry)
                    },
                    navigateToArtistDetails = { artistId ->
                        Log.i(TAG, "id: $artistId")
                        appState.navigateToArtistDetails(artistId, backStackEntry)
                    },
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