package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.repository.PlaylistRepo
import com.example.music.domain.model.PlaylistDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.MediaRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

private const val TAG = "Get Playlist Details V2"

/**
 * Use case to retrieve data for [PlaylistDetailsFilterResult] domain model for PlaylistDetailsScreen UI.
 * @property playlistRepo [PlaylistRepo] The repository for accessing Playlist data
 * @property mediaRepo [MediaRepo] Content Resolver for MediaStore
 */
class GetPlaylistDetailsV2 @Inject constructor(
    private val playlistRepo: PlaylistRepo,
    private val mediaRepo: MediaRepo,
) {
    /**
     * Invoke with playlistId to retrieve PlaylistDetailsFilterResult data
     * @param playlistId [Long] to return flow of PlaylistDetailsFilterResult
     */
    operator fun invoke(playlistId: Long): Flow<PlaylistDetailsFilterResult> {
        Log.i(TAG, "Start - playlistId: $playlistId")
        val playlistFlow = playlistRepo.getPlaylistWithExtraInfo(playlistId)

        Log.i(TAG, "Get Songs by Playlist ID")
        val songIdsFlow = playlistRepo.getSongsByPlaylistId(playlistId) // when playlist has no songs, the id list returns a 0 instead of null

        return combine(
            playlistFlow,
            songIdsFlow,
        ) { playlist, songIds ->
            Log.i(TAG, "playlist: ${playlist.playlist.name} + ${playlist.songCount} songs")
            Log.i(TAG, "playlist song IDs: $songIds")

            if (playlist.songCount > 0){
                val songs = mediaRepo.getAudios(songIds)
                Log.i(TAG, "songs: ${songs.size}")
                PlaylistDetailsFilterResult(
                    playlist = playlist.asExternalModel(),
                    songs = songs.map { it.asExternalModel() }
                )
            } else { // playlist.songCount less than or equal to 0 is an error
                PlaylistDetailsFilterResult(
                    playlist = playlist.asExternalModel(),
                    songs = emptyList()
                )
            }
        }
    }
}