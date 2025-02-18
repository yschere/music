package com.example.music.domain

import com.example.music.data.database.model.GenreWithExtraInfo
import com.example.music.data.repository.GenreRepo
import com.example.music.model.GenreInfo
import com.example.music.model.asExternalModel
import com.example.music.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for retrieving library genres to populate Genres List in Library Screen.
 */
class GetLibraryGenresUseCase @Inject constructor(
    private val genreRepo: GenreRepo
) {
    /**
     * Create a list of [GenreInfo] from the list of genres in [genreRepo].
     * @param sortOption: the column to sort by. If not met, default to sorting by genre name.
     * @param isAscending: the order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<List<GenreInfo>> {
        val genresList: Flow<List<GenreWithExtraInfo>>// = flowOf()
        domainLogger.info { "Building Genre List: \nSort Option: $sortOption, isAscending: $isAscending" }

        //sortOption values changed to support enum values AppPreferences dataStore
        when (sortOption) {
            /*"albumCount" -> {
                genresList =
                    if (isAscending) genreRepo.sortGenresByAlbumCountAsc() else genreRepo.sortGenresByAlbumCountDesc()
                return genresList.map { item ->
                    item.map { it.asExternalModel() } }
            }*/

            "SONG_COUNT" -> { //"songCount" -> {
                genresList =
                    if (isAscending) genreRepo.sortGenresBySongCountAsc()
                    else genreRepo.sortGenresBySongCountDesc()
            }

            else -> { //"NAME" //"name"
                genresList =
                    if (isAscending) genreRepo.sortGenresByNameAsc()
                    else genreRepo.sortGenresByNameDesc()
            }
        }

        //using this as the final catch all, but using the when cases to return if the option is met
        return genresList.map { items ->
            domainLogger.info { "********** Library Genres count: ${items.size} **********" }
            items.map { item ->
                item.asExternalModel()
            }
        }
    }
}