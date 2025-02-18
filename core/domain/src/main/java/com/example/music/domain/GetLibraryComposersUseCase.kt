package com.example.music.domain

import com.example.music.data.database.model.Artist
import com.example.music.data.repository.ArtistRepo
import com.example.music.model.ArtistInfo
import com.example.music.model.ArtistSortModel
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
class GetLibraryComposersUseCase @Inject constructor(
    private val artistRepo: ArtistRepo
) {
    /**
     * Create a [ArtistSortModel] from the list of artists in [artistRepo].
     * @param sortOption: the column to sort by. If not met, default to sorting by artist name.
     * @param isAscending: the order to sort by. If true, sort Ascending. Else false, sort Descending.
     */
    operator fun invoke(sortOption: String, isAscending: Boolean): Flow<ArtistSortModel> {
        //how to choose which one is mapped, since either one can happen
        var artistsList: Flow<List<Artist>> = flowOf()
        when (sortOption) {
            "albumCount" -> {
                artistsList = if (isAscending) artistRepo.sortArtistsByAlbumCountAsc() else artistRepo.sortArtistsByAlbumCountDesc()
                return artistsList.map { artists ->
                    ArtistSortModel(
                        artists = artists.map { it.asExternalModel() },
                        count = artistRepo.count()
                    )
                }
            }
            "songCount" -> {
                artistsList = if (isAscending) artistRepo.sortArtistsBySongCountAsc() else artistRepo.sortArtistsBySongCountDesc()
                return artistsList.map { artists ->
                    ArtistSortModel(
                        artists = artists.map { it.asExternalModel() },
                        count = artistRepo.count()
                    )
                }
            }
            else -> {
                artistsList = if (isAscending) artistRepo.sortArtistsByNameAsc() else artistRepo.sortArtistsByNameDesc()
            }
        }

        //using this as the final catch all, but using the when cases to return if the option is met
        return artistsList.map { artists ->
            ArtistSortModel(
                artists = artists.map { it.asExternalModel() },
                count = artistRepo.count()
            )
        }
    }
}