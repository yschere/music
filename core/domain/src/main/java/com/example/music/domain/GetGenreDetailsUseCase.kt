package com.example.music.domain

import com.example.music.data.repository.GenreRepo
import com.example.music.data.repository.SongRepo
import com.example.music.model.GenreDetailsFilterResult
import com.example.music.model.asExternalModel
import com.example.music.util.domainLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Use case to retrieve Genre data, songs in Genre as List<GenreInfo>, and songs in Genre as List<PlayerSong>.
 * @param genreId [Long] to return flow of PlayerSong(song, artist, album)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetGenreDetailsUseCase @Inject constructor(
    val getSongDataUseCase: GetSongDataUseCase,
    private val genreRepo: GenreRepo,
    private val songRepo: SongRepo,
) {

    operator fun invoke(genreId: Long): Flow<GenreDetailsFilterResult> {
        domainLogger.info { "Get Genre Details Use Case - start: GenreID: $genreId" }
        val genreFlow = genreRepo.getGenreWithExtraInfo(genreId)

        domainLogger.info { "Get Genre Details Use Case - Get Songs by Genre ID" }
        val songsFlow = songRepo.getSongsByGenreId(genreId)

        val pSongsFlow = songsFlow.flatMapLatest { item ->
            domainLogger.info { "Get Genre Details Use Case - Get Player Songs: ${item.size}" }
            getSongDataUseCase(item)
        }

        return combine(
            genreFlow,
            songsFlow,
            pSongsFlow,
        ) {
            genre, songs, pSongs ->
            GenreDetailsFilterResult(
                genre.asExternalModel(),
                songs.map{ it.asExternalModel() },
                pSongs,
            )
        }
    }
}