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
import androidx.navigation.navOptions
import com.example.music.R
import com.example.music.util.logger

/**
 * List of navigation accessible screens for [MusicApp]
 */
sealed class Screen(val route: String) {
    object Home : Screen("home") {
        fun createRoute() = "home"
    }

    object Library : Screen("library") {
        fun createRoute() = "library"
    }

    object Player : Screen("player/{$ARG_SONG_ID}") {
        fun createRoute(songId: Long) = "player/$songId"
    }

    object QueueList : Screen("queue") {
        fun createRoute() = "queue"
    }

    object Settings : Screen("settings") {
        fun createRoute() = "settings"
    }

    /* //not sure if this is gonna be song details or edit tags, maybe make both as separate screen objects
    object SongDetails : Screen("song/{$arg_song_id}") {
        val SONG_ID = "songId"
        fun createRoute(songId: Long) = "song/$songId"
    } // correlates to MusicAppState navController function navigateTo___
    */

    object AlbumDetails : Screen("album/{$ARG_ALBUM_ID}") {
        fun createRoute(albumId: Long) = "album/$albumId"
    }

    object ArtistDetails : Screen("artist/{$ARG_ARTIST_ID}") {
        fun createRoute(artistId: Long) = "artist/$artistId"
    }

    object PlaylistDetails : Screen("playlist/{$ARG_PLAYLIST_ID}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }

    object GenreDetails : Screen("genre/{$ARG_GENRE_ID}") {
        fun createRoute(genreId: Long) = "genre/$genreId"
    }

    object ComposerDetails : Screen("composer/{$ARG_COMPOSER_ID}") {
        fun createRoute(composerId: Long) = "composer/$composerId"
    }

    companion object {
        const val ARG_SONG_ID = "songId"
        const val ARG_ALBUM_ID = "albumId"
        const val ARG_ARTIST_ID = "artistId"
        const val ARG_PLAYLIST_ID = "playlistId"
        const val ARG_GENRE_ID = "genreId"
        const val ARG_COMPOSER_ID = "composerId"
    }

    /*  multiple screens needed for this
        home screen -> should have context-less navigation
        player screen -> should have context-less navigation
        all playlists screen -> should have context-less navigation (accessed thru library)
        all artists screen -> should have context-less navigation (accessed thru library)
        all albums screen -> should have context-less navigation (accessed thru library)
        all songs screen -> should have context-less navigation (accessed thru library)
        all genres screen -> should have context-less navigation (accessed thru library)
        all composers screen -> should have context-less navigation (accessed thru library)

        selected playlist screen -> needs context of selected item
        selected artist screen -> needs context of selected item
        selected album screen -> needs context of selected item
        selected genre screen -> needs context of selected item
        selected composer screen -> needs context of selected item

        edit song tags screen -> needs context of selected item
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

    fun navigateToHome(from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            logger.info { "\n\n\n\n\n***************** SWITCHING TO HOME VIEW *****************\n\n" }
            navController.navigate(Screen.Home.createRoute())
        }
    }

    fun navigateToLibrary(from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            logger.info { "\n\n\n\n\n***************** SWITCHING TO LIBRARY VIEW *****************\n\n" }
            navController.navigate(Screen.Library.createRoute())
        }
    }

    fun navigateToPlayer(songId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            logger.info { "\n\n\n\n\n***************** SWITCHING TO PLAYER VIEW *****************\n\n" }
            navController.navigate(Screen.Player.createRoute(songId),
                navOptions {
                    anim {
                        enter = R.anim.fade_in
                        exit = R.anim.fade_out
                    }
                })
        }
    }

    fun navigateToSettings(from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            logger.info { "\n\n\n\n\n***************** SWITCHING TO SETTINGS VIEW *****************\n\n" }
            navController.navigate(Screen.Settings.createRoute())
        }
    }

    fun navigateToAlbumDetails(albumId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            logger.info { "\n\n\n\n\n***************** SWITCHING TO ALBUM DETAILS VIEW *****************" }
            navController.navigate(Screen.AlbumDetails.createRoute(albumId))
        }
    }

    fun navigateToArtistDetails(artistId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            logger.info { "\n\n\n\n\n***************** SWITCHING TO ARTIST DETAILS VIEW *****************" }
            navController.navigate(Screen.ArtistDetails.createRoute(artistId))
        }
    }

    fun navigateToComposerDetails(composerId: Long, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            logger.info { "\n\n\n\n\n***************** SWITCHING TO COMPOSER DETAILS VIEW *****************" }
            navController.navigate(Screen.ComposerDetails.createRoute(composerId))
        }
    }

    fun navigateToGenreDetails(genreId: Long, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            logger.info { "\n\n\n\n\n***************** SWITCHING TO GENRE DETAILS VIEW *****************" }
            navController.navigate(Screen.GenreDetails.createRoute(genreId))
        }
    }

    fun navigateToPlaylistDetails(playlistId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            logger.info { "\n\n\n\n\n***************** SWITCHING TO PLAYLIST DETAILS VIEW *****************" }
            navController.navigate(Screen.PlaylistDetails.createRoute(playlistId))
        }
    }

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
            logger.info { "\n\n\n\n\n***************** SWITCHING TO QUEUE LIST VIEW *****************" }
            navController.navigate(Screen.QueueList.createRoute())
        }
    } // not same naming convention, but is the same principle as the navigateTo__ functions

    fun navigateBack() {
        logger.info { "\n\n\n***************** POPPING NAV STACK - BACK BTN PRESSED *****************\n\n\n" }
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
