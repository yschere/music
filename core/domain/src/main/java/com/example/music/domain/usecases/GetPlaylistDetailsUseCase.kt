package com.example.music.domain.usecases

import com.example.music.data.repository.PlaylistRepo
import com.example.music.domain.model.PlaylistDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.domainLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Use case to retrieve data for [PlaylistDetailsFilterResult] domain model for PlaylistDetailsScreen UI.
 * @property getSongDataUseCase [GetSongDataUseCase] Use case for generating PlayerSong
 * @property playlistRepo [PlaylistRepo] The repository for accessing Playlist and SongPlaylistEntry data
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetPlaylistDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val playlistRepo: PlaylistRepo,
) {
    /**
     * Invoke with playlistId to retrieve PlaylistDetailsFilterResult data
     * @param playlistId [Long] to return flow of PlaylistDetailsFilterResult
     */
    operator fun invoke(playlistId: Long): Flow<PlaylistDetailsFilterResult> {
        domainLogger.info { "Get Playlist Details Use Case - start: PlaylistID: $playlistId" }
        val playlistFlow = playlistRepo.getPlaylistWithExtraInfo(playlistId)

        domainLogger.info { "Get Playlist Details Use Case - Get Songs by Playlist ID" }
        val songsFlow = playlistRepo.sortSongsInPlaylistByTrackNumberAsc(playlistId)

        val pSongsListFlow = songsFlow.flatMapLatest { item ->
            domainLogger.info { "Get Playlist Details Use Case - Get Player Songs: ${item.size}" }
            getSongDataUseCase(item)
        }

        return combine(
            playlistFlow,
            songsFlow,
            pSongsListFlow,
        ) {
            playlist,
            songs,
            pSongs, ->
            PlaylistDetailsFilterResult(
                playlist.asExternalModel(),
                songs.map{ it.asExternalModel() },
                pSongs,
            )
        }
    }
}