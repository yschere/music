package com.example.music.ui

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
import com.example.music.ui.settings.SettingsScreen

@Composable
fun MusicApp(
    displayFeatures: List<DisplayFeature>,
    appState: MusicAppState = rememberMusicAppState()
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
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

                    //would it make sense to include all the variations of navigable screens that could come from here?
                    navigateToLibrary = { appState.navigateToLibrary(backStackEntry) },

                    navigateToPlaylistDetails = { playlist ->
                        appState.navigateToPlaylistDetails(playlist.id, backStackEntry)
                    },

                    navigateToPlayer = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },

                    //TODO: PlayerSong support
                    navigateToPlayerSong = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },

                    navigateToSettings = { appState.navigateToSettings(backStackEntry) },
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
                    //navigateToSettings = { appState.navigateToSettings(backStackEntry) },
                    onShowQueuePress = { appState.showQueueList(backStackEntry) } //navigation to queue list screen (not sure if this will stay as screen or pop up view)
                )
            }

            //Queuelist Screen Navigation Router

            //Library Screen Navigation Router
            composable(Screen.Library.route) { backStackEntry ->
                LibraryScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass, //needed for screens meant to use full screen
                    navigateBack = appState::navigateBack, //navigation back button
                    navigateToHome = { appState.navigateToHome(backStackEntry) },
                    navigateToLibrary = { appState.navigateToLibrary(backStackEntry) },
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
                    navigateToPlayer =  { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },
                    //TODO: PlayerSong support
                    navigateToPlayerSong = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },
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
                    navigateToPlayer =  { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },
                    //TODO: PlayerSong support
                    navigateToPlayerSong = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },
                    navigateToSettings = { appState.navigateToSettings(backStackEntry) },
                )
            }

            // Selected Album Details Navigation Router
            composable(Screen.AlbumDetails.route) { backStackEntry ->
                AlbumDetailsScreen(
                    //windowSizeClass = adaptiveInfo.windowSizeClass, //needed for screens meant to use full screen

                    // want ability to return to previous screen
                    navigateBack = appState::navigateBack,

                    // want ability to navigate to song player when song selected to play -- dependent on song list being on screen
                    navigateToPlayer = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },

                    navigateToPlayerSong = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },

                    //dependent on context, ie if showing in pane or as own screen
                    showBackButton = true
                )
            }

            // Selected Artist Details Navigation Router
            composable(Screen.ArtistDetails.route) { backStackEntry ->
                ArtistDetailsScreen(
                    //keeping for now in case window class size becomes relevant
                    //windowSizeClass = adaptiveInfo.windowSizeClass,

                    // want ability to return to previous screen
                    navigateBack = appState::navigateBack,

                    // want ability to navigate to selected album details
                    navigateToAlbumDetails = { album ->
                        appState.navigateToAlbumDetails(album.id, backStackEntry)
                    },

                    // want ability to navigate to song player when song selected to play -- dependent on song list being on screen
                    navigateToPlayer = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },

                    // want ability to navigate to song player when song selected to play -- dependent on song list being on screen
                    navigateToPlayerSong = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },

                    //dependent on context, ie if showing in pane or as own screen
                    showBackButton = true,
                )
            }

            // Selected Composer Details Navigation Router
            composable(Screen.ComposerDetails.route) { backStackEntry ->
                ComposerDetailsScreen(
                    //keeping for now in case window class size becomes relevant
                    //windowSizeClass = adaptiveInfo.windowSizeClass,

                    // want ability to return to previous screen
                    navigateBack = appState::navigateBack,

                    // want ability to navigate to song player when song selected to play -- dependent on song list being on screen
                    navigateToPlayer = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },

                    // want ability to navigate to song player when song selected to play -- dependent on song list being on screen
                    navigateToPlayerSong = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },
                )
            }

            // Selected Genre Details Navigation Router
            composable(Screen.GenreDetails.route) { backStackEntry ->
                GenreDetailsScreen(
                    //keeping for now in case window class size becomes relevant
                    //windowSizeClass = adaptiveInfo.windowSizeClass,

                    // want ability to return to previous screen
                    navigateBack = appState::navigateBack,

                    // want ability to navigate to selected album details
//                    navigateToAlbumDetails = { album ->
//                        appState.navigateToAlbumDetails(album.id, backStackEntry)
//                    },

                    // want ability to navigate to song player when song selected to play -- dependent on song list being on screen
                    navigateToPlayer = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },

                    // want ability to navigate to song player when song selected to play -- dependent on song list being on screen
                    navigateToPlayerSong = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },
                )
            }

            // Selected Playlist Details Navigation Router
            composable(Screen.PlaylistDetails.route) { backStackEntry ->
                PlaylistDetailsScreen(
                    //keeping for now in case window class size becomes relevant
                    //windowSizeClass = adaptiveInfo.windowSizeClass,

                    // want ability to return to previous screen
                    navigateBack = appState::navigateBack,

                    // want ability to navigate to song player when song selected to play -- dependent on song list being on screen
                    navigateToPlayer = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },

                    //TODO: PlayerSong support
                    navigateToPlayerSong = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
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