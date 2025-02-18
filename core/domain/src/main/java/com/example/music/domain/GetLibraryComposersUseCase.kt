package com.example.music.domain

import com.example.music.data.database.model.ComposerWithExtraInfo
import com.example.music.data.repository.ComposerRepo
import com.example.music.model.ComposerInfo
import com.example.music.model.asExternalModel
import com.example.music.util.domainLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for retrieving library composers to populate Composers List in Library Screen.
 */
class GetLibraryComposersUseCase @Inject constructor(
    private val composerRepo: ComposerRepo
) {
    /**
     * Create a list of [ComposerInfo] from the list of composers in [composerRepo].
     * @param sortOption: the column to sort by. If not met, default to sorting by composer name.
     * @param isAscending: the order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<List<ComposerInfo>> {
        val composersList: Flow<List<ComposerWithExtraInfo>>// = flowOf()
        domainLogger.info { "Building Composer List: \nSort Option: $sortOption, isAscending: $isAscending" }

        //sortOption values changed to support enum values AppPreferences dataStore
        when (sortOption) {
            /*"albumCount" -> {
                composersList =
                    if (isAscending) composerRepo.sortComposersByAlbumCountAsc() else composerRepo.sortComposersByAlbumCountDesc()
                return composersList.map { item ->
                    item.map { it.asExternalModel() } }
            }*/

            "SONG_COUNT" -> { //"songCount" -> {
                composersList =
                    if (isAscending) composerRepo.sortComposersBySongCountAsc()
                    else composerRepo.sortComposersBySongCountDesc()
            }

            else -> { //"NAME" //"name"
                composersList =
                    if (isAscending) composerRepo.sortComposersByNameAsc()
                    else composerRepo.sortComposersByNameDesc()
            }
        }

        //using this as the final catch all, but using the when cases to return if the option is met
        return composersList.map { items ->
            domainLogger.info { "********** Library Composers count: ${items.size} **********" }
            items.map { item ->
                item.asExternalModel()
            }
        }
    }
}