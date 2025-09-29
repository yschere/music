package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.repository.PlaylistRepo
import com.example.music.domain.model.PlaylistDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.domain.model.getArtworkUris
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

private const val TAG = "Get Playlist Details"

/**
 * Use case to retrieve data for [PlaylistDetailsFilterResult] domain model for PlaylistDetailsScreen UI.
 * @property playlistRepo The repository for accessing Playlist data
 * @property mediaRepo Content Resolver for MediaStore
 */
class GetPlaylistDetails @Inject constructor(
    private val playlistRepo: PlaylistRepo,
    private val mediaRepo: MediaRepo,
) {
    operator fun invoke(playlistId: Long): Flow<PlaylistDetailsFilterResult> {
        Log.i(TAG, "START --- playlistId: $playlistId")
        val playlistFlow = playlistRepo.getPlaylistWithExtraInfo(playlistId)

        Log.i(TAG, "Get Songs by Playlist ID")
        val songIdsFlow = playlistRepo.getSongsByPlaylistId(playlistId)
        // when playlist has no songs, the id list returns a 0 instead of null

        return combine(
            playlistFlow,
            songIdsFlow,
        ) { playlist, songIds ->
            Log.i(TAG, "playlist: ${playlist.playlist.name} + ${playlist.songCount} songs")
            Log.i(TAG, "playlist song IDs: $songIds")

            val p = playlist.asExternalModel()
            if (p.songCount > 0){
                val songs = mediaRepo.getAudios(songIds).map { it.asExternalModel() }
                Log.i(TAG, "songs: ${songs.size}")
                PlaylistDetailsFilterResult(
                    playlist = p.copy(playlistImage = p.getArtworkUris(songs)),
                    songs = songs,
                )
            } else { // playlist.songCount less than or equal to 0 will be set to empty
                PlaylistDetailsFilterResult(
                    playlist = p,
                    songs = emptyList(),
                )
            }
        }
    }
}