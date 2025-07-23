package com.example.music.domain.usecases

import com.example.music.data.repository.PlaylistRepo
import com.example.music.domain.model.PlaylistDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.MediaRepo
import com.example.music.domain.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/** Changelog:
 * 4/5/2025 - Created GetPlaylistDetailsV2 to accommodate change to song_playlist_entries
 * from MusicDatabase now using MediaStore Audio ids instead of previewData Song ids.
 */

/** logger tag for this class */
private const val TAG = "Get Playlist Details V2"

/**
 * Use case to retrieve data for [PlaylistDetailsFilterResult] domain model for PlaylistDetailsScreen UI.
 * @property resolver [MediaRepo] Content Resolver for MediaStore
 * @property playlistRepo [PlaylistRepo] The repository for accessing Playlist data
 */
class GetPlaylistDetailsV2 @Inject constructor(
    private val playlistRepo: PlaylistRepo,
    private val resolver: MediaRepo,
) {
    /**
     * Invoke with playlistId to retrieve PlaylistDetailsFilterResult data
     * @param playlistId [Long] to return flow of PlaylistDetailsFilterResult
     */
    operator fun invoke(playlistId: Long): Flow<PlaylistDetailsFilterResult> {
        domainLogger.info { "$TAG - start - playlistId: $playlistId" }
        val playlistFlow = playlistRepo.getPlaylistWithExtraInfo(playlistId)

        domainLogger.info { "$TAG - Get Songs by Playlist ID" }
        val songIdsFlow = playlistRepo.getSongsByPlaylistId(playlistId) // when playlist has no songs, the id list returns a 0 instead of null

        return combine(
            playlistFlow,
            songIdsFlow,
        ) { playlist, songIds ->
            domainLogger.info { "$TAG - playlist: ${playlist.playlist.name} + ${playlist.songCount} songs" }
            domainLogger.info { "$TAG - playlist song IDs: $songIds" }

            if (playlist.songCount > 0){
                val songs = resolver.getAudios(songIds)
                domainLogger.info { "$TAG - songs: ${songs.size}" }
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