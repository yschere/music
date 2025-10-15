package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.Playlist
import com.example.music.data.mediaresolver.model.uri
import com.example.music.data.util.FLAG
import com.example.music.domain.model.PlaylistDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.model.getArtworkUris
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Playlist Details"

/**
 * Use case to retrieve data for [PlaylistDetailsFilterResult] domain model which returns
 * the PlaylistInfo data and the playlist's songs as list of SongInfo to populate the
 * PlaylistDetails screen.
 * @property mediaRepo Content Resolver Repository for MediaStore
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
                mediaRepo.findPlaylistTracks(playlistId)?.map { track ->
                    mediaRepo.getAudio(track.audioId)
                }
            },
        ) { p, audios ->
            Log.i(TAG, "PLAYLIST: $p ---\n" +
                "Playlist ID: ${p.id}\n" +
                "Playlist Name: ${p.name}\n" +
                "Number Songs: ${p.numTracks}"
            )

            var playlist = p.asExternalModel()
            val songs = audios?.map { audio ->
                if (FLAG) Log.i(TAG, "SONG: ${audio.title}")
                audio.asExternalModel()//.copy(artworkBitmap = mediaRepo.loadThumbnail(audio.uri))
            }
            if (songs != null) playlist = playlist.copy(playlistImage = playlist.getArtworkUris(songs))
            PlaylistDetailsFilterResult(
                playlist = playlist,
                songs = songs ?: emptyList(),
            )
        }
    }
}