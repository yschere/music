/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.music.ui

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.window.layout.DisplayFeature
import com.example.music.ui.album.AlbumDetailsScreen
import com.example.music.ui.artist.ArtistDetailsScreen
import com.example.music.ui.home.MainScreen
import com.example.music.ui.player.PlayerScreen

//@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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

                    //navigateToLibrary = appState.navigateToLibrary(backStackEntry),
                    //would it make sense to include all the variations of navigable screens that could come from here?
                    navigateToPlayer = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    }
                )
            }

            //Player Screen Navigation Router
            composable(Screen.Player.route) { backStackEntry ->
                PlayerScreen(
                    windowSizeClass = adaptiveInfo.windowSizeClass, //needed for screens meant to use full screen
                    displayFeatures = displayFeatures, //used to determine physical properties of display device to accommodate view accordingly
                    onBackPress = appState::navigateBack, //navigation back button
                    onShowQueuePress = {appState.showQueueList(backStackEntry)} //navigation to queue list screen (not sure if this will stay as screen or pop up view)
                    //TODO: update this after create function for show queue list
                )
            }

            //how does navigation to a portion of screen component work?
            // is it the same as fragment navigation?
            // should I make all the general page views be like this too?
            // ie genres, albums, artists be a subsection of the home page like
            // library and discover?
//            composable(Screen.Library.route) {
//
//            }

            /*
                this is for Albums screen when navigating from the Library !! Unless there's an undiscovered reason,
                this cannot be reused for when navigating from the genre or artist screen, since those can have list of albums

                ----could a recycler view work on an entire screen? so that on library tab selection, it can determine what needs
                to be included per tab----
             */
            // General Albums List Navigation Router
//            composable(Screen.Albums.route) { backStackEntry ->
//
//                // want ability to navigate to selected album details
//                navigateToAlbumDetails = { album ->
//                    appState.navigateToAlbumDetails(album.id, backStackEntry)
//                },
//
//                // want ability to return to previous screen
//                navigateBack = appState::navigateBack,
//
//                //should not be a thing, but cannot decide if want back btn or side Nav menu btn
//                showBackButton = true,
//            }


            /*
            // General Artists List Navigation Router
            composable(Screen.Artists.route) { backStackEntry ->
                navigateToArtistDetails = { artist ->
                    appState.navigateToArtistDetails(artist.id, backStackEntry)
                },
            }
             */

            // Selected Album Details Navigation Router
            composable(Screen.AlbumDetails.route) { backStackEntry ->
                AlbumDetailsScreen(
                    //keeping for now in case window class size becomes relevant
                    //windowSizeClass = adaptiveInfo.windowSizeClass,// = adaptiveInfo.windowSizeClass,

                    // want ability to navigate to song player when song selected to play -- dependent on song list being on screen
                    navigateToPlayer = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },

                    // want ability to return to previous screen
                    navigateBack = appState::navigateBack,

                    //should not be a thing, but cannot decide if want back btn or side Nav menu btn
                    showBackButton = true,
                )
            }

            // Selected Artist Details Navigation Router
            composable(Screen.ArtistDetails.route) { backStackEntry ->
                ArtistDetailsScreen(
//                    windowSizeClass = adaptiveInfo.windowSizeClass,// = adaptiveInfo.windowSizeClass,
//                    navigateToArtistDetails = { artist ->
//                        appState.navigateToArtistDetails(artist.id)
//                    },

                    // want ability to navigate to selected album details
                    navigateToAlbumDetails = { album ->
                        appState.navigateToAlbumDetails(album.id, backStackEntry)
                    },

                    // want ability to navigate to song player when song selected to play -- dependent on song list being on screen
                    navigateToPlayer = { song ->
                        appState.navigateToPlayer(song.id, backStackEntry)
                    },

                    // want ability to return to previous screen
                    navigateBack = appState::navigateBack,

                    //should not be a thing, but cannot decide if want back btn or side Nav menu btn
                    showBackButton = true,
                )
            }
        }
    } else {
        //OfflineDialog { appState.refreshOnline() }
    }
}

//@Composable
//fun OfflineDialog(onRetry: () -> Unit) {
//    AlertDialog(
//        onDismissRequest = {},
//        title = { Text(text = stringResource(R.string.connection_error_title)) },
//        text = { Text(text = stringResource(R.string.connection_error_message)) },
//        confirmButton = {
//            TextButton(onClick = onRetry) {
//                Text(stringResource(R.string.retry_label))
//            }
//        }
//    )
//}
