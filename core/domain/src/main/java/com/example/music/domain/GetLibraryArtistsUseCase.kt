package com.example.music.domain

import com.example.music.data.database.model.ArtistWithExtraInfo
import com.example.music.data.repository.ArtistRepo
import com.example.music.model.ArtistInfo
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.music.util.domainLogger
import javax.inject.Inject

/**
 * Use case for retrieving library artists to populate Artists List in Library Screen.
 */
class GetLibraryArtistsUseCase @Inject constructor(
    private val artistRepo: ArtistRepo
) {
    /**
     * Create a list of [ArtistInfo] from the list of artists in [artistRepo].
     * @param sortOption: the column to sort by. If not met, default to sorting by artist name.
     * @param isAscending: the order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<List<ArtistInfo>> {
        val artistsList: Flow<List<ArtistWithExtraInfo>>// = flowOf()
        domainLogger.info { "Building Artists List: \nSort Option: $sortOption, isAscending: $isAscending" }

        //sortOption values changed to support enum values AppPreferences dataStore
        when (sortOption) {

            "ALBUM_COUNT" -> { //"albumCount" -> {
                artistsList =
                    if (isAscending) artistRepo.sortArtistsByAlbumCountAsc()
                    else artistRepo.sortArtistsByAlbumCountDesc()
            }

            "SONG_COUNT" -> { //"songCount" -> {
                artistsList =
                    if (isAscending) artistRepo.sortArtistsBySongCountAsc()
                    else artistRepo.sortArtistsBySongCountDesc()
            }

            else -> { //"NAME" //"name"
                artistsList =
                    if (isAscending) artistRepo.sortArtistsByNameAsc()
                    else artistRepo.sortArtistsByNameDesc()
            }
        }

        //using this as the final catch all, but using the when cases to return if the option is met
        return artistsList.map { items ->
            domainLogger.info { "********** Library Artists count: ${items.size} **********" }
            items.map { item ->
                item.asExternalModel()
            }
        }
    }
}