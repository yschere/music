package com.example.music.domain

import com.example.music.data.database.model.Genre
import com.example.music.data.repository.GenreStore
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
    private val genreStore: GenreStore
) {
    /**
     * Create a [GenreSortModel] from the list of genres in [genreStore].
     * @param sortOption: the column to sort by. If not met, default to sorting by genre name.
     * @param isAscending: the order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<GenreSortModel> {
        //how to choose which one is mapped, since either one can happen
        var genresList: Flow<List<Genre>> = flowOf()
        when (sortOption) {
            "albumCount" -> {
                genresList = if (isAscending) genreStore.sortGenresByAlbumCountAsc() else genreStore.sortGenresByAlbumCountDesc()
                return genresList.map { genres ->
                    GenreSortModel(
                        genres = genres.map { it.asExternalModel() },
                        count = genreStore.count()
                    )
                }
            }
            "songCount" -> {
                genresList = if (isAscending) genreStore.sortGenresBySongCountAsc() else genreStore.sortGenresBySongCountDesc()
                return genresList.map { genres ->
                    GenreSortModel(
                        genres = genres.map { it.asExternalModel() },
                        count = genreStore.count()
                    )
                }
            }
            else -> {
                genresList = if (isAscending) genreStore.sortGenresByNameAsc() else genreStore.sortGenresByNameDesc()
            }
        }

        //using this as the final catch all, but using the when cases to return if the option is met
        return genresList.map { genres ->
            GenreSortModel(
                genres = genres.map { it.asExternalModel() },
                count = genreStore.count()
            )
        }
    }
}