package com.example.music.domain

import com.example.music.data.database.model.Genre
import com.example.music.data.repository.GenreRepo
import com.example.music.model.GenreInfo
import com.example.music.model.GenreSortModel
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import javax.inject.Inject

/**
 * Use case for retrieving library albums to populate Albums List in Library Screen.
 */
class GetLibraryGenresUseCase @Inject constructor(
    private val genreRepo: GenreRepo
) {
    /**
     * Create a [GenreSortModel] from the list of genres in [genreRepo].
     * @param sortOption: the column to sort by. If not met, default to sorting by genre name.
     * @param isAscending: the order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<GenreSortModel> {
        //how to choose which one is mapped, since either one can happen
        var genresList: Flow<List<Genre>> = flowOf()
        when (sortOption) {
            "albumCount" -> {
                genresList = if (isAscending) genreRepo.sortGenresByAlbumCountAsc() else genreRepo.sortGenresByAlbumCountDesc()
                return genresList.map { genres ->
                    GenreSortModel(
                        genres = genres.map { it.asExternalModel() },
                        count = genreRepo.count()
                    )
                }
            }
            "songCount" -> {
                genresList = if (isAscending) genreRepo.sortGenresBySongCountAsc() else genreRepo.sortGenresBySongCountDesc()
                return genresList.map { genres ->
                    GenreSortModel(
                        genres = genres.map { it.asExternalModel() },
                        count = genreRepo.count()
                    )
                }
            }
            else -> {
                genresList = if (isAscending) genreRepo.sortGenresByNameAsc() else genreRepo.sortGenresByNameDesc()
            }
        }

        //using this as the final catch all, but using the when cases to return if the option is met
        return genresList.map { genres ->
            GenreSortModel(
                genres = genres.map { it.asExternalModel() },
                count = genreRepo.count()
            )
        }
    }
}