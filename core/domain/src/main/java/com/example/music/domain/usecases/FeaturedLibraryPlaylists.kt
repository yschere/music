package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
import com.example.music.data.util.FLAG
import com.example.music.domain.model.FeaturedLibraryItemsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.model.getArtworkUris
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

private const val TAG = "Featured Library Playlists"

/**
 * Goal: to create use case which returns the most recently played playlists and the most recently added songs to populate the Home screen
 *
 * Generates featured library items of PLAYLISTS and SONGS from MediaStore
 */
class FeaturedLibraryPlaylists @Inject constructor(
    private val mediaRepo: MediaRepo
) {
    operator fun invoke(): Flow<FeaturedLibraryItemsFilterResult> {
        Log.i(TAG, "Start fetching most recent playlists and most recent songs")
        val recentPlaylistsFlow = mediaRepo.mostRecentPlaylists(5)
        val recentSongsFlow = mediaRepo.mostRecentSongsIds(10)

        return combine(
            recentPlaylistsFlow,
            recentSongsFlow
        ) { recentPlaylists, featuredSongs ->
            if (FLAG) Log.i(TAG, "playlists size: ${recentPlaylists.size} :: songs size: ${featuredSongs.size}")
            FeaturedLibraryItemsFilterResult(
                recentPlaylists = recentPlaylists.map { playlistId ->
                    if (FLAG) Log.i(TAG, "Fetch Playlist from ID - $playlistId")
                    val playlist = mediaRepo.getPlaylist(playlistId).asExternalModel()
                    val songs = mediaRepo.findPlaylistTracks(playlistId)
                        .map { track ->
                            if (FLAG) Log.i(TAG, "Track ID: ${track.id} -> Title: ${track.title}")
                            mediaRepo.getAudio(track.audioId)
                        }.map {
                            it.asExternalModel()
                        }
                    playlist.copy(playlistImage = playlist.getArtworkUris(songs))//.copy(songCount = songs.size)
                },
                recentlyAddedSongs = featuredSongs.map { songID ->
                    if (FLAG) Log.i(TAG, "Fetch Song from SongID - $songID")
                    val audio = mediaRepo.getAudio(songID)
                    audio.asExternalModel().copy(artworkBitmap = mediaRepo.loadThumbnail(audio.uri))
                },
            )
        }
    }
}