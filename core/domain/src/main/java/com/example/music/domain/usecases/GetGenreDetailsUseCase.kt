package com.example.music.domain.usecases

import com.example.music.data.repository.GenreRepo
import com.example.music.domain.model.GenreDetailsFilterResult
import com.example.music.domain.model.asExternalModel
import com.example.music.domain.util.domainLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Use case to retrieve data for [GenreDetailsFilterResult] domain model for GenreDetailsScreen UI.
 * @property getSongDataUseCase [GetSongDataUseCase] Use case for generating PlayerSong
 * @property genreRepo [GenreRepo] The repository for accessing Genre data
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetGenreDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val genreRepo: GenreRepo,
) {
    /**
     * Invoke with genreId to retrieve GenreDetailsFilterResult data
     * @param genreId [Long] to return flow of GenreDetailsFilterResult
     */
    operator fun invoke(genreId: Long): Flow<GenreDetailsFilterResult> {
        domainLogger.info { "Get Genre Details Use Case - start: GenreID: $genreId" }
        val genreFlow = genreRepo.getGenreWithExtraInfo(genreId)

        domainLogger.info { "Get Genre Details Use Case - Get Songs by Genre ID" }
        val songsFlow = genreRepo.getSongsByGenreId(genreId)

        val pSongsFlow = songsFlow.flatMapLatest { item ->
            domainLogger.info { "Get Genre Details Use Case - Get Player Songs: ${item.size}" }
            getSongDataUseCase(item)
        }

        return combine(
            genreFlow,
            songsFlow,
            pSongsFlow,
        ) {
            genre,
            songs,
            pSongs, ->
            GenreDetailsFilterResult(
                genre.asExternalModel(),
                songs.map{ it.asExternalModel() },
                pSongs,
            )
        }
    }
}