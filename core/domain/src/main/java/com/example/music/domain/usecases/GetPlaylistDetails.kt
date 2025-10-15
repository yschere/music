package com.example.music.domain.usecases

import android.util.Log
import com.example.music.domain.model.PlaylistDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.Playlist
import com.example.music.data.mediaresolver.model.uri
import com.example.music.data.util.FLAG
import com.example.music.domain.model.getArtworkUris
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Playlist Details"

/**
 * Use case to retrieve data for [PlaylistDetailsFilterResult] domain model for PlaylistDetailsScreen UI.
 * @property mediaRepo Content Resolver for MediaStore
 */
class GetPlaylistDetails @Inject constructor(
    private val mediaRepo: MediaRepo,
) {
    operator fun invoke(playlistId: Long): Flow<PlaylistDetailsFilterResult> {
        Log.i(TAG, "START --- playlistId: $playlistId")
        val playlistFlow: Flow<Playlist> = mediaRepo.getPlaylistFlow(playlistId)

        return combine(
            playlistFlow,
            playlistFlow.map {
                mediaRepo.findPlaylistTracks(playlistId).map { track ->
                    mediaRepo.getAudio(track.audioId)
                }
            },
        ) { playlist, audios ->
            Log.i(TAG, "PLAYLIST: $playlist ---\n" +
                "Playlist ID: ${playlist.id}\n" +
                "Playlist Name: ${playlist.name}\n" +
                "Number Songs: ${playlist.numTracks}"
            )

            val p = playlist.asExternalModel()
            if (p.songCount > 0){
                val songs = audios.map { song ->
                    if (FLAG) Log.i(TAG, "SONG: ${song.title}")
                    song.asExternalModel()//.copy(artworkBitmap = mediaRepo.loadThumbnail(song.uri))
                }
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