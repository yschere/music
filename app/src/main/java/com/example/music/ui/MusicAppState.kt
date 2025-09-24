package com.example.music.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

private const val TAG = "Music App State Controller"

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
    object Player : Screen("player") {
        fun createRoute() = "player"
    }
    object Search : Screen("search") {
        fun createRoute() = "search"
    }
    object Settings : Screen("settings") {
        fun createRoute() = "settings"
    }

    object AlbumDetails : Screen("album/{$ARG_ALBUM_ID}") {
        fun createRoute(albumId: Long) = "album/$albumId"
    }
    object ArtistDetails : Screen("artist/{$ARG_ARTIST_ID}") {
        fun createRoute(artistId: Long) = "artist/$artistId"
    }
    object ComposerDetails : Screen("composer/{$ARG_COMPOSER_ID}") {
        fun createRoute(composerId: Long) = "composer/$composerId"
    }
    object GenreDetails : Screen("genre/{$ARG_GENRE_ID}") {
        fun createRoute(genreId: Long) = "genre/$genreId"
    }
    object PlaylistDetails : Screen("playlist/{$ARG_PLAYLIST_ID}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }

    /* ******* Edit Tag Section *******
     * not sure if this is gonna be song info/details or edit tags, maybe make both as separate screen objects
     * for now, just going to make some semblance of what the edit tag set of nav routes could look like
     * actually, a song info screen could probably also just be a modal

    object EditSongTags : Screen("edit/song/{$arg_song_id}") {
        fun createRoute(songId: Long) = "edit/song/$songId"
    } // this would need its own screen
    object EditGenreTags : Screen("edit/genre/{$arg_genre_id}") {
        fun createRoute(genreId: Long) = "edit/genre/$genreId"
    } // this could probably be done in a modal (its just name change)
    object EditComposerTags : Screen("edit/composer/{$arg_composer_id}") {
        fun createRoute(composerId: Long) = "edit/composer/$songId"
    } // this could probably be done in a modal (its just name change)
    object EditArtistTags : Screen("edit/artist/{$arg_artist_id}") {
        fun createRoute(artistId: Long) = "edit/artist/$artistId"
    } // this could probably be done in a modal (its just name change)
    object EditAlbumTags : Screen("edit/album/{$arg_album_id}") {
        fun createRoute(albumId: Long) = "edit/album/$albumId"
    } // this would need its own screen
    */

    companion object {
        const val ARG_ALBUM_ID = "albumId"
        const val ARG_ARTIST_ID = "artistId"
        const val ARG_COMPOSER_ID = "composerId"
        const val ARG_GENRE_ID = "genreId"
        const val ARG_PLAYLIST_ID = "playlistId"
        const val ARG_SONG_ID = "songId"
    }

    /*  multiple screens needed for this
        edit song tags screen -> needs context of selected item

        need to figure out if popups are separate screen or context popup for:
        multi-select(?) - undecided if it should be its own screen w/ route or if its some fragment? not sure how to access this since the full song list is within library context
        add to playlist - same issue/principle as multi-select, they both need access to song list with checkbox/selection support
        edit playlist - tangentially same issue as add/multi-select
        delete playlist - want this to be a modal with an are you sure CtA/confirmation
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
        Log.i(TAG, "refreshOnline call")
        isOnline = checkIfOnline()
    }

    fun navigateToHome(from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            Log.i(TAG, "***************** SWITCHING TO HOME VIEW *****************")
            navController.navigate(Screen.Home.createRoute())
        }
    }

    fun navigateToLibrary(from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            Log.i(TAG, "***************** SWITCHING TO LIBRARY VIEW *****************")
            navController.navigate(Screen.Library.createRoute())
        }
    }

    fun navigateToPlayer(from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            Log.i(TAG, "***************** SWITCHING TO PLAYER VIEW *****************")
            navController.navigate(Screen.Player.createRoute())
        }
    }

    fun navigateToSearch(from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            Log.i(TAG, "***************** SWITCHING TO SEARCH VIEW *****************")
            navController.navigate(Screen.Search.createRoute())
        }
    }

    fun navigateToSettings(from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            Log.i(TAG, "***************** SWITCHING TO SETTINGS VIEW *****************")
            navController.navigate(Screen.Settings.createRoute())
        }
    }

    fun navigateToAlbumDetails(albumId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            Log.i(TAG, "***************** SWITCHING TO ALBUM DETAILS VIEW *****************")
            navController.navigate(Screen.AlbumDetails.createRoute(albumId))
        }
    }

    fun navigateToArtistDetails(artistId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            Log.i(TAG, "***************** SWITCHING TO ARTIST DETAILS VIEW *****************")
            navController.navigate(Screen.ArtistDetails.createRoute(artistId))
        }
    }

    fun navigateToComposerDetails(composerId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            Log.i(TAG, "***************** SWITCHING TO COMPOSER DETAILS VIEW *****************")
            navController.navigate(Screen.ComposerDetails.createRoute(composerId))
        }
    }

    fun navigateToGenreDetails(genreId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            Log.i(TAG, "***************** SWITCHING TO GENRE DETAILS VIEW *****************")
            navController.navigate(Screen.GenreDetails.createRoute(genreId))
        }
    }

    fun navigateToPlaylistDetails(playlistId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            Log.i(TAG, "***************** SWITCHING TO PLAYLIST DETAILS VIEW *****************")
            navController.navigate(Screen.PlaylistDetails.createRoute(playlistId))
        }
    }

    /* //FUTURE THOUGHT: determine if this is the song data page or the song edit tags page
    // I have a feeling in either case it will still have the same questions
    // as the AlbumsList and ArtistsList screens
    fun navigateToSongDetails(songId: Long, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.SongDetails.createRoute(songId))
        }
    } */

    fun navigateBack() {
        Log.i(TAG, "***************** POPPING NAV STACK - BACK BTN PRESSED *****************")
        navController.popBackStack()
    }

    @SuppressLint("ObsoleteSdkInt")
    @Suppress("DEPRECATION")
    private fun checkIfOnline(): Boolean {
        return true //using this to skip over online status check
        /* //og means of checking if device is online
        val cm = getSystemService(context, ConnectivityManager::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            cm?.activeNetworkInfo?.isConnectedOrConnecting == true
        } */
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
