package com.example.music.domain.usecases

import com.example.music.data.repository.ComposerRepo
import com.example.music.domain.model.ComposerDetailsFilterResult
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
 * Use case to retrieve data for [ComposerDetailsFilterResult] domain model for ComposerDetailsScreen UI.
 * @property getSongDataUseCase [GetSongDataUseCase] Use case for generating PlayerSong
 * @property composerRepo [ComposerRepo] The repository for accessing Composer data
 */
class GetComposerDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val composerRepo: ComposerRepo,
) {
    /**
     * Invoke with composerId to retrieve ComposerDetailsFilterResult data
     * @param composerId [Long] to return flow of ComposerDetailsFilterResult
     */
    operator fun invoke(composerId: Long): Flow<ComposerDetailsFilterResult> {
        domainLogger.info { "Get Composer Details Use Case - start: ComposerID: $composerId" }
        val composerFlow = composerRepo.getComposerWithExtraInfo(composerId)

        domainLogger.info { "Get Composer Details Use Case - Get Songs by Composer ID" }
        val songsFlow = composerRepo.getSongsByComposerId(composerId)

        return combine(
            composerFlow,
            songsFlow,
        ) {
            composer,
            songs, ->
            ComposerDetailsFilterResult(
                composer.asExternalModel(),
                songs.map{ it.asExternalModel() },
            )
        }
    }
}