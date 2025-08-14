package com.example.music.ui.shared

import android.app.Notification.Action
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.RemoveFromQueue
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.music.R

/**
 * Support class for More Options Modals to represent an action item.
 */
data class ActionItem(
    val name: String = "",
    val icon: ImageVector,
    val contentDescription: Int = 0, // resource string name
)

/**
 * Object that contains all the possible actions for the MoreOptions bottom modals.
 */
object Actions {
    val PlayItem: ActionItem = ActionItem("Play", Icons.Filled.PlayArrow, R.string.icon_play) //{ /*navigateToPlayerSong(song)*/ }
    val PlayItemNext: ActionItem = ActionItem("Play next", Icons.AutoMirrored.Filled.QueueMusic, R.string.icon_play_next)
    val ShuffleItem: ActionItem = ActionItem("Shuffle", Icons.Filled.Shuffle, R.string.icon_shuffle) // is for playlist, album, artist, composer, genre
    val AddToPlaylist: ActionItem = ActionItem("Add to Playlist", Icons.AutoMirrored.Filled.PlaylistAdd, R.string.icon_add_to_playlist) // is for individual song, for multiple songs if selection works, for album, for artist, for composer, for genre, for playlist
    val AddToQueue: ActionItem = ActionItem("Add to Queue", Icons.Filled.Queue, R.string.icon_add_to_queue) // is for individual song, for multiple songs if selection works, for album, for artist, for composer, for genre, for playlist

    val GoToArtist: ActionItem = ActionItem("Go to Artist", Icons.Filled.Person, R.string.icon_artist) // navigateToArtistDetails(transform artistName to artist.Id)
    val GoToAlbumArtist: ActionItem = ActionItem("Go to Album Artist", Icons.Filled.Person, R.string.icon_artist) // navigateToArtistDetails(transform artistName to artist.Id)
    val GoToAlbum: ActionItem = ActionItem("Go to Album", Icons.Filled.Album, R.string.icon_album) // navigateToAlbumDetails(transform albumTitle to album.Id)
    val GoToComposer: ActionItem = ActionItem("Go to Composer", Icons.Filled.Person, R.string.icon_composer)
    val GoToGenre: ActionItem = ActionItem("Go to Genre", Icons.Filled.Category, R.string.icon_genre)
    val GoToPlaylist: ActionItem = ActionItem("Go to Playlist", Icons.Filled.LibraryMusic, R.string.icon_playlist)
    val GoToQueue: ActionItem = ActionItem("Go to Queue list", Icons.AutoMirrored.Filled.QueueMusic, R.string.icon_queue)

    val EditAlbumTags:ActionItem = ActionItem("Edit Album Tags", Icons.Filled.Edit, R.string.icon_edit)
    val EditArtistTags: ActionItem = ActionItem("Edit Artist Tags", Icons.Filled.Edit, R.string.icon_edit)
    val EditComposerTags: ActionItem = ActionItem("Edit Composer Tags", Icons.Filled.Edit, R.string.icon_edit)
    val EditGenreTags: ActionItem = ActionItem("Edit Genre Tags", Icons.Filled.Edit, R.string.icon_edit)
    val EditPlaylistTags: ActionItem = ActionItem("Edit Playlist Tags", Icons.Filled.Edit, R.string.icon_edit)
    val EditSongTags: ActionItem = ActionItem("Edit Song Tags", Icons.Filled.Edit, R.string.icon_edit)

    val ViewSongDetails: ActionItem = ActionItem("View Song Details", Icons.Filled.Info, R.string.icon_song_details)
    val EditPlaylistOrder: ActionItem = ActionItem("Edit Playlist Song Order", Icons.Filled.Reorder, R.string.icon_reorder)
    val SaveQueueToPlaylist: ActionItem = ActionItem("Save Queue to a Playlist", Icons.Filled.Save, R.string.icon_queue_save)
    val ClearQueue: ActionItem = ActionItem("Clear the Queue", Icons.Filled.ClearAll, R.string.icon_queue_clear)
    val CreatePlaylist: ActionItem = ActionItem("Create Playlist", Icons.Filled.AddCircle, R.string.icon_create_new_playlist)
    val ExportPlaylist: ActionItem = ActionItem("Export Playlist", Icons.Filled.Download, R.string.icon_export)
    val DeletePlaylist: ActionItem = ActionItem("Delete Playlist", Icons.Filled.PlaylistRemove, R.string.icon_delete_playlist)

    val RemoveFromPlaylist: ActionItem = ActionItem("Remove Song From Playlist", Icons.Filled.PlaylistRemove, R.string.icon_remove_song_playlist) // for song in playlist
    val RemoveFromQueue: ActionItem = ActionItem("Remove Song from Queue", Icons.Filled.RemoveFromQueue, R.string.icon_remove_song_queue)
    val DeleteFromLibrary: ActionItem = ActionItem("Delete From Library", Icons.Filled.Delete, R.string.icon_delete_song) // for song in library
}