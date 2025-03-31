package com.example.music.domain.usecases

import com.example.music.data.database.model.ArtistWithExtraInfo
import com.example.music.data.repository.ArtistRepo
import com.example.music.domain.model.ArtistInfo
import com.example.music.domain.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.music.domain.util.domainLogger
import javax.inject.Inject

/**
 * Use case for retrieving library artists to populate Artists List in Library Screen.
 * @property artistRepo [ArtistRepo] The repository for accessing Artist data
 */
class GetLibraryArtistsUseCase @Inject constructor(
    private val artistRepo: ArtistRepo
) {
    /**
     * Invoke to create a list of [ArtistInfo] from all of the artists in [artistRepo].
     * @param sortOption [String] The data property/attribute to sort by. If not met, default to sorting by artist name.
     * @param isAscending [Boolean] The order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<List<ArtistInfo>> {
        val artistsList: Flow<List<ArtistWithExtraInfo>>// = flowOf()
        domainLogger.info { "Building Artists List:\n Sort Option: $sortOption, isAscending: $isAscending" }

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

        return artistsList.map { items ->
            domainLogger.info { "********** Library Artists count: ${items.size} **********" }
            items.map { item ->
                item.asExternalModel()
            }
        }
    }
}