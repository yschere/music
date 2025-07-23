package com.example.music.domain.usecases

import com.example.music.data.repository.AlbumRepo
import com.example.music.domain.model.AlbumDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import com.example.music.domain.util.domainLogger
import javax.inject.Inject

/** Changelog:
 * 4/2/2025 - Removing SongInfo to PlayerSong conversion. PlayerSong is no longer
 * needed to display Song data in LazyList or LazyGrid in the UI, as SongInfo has
 * been updated to support this.
 */

/**
 * Use case to retrieve data for [AlbumDetailsFilterResult] domain model for AlbumDetailsScreen UI.
 * @property getSongDataUseCase [GetSongDataUseCase] Use case for generating PlayerSong
 * @property albumRepo [AlbumRepo] The repository for accessing Album data
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetAlbumDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val albumRepo: AlbumRepo,
) {
    /**
     * Invoke with albumId to retrieve AlbumDetailsFilterResult data
     * @param albumId [Long] to return flow of AlbumDetailsFilterResult
     */
    operator fun invoke(albumId: Long): Flow<AlbumDetailsFilterResult> {
        domainLogger.info { "Get Album Details Use Case - start: AlbumID: $albumId" }
        val albumFlow = albumRepo.getAlbumWithExtraInfo(albumId)

        val artistFlow = albumRepo.getAlbumArtistByAlbumId(albumId)

        domainLogger.info { "Get Album Details Use Case - Get Songs by Album ID" }
        val songsFlow = albumRepo.getSongsByAlbumId(albumId)

        return combine(
            albumFlow,
            artistFlow,
            songsFlow,
        ) {
            album,
            artist,
            songs, ->
            AlbumDetailsFilterResult(
                album.asExternalModel(),
                artist.asExternalModel(),
                songs.map{ it.asExternalModel() },
            )
        }
    }
}