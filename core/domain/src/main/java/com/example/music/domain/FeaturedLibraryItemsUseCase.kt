package com.example.music.domain

import com.example.music.data.repository.PlaylistRepo
import com.example.music.data.repository.SongRepo
import com.example.music.model.FeaturedLibraryItemsFilterResult
import com.example.music.model.asExternalModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.log4j.BasicConfigurator

private val logger = KotlinLogging.logger{}

/**
 * Goal: to create use case which returns the most recently played playlists and the most recently added songs to populate the Home screen
 */
class FeaturedLibraryItemsUseCase @Inject constructor(
        private val songRepo:SongRepo,
        private val playlistRepo:PlaylistRepo
) {

    //TODO: trying to rework this so that it checks stores for these items, should only be reliant on passed in stores, not individual items
    operator fun invoke(): Flow<FeaturedLibraryItemsFilterResult> {
        BasicConfigurator.configure()
        logger.info { "FeaturedLibraryItemsUseCase start" }
        val recentPlaylistsFlow = playlistRepo.sortPlaylistsByDateLastPlayedDesc(5)
        logger.info { "FeaturedLibraryItemsUseCase - recentPlaylistsFlow: $recentPlaylistsFlow" }
        val recentSongsFlow = songRepo.sortSongsByDateLastPlayedDesc(10) //TODO: set this back to sortSongsByDateLastPlayed, limit 10
        logger.info { "FeaturedLibraryItemsUseCase - recentlyAddedSongsFlow: $recentSongsFlow" }

        return combine(recentPlaylistsFlow, recentSongsFlow) { recentPlaylists, featuredSongs ->
            FeaturedLibraryItemsFilterResult(
                recentPlaylists = recentPlaylists.map { it.asExternalModel() },
                recentlyAddedSongs = featuredSongs.map { it.asExternalModel() }
            )
        }
    }
}