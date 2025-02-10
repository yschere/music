package com.example.music.ui

import android.annotation.SuppressLint
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
 * List of navigation accessible screens for [MusicApp]
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")

    object Library : Screen("library")

    object Player : Screen("player/{$ARG_SONG_ID}") {
        fun createRoute(songId: Long) = "player/$songId"
    }

    object QueueList : Screen("queue")

    /*
    object Artists : Screen("artists")
    object Albums : Screen("albums")
    object Genres : Screen("genres")

    object LibArtists : Screen("library/artists")
    object LibAlbums : Screen("library/albums")
    object LibGenres : Screen("library/genres")

    //not sure if this is gonna be song details or edit tags, maybe make both as separate screen objects
    object SongDetails : Screen("song/{$arg_song_id}") {
        val SONG_ID = "songId"
        fun createRoute(songId: Long) = "song/$songId"
    } // correlates to MusicAppState navController function navigateTo___
    */

    object AlbumDetails : Screen("album/{$ARG_ALBUM_ID}") {
        //val ALBUM_ID = "albumId"
        fun createRoute(albumId: Long) = "album/$albumId"
    }

    object ArtistDetails : Screen("artist/{$ARG_ARTIST_ID}") {
        //val ARTIST_ID = "artistId"
        fun createRoute(artistId: Long) = "artist/$artistId"
    }

    object PlaylistDetails : Screen("playlist/{$ARG_PLAYLIST_ID}") {
        //val PLAYLIST_ID = "playlistId"
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }

    companion object {
        const val ARG_SONG_ID = "songId"
        const val ARG_ALBUM_ID = "albumId"
        const val ARG_ARTIST_ID = "artistId"
        const val ARG_PLAYLIST_ID = "playlistId"
        //const val ARG_GENRE_ID = "genreId"
        //const val ARG_COMPOSER_ID = "composerId"
    }

    /*  multiple screens needed for this
        home screen -> should have context-less navigation
        player screen -> should have context-less navigation
        all playlists screen -> should have context-less navigation
        all artists screen -> should have context-less navigation
        all albums screen -> should have context-less navigation
        all songs screen -> should have context-less navigation
        all genres screen -> should have context-less navigation
        all composers screen -> should have context-less navigation

        selected playlist screen -> needs context of selected item
        selected artist screen -> needs context of selected item
        selected album screen -> needs context of selected item
        selected genre screen -> needs context of selected item
        selected composer screen -> needs context of selected item

        edit song tags screen -> needs context of prev screen
        settings screen -> should have context-less navigation
        item options screen -> needs context of selected item


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

    /* //TODO: maybe this won't be needed if this is a list view that is not a separate navigation screen?
    // same for ArtistsList and GenresList and Playlists
    fun navigateToAlbumsList(from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.Albums.createRoute())
        }
    } */

    fun navigateToHome(from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.Home)
        }
    }

    fun navigateToLibrary(from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.Library)
        }
    }

    fun navigateToPlayer(songId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.Player.createRoute(songId))
        }
    }

    fun navigateToArtistDetails(artistId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.ArtistDetails.createRoute(artistId))
        }
    }

    fun navigateToAlbumDetails(albumId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.AlbumDetails.createRoute(albumId))
        }
    }

    fun navigateToPlaylistDetails(playlistId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.PlaylistDetails.createRoute(playlistId))
        }
    }

    /* fun navigateToGenreDetails(genreId: Long, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.GenreDetails.createRoute(genreId))
        }
    } */

    /* fun navigateToComposerDetails(composerId: Long, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.ComposerDetails.createRoute(composerId))
        }
    } */

    /* //TODO: determine if this is the song data page or the song edit tags page
    // I have a feeling in either case it will still have the same questions
    // as the AlbumsList and ArtistsList screens
    fun navigateToSongDetails(songId: Long, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.SongDetails.createRoute(songId))
        }
    } */

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

    @SuppressLint("ObsoleteSdkInt")
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
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
