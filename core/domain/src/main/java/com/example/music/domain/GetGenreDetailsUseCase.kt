package com.example.music.domain

import com.example.music.data.repository.ComposerRepo
import com.example.music.model.ComposerDetailsFilterResult
import com.example.music.model.asExternalModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Use case to retrieve Composer data, songs in Composer as List<ComposerInfo>, and songs in Composer as List<PlayerSong>.
 * @param composerId [Long] to return flow of PlayerSong(song, artist, album)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetComposerDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val composerRepo: ComposerRepo,
) {

    operator fun invoke(composerId: Long): Flow<ComposerDetailsFilterResult> {
        val composerFlow = composerRepo.getComposerById(composerId)

        val songsFlow = composerRepo.getSongsByComposerId(composerId)

        val pSongsFlow = songsFlow.flatMapLatest { item ->
            getSongDataUseCase(item)
        }

        return combine(
            composerFlow,
            songsFlow,
            pSongsFlow,
        ) {
            composer, songs, pSongs ->
            ComposerDetailsFilterResult(
                composer.asExternalModel(),
                songs.map{ it.asExternalModel() },
                pSongs,
            )
        }
    }
}