package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.database.model.ComposerWithExtraInfo
import com.example.music.data.repository.ComposerRepo
import com.example.music.data.repository.ComposerSortList
import com.example.music.data.util.FLAG
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
    operator fun invoke(
        sortColumn: String,
        isAscending: Boolean
    ): Flow<List<ComposerInfo>> {
        val composersList: Flow<List<ComposerWithExtraInfo>>
        Log.i(TAG, "START --- sortColumn: $sortColumn - isAscending: $isAscending")

        when (sortColumn) {
            ComposerSortList[0] -> { //"Name"
                composersList =
                    if (isAscending) composerRepo.sortComposersByNameAsc()
                    else composerRepo.sortComposersByNameDesc()
            }

            ComposerSortList[1] -> { // "Song Count"
                composersList =
                    if (isAscending) composerRepo.sortComposersBySongCountAsc()
                    else composerRepo.sortComposersBySongCountDesc()
            }

            else -> {
                composersList =
                    if (isAscending) composerRepo.sortComposersByNameAsc()
                    else composerRepo.sortComposersByNameDesc()
            }
        }

        return composersList.map { composers ->
            Log.i(TAG, "********** Library Composers count: ${composers.size} **********")
            composers.map { composer ->
                if (FLAG) Log.i(TAG, "**** Composer: ${composer.composer.id} + ${composer.composer.name} ****")
                composer.asExternalModel()
            }
        }
    }
}