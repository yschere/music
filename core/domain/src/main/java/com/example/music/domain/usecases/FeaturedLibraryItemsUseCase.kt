package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.mediaresolver.MediaRepo
import com.example.music.data.mediaresolver.model.uri
import com.example.music.data.repository.PlaylistRepo
import com.example.music.data.repository.SongRepo
import com.example.music.domain.model.FeaturedLibraryItemsFilterResult
import com.example.music.domain.model.asExternalModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val TAG = "FeaturedLibraryItemsUseCase"

/**
 * Goal: to create use case which returns the most recently played playlists and the most recently added songs to populate the Home screen
 */
class FeaturedLibraryItemsUseCase @Inject constructor(
    private val mediaRepo: MediaRepo,
    private val playlistRepo: PlaylistRepo
) {
    operator fun invoke(): Flow<FeaturedLibraryItemsFilterResult> {
        Log.i(TAG, "Start fetching most recent playlists and most recent songs")
        val recentPlaylistsFlow = playlistRepo.sortPlaylistsByDateLastPlayedDesc(5)
        val recentSongsFlow = mediaRepo.mostRecentSongsIds(10)

        return combine(
            recentPlaylistsFlow,
            recentSongsFlow
        ) { recentPlaylists, featuredSongs ->
            FeaturedLibraryItemsFilterResult(
                recentPlaylists = recentPlaylists.map { playlist ->
                    Log.i(TAG, "Fetch Playlist from ID - ${playlist.playlist.id}")
                    playlist.asExternalModel()
                },
                recentlyAddedSongs = featuredSongs.map { songID ->
                    Log.i(TAG, "Fetch Song from SongID - $songID")
                    val audio = mediaRepo.getAudio(songID)
                    audio.asExternalModel().copy(artworkBitmap = mediaRepo.loadThumbnail(audio.uri))
                },
            )
        }
    }
}