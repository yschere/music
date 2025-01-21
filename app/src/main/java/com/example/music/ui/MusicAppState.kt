/*
 * Copyright 2021 The Android Open Source Project
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

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * List of screens for [MusicApp]
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    //object Library : Screen("library")
    object Player : Screen("player/{$arg_song_id}") {
        fun createRoute(songId: Long) = "player/$songId"
    } //TODO: correlates to MusicAppState navController function navigateTo___
    object QueueList : Screen("queue")

    object Artists : Screen("artists")
    object Albums : Screen("albums")
    object Genres : Screen("genres")

    object LibArtists : Screen("library/artists")
    object LibAlbums : Screen("library/albums")
    object LibGenres : Screen("library/genres")

//not sure if this is gonna be song details or edit tags, maybe make both as separate screen objects
//    object SongDetails : Screen("song/{$arg_song_id}") {
//        val SONG_ID = "songId"
//        fun createRoute(songId: Long) = "song/$songId"
//    } // correlates to MusicAppState navController function navigateTo___

    object AlbumDetails : Screen("album/{$arg_album_id}") {
        //val ALBUM_ID = "albumId"
        fun createRoute(albumId: Long) = "album/$albumId"
    } // correlates to MusicAppState navController function navigateTo___

    object ArtistDetails : Screen("artist/{$arg_artist_id}") {
        //val ARTIST_ID = "artistId"
        fun createRoute(artistId: Long) = "artist/$artistId"
    } // correlates to MusicAppState navController function navigateTo___

    companion object {
        //val ARG_PODCAST_URI = "podcastUri"
        //val ARG_EPISODE_URI = "episodeUri"
        val arg_song_id = "songId"
        val arg_album_id = "albumId"
        val arg_artist_id = "artistId"
    }

    /*  multiple screens needed for this
        home screen
        player screen
        all playlists screen
        all artists screen
        all albums screen
        all songs screen
        all genres screen
        all years screen (?)

        selected playlist screen
        selected artist screen
        selected album screen
        selected genre screen
        selected year screen

        edit song tags screen
        edit album tags screen

        options screen


        need to figure out if popups are separate screen or context popup for:
        add to queue(?) multi-select(?)
        add to playlist
        edit playlist
        delete playlist
        filter/sort options
     */
}

@Composable
fun rememberMusicAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    MusicAppState(navController, context)
}

class MusicAppState(
    val navController: NavHostController,
    private val context: Context
) {
    var isOnline by mutableStateOf(checkIfOnline())
        private set

    fun refreshOnline() {
        isOnline = checkIfOnline()
    }

    //TODO: maybe this won't be needed if this is a list view that is not a separate navigation screen?
    // same for ArtistsList and GenresList and Playlists
//    fun navigateToAlbumsList(from: NavBackStackEntry) {
//        if (from.lifecycleIsResumed()) {
//            navController.navigate(Screen.Albums.createRoute())
//        }
//    }

    fun navigateToPlayer(songId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.Player.createRoute(songId))
        }
    }

    fun navigateToArtistDetails(artistId: Long, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.ArtistDetails.createRoute(artistId))
        }
    }

    fun navigateToAlbumDetails(albumId: Long, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.AlbumDetails.createRoute(albumId))
        }
    }

    //TODO: determine if this is the song data page or the song edit tags page
    // I have a feeling in either case it will still have the same questions
    // as the AlbumsList and ArtistsList screens
//    fun navigateToSongDetails(songId: Long, from: NavBackStackEntry) {
//        if (from.lifecycleIsResumed()) {
//            navController.navigate(Screen.SongDetails.createRoute(songId))
//        }
//    }

    //TODO: determine if this is a separate navigable screen or is in similar
    // vein to AlbumsList and ArtistsList
    fun showQueueList(from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.QueueList)
        }
    } // not same naming convention, but is the same principle as the navigateTo__ functions

    fun navigateBack() {
        navController.popBackStack()
    }

    @Suppress("DEPRECATION")
    private fun checkIfOnline(): Boolean {
        val cm = getSystemService(context, ConnectivityManager::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            cm?.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
