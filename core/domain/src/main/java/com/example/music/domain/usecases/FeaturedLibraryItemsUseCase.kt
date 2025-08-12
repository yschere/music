package com.example.music.domain.usecases

import android.util.Log
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
        private val songRepo: SongRepo,
        private val playlistRepo: PlaylistRepo
) {

    //TODO: trying to rework this so that it checks stores for these items, should only be reliant on passed in stores, not individual items
    operator fun invoke(): Flow<FeaturedLibraryItemsFilterResult> {
        Log.i(TAG, "Start")
        val recentPlaylistsFlow = playlistRepo.sortPlaylistsByDateLastPlayedDesc(5)
        Log.i(TAG, "recentPlaylistsFlow: $recentPlaylistsFlow")
        val recentSongsFlow = songRepo.sortSongsByDateLastPlayedDesc(10) //TODO: set this back to sortSongsByDateLastPlayed, limit 10
        Log.i(TAG, "recentlyAddedSongsFlow: $recentSongsFlow")

        return combine(
            recentPlaylistsFlow,
            recentSongsFlow
        ) {
          recentPlaylists,
          featuredSongs ->
            FeaturedLibraryItemsFilterResult(
                recentPlaylists = recentPlaylists.map { it.asExternalModel() },
                recentlyAddedSongs = featuredSongs.map { it.asExternalModel() }
            )
        }
    }
}