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

/**
 * Use case to retrieve data for [ComposerDetailsFilterResult] domain model for ComposerDetailsScreen UI.
 * @property getSongDataUseCase [GetSongDataUseCase] Use case for generating PlayerSong
 * @property composerRepo [ComposerRepo] The repository for accessing Composer data
 */
@OptIn(ExperimentalCoroutinesApi::class)
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

        val pSongsFlow = songsFlow.flatMapLatest { item ->
            domainLogger.info { "Get Composer Details Use Case - Get Player Songs: ${item.size}" }
            getSongDataUseCase(item)
        }

        return combine(
            composerFlow,
            songsFlow,
            pSongsFlow,
        ) {
            composer,
            songs,
            pSongs, ->
            ComposerDetailsFilterResult(
                composer.asExternalModel(),
                songs.map{ it.asExternalModel() },
                pSongs,
            )
        }
    }
}