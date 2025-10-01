package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.database.model.ComposerWithExtraInfo
import com.example.music.data.repository.ComposerRepo
import com.example.music.domain.model.ComposerInfo
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "Get Library Composers"

/**
 * Use case for retrieving library composers to populate Composers List in Library Screen.
 * @property composerRepo The repository for accessing Composer data
 */
class GetLibraryComposers @Inject constructor(
    private val composerRepo: ComposerRepo
) {
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<List<ComposerInfo>> {
        val composersList: Flow<List<ComposerWithExtraInfo>>// = flowOf()
        Log.i(TAG, "Building Composer List:\n" +
            "Sort Option: $sortOption, isAscending: $isAscending")

        //sortOption values changed to support enum values AppPreferences dataStore
        when (sortOption) {

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

        return composersList.map { items ->
            Log.i(TAG, "********** Library Composers count: ${items.size} **********")
            items.map { item ->
                item.asExternalModel()
            }
        }
    }
}