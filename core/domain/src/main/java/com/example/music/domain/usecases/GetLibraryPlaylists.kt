package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.database.model.PlaylistWithExtraInfo
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.repository.PlaylistRepo
import com.example.music.domain.model.PlaylistInfo
import com.example.music.domain.model.SongInfo
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.model.getArtworkUris
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

private const val TAG = "Get Library Playlists"

/**
 * Use case for retrieving library playlists to populate Playlists List in Library Screen.
 * @property playlistRepo The repository for accessing Playlist and SongPlaylistEntry data
 */
class GetLibraryPlaylists @Inject constructor(
    private val playlistRepo: PlaylistRepo,
    private val mediaRepo: MediaRepo,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<List<PlaylistInfo>> {
        val playlistsList: Flow<List<PlaylistWithExtraInfo>>
        Log.i(TAG, "Building Playlists List:\n" +
            "Sort Option: $sortOption, isAscending: $isAscending")

        //sortOption values changed to support enum values AppPreferences dataStore
        when (sortOption) {

            "DATE_CREATED" -> { //"dateCreated" -> {
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByDateCreatedAsc()
                    else playlistRepo.sortPlaylistsByDateCreatedDesc()
            }

            "DATE_LAST_ACCESSED" -> { //"dateLastAccessed" -> {
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByDateLastAccessedAsc()
                    else playlistRepo.sortPlaylistsByDateLastAccessedDesc()
            }

            /*"DATE_LAST_PLAYED" -> { //"dateLastPlayed" -> {
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsByDateLastPlayedAsc()
                    else playlistRepo.sortPlaylistsByDateLastPlayedDesc()
            }*/

            "SONG_COUNT" -> { //"songCount" -> {
                playlistsList =
                    if (isAscending) playlistRepo.sortPlaylistsBySongCountAsc()
                    else playlistRepo.sortPlaylistsBySongCountDesc()
            }

            else -> { //"NAME" //"name"
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
    }
}