package com.example.music.domain.usecases

import android.provider.MediaStore
import android.util.Log
import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.Playlist
import com.example.music.data.repository.PlaylistRepo
import com.example.music.data.repository.PlaylistSortList
import com.example.music.data.util.FLAG
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.model.getArtworkUris
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Library Playlists"

/**
 * Use case for retrieving library playlists to populate Playlists List in Library Screen.
 * @property playlistRepo The repository for accessing Playlist and SongPlaylistEntry data
 * @property mediaRepo Content Resolver for MediaStore
 */
class GetLibraryPlaylists @Inject constructor(
//    private val playlistRepo: PlaylistRepo,
    private val mediaRepo: MediaRepo,
) {
    /*operator fun invoke(
        sortColumn: String,
        isAscending: Boolean
    ): Flow<List<PlaylistInfo>> {
        val playlistsList: Flow<List<PlaylistWithExtraInfo>>
        Log.i(TAG, "START --- sortColumn: $sortColumn - isAscending: $isAscending")

        when (sortColumn) {
            PlaylistSortList[0] -> { //"Name"
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByNameAsc()
                    else playlistRepo.sortPlaylistsByNameDesc()
            }

            PlaylistSortList[1] -> { //"Song Count"
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsBySongCountAsc()
                    else playlistRepo.sortPlaylistsBySongCountDesc()
            }

            PlaylistSortList[2] -> { //"Date Created"
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByDateCreatedAsc()
                    else playlistRepo.sortPlaylistsByDateCreatedDesc()
            }

            PlaylistSortList[3] -> { //"Date Last Accessed"
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByDateLastAccessedAsc()
                    else playlistRepo.sortPlaylistsByDateLastAccessedDesc()
            }

            /*"DATE_LAST_PLAYED" -> { //"dateLastPlayed" -> {
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByDateLastPlayedAsc()
                    else playlistRepo.sortPlaylistsByDateLastPlayedDesc()
            }*/

            else -> {
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByNameAsc()
                    else playlistRepo.sortPlaylistsByNameDesc()
            }
        }

        return playlistsList.map { items ->
            Log.i(TAG, "********** Library Playlists count: ${items.size} **********")
            items.map { item ->
                val playlist = item.asExternalModel()
                if (playlist.songCount > 0) {
                    val songIdsFlow = playlistRepo.getSongsByPlaylistId(playlist.id, 4)
                    val songs = mediaRepo.getAudios(songIdsFlow.first()).map { song ->
                        song.asExternalModel()
                    }
                    Log.i(TAG, "song size: ${songs.size}")
                    playlist.copy(playlistImage = playlist.getArtworkUris(songs))
                } else {
                    playlist
                }
            }
        }
    }*/

    suspend operator fun invoke(
        sortColumn: String,
        isAscending: Boolean
    ): List<PlaylistInfo> {
        var playlistsList: List<Playlist>
        Log.i(TAG, "START --- sortColumn: $sortColumn - isAscending: $isAscending")

        when (sortColumn) {
            PlaylistSortList[0] -> { //"Name"
                playlistsList = mediaRepo.getAllPlaylists(
                    order = MediaStore.Audio.Playlists.NAME,
                    ascending = isAscending
                ).sortedBy { it.name.lowercase() }
                if (!isAscending) playlistsList = playlistsList.reversed()
            }

            PlaylistSortList[1] -> { //"Song Count"
                playlistsList = mediaRepo.getAllPlaylists(
                    order = MediaStore.Audio.Playlists.NAME,
                    ascending = isAscending
                ).sortedBy { it.numTracks }
                if (!isAscending) playlistsList = playlistsList.reversed()
            }

            PlaylistSortList[2] -> { //"Date Created"
                playlistsList = mediaRepo.getAllPlaylists(
                    order = MediaStore.Audio.Playlists.DATE_ADDED,
                    ascending = isAscending
                ).sortedBy { it.dateAdded }
                if (!isAscending) playlistsList = playlistsList.reversed()
            }

            PlaylistSortList[3] -> { //"Date Last Accessed"
                playlistsList = mediaRepo.getAllPlaylists(
                    order = MediaStore.Audio.Playlists.DATE_MODIFIED,
                    ascending = isAscending
                ).sortedBy { it.dateModified }
                if (!isAscending) playlistsList = playlistsList.reversed()
            }

            else -> {
                playlistsList = mediaRepo.getAllPlaylists(
                    order = MediaStore.Audio.Playlists.NAME,
                    ascending = isAscending
                )
            }
        }

        Log.i(TAG, "********** Library Playlists count: ${playlistsList.size} **********")
        return playlistsList.map { item ->
            val playlist = item.asExternalModel()
            if (FLAG) Log.i(TAG, "**** Playlist: ${playlist.id} + ${playlist.name} + ${playlist.songCount} ****")
            if (playlist.songCount > 0) {
                val songIds = mediaRepo.findPlaylistTracks(playlist.id, 4).map { it.audioId }
                val songs = mediaRepo.getAudios(songIds).map { song ->
                    song.asExternalModel()
                }
                playlist.copy(playlistImage = playlist.getArtworkUris(songs))
            } else {
                playlist
            }
        }
    }
}